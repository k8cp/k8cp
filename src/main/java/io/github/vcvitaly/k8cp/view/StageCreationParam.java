package io.github.vcvitaly.k8cp.view;

import io.github.vcvitaly.k8cp.enumeration.FxmlView;
import io.github.vcvitaly.k8cp.exception.ParamFieldNotFulfilledException;
import javafx.fxml.Initializable;
import javafx.stage.Modality;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StageCreationParam {
    private final FxmlView fxmlView;
    private final Modality modality;
    private final String title;
    private final Initializable controller;
    private final Boolean resizeable;

    private StageCreationParam(FxmlView fxmlView, Modality modality, String title, Initializable controller, Boolean resizeable) {
        if (fxmlView == null) {
            throw new ParamFieldNotFulfilledException("fxmlView");
        }
        this.fxmlView = fxmlView;
        this.modality = modality;
        this.title = title;
        this.controller = controller;
        this.resizeable = resizeable;
    }
}
