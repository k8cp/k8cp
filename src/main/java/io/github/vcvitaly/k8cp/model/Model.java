package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.client.KubeClient;
import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.client.impl.KubeClientImpl;
import io.github.vcvitaly.k8cp.client.impl.LocalFsClientImpl;
import io.github.vcvitaly.k8cp.domain.KubeConfigContainer;
import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.domain.KubePod;
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
import io.github.vcvitaly.k8cp.view.ViewFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
    private static final ViewFactory viewFactory = ViewFactory.getInstance();

    private Model() {}

    public ObservableList<KubeConfigContainer> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final String homePath = HomePathProviderHolder.homePathProvider.provideHomePath();
        final List<KubeConfigContainer> configChoices = KubeConfigSelectionServiceHolder.kubeConfigSelectionService
                .getConfigChoices(Paths.get(homePath, Constants.KUBE_FOLDER).toString());
        return FXCollections.observableList(configChoices);
    }

    public KubeConfigContainer getKubeConfigSelectionDto(Path path) throws KubeContextExtractionException {
        return KubeConfigSelectionServiceHolder.kubeConfigSelectionService.toKubeConfig(path);
    }

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

    public static Model getInstance() {
        return ModelHolder.model;
    }

    public static List<KubeNamespace> getKubeNamespaces() throws KubeApiException {
        return KubeServiceHolder.kubeService.getNamespaces();
    }

    public static List<KubePod> getKubePods() throws KubeApiException {
        if (kubeNamespaceSelectionRef.get() == null) {
            throw new IllegalStateException("Kube namespace has to be selected first");
        }
        return KubeServiceHolder.kubeService.getPods(kubeNamespaceSelectionRef.get().name());
    }

    private static class ModelHolder {
        private static final Model model = new Model();
    }

    private static class KubeClientHolder {
        private static final KubeClient kubeClient = getInstance();

        private static KubeClient getInstance() {
            final KubeConfigContainer kubeConfigContainer = kubeConfigSelectionRef.get();
            if (kubeConfigContainer == null) {
                throw new IllegalStateException("Kube config initialization has to be done first");
            }
            final KubeClient instance = new KubeClientImpl(kubeConfigContainer);
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class SizeConverterHolder {
        private static final SizeConverter sizeConverter = getInstance();

        private static SizeConverter getInstance() {
            final SizeConverter sizeConverter = new SizeConverterImpl();
            logCreatedNewInstanceOf(sizeConverter);
            return sizeConverter;
        }
    }

    private static class KubeServiceHolder {
        private static final KubeService kubeService = getInstance();

        private static KubeService getInstance() {
            final KubeServiceImpl instance = new KubeServiceImpl(
                    KubeClientHolder.kubeClient, SizeConverterHolder.sizeConverter
            );
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class LocalFsClientHolder {
        private static final LocalFsClient localFsClient = getInstance();

        private static LocalFsClient getInstance() {
            final LocalFsClientImpl instance = new LocalFsClientImpl();
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class LocalFsServiceHolder {
        private static final LocalFsService localFsService = getInstance();

        private static LocalFsService getInstance() {
            final LocalFsServiceImpl instance = new LocalFsServiceImpl(
                    LocalFsClientHolder.localFsClient, SizeConverterHolder.sizeConverter
            );
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class KubeConfigHelperHolder {
        private static final KubeConfigHelper kubeConfigHelper = getInstance();

        private static KubeConfigHelper getInstance() {
            final KubeConfigHelper instance = new KubeConfigHelperImpl();
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static class HomePathProviderHolder {
        private static final HomePathProvider homePathProvider = getInstance();

        private static HomePathProvider getInstance() {
            final HomePathProvider instance = new HomePathProviderImpl();
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    public static class KubeConfigSelectionServiceHolder {
        private static final KubeConfigSelectionService kubeConfigSelectionService = getInstance();

        private static KubeConfigSelectionService getInstance() {
            final KubeConfigSelectionService instance = new KubeConfigSelectionServiceImpl(
                    LocalFsClientHolder.localFsClient, KubeConfigHelperHolder.kubeConfigHelper
            );
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static void logCreatedNewInstanceOf(Object o) {
        log.info(NEW_INSTANCE_OF_MSG.formatted(o.getClass().getSimpleName()));
    }
}
