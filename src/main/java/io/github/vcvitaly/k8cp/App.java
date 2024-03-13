package io.github.vcvitaly.k8cp;

import io.github.vcvitaly.k8cp.model.Model;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Model.getViewFactory().showKubeConfigSelectionWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}