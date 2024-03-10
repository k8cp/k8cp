package io.github.vcvitaly.k8cp.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FxmlView {
    MAIN("/fxml/mainView.fxml"),
    ABOUT("/fxml/aboutView.fxml");

    @Getter
    private final String resourcePath;
}
