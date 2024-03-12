package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.dto.KubeConfigSelectionDto;
import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.exception.KubeContextExtractionException;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.util.Constants;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeConfigSelectionController implements Initializable {
    public ChoiceBox<KubeConfigSelectionDto> kubeConfigSelector;
    public Button fsChooserBtn;
    public Label selectedKubeConfigFileLbl;
    public Button nextBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final ObservableList<KubeConfigSelectionDto> kubeConfigList;
        fsChooserBtn.setOnAction(e -> {
            final File file = getFileFromFileChooser();
            if (file != null) {
                try {
                    final KubeConfigSelectionDto selection = Model.getInstance().getKubeConfigSelectionDto(file.toPath());
                    Model.getInstance().setKubeConfigSelection(selection);
                    nextBtn.setDisable(false);
                } catch (KubeContextExtractionException ex) {
                    Model.getInstance().getViewFactory().showErrorModal(ex.getMessage());
                }
            }
        });
        try {
            kubeConfigList = Model.getInstance().getKubeConfigList();
            if (!kubeConfigList.isEmpty()) {
                setItemsIfKubeConfigsFound(kubeConfigList);
            } else {
                setItemsIfNoKubeConfigFound();
            }
        } catch (FileSystemException | KubeContextExtractionException e) {
            log.error("Could not get kube config list", e);
            setItemsIfNoKubeConfigFound();
            Model.getInstance().getViewFactory().showErrorModal(e.getMessage());
        }
    }

    private void setItemsIfKubeConfigsFound(ObservableList<KubeConfigSelectionDto> kubeConfigList) {
        kubeConfigSelector.setItems(kubeConfigList);
        kubeConfigSelector.setValue(
                kubeConfigList.stream()
                        .filter(selection -> selection.fileName().equals(Constants.DEFAULT_CONFIG_FILE_NAME))
                        .findFirst()
                        .orElseGet(() -> kubeConfigList.stream().findFirst().get())
        );
        kubeConfigSelector.valueProperty().addListener(observable -> setKubeConfigSelection());
        nextBtn.setDisable(false);
    }

    private void setItemsIfNoKubeConfigFound() {
        kubeConfigSelector.setItems(FXCollections.emptyObservableList());
    }

    private void setKubeConfigSelection() {
        final KubeConfigSelectionDto selection = kubeConfigSelector.getValue();
        Model.getInstance().setKubeConfigSelection(selection);
    }

    private File getFileFromFileChooser() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        return fileChooser.showOpenDialog(Model.getInstance().getViewFactory().getCurrentStage());
    }
}
