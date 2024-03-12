package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.client.KubeClient;
import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.client.impl.KubeClientImpl;
import io.github.vcvitaly.k8cp.client.impl.LocalFsClientImpl;
import io.github.vcvitaly.k8cp.domain.KubeConfigContainer;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.service.HomePathProvider;
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.KubeService;
import io.github.vcvitaly.k8cp.service.SizeConverter;
import io.github.vcvitaly.k8cp.service.impl.HomePathProviderImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigHelperImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigSelectionServiceImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeServiceImpl;
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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Model {
    private static final AtomicReference<KubeConfigContainer> kubeConfigSelectionRef = new AtomicReference<>();
    private static final String NEW_INSTANCE_OF_MSG = "Created a new instance of %s";

    @Getter
    @Setter
    private String podName;
    @Getter
    private final ViewFactory viewFactory;
    private final LocalFsClient localFsClient;
    private final KubeConfigHelper kubeConfigHelper;
    private final HomePathProvider homePathProvider;
    private final KubeConfigSelectionService kubeConfigSelectionService;

    private Model() {
        viewFactory = ViewFactory.getInstance();
        localFsClient = new LocalFsClientImpl();
        kubeConfigHelper = new KubeConfigHelperImpl();
        homePathProvider = new HomePathProviderImpl();
        kubeConfigSelectionService = new KubeConfigSelectionServiceImpl(localFsClient, kubeConfigHelper);
    }

    public ObservableList<KubeConfigContainer> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final String homePath = homePathProvider.provideHomePath();
        final List<KubeConfigContainer> configChoices = kubeConfigSelectionService
                .getConfigChoices(Paths.get(homePath, Constants.KUBE_FOLDER).toString());
        return FXCollections.observableList(configChoices);
    }

    public KubeConfigContainer getKubeConfigSelectionDto(Path path) throws KubeContextExtractionException {
        return kubeConfigSelectionService.toKubeConfig(path);
    }

    public void setKubeConfigSelection(KubeConfigContainer selection) {
        kubeConfigSelectionRef.set(selection);
        log.info("Set selection to [{}]", selection);
    }

    public static Model getInstance() {
        return ModelHolder.model;
    }

    public static KubeService getKubeService() {
        return KubeServiceHolder.kubeService;
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

    private static class KubeServiceHolder {
        private static final KubeService kubeService = getInstance();

        private static KubeService getInstance() {
            final SizeConverter sizeConverter = new SizeConverterImpl();
            logCreatedNewInstanceOf(sizeConverter);
            final KubeServiceImpl instance = new KubeServiceImpl(KubeClientHolder.kubeClient, sizeConverter);
            logCreatedNewInstanceOf(instance);
            return instance;
        }
    }

    private static void logCreatedNewInstanceOf(Object o) {
        log.info(NEW_INSTANCE_OF_MSG.formatted(o.getClass().getSimpleName()));
    }
}
