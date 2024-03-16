package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.client.KubeClient;
import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.client.impl.KubeClientImpl;
import io.github.vcvitaly.k8cp.client.impl.LocalFsClientImpl;
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
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.LocalFsService;
import io.github.vcvitaly.k8cp.service.LocalOsFamilyDetector;
import io.github.vcvitaly.k8cp.service.PathProvider;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigHelperImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigSelectionServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.LocalFsServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.LocalOsFamilyDetectorImpl;
import io.github.vcvitaly.k8cp.service.impl.PathProviderImpl;
import io.github.vcvitaly.k8cp.service.impl.SizeConverterImpl;
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
import java.util.function.BiPredicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Model {
    private static final String NEW_INSTANCE_OF_MSG = "Created a new instance of %s";
    private static final AtomicReference<KubeConfigContainer> kubeConfigSelectionRef = new AtomicReference<>();
    private static final AtomicReference<KubeNamespace> kubeNamespaceSelectionRef = new AtomicReference<>();
    private static final AtomicReference<KubePod> kubePodSelectionRef = new AtomicReference<>();
    @Getter
    private static final AtomicReference<PathRefreshEvent> localPathEventRef = new AtomicReference<>(
            PathRefreshEvent.of(PathRefreshEventSource.LOCAL_INIT, PathProviderHolder.instance.provideLocalHomePath())
    );
    @Getter
    private static final AtomicReference<PathRefreshEvent> remotePathEventRef = new AtomicReference<>(
            PathRefreshEvent.of(PathRefreshEventSource.REMOTE_INIT, PathProviderHolder.instance.provideRemoteRootPath())
    );
    private static final AtomicReference<List<BreadCrumbFile>> localBreadcrumbTree = new AtomicReference<>();
    private static final AtomicReference<List<BreadCrumbFile>> remoteBreadcrumbTree = new AtomicReference<>();
    private static final AtomicReference<List<FileInfoContainer>> localFiles = new AtomicReference<>();
    private static final AtomicReference<List<FileInfoContainer>> remoteFiles = new AtomicReference<>();

    private Model() {}

    public static ObservableList<KubeConfigContainer> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final String homePath = PathProviderHolder.instance.provideLocalHomePath();
        final List<KubeConfigContainer> configChoices = KubeConfigSelectionServiceHolder.instance
                .getConfigChoices(Paths.get(homePath, Constants.KUBE_FOLDER).toString());
        return FXCollections.observableList(configChoices);
    }

    public static KubeConfigContainer getKubeConfigSelectionDto(Path path) throws KubeContextExtractionException {
        return KubeConfigSelectionServiceHolder.instance.toKubeConfig(path);
    }

    public static List<KubeNamespace> getKubeNamespaces() throws KubeApiException {
        return KubeServiceHolder.instance.getNamespaces();
    }

    public static List<KubePod> getKubePods() throws KubeApiException {
        if (kubeNamespaceSelectionRef.get() == null) {
            throw logAndReturnRuntimeException(new IllegalStateException("Kube namespace has to be selected first"));
        }
        return KubeServiceHolder.instance.getPods(kubeNamespaceSelectionRef.get().name());
    }

    public static FileInfoContainer getLocalParentDirectory() {
        final String parentPath = getLocalParentPath();
        return getParentDirectory(parentPath);
    }

    public static void resolveLocalFiles() throws IOOperationException {
        final PathRefreshEvent localPathEvent = getLocalPathEvent();
        final String currentPath = localPathEvent.data().path();
        final List<FileInfoContainer> files = new ArrayList<>(
                LocalFsServiceHolder.instance.listFiles(currentPath, false)
        );
        if (!LocalFileUtil.isRoot(Paths.get(currentPath))) {
            files.add(getLocalParentDirectory());
        }
        files.sort(Comparator.naturalOrder());
        localFiles.set(files);
        logInfoWithEvent(localPathEvent, "resolved the local files for [%s]".formatted(currentPath));
    }

    public static void resolveLocalBreadcrumbTree() {
        final PathRefreshEvent localPathEvent = getLocalPathEvent();
        final String currentPathStr = localPathEvent.data().path();
        Path tmpPath = Paths.get(currentPathStr);
        final Queue<BreadCrumbFile> reversedTree = new LinkedList<>();
        while (tmpPath != null) {
            reversedTree.add(toBreadCrumbFile(tmpPath));
            tmpPath = tmpPath.getParent();
        }
        final List<BreadCrumbFile> tree = reversedTree.stream().toList().reversed();
        localBreadcrumbTree.set(tree);
        logInfoWithEvent(localPathEvent, "resolved the local breadcrumb tree for [%s] to [%s]".formatted(currentPathStr, tree));
    }

    public static List<RootInfoContainer> listLocalRoots() throws IOOperationException {
        final OsFamily osFamily = LocalOsFamilyDetectorHolder.instance.detectOsFamily();
        LocalFsService fsService = LocalFsServiceHolder.instance;
        return switch (osFamily) {
            case WINDOWS -> fsService.listWindowsRoots();
            case LINUX -> fsService.listLinuxRoots();
            case MACOS -> fsService.listMacosRoots();
        };
    }

    public static RootInfoContainer getMainRoot() {
        final OsFamily osFamily = LocalOsFamilyDetectorHolder.instance.detectOsFamily();
        return switch (osFamily) {
            case WINDOWS -> new RootInfoContainer(
                    Constants.WINDOWS_ROOT,
                    LocalFileUtil.normalizeRootPath(Paths.get(Constants.WINDOWS_ROOT))
            );
            case LINUX, MACOS -> new RootInfoContainer(Constants.UNIX_ROOT, Constants.UNIX_ROOT);
        };
    }

    public static FileInfoContainer getRemoteParentDirectory() {
        final String parentPath = getRemoteParentPath();
        return getParentDirectory(parentPath);
    }

    public static void resolveRemoteFiles() throws IOOperationException {
        final PathRefreshEvent remotePathEvent = getRemotePathEvent();
        final String currentPath = remotePathEvent.data().path();
        final List<FileInfoContainer> files = new ArrayList<>(
                KubeServiceHolder.instance.listFiles(
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
        logInfoWithEvent(remotePathEvent, "resolved the remote files for [%s]".formatted(currentPath));
    }

    public static void resolveRemoteBreadcrumbTree() {
        final PathRefreshEvent remotePathEvent = getRemotePathEvent();
        final String currentPath = remotePathEvent.data().path();
        String tmpPath = currentPath;
        final List<BreadCrumbFile> reversedTree = new LinkedList<>();
        while (!UnixPathUtil.isRoot(tmpPath)) {
            reversedTree.add(toBreadCrumbFile(tmpPath));
            tmpPath = UnixPathUtil.getParentPath(tmpPath);
        }
        reversedTree.add(toBreadCrumbFile(tmpPath));
        final List<BreadCrumbFile> tree = reversedTree.reversed();
        remoteBreadcrumbTree.set(tree);
        logInfoWithEvent(remotePathEvent, "resolved the remote breadcrumb tree for [%s] to [%s]".formatted(currentPath, tree));
    }

    /* Setters */
    public static void setKubeConfigSelection(KubeConfigContainer selection) {
        kubeConfigSelectionRef.set(selection);
        log.info("Set kube config selection to [{}]", selection);
    }

    public static void setKubeNamespaceSelection(KubeNamespace selection) {
        kubeNamespaceSelectionRef.set(selection);
        log.info("Set kube namespace selection to [{}]", selection);
    }

    public static void setKubePodSelection(KubePod selection) {
        kubePodSelectionRef.set(selection);
        log.info("Set kube pod selection to [{}]", selection);
    }

    public static boolean setLocalPathEventRef(PathRefreshEvent event) {
        final boolean comparedAndSet = compareAndSetLocalPathEventRef(event);
        if (comparedAndSet) {
            log.info("Set local path event ref to [{}]", event);
        }
        return comparedAndSet;
    }

    public static boolean setLocalPathRefToParent(PathRefreshEventSource source) {
        final String parent = getLocalParentPath();
        return setLocalPathEventRef(PathRefreshEvent.of(source, parent));
    }

    public static boolean setLocalPathRefToHome(PathRefreshEventSource source) {
        final String home = PathProviderHolder.instance.provideLocalHomePath();
        return setLocalPathEventRef(PathRefreshEvent.of(source, home));
    }

    public static boolean setLocalPathRefToRoot(PathRefreshEventSource source) {
        final String root = PathProviderHolder.instance.provideLocalRootPath();
        return setLocalPathEventRef(PathRefreshEvent.of(source, root));
    }

    public static boolean setRemotePathEventRef(PathRefreshEvent event) {
        final boolean comparedAndSet = compareAndSetRemotePathEventRef(event);
        if (comparedAndSet) {
            log.info("Set remote path event ref to [{}]", event);
        }
        return comparedAndSet;
    }

    public static boolean setRemotePathRefToParent(PathRefreshEventSource source) {
        final String parent = getRemoteParentPath();
        return setRemotePathEventRef(PathRefreshEvent.of(source, parent));
    }

    public static boolean setRemotePathRefToHome(PathRefreshEventSource source) throws IOOperationException {
        final String home = KubeServiceHolder.instance.getHomeDir(
                kubeNamespaceSelectionRef.get().name(),
                kubePodSelectionRef.get().name()
        );
        return setRemotePathEventRef(PathRefreshEvent.of(source, home));
    }

    public static boolean setRemotePathRefToRoot(PathRefreshEventSource source) {
        final String rootPath = PathProviderHolder.instance.provideRemoteRootPath();
        return setRemotePathEventRef(PathRefreshEvent.of(source, rootPath));
    }

    /* Getters */
    public static synchronized String getLocalPath() {
        return localPathEventRef.get().data().path();
    }

    public static synchronized String getRemotePath() {
        return remotePathEventRef.get().data().path();
    }

    public static PathRefreshEvent getLocalPathEvent() {
        return localPathEventRef.get();
    }

    public static synchronized PathRefreshEvent getRemotePathEvent() {
        return remotePathEventRef.get();
    }

    // TODO should it be synchronized?
    public static List<BreadCrumbFile> getRemoteBreadcrumbTree() {
        return remoteBreadcrumbTree.get();
    }

    public static List<FileInfoContainer> getRemoteFiles() {
        return remoteFiles.get();
    }

    public static List<BreadCrumbFile> getLocalBreadcrumbTree() {
        return localBreadcrumbTree.get();
    }

    public static List<FileInfoContainer> getLocalFiles() {
        return localFiles.get();
    }

    /* Private methods */
    private static void logCreatedNewInstanceOf(Object o) {
        log.info(NEW_INSTANCE_OF_MSG.formatted(o.getClass().getSimpleName()));
    }

    private static RuntimeException logAndReturnRuntimeException(RuntimeException e) {
        log.error("Error: ", e);
        return e;
    }

    private static BreadCrumbFile toBreadCrumbFile(Path localPath) {
        final String pathName = LocalFileUtil.getPathFilename(localPath);
        return new BreadCrumbFile(localPath.toString(), pathName);
    }

    private static BreadCrumbFile toBreadCrumbFile(String remotePath) {
        return new BreadCrumbFile(remotePath, UnixPathUtil.getFilename(remotePath));
    }

    private static String getLocalParentPath() {
        final Path path = Paths.get(getLocalPath());
        if (LocalFileUtil.isRoot(path)) {
            return path.toString();
        }
        return path.getParent().toString();
    }

    private static synchronized boolean compareAndSetLocalPathEventRef(PathRefreshEvent event) {
        return compareAndSetRef(getLocalPathEventRef(), event, PathRefreshEvent::equalsByData);
    }

    private static synchronized boolean compareAndSetRemotePathEventRef(PathRefreshEvent event) {
        return compareAndSetRef(getRemotePathEventRef(), event, PathRefreshEvent::equalsByData);
    }

    private static <T> boolean compareAndSetRef(AtomicReference<T> ref, T t, BiPredicate<T, T> equalsPredicate) {
        final T cur = ref.get();
        if (!equalsPredicate.test(cur, t)) {
            ref.set(t);
            return true;
        }
        return false;
    }

    private static FileInfoContainer getParentDirectory(String parentPath) {
        return FileInfoContainer.builder()
                .path(parentPath)
                .name(Constants.PARENT_DIR_NAME)
                .fileType(FileType.PARENT_DIRECTORY)
                .build();
    }

    private static String getRemoteParentPath() {
        final String curPath = getRemotePath();
        return UnixPathUtil.getParentPath(curPath);
    }

    private static void logInfoWithEvent(PathRefreshEvent event, String msg) {
        log.info("Event[source=%s,uuid=%s] - %s".formatted(event.source(), event.data().uuid(), msg));
    }

    /* Holders */
    private static class KubeClientHolder {
        private static final KubeClient instance = getInstance();

        private static KubeClient getInstance() {
            final KubeConfigContainer kubeConfigContainer = kubeConfigSelectionRef.get();
            if (kubeConfigContainer == null) {
                throw logAndReturnRuntimeException(new IllegalStateException("Kube config initialization has to be done first"));
            }
            final KubeClient instance = new KubeClientImpl(kubeConfigContainer);
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class SizeConverterHolder {
        private static final SizeConverter instance = getInstance();

        private static SizeConverter getInstance() {
            final SizeConverter sizeConverter = new SizeConverterImpl();
            logCreatedNewInstanceOf(sizeConverter);
            return sizeConverter;
        }
    }

    private static class KubeServiceHolder {
        private static final KubeService instance = getInstance();

        private static KubeService getInstance() {
            final KubeServiceImpl instance = new KubeServiceImpl(
                    KubeClientHolder.instance, SizeConverterHolder.instance
            );
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class LocalFsClientHolder {
        private static final LocalFsClient instance = getInstance();

        private static LocalFsClient getInstance() {
            final LocalFsClientImpl instance = new LocalFsClientImpl();
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class LocalFsServiceHolder {
        private static final LocalFsService instance = getInstance();

        private static LocalFsService getInstance() {
            final LocalFsServiceImpl instance = new LocalFsServiceImpl(
                    LocalFsClientHolder.instance, SizeConverterHolder.instance
            );
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class KubeConfigHelperHolder {
        private static final KubeConfigHelper instance = getInstance();

        private static KubeConfigHelper getInstance() {
            final KubeConfigHelper instance = new KubeConfigHelperImpl();
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class PathProviderHolder {
        private static final PathProvider instance = getInstance();

        private static PathProvider getInstance() {
            final PathProvider instance = new PathProviderImpl(LocalOsFamilyDetectorHolder.instance);
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    public static class KubeConfigSelectionServiceHolder {
        private static final KubeConfigSelectionService instance = getInstance();

        private static KubeConfigSelectionService getInstance() {
            final KubeConfigSelectionService instance = new KubeConfigSelectionServiceImpl(
                    LocalFsClientHolder.instance, KubeConfigHelperHolder.instance
            );
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class LocalOsFamilyDetectorHolder {
        private static final LocalOsFamilyDetector instance = getInstance();

        private static LocalOsFamilyDetector getInstance() {
            final LocalOsFamilyDetector instance = new LocalOsFamilyDetectorImpl();
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }
}
