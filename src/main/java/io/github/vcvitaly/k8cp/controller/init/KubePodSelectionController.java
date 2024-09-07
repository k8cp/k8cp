package io.github.vcvitaly.k8cp.controller.init;

import io.github.vcvitaly.k8cp.domain.KubePod;
import io.github.vcvitaly.k8cp.domain.PodContainer;
import io.github.vcvitaly.k8cp.exception.KubeApiException;
import io.github.vcvitaly.k8cp.context.ServiceLocator;
import io.github.vcvitaly.k8cp.util.ItemSelectionUtil;
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
public class KubePodSelectionController implements Initializable {
    public Label podSelectionLbl;
    public ChoiceBox<KubePod> podSelector;
    public Label podErrorLbl;
    public Label containerSelectionLbl;
    public ChoiceBox<PodContainer> containerSelector;
    public Label containerSelectionError;
    public Button prevBtn;
    public Button nextBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prevBtn.setOnAction(e -> onPrev());
        nextBtn.setOnAction(e -> onNext());
        try {
            final List<KubePod> pods = ServiceLocator.getModel().getKubePods();
            if (!pods.isEmpty()) {
                podSelector.setItems(FXCollections.observableList(pods));
                final KubePod selectedItem = ItemSelectionUtil.getSelectionItem(
                        pods,
                        selection -> true
                );
                podSelector.setValue(selectedItem);
                setKubePodSelection(selectedItem);
                podSelector.valueProperty().addListener(observable -> setKubePodSelection());
                nextBtn.setDisable(false);
            } else {
                podSelectionLbl.setVisible(false);
                podSelector.setVisible(false);
                podErrorLbl.setText("There are no pods in this namespace");
            }
        } catch (KubeApiException e) {
            log.error("Could not get pod list", e);
            podErrorLbl.setText("Could not get pod list");
            ServiceLocator.getView().showErrorModal(e.getMessage());
        }
    }

    private void onNext() {
        final Stage selectionStage = (Stage) nextBtn.getScene().getWindow();
        ServiceLocator.getView().closeStage(selectionStage);
        ServiceLocator.getView().showMainWindow();
    }

    private void onPrev() {
        final Stage selectionStage = (Stage) prevBtn.getScene().getWindow();
        ServiceLocator.getView().closeStage(selectionStage);
        ServiceLocator.getView().showKubeNamespaceSelectionWindow();
    }

    private void setKubePodSelection() {
        final KubePod selection = podSelector.getValue();
        setKubePodSelection(selection);
    }

    private void setKubePodSelection(KubePod selection) {
        ServiceLocator.getModel().setKubePodSelection(selection);
    }
}
