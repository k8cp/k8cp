package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.enumeration.FileManagerColumn;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.model.Mock;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.BreadCrumbBar;

@Slf4j
public class MainController implements Initializable {
    public Button parentLeftBtn;
    public Button rootLeftBtn;
    public Button homeLeftBtn;
    public Button refreshLeftBtn;
    public Button copyLeftBtn;
    public Button moveLeftBtn;
    public Button deleteLeftBtn;
    public Button renameLeftBtn;
    public BreadCrumbBar<BreadCrumbFile> leftBreadcrumbBar;
    public TableView<FileManagerItem> leftView;
    public Button parentRightBtn;
    public Button rootRightBtn;
    public Button homeRightBtn;
    public Button refreshRightBtn;
    public Button copyRightBtn;
    public Button moveRightBtn;
    public Button deleteRightBtn;
    public Button renameRightBtn;
    public BreadCrumbBar<BreadCrumbFile> rightBreadcrumbBar;
    public TableView<FileManagerItem> rightView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initLeftView();
        } catch (IOOperationException e) {
            log.error("Could list local files", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
        mockRightView();
    }

    private void mockRightBreadcrumbBar() {
        rightBreadcrumbBar.setSelectedCrumb(Mock.rightBreadcrumbItem());
    }

    private void initLeftView() throws IOOperationException {
        final TreeItem<BreadCrumbFile> treeItem = View.getInstance().toTreeItem(Model.resolveLocalBreadcrumbTree());
        leftBreadcrumbBar.setSelectedCrumb(treeItem);
        leftView.setPlaceholder(getNoRowsToDisplayLbl());
        leftView.getColumns().addAll(getTableColumns());
        final List<FileManagerItem> fileMangerItems = View.getInstance().toFileMangerItems(Model.listLocalFiles());
        leftView.setItems(FXCollections.observableList(fileMangerItems));
    }

    private void mockRightView() {
        mockRightBreadcrumbBar();
        rightView.setPlaceholder(getNoRowsToDisplayLbl());
        rightView.getColumns().addAll(getTableColumns());
        rightView.setItems(Mock.rightViewItems());
    }

    private Label getNoRowsToDisplayLbl() {
        return new Label("No rows to display");
    }

    private List<TableColumn<FileManagerItem, String>> getTableColumns() {
        return List.of(
                getTableColumn(FileManagerColumn.NAME),
                getTableColumn(FileManagerColumn.SIZE),
                getTableColumn(FileManagerColumn.TYPE),
                getTableColumn(FileManagerColumn.CHANGED)
        );
    }

    private TableColumn<FileManagerItem, String> getTableColumn(FileManagerColumn column) {
        TableColumn<FileManagerItem, String> col = new TableColumn<>(column.getColName());
        col.setCellValueFactory(new PropertyValueFactory<>(column.getFileManagerItemFieldName()));
        return col;
    }
}