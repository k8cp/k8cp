package io.github.vcvitaly.k8cp;

import io.github.vcvitaly.k8cp.util.ResourceUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/mainView.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 640, 480);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.getIcons().add(getMainIcon());
        stage.show();
    }

    private Image getMainIcon() {
        return new Image(
                ResourceUtil.getResource("/images/k8cp_icon.png").toString()
        );
    }

    public static void main(String[] args) {
        launch();
    }
}