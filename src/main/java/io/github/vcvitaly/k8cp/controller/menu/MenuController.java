package io.github.vcvitaly.k8cp.controller.menu;

import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MenuController implements Initializable {
    public MenuItem aboutMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        aboutMenuItem.setOnAction(event -> onAbout());
    }

    private void onAbout() {
        View.getInstance().showAboutModal();
    }
}
