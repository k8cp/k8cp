package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.client.impl.LocalFsClientImpl;
import io.github.vcvitaly.k8cp.domain.KubeConfig;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.service.HomePathProvider;
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.impl.HomePathProviderImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigHelperImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigSelectionServiceImpl;
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

    @Getter
    private final AtomicReference<KubeConfig> kubeConfigSelectionRef;

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
        kubeConfigSelectionRef = new AtomicReference<>();
        viewFactory = ViewFactory.getInstance();
        localFsClient = new LocalFsClientImpl();
        kubeConfigHelper = new KubeConfigHelperImpl();
        homePathProvider = new HomePathProviderImpl();
        kubeConfigSelectionService = new KubeConfigSelectionServiceImpl(localFsClient, kubeConfigHelper);
    }

    public ObservableList<KubeConfig> getKubeConfigList() throws IOOperationException, KubeContextExtractionException {
        final String homePath = homePathProvider.provideHomePath();
        final List<KubeConfig> configChoices = kubeConfigSelectionService
                .getConfigChoices(Paths.get(homePath, Constants.KUBE_FOLDER).toString());
        return FXCollections.observableList(configChoices);
    }

    public KubeConfig getKubeConfigSelectionDto(Path path) throws KubeContextExtractionException {
        return kubeConfigSelectionService.toKubeConfig(path);
    }

    public KubeConfig getKubeConfigSelection() {
        return kubeConfigSelectionRef.get();
    }

    public void setKubeConfigSelection(KubeConfig selection) {
        kubeConfigSelectionRef.set(selection);
    }

    public static Model getInstance() {
        return ModelHolder.model;
    }

    private static class ModelHolder {
        private static final Model model = new Model();
    }
}
