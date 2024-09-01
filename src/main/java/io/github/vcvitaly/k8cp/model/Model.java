package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.context.Context;
import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.KubeConfigContainer;
import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
import io.github.vcvitaly.k8cp.domain.PathRefreshEvent;
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.enumeration.OsFamily;
import io.github.vcvitaly.k8cp.enumeration.PathRefreshEventSource;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.LocalFsService;
import io.github.vcvitaly.k8cp.service.LocalOsFamilyDetector;
import io.github.vcvitaly.k8cp.service.PathProvider;
import io.github.vcvitaly.k8cp.util.Constants;
import io.github.vcvitaly.k8cp.util.PathUtil;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
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
    private AtomicReference<PathRefreshEvent> localPathEventRef;
    @Getter
    private AtomicReference<PathRefreshEvent> remotePathEventRef;
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
        localPathEventRef = new AtomicReference<>(
                PathRefreshEvent.of(PathRefreshEventSource.LOCAL_INIT, this.pathProvider.provideLocalHomePath())
        );
        remotePathEventRef = new AtomicReference<>(
                PathRefreshEvent.of(PathRefreshEventSource.REMOTE_INIT, this.pathProvider.provideRemoteRootPath())
        );
    }

    public ObservableList<KubeConfigContainer> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final Path homePath = pathProvider.provideLocalHomePath();
        final List<KubeConfigContainer> configChoices = kubeConfigSelectionService
                .getConfigChoices(homePath.resolve(Constants.KUBE_FOLDER));
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
            throw logAndReturnRuntimeException(new IllegalStateException("A kube namespace has to be selected first"));
        }
        return kubeServiceSupplier.get().getPods(kubeNamespaceSelectionRef.get().name());
    }

    public FileInfoContainer getLocalParentDirectory() {
        final Path parentPath = getLocalParentPath();
        return getParentDirectory(parentPath);
    }

    public void resolveLocalFiles() throws IOOperationException {
        final PathRefreshEvent localPathEvent = getLocalPathEvent();
        final Path currentPath = localPathEvent.data().path();
        final List<FileInfoContainer> files = new ArrayList<>(
                localFsService.listFiles(currentPath, false)
        );
        if (!PathUtil.isRoot(currentPath)) {
            files.add(getLocalParentDirectory());
        }
        files.sort(Comparator.naturalOrder());
        localFiles.set(files);
        logInfoWithEvent(localPathEvent, "resolved the local files for [%s]".formatted(currentPath));
    }

    public void resolveLocalBreadcrumbTree() {
        final PathRefreshEvent localPathEvent = getLocalPathEvent();
        final Path currentPath = localPathEvent.data().path();
        final List<BreadCrumbFile> tree = resolveBreadCrumbFiles(currentPath, localPathEvent.source(), localPathEvent.data().uuid());
        localBreadcrumbTree.set(tree);
        logInfoWithEvent(localPathEvent, "resolved the local breadcrumb tree for [%s] to [%s]".formatted(currentPath, tree));
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
        return localFsService.getMainRoot(osFamily);
    }

    public FileInfoContainer getRemoteParentDirectory() {
        final Path parentPath = getRemoteParentPath();
        return getParentDirectory(parentPath);
    }

    public void resolveRemoteFiles() throws IOOperationException {
        final PathRefreshEvent remotePathEvent = getRemotePathEvent();
        final Path currentPath = remotePathEvent.data().path();
        final List<FileInfoContainer> files = new ArrayList<>(
                kubeServiceSupplier.get().listFiles(
                        kubeNamespaceSelectionRef.get().name(),
                        kubePodSelectionRef.get().name(),
                        currentPath,
                        false
                )
        );
        if (!PathUtil.isRoot(currentPath)) {
            files.add(getRemoteParentDirectory());
        }
        files.sort(Comparator.naturalOrder());
        remoteFiles.set(files);
        logInfoWithEvent(remotePathEvent, "resolved the remote files for [%s]".formatted(currentPath));
    }

    public void resolveRemoteBreadcrumbTree() {
        final PathRefreshEvent remotePathEvent = getRemotePathEvent();
        final Path currentPath = remotePathEvent.data().path();
        final List<BreadCrumbFile> tree = resolveBreadCrumbFiles(currentPath, remotePathEvent.source(), remotePathEvent.data().uuid());
        remoteBreadcrumbTree.set(tree);
        logInfoWithEvent(remotePathEvent, "resolved the remote breadcrumb tree for [%s] to [%s]".formatted(currentPath, tree));
    }

    /* Setters */
    public void setKubeConfigSelection(KubeConfigContainer selection) {
        Context.kubeConfigSelectionRef.set(selection);
        log.info("Set the kube config selection to [{}]", selection);
    }

    public void setKubeNamespaceSelection(KubeNamespace selection) {
        kubeNamespaceSelectionRef.set(selection);
        log.info("Set the kube namespace selection to [{}]", selection);
    }

    public void setKubePodSelection(KubePod selection) {
        kubePodSelectionRef.set(selection);
        log.info("Set the kube pod selection to [{}]", selection);
    }

    public boolean setLocalPathEventRef(PathRefreshEvent event) {
        final boolean comparedAndSet = compareAndSetLocalPathEventRef(event);
        if (comparedAndSet) {
            log.info("Set the local path event ref event to [{}]", event);
        }
        return comparedAndSet;
    }

    public boolean setLocalPathEventRefToParent(PathRefreshEventSource source) {
        final Path parent = getLocalParentPath();
        return setLocalPathEventRef(PathRefreshEvent.of(source, parent));
    }

    public boolean setLocalPathEventRefToHome(PathRefreshEventSource source) {
        final Path home = pathProvider.provideLocalHomePath();
        return setLocalPathEventRef(PathRefreshEvent.of(source, home));
    }

    public boolean setLocalPathEventRefToRoot(PathRefreshEventSource source) {
        final Path root = pathProvider.provideLocalRootPath();
        return setLocalPathEventRef(PathRefreshEvent.of(source, root));
    }

    public boolean setRemotePathEventRef(PathRefreshEvent event) {
        final boolean comparedAndSet = compareAndSetRemotePathEventRef(event);
        if (comparedAndSet) {
            log.info("Set the remote path event ref to [{}]", event);
        }
        return comparedAndSet;
    }

    public boolean setRemotePathEventRefToParent(PathRefreshEventSource source) {
        final Path parent = getRemoteParentPath();
        return setRemotePathEventRef(PathRefreshEvent.of(source, parent));
    }

    public boolean setRemotePathEventRefToHome(PathRefreshEventSource source) throws IOOperationException {
        final Path home = PathUtil.getPath(
                kubeServiceSupplier.get().getHomeDir(
                        kubeNamespaceSelectionRef.get().name(),
                        kubePodSelectionRef.get().name()
                )
        );
        return setRemotePathEventRef(PathRefreshEvent.of(source, home));
    }

    public boolean setRemotePathEventRefToRoot(PathRefreshEventSource source) {
        final Path rootPath = pathProvider.provideRemoteRootPath();
        return setRemotePathEventRef(PathRefreshEvent.of(source, rootPath));
    }

    /* Getters */
    public synchronized Path getLocalPath() {
        return localPathEventRef.get().data().path();
    }

    public synchronized Path getRemotePath() {
        return remotePathEventRef.get().data().path();
    }

    public synchronized PathRefreshEvent getLocalPathEvent() {
        return localPathEventRef.get();
    }

    public synchronized PathRefreshEvent getRemotePathEvent() {
        return remotePathEventRef.get();
    }

    // TODO should it be synchronized?
    public synchronized List<BreadCrumbFile> getRemoteBreadcrumbTree() {
        return remoteBreadcrumbTree.get();
    }

    public synchronized List<FileInfoContainer> getRemoteFiles() {
        return remoteFiles.get();
    }

    public synchronized List<BreadCrumbFile> getLocalBreadcrumbTree() {
        return localBreadcrumbTree.get();
    }

    public synchronized List<FileInfoContainer> getLocalFiles() {
        return localFiles.get();
    }

    /* Private methods */
    private BreadCrumbFile toBreadCrumbFile(Path path, PathRefreshEventSource source, UUID uuid) {
        final String pathName = PathUtil.getPathFilename(path);
        return new BreadCrumbFile(PathRefreshEvent.of(source, uuid, path), pathName);
    }

    private Path getLocalParentPath() {
        final Path path = getLocalPath();
        return getParentOrItself(path);
    }

    private boolean compareAndSetLocalPathEventRef(PathRefreshEvent event) {
        return compareAndSetRef(getLocalPathEventRef(), event, PathRefreshEvent::equalsByData);
    }

    private boolean compareAndSetRemotePathEventRef(PathRefreshEvent event) {
        return compareAndSetRef(getRemotePathEventRef(), event, PathRefreshEvent::equalsByData);
    }

    private synchronized <T> boolean compareAndSetRef(AtomicReference<T> ref, T t, BiPredicate<T, T> equalsPredicate) {
        final T cur = ref.get();
        if (!equalsPredicate.test(cur, t)) {
            ref.set(t);
            return true;
        }
        return false;
    }

    private FileInfoContainer getParentDirectory(Path parentPath) {
        return FileInfoContainer.builder()
                .path(parentPath)
                .name(Constants.PARENT_DIR_NAME)
                .fileType(FileType.PARENT_DIRECTORY)
                .build();
    }

    private Path getRemoteParentPath() {
        final Path curPath = getRemotePath();
        return getParentOrItself(curPath);
    }

    private RuntimeException logAndReturnRuntimeException(RuntimeException e) {
        log.error("Error: ", e);
        return e;
    }

    private List<BreadCrumbFile> resolveBreadCrumbFiles(Path tmpPath, PathRefreshEventSource source, UUID uuid) {
        final Queue<BreadCrumbFile> reversedTree = new LinkedList<>();
        while (tmpPath != null) {
            reversedTree.add(toBreadCrumbFile(tmpPath, source, uuid));
            tmpPath = tmpPath.getParent();
        }
        return reversedTree.stream().toList().reversed();
    }

    private Path getParentOrItself(Path path) {
        if (PathUtil.isRoot(path)) {
            return path;
        }
        return path.getParent();
    }

    private void logInfoWithEvent(PathRefreshEvent event, String msg) {
        log.info("Event[source={},uuid={}] - {}", event.source(), event.data().uuid(), msg);
    }
}
