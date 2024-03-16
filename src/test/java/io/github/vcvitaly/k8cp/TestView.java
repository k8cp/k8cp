package io.github.vcvitaly.k8cp;

import io.github.vcvitaly.k8cp.enumeration.FxmlView;
import io.github.vcvitaly.k8cp.util.Constants;
import io.github.vcvitaly.k8cp.util.FxmlLoaderUtil;
import io.github.vcvitaly.k8cp.view.StageCreationParam;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestView {

    private static void loadAndShow(Stage stage, StageCreationParam param) {
        final FxmlView fxmlView = param.getFxmlView();
        final FXMLLoader loader = FxmlLoaderUtil.createFxmlLoader(fxmlView);
        final Initializable controller = param.getController();
        if (controller != null) {
            loader.setController(controller);
        }
        final Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
        stage.setTitle(param.getTitle() != null ? param.getTitle() : Constants.TITLE);
        if (param.getModality() != null) {
            stage.initModality(param.getModality());
        }
        final Boolean resizeable = param.getResizeable();
        if (resizeable != null) {
            stage.setResizable(resizeable);
        }
        stage.show();
//        setCurrentStage(stage);
    }
}
