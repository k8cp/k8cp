package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.dto.BreadCrumbFileDto;
import io.github.vcvitaly.k8cp.dto.FileItemDto;
import io.github.vcvitaly.k8cp.model.Mock;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
        leftView.setPlaceholder(getNoRowsToDisplayLbl());
        leftView.getColumns().addAll(getTableColumns());
        leftView.setItems(Mock.leftViewItems());
    }

    private void mockRightView() {
        rightView.setPlaceholder(getNoRowsToDisplayLbl());
        rightView.getColumns().addAll(getTableColumns());
        rightView.setItems(Mock.rightViewItems());
    }

    private Label getNoRowsToDisplayLbl() {
        return new Label("No rows to display");
    }

    private List<TableColumn<FileItemDto, String>> getTableColumns() {
        return List.of(
                getTableColumn("Name", "name"),
                getTableColumn("Size", "size"),
                getTableColumn("Type", "fileType"),
                getTableColumn("Changed", "changedAt")
        );
    }

    private TableColumn<FileItemDto, String> getTableColumn(String colName, String dtoFieldName) {
        TableColumn<FileItemDto, String> col = new TableColumn<>(colName);
        col.setCellValueFactory(new PropertyValueFactory<>(dtoFieldName));
        return col;
    }
}