package io.github.vcvitaly.k8cp.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FxmlView {
    MAIN("/fxml/main.fxml"),
    ABOUT("/fxml/about.fxml"),
    CONFIG_CHOICE("/fxml/kubeConfigSelection.fxml"),
    ERROR("/fxml/error.fxml");

    @Getter
    private final String resourcePath;
}
