package io.github.vcvitaly.k8cp.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ErrorController implements Initializable {
    public Label errorLbl;

    /* TODO (VChura) implement functionality to report an error */

    public ErrorController(String errorMsg) {
        errorLbl.setText(errorMsg);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
