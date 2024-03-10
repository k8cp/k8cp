package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.dto.BreadCrumbFileDto;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import org.controlsfx.control.BreadCrumbBar;

public class MainController implements Initializable {
    public Button parentLeftBtn;
    public Button homeLeftBtn;
    public Button refreshLeftBtn;
    public Button copyLeftBtn;
    public Button moveLeftBtn;
    public Button deleteLeftBtn;
    public Button renameLeftBtn;
    public BreadCrumbBar<BreadCrumbFileDto> leftBreadcrumbBar;
    public Button parentRightBtn;
    public Button homeRightBtn;
    public Button refreshRightBtn;
    public Button copyRightBtn;
    public Button moveRightBtn;
    public Button deleteRightBtn;
    public Button renameRightBtn;
    public BreadCrumbBar<BreadCrumbFileDto> rightBreadcrumbBar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mockLeftBreadcrumbBar();
        mockRightBreadcrumbBar();
    }

    private void mockLeftBreadcrumbBar() {
        BreadCrumbFileDto dto1 = new BreadCrumbFileDto("C:\\", "C");
        BreadCrumbFileDto dto2 = new BreadCrumbFileDto("C:\\Users\\", "Users");
        final TreeItem<BreadCrumbFileDto> treeItem = BreadCrumbBar.buildTreeModel(dto1, dto2);
        leftBreadcrumbBar.setSelectedCrumb(treeItem);
    }

    private void mockRightBreadcrumbBar() {
        BreadCrumbFileDto dto1 = new BreadCrumbFileDto("/home", "home");
        BreadCrumbFileDto dto2 = new BreadCrumbFileDto("/home/user", "user");
        final TreeItem<BreadCrumbFileDto> treeItem = BreadCrumbBar.buildTreeModel(dto1, dto2);
        rightBreadcrumbBar.setSelectedCrumb(treeItem);
    }
}