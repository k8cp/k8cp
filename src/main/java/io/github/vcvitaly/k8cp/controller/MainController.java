package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.FileManagerColumn;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.model.Mock;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.util.FileUtil;
import io.github.vcvitaly.k8cp.util.ItemSelectionUtil;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.BreadCrumbBar;

@Slf4j
public class MainController implements Initializable {
    public Button leftParentBtn;
    public Button leftRootBtn;
    public Button leftHomeBtn;
    public Button leftRefreshBtn;
    public Button leftCopyBtn;
    public Button leftMoveBtn;
    public Button leftDeleteBtn;
    public Button leftRenameBtn;
    public ChoiceBox<RootInfoContainer> localRootSelector;
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
        } catch (Exception e) {
            log.error("Could not init left view", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
        mockRightView();
    }

    private void mockRightBreadcrumbBar() {
        rightBreadcrumbBar.setSelectedCrumb(Mock.rightBreadcrumbItem());
    }

    private void initLeftView() throws IOOperationException {
        leftView.setPlaceholder(getNoRowsToDisplayLbl());
        leftView.getColumns().addAll(getTableColumns());
        initLeftViewCrumb();
        initLeftViewItems();
        initLeftViewButtons();
        initLocalRootSelector();
        initLocalViewSelectionAction();
        initLocalViewKeyPressed();
        leftBreadcrumbBar.selectedCrumbProperty()
                .addListener((observable, oldValue, newValue) -> onLeftBreadcrumb(newValue.getValue()));
    }
    private void initLeftViewCrumb() {
        final TreeItem<BreadCrumbFile> treeItem = View.getInstance().toTreeItem(Model.resolveLocalBreadcrumbTree());
        leftBreadcrumbBar.setSelectedCrumb(treeItem);
    }

    private void initLeftViewItems() {
        try {
            final List<FileManagerItem> fileMangerItems = View.getInstance().toFileMangerItems(Model.listLocalFiles());
            leftView.setItems(FXCollections.observableList(fileMangerItems));
        } catch (IOOperationException e) {
            log.error("Could list local files", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    private void initLeftViewButtons() {
        leftParentBtn.setOnAction(e -> onLeftParentBtn());
        leftHomeBtn.setOnAction(e -> onLeftHomeBtn());
        leftRootBtn.setOnAction(e -> onLeftRootBtn());
        leftRefreshBtn.setOnAction(e -> onLeftRefreshBtn());
    }

    private void initLocalRootSelector() throws IOOperationException {
        final List<RootInfoContainer> roots = Model.listLocalRoots();
        localRootSelector.setItems(FXCollections.observableList(roots));
        localRootSelector.setValue(ItemSelectionUtil.getSelectionItem(roots, rootInfoContainer -> true));
        localRootSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                onLocalRootSelection();
            }
        });
    }

    private void onLeftParentBtn() {
        Model.setLocalPathRefToParent();
        localRootSelector.setValue(Model.getMainRoot());
        initLeftViewCrumb();
        initLeftViewItems();
    }

    private void onLeftHomeBtn() {
        Model.setLocalPathRefToHome();
        localRootSelector.setValue(Model.getMainRoot());
        initLeftViewCrumb();
        initLeftViewItems();
    }

    private void onLeftRootBtn() {
        Model.setLocalPathRefToRoot();
        localRootSelector.setValue(Model.getMainRoot());
        initLeftViewCrumb();
        initLeftViewItems();
    }

    private void onLeftRefreshBtn() {
        initLeftViewCrumb();
        initLeftViewItems();
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

    private void onLeftBreadcrumb(BreadCrumbFile selection) {
        Model.setLocalPathRef(selection.getPath());
        initLeftViewItems();
    }

    private void onLocalRootSelection() {
        final RootInfoContainer root = localRootSelector.getValue();
        final String rootPath = root.path();
        if (!FileUtil.isInTheSameRoot(rootPath, Model.getLocalPathRef())) {
            Model.setLocalPathRef(rootPath);
            initLeftViewCrumb();
            initLeftViewItems();
        }
    }

    private void initLocalViewSelectionAction() {
        leftView.setRowFactory(tv -> {
            final TableRow<FileManagerItem> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    handleLeftViewSelectionAction(row.getItem());
                }
            });
            return row;
        });
    }

    private void handleLeftViewSelectionAction(FileManagerItem item) {
        final FileType fileType = FileType.ofValueName(item.getFileType());
        if (fileType == FileType.DIRECTORY || fileType == FileType.PARENT_DIRECTORY) {
            Model.setLocalPathRef(item.getPath());
            initLeftViewCrumb();
            initLeftViewItems();
        } else if (fileType == FileType.FILE) {
            final String fileItemInfo = View.getInstance().toFileItemInfo(item);
            View.getInstance().showFileInfoModal(fileItemInfo);
        } else {
            throw new IllegalArgumentException("Unsupported file type " + fileType);
        }
    }

    private void initLocalViewKeyPressed() {
        leftView.setOnKeyPressed(e -> {
            if (!leftView.getSelectionModel().isEmpty() && e.getCode() == KeyCode.ENTER) {
                handleLeftViewSelectionAction(leftView.getSelectionModel().getSelectedItem());
                e.consume();
            }
        });
    }
}