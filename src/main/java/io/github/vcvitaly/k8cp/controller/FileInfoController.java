package io.github.vcvitaly.k8cp.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FileInfoController implements Initializable {
    public Label fileInfoLbl;

    private final String fileInfo;

    public FileInfoController(String fileInfo) {
        this.fileInfo = fileInfo;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileInfoLbl.setText(fileInfo);
    }
}
