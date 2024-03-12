package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.domain.KubeNamespace;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.model.Model;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeNamespaceSelectionController implements Initializable {
    public ChoiceBox<KubeNamespace> namespaceSelector;
    public Label errorLbl;
    public Button prevBtn;
    public Button nextBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nextBtn.setOnAction(e -> onNext());
        try {
            final List<KubeNamespace> namespaces = Model.getKubeService().getNamespaces();
            namespaceSelector.setItems(FXCollections.observableList(namespaces));
            nextBtn.setDisable(false);
        } catch (KubeApiException e) {
            log.error("Could not get namespaces list", e);
            errorLbl.setText("Could not find any namespace on that cluster");
            Model.getInstance().getViewFactory().showErrorModal(e.getMessage());
        }
    }

    private void onNext() {
        final Stage selectionStage = (Stage) nextBtn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(selectionStage);
        Model.getInstance().getViewFactory().showMainWindow();
    }
}
