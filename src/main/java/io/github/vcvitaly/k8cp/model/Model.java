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
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.service.HomePathProvider;
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.LocalFsService;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import io.github.vcvitaly.k8cp.service.impl.HomePathProviderImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigHelperImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigSelectionServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.LocalFsServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.SizeConverterImpl;
import io.github.vcvitaly.k8cp.util.Constants;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private static final AtomicReference<String> localPathRef = new AtomicReference<>(HomePathProviderHolder.instance.provideHomePath());

    private Model() {}

    public static ObservableList<KubeConfigContainer> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final String homePath = HomePathProviderHolder.instance.provideHomePath();
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
        final Path currentPath = Paths.get(localPathRef.get());
        return FileInfoContainer.builder()
                .path(currentPath.getParent().toString())
                .name(Constants.PARENT_DIR_NAME)
                .fileType(FileType.PARENT_DIRECTORY)
                .build();
    }

    public static List<FileInfoContainer> listLocalFiles() throws IOOperationException {
        return LocalFsServiceHolder.instance.listFiles(localPathRef.get());
    }

    public static List<BreadCrumbFile> resolveLocalBreadcrumbTree() {
        final Path currentPath = Paths.get(localPathRef.get());
        final List<BreadCrumbFile> tree = new ArrayList<>(
                Collections.singleton(toBreadCrumbFile(currentPath.getRoot()))
        );
        for (Path element : currentPath) {
            tree.add(toBreadCrumbFile(element));
        }
        return tree;
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
        localPathRef.set(path);
        log.info("Set local path ref to [{}]", path);
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
        final String pathName = path.getParent() != null ? path.getFileName().toString() : normalizeRootPath(path);
        return new BreadCrumbFile(path.toString(), pathName);
    }

    private static String normalizeRootPath(Path root) {
        return root.toString().replace(":\\", "");
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

    private static class HomePathProviderHolder {
        private static final HomePathProvider instance = getInstance();

        private static HomePathProvider getInstance() {
            final HomePathProvider instance = new HomePathProviderImpl();
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
}
