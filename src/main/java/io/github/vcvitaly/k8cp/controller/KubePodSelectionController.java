package io.github.vcvitaly.k8cp.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class KubePodSelectionController implements Initializable {
    public Label choosePodLbl;
    public ChoiceBox podSelector;
    public Label errorLbl;
    public Button prevBtn;
    public Button nextBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
