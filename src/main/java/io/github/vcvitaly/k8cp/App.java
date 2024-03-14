package io.github.vcvitaly.k8cp;

import io.github.vcvitaly.k8cp.view.View;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        View.getInstance().showKubeConfigSelectionWindow();
    }

    public static void main(String[] args) {
        launch();
    }
}