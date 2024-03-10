package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.dto.BreadCrumbFileDto;
import io.github.vcvitaly.k8cp.dto.FileItemDto;
import io.github.vcvitaly.k8cp.model.Mock;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
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
    public TableView<FileItemDto> leftView;
    public Button parentRightBtn;
    public Button homeRightBtn;
    public Button refreshRightBtn;
    public Button copyRightBtn;
    public Button moveRightBtn;
    public Button deleteRightBtn;
    public Button renameRightBtn;
    public BreadCrumbBar<BreadCrumbFileDto> rightBreadcrumbBar;
    public TableView<FileItemDto> rightView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mockLeftBreadcrumbBar();
        mockRightBreadcrumbBar();
        mockLeftView();
        mockRightView();
    }

    private void mockLeftBreadcrumbBar() {
        leftBreadcrumbBar.setSelectedCrumb(Mock.leftBreadcrumbItem());
    }

    private void mockRightBreadcrumbBar() {
        rightBreadcrumbBar.setSelectedCrumb(Mock.rightBreadcrumbItem());
    }

    private void mockLeftView() {
        leftView.setItems(Mock.leftViewItems());
    }
    private void mockRightView() {
        rightView.setItems(Mock.rightViewItems());
    }
}