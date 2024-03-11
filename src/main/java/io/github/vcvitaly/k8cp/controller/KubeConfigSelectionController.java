package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.dto.KubeConfigChoiceDto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

public class KubeConfigSelectionController implements Initializable {
    public ChoiceBox<KubeConfigChoiceDto> kubeConfigSelector;
    public Button fsChooserBtn;
    public Button nextBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
