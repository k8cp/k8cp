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
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.enumeration.OsFamily;
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
import io.github.vcvitaly.k8cp.util.FileUtil;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Model {
    private static final String NEW_INSTANCE_OF_MSG = "Created a new instance of %s";
    private static final AtomicReference<KubeConfigContainer> kubeConfigSelectionRef = new AtomicReference<>();
    private static final AtomicReference<KubeNamespace> kubeNamespaceSelectionRef = new AtomicReference<>();
    private static final AtomicReference<KubePod> kubePodSelectionRef = new AtomicReference<>();
    private static final AtomicReference<String> localPathRef = new AtomicReference<>(PathProviderHolder.instance.provideLocalHomePath());

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
        return FileInfoContainer.builder()
                .path(parentPath)
                .name(Constants.PARENT_DIR_NAME)
                .fileType(FileType.PARENT_DIRECTORY)
                .build();
    }

    public static List<FileInfoContainer> listLocalFiles() throws IOOperationException {
        final String localPath = getLocalPathRef();
        final List<FileInfoContainer> files = new ArrayList<>(
                LocalFsServiceHolder.instance.listFiles(localPath, false)
        );
        if (!FileUtil.isRoot(Paths.get(localPath))) {
            files.add(getLocalParentDirectory());
        }
        files.sort(Comparator.naturalOrder());
        return files;
    }

    public static List<BreadCrumbFile> resolveLocalBreadcrumbTree() {
        final String currentPathStr = getLocalPathRef();
        Path currentPath = Paths.get(currentPathStr);
        final Queue<BreadCrumbFile> reversedTree = new LinkedList<>();
        while (currentPath != null) {
            reversedTree.add(toBreadCrumbFile(currentPath));
            currentPath = currentPath.getParent();
        }
        return reversedTree.stream().toList().reversed();
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
            case WINDOWS -> new RootInfoContainer(Constants.WINDOWS_ROOT, FileUtil.normalizeRootPath(Paths.get(Constants.WINDOWS_ROOT)));
            case LINUX, MACOS -> new RootInfoContainer(Constants.UNIX_ROOT, Constants.UNIX_ROOT);
        };
    }

    public static FileInfoContainer getRemoteParentDirectory() {
        // TODO
        return null;
    }

    public static List<FileInfoContainer> listRemoteFiles() {
        // TODO
        return null;
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

    public static void setLocalPathRef(String path) {
        if (compareAndSetLocalPathRef(path)) {
            log.info("Set local path ref to [{}]", path);
        }
    }

    public static void setLocalPathRefToParent() {
        final String parent = getLocalParentPath();
        if (compareAndSetLocalPathRef(parent)) {
            log.info("Set local path ref to parent [{}]", parent);
        }

    }

    public static void setLocalPathRefToHome() {
        final String homePath = PathProviderHolder.instance.provideLocalHomePath();
        if (compareAndSetLocalPathRef(homePath)) {
            log.info("Set local path ref to home path [{}]", homePath);
        }
    }

    public static void setLocalPathRefToRoot() {
        final String rootPath = PathProviderHolder.instance.provideLocalRootPath();
        if (compareAndSetLocalPathRef(rootPath)) {
            log.info("Set local path ref to root path [{}]", rootPath);
        }
    }

    /* Private methods */
    private static void logCreatedNewInstanceOf(Object o) {
        log.info(NEW_INSTANCE_OF_MSG.formatted(o.getClass().getSimpleName()));
    }

    private static RuntimeException logAndReturnRuntimeException(RuntimeException e) {
        log.error("Error: ", e);
        return e;
    }

    private static BreadCrumbFile toBreadCrumbFile(Path path) {
        final String pathName = FileUtil.getPathFilename(path);
        return new BreadCrumbFile(path.toString(), pathName);
    }

    private static String getLocalParentPath() {
        final Path path = Paths.get(getLocalPathRef());
        if (FileUtil.isRoot(path)) {
            return path.toString();
        }
        return path.getParent().toString();
    }

    private static synchronized boolean compareAndSetLocalPathRef(String path) {
        final String curPath = localPathRef.get();
        if (!path.equals(curPath)) {
            localPathRef.set(path);
            return true;
        }
        return false;
    }

    private static synchronized String getLocalPathRef() {
        return localPathRef.get();
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
