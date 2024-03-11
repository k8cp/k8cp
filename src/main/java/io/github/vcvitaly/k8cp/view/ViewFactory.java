package io.github.vcvitaly.k8cp.view;

import io.github.vcvitaly.k8cp.enumeration.FxmlView;
import io.github.vcvitaly.k8cp.util.Constants;
import io.github.vcvitaly.k8cp.util.FxmlLoaderUtil;
import io.github.vcvitaly.k8cp.util.ResourceUtil;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class ViewFactory {

    private static final String MAIN_ICON_PATH = "/images/k8cp_icon.png";

    public void showMainWindow() {
        createStageAndShow(FxmlView.MAIN, Modality.NONE, Constants.TITLE);
    }

    public void showAboutModal() {
        createStageAndShow(FxmlView.ABOUT, Modality.APPLICATION_MODAL, Constants.TITLE);
    }

    private void createStageAndShow(FxmlView fxmlView, Modality modality, String title) {
        FXMLLoader loader = FxmlLoaderUtil.createFxmlLoader(fxmlView);
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(getMainIcon());
        stage.setTitle(title);
        stage.initModality(modality);
        stage.show();
        log.info("Shown " + fxmlView);
    }

    private Image getMainIcon() {
        return new Image(
                ResourceUtil.getResource(MAIN_ICON_PATH).toString()
        );
    }
}
