package io.github.vcvitaly.k8cp.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ErrorController implements Initializable {
    public Label errorLbl;

    private final String errorMsg;

    /* TODO (VChura) implement functionality to report an error */

    public ErrorController(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLbl.setText(errorMsg);
    }
}
