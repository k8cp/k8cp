package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.KubeConfigContainer;
import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.enumeration.OsFamily;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.context.Context;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.LocalFsService;
import io.github.vcvitaly.k8cp.service.LocalOsFamilyDetector;
import io.github.vcvitaly.k8cp.service.PathProvider;
import io.github.vcvitaly.k8cp.util.Constants;
import io.github.vcvitaly.k8cp.util.LocalFileUtil;
import io.github.vcvitaly.k8cp.util.UnixPathUtil;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Model {
    private final AtomicReference<KubeNamespace> kubeNamespaceSelectionRef = new AtomicReference<>();
    private final AtomicReference<KubePod> kubePodSelectionRef = new AtomicReference<>();
    @Getter
    private final AtomicReference<String> localPathRef;
    @Getter
    private final AtomicReference<String> remotePathRef;
    private final AtomicReference<List<BreadCrumbFile>> localBreadcrumbTree = new AtomicReference<>();
    private final AtomicReference<List<BreadCrumbFile>> remoteBreadcrumbTree = new AtomicReference<>();
    private final AtomicReference<List<FileInfoContainer>> localFiles = new AtomicReference<>();
    private final AtomicReference<List<FileInfoContainer>> remoteFiles = new AtomicReference<>();

    private final Supplier<KubeService> kubeServiceSupplier;
    private final LocalFsService localFsService;
    private final KubeConfigSelectionService kubeConfigSelectionService;
    private final PathProvider pathProvider;
    private final LocalOsFamilyDetector localOsFamilyDetector;

    @Builder
    public Model(
            Supplier<KubeService> kubeServiceSupplier,
            LocalFsService localFsService,
            KubeConfigSelectionService kubeConfigSelectionService,
            PathProvider pathProvider,
            LocalOsFamilyDetector localOsFamilyDetector
    ) {
        this.kubeServiceSupplier = kubeServiceSupplier;
        this.localFsService = localFsService;
        this.kubeConfigSelectionService = kubeConfigSelectionService;
        this.pathProvider = pathProvider;
        this.localOsFamilyDetector = localOsFamilyDetector;
        localPathRef = new AtomicReference<>(this.pathProvider.provideLocalHomePath());
        remotePathRef = new AtomicReference<>(this.pathProvider.provideRemoteRootPath());
    }

    public ObservableList<KubeConfigContainer> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final String homePath = pathProvider.provideLocalHomePath();
        final List<KubeConfigContainer> configChoices = kubeConfigSelectionService
                .getConfigChoices(Paths.get(homePath, Constants.KUBE_FOLDER).toString());
        return FXCollections.observableList(configChoices);
    }

    public KubeConfigContainer getKubeConfigSelectionDto(Path path) throws KubeContextExtractionException {
        return kubeConfigSelectionService.toKubeConfig(path);
    }

    public List<KubeNamespace> getKubeNamespaces() throws KubeApiException {
        return kubeServiceSupplier.get().getNamespaces();
    }

    public List<KubePod> getKubePods() throws KubeApiException {
        if (kubeNamespaceSelectionRef.get() == null) {
            throw logAndReturnRuntimeException(new IllegalStateException("Kube namespace has to be selected first"));
        }
        return kubeServiceSupplier.get().getPods(kubeNamespaceSelectionRef.get().name());
    }

    public FileInfoContainer getLocalParentDirectory() {
        final String parentPath = getLocalParentPath();
        return getParentDirectory(parentPath);
    }

    public void resolveLocalFiles() throws IOOperationException {
        final String currentPath = getLocalPath();
        final List<FileInfoContainer> files = new ArrayList<>(
                localFsService.listFiles(currentPath, false)
        );
        if (!LocalFileUtil.isRoot(Paths.get(currentPath))) {
            files.add(getLocalParentDirectory());
        }
        files.sort(Comparator.naturalOrder());
        localFiles.set(files);
        log.info("Resolved local files for [%s]".formatted(currentPath));
    }

    public void resolveLocalBreadcrumbTree() {
        final String currentPathStr = getLocalPath();
        Path tmpPath = Paths.get(currentPathStr);
        final Queue<BreadCrumbFile> reversedTree = new LinkedList<>();
        while (tmpPath != null) {
            reversedTree.add(toBreadCrumbFile(tmpPath));
            tmpPath = tmpPath.getParent();
        }
        final List<BreadCrumbFile> tree = reversedTree.stream().toList().reversed();
        localBreadcrumbTree.set(tree);
        log.info("Resolved local breadcrumb tree for [%s] to [%s]".formatted(currentPathStr, tree));
    }

    public List<RootInfoContainer> listLocalRoots() throws IOOperationException {
        final OsFamily osFamily = localOsFamilyDetector.detectOsFamily();
        return switch (osFamily) {
            case WINDOWS -> localFsService.listWindowsRoots();
            case LINUX -> localFsService.listLinuxRoots();
            case MACOS -> localFsService.listMacosRoots();
        };
    }

    public RootInfoContainer getMainRoot() {
        final OsFamily osFamily = localOsFamilyDetector.detectOsFamily();
        return switch (osFamily) {
            case WINDOWS -> new RootInfoContainer(
                    Constants.WINDOWS_ROOT,
                    LocalFileUtil.normalizeRootPath(Paths.get(Constants.WINDOWS_ROOT))
            );
            case LINUX, MACOS -> new RootInfoContainer(Constants.UNIX_ROOT, Constants.UNIX_ROOT);
        };
    }

    public FileInfoContainer getRemoteParentDirectory() {
        final String parentPath = getRemoteParentPath();
        return getParentDirectory(parentPath);
    }

    public void resolveRemoteFiles() throws IOOperationException {
        final String currentPath = getRemotePath();
        final List<FileInfoContainer> files = new ArrayList<>(
                kubeServiceSupplier.get().listFiles(
                        kubeNamespaceSelectionRef.get().name(),
                        kubePodSelectionRef.get().name(),
                        currentPath,
                        false
                )
        );
        if (!UnixPathUtil.isRoot(currentPath)) {
            files.add(getRemoteParentDirectory());
        }
        files.sort(Comparator.naturalOrder());
        remoteFiles.set(files);
        log.info("Resolved remote files for [%s]".formatted(currentPath));
    }

    public void resolveRemoteBreadcrumbTree() {
        final String currentPath = getRemotePath();
        String tmpPath = currentPath;
        final List<BreadCrumbFile> reversedTree = new LinkedList<>();
        while (!UnixPathUtil.isRoot(tmpPath)) {
            reversedTree.add(toBreadCrumbFile(tmpPath));
            tmpPath = UnixPathUtil.getParentPath(tmpPath);
        }
        reversedTree.add(toBreadCrumbFile(tmpPath));
        final List<BreadCrumbFile> tree = reversedTree.reversed();
        remoteBreadcrumbTree.set(tree);
        log.info("Resolved remote breadcrumb tree for [%s] to [%s]".formatted(currentPath, tree));
    }

    /* Setters */
    public void setKubeConfigSelection(KubeConfigContainer selection) {
        Context.kubeConfigSelectionRef.set(selection);
        log.info("Set kube config selection to [{}]", selection);
    }

    public void setKubeNamespaceSelection(KubeNamespace selection) {
        kubeNamespaceSelectionRef.set(selection);
        log.info("Set kube namespace selection to [{}]", selection);
    }

    public void setKubePodSelection(KubePod selection) {
        kubePodSelectionRef.set(selection);
        log.info("Set kube pod selection to [{}]", selection);
    }

    public boolean setLocalPathRef(String path) {
        final boolean comparedAndSet = compareAndSetLocalPathRef(path);
        if (comparedAndSet) {
            log.info("Set local path ref to [{}]", path);
        }
        return comparedAndSet;
    }

    public void setLocalPathRefToParent() {
        final String parent = getLocalParentPath();
        if (compareAndSetLocalPathRef(parent)) {
            log.info("Set local path ref to parent [{}]", parent);
        }
    }

    public void setLocalPathRefToHome() {
        final String home = pathProvider.provideLocalHomePath();
        if (compareAndSetLocalPathRef(home)) {
            log.info("Set local path ref to home path [{}]", home);
        }
    }

    public void setLocalPathRefToRoot() {
        final String root = pathProvider.provideLocalRootPath();
        if (compareAndSetLocalPathRef(root)) {
            log.info("Set local path ref to root path [{}]", root);
        }
    }

    public boolean setRemotePathRef(String path) {
        final boolean comparedAndSet = compareAndSetRemotePathRef(path);
        if (comparedAndSet) {
            log.info("Set remote path ref to [{}]", path);
        }
        return comparedAndSet;
    }

    public boolean setRemotePathRefToParent() {
        final String parent = getRemoteParentPath();
        final boolean comparedAndSet = compareAndSetRemotePathRef(parent);
        if (comparedAndSet) {
            log.info("Set remote path ref to parent [{}]", parent);
        }
        return comparedAndSet;
    }

    public void setRemotePathRefToHome() throws IOOperationException {
        final String home = kubeServiceSupplier.get().getHomeDir(
                kubeNamespaceSelectionRef.get().name(),
                kubePodSelectionRef.get().name()
        );
        if (compareAndSetRemotePathRef(home)) {
            log.info("Set remote path ref to home path [{}]", home);
        }
    }

    public void setRemotePathRefToRoot() {
        final String rootPath = pathProvider.provideRemoteRootPath();
        if (compareAndSetRemotePathRef(rootPath)) {
            log.info("Set remote path ref to root path [{}]", rootPath);
        }
    }

    /* Getters */
    public synchronized String getLocalPath() {
        return localPathRef.get();
    }

    public synchronized String getRemotePath() {
        return remotePathRef.get();
    }

    // TODO should it be synchronized?
    public List<BreadCrumbFile> getRemoteBreadcrumbTree() {
        return remoteBreadcrumbTree.get();
    }

    public List<FileInfoContainer> getRemoteFiles() {
        return remoteFiles.get();
    }

    public List<BreadCrumbFile> getLocalBreadcrumbTree() {
        return localBreadcrumbTree.get();
    }

    public List<FileInfoContainer> getLocalFiles() {
        return localFiles.get();
    }

    /* Private methods */
    private BreadCrumbFile toBreadCrumbFile(Path localPath) {
        final String pathName = LocalFileUtil.getPathFilename(localPath);
        return new BreadCrumbFile(localPath.toString(), pathName);
    }

    private BreadCrumbFile toBreadCrumbFile(String remotePath) {
        return new BreadCrumbFile(remotePath, UnixPathUtil.getFilename(remotePath));
    }

    private String getLocalParentPath() {
        final Path path = Paths.get(getLocalPath());
        if (LocalFileUtil.isRoot(path)) {
            return path.toString();
        }
        return path.getParent().toString();
    }

    private synchronized boolean compareAndSetLocalPathRef(String path) {
        return compareAndSetRef(getLocalPathRef(), path);
    }

    private synchronized boolean compareAndSetRemotePathRef(String path) {
        return compareAndSetRef(getRemotePathRef(), path);
    }

    private <T> boolean compareAndSetRef(AtomicReference<T> ref, T t) {
        final T cur = ref.get();
        if (!t.equals(cur)) {
            ref.set(t);
            return true;
        }
        return false;
    }

    private FileInfoContainer getParentDirectory(String parentPath) {
        return FileInfoContainer.builder()
                .path(parentPath)
                .name(Constants.PARENT_DIR_NAME)
                .fileType(FileType.PARENT_DIRECTORY)
                .build();
    }

    private String getRemoteParentPath() {
        final String curPath = getRemotePath();
        return UnixPathUtil.getParentPath(curPath);
    }

    private RuntimeException logAndReturnRuntimeException(RuntimeException e) {
        log.error("Error: ", e);
        return e;
    }
}
