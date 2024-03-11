package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.client.LocalFsClient;
import io.github.vcvitaly.k8cp.client.impl.LocalFsClientImpl;
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import io.github.vcvitaly.k8cp.service.KubeConfigSelectionService;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigHelperImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigSelectionServiceImpl;
import io.github.vcvitaly.k8cp.view.ViewFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Model {

    @Getter
    @Setter
    private String podName;

    @Getter
    private final ViewFactory viewFactory;

    private final LocalFsClient localFsClient;

    private final KubeConfigHelper kubeConfigHelper;

    private final KubeConfigSelectionService kubeConfigSelectionService;

    private Model() {
        viewFactory = new ViewFactory();
        localFsClient = new LocalFsClientImpl();
        kubeConfigHelper = new KubeConfigHelperImpl();
        kubeConfigSelectionService = new KubeConfigSelectionServiceImpl(localFsClient, kubeConfigHelper);
    }

    public static Model getInstance() {
        return ModelHolder.model;
    }

    private static class ModelHolder {
        private static final Model model = new Model();
    }
}
