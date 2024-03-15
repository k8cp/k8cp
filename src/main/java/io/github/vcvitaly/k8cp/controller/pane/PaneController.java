package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.enumeration.FileManagerColumn;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.util.ThrowingSupplier;
import io.github.vcvitaly.k8cp.view.View;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.BreadCrumbBar;
import org.slf4j.Logger;

public abstract class PaneController implements Initializable {
    private static final String NO_ROWS_TO_DISPLAY_MSG = "No rows to display";

    protected Label getNoRowsToDisplayLbl() {
        return new Label(NO_ROWS_TO_DISPLAY_MSG);
    }

    protected List<TableColumn<FileManagerItem, String>> getTableColumns() {
        return List.of(
                getTableColumn(FileManagerColumn.NAME),
                getTableColumn(FileManagerColumn.SIZE),
                getTableColumn(FileManagerColumn.TYPE),
                getTableColumn(FileManagerColumn.CHANGED)
        );
    }

    protected TableColumn<FileManagerItem, String> getTableColumn(FileManagerColumn column) {
        TableColumn<FileManagerItem, String> col = new TableColumn<>(column.getColName());
        col.setCellValueFactory(new PropertyValueFactory<>(column.getFileManagerItemFieldName()));
        return col;
    }

    protected abstract TableView<FileManagerItem> getView();

    protected abstract BreadCrumbBar<BreadCrumbFile> getBreadcrumbBar();

    protected abstract Button getParentBtn();

    protected abstract Button getRootBtn();

    protected abstract Button getHomeBtn();

    protected abstract Button getRefreshBtn();

    protected abstract Logger getLog();

    protected abstract Consumer<String> getPathRefSettingConsumer();

    protected void initView() {
        getView().setPlaceholder(getNoRowsToDisplayLbl());
        getView().getColumns().addAll(getTableColumns());
        initViewCrumb();
        initViewItems();
        initViewButtons();
        initViewMouseSelection(getPathRefSettingConsumer());
        initViewEnterKeySelection(getPathRefSettingConsumer());
        initBreadCrumbListener();
    }

    protected abstract void initViewCrumb();

    protected void initViewCrumb(List<BreadCrumbFile> breadCrumbFiles) {
        final TreeItem<BreadCrumbFile> treeItem = View.getInstance().toTreeItem(breadCrumbFiles);
        getBreadcrumbBar().setSelectedCrumb(treeItem);
    }

    protected abstract void initViewItems();

    protected void initViewItems(ThrowingSupplier<List<FileInfoContainer>> supplier, String viewType) {
        try {
            final List<FileManagerItem> fileMangerItems = View.getInstance().toFileMangerItems(supplier.get());
            getView().setItems(FXCollections.observableList(fileMangerItems));
        } catch (Exception e) {
            getLog().error("Could not list the %s files".formatted(viewType), e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    protected void initViewButtons() {
        getParentBtn().setOnAction(e -> onParentBtn());
        getRootBtn().setOnAction(e -> onRootBtn());
        getHomeBtn().setOnAction(e -> onHomeBtn());
        getRefreshBtn().setOnAction(e -> onRefreshBtn());
    }

    protected void onRefreshBtn() {
        initViewCrumb();
        initViewItems();
    }

    protected void handleViewSelectionAction(Consumer<String> pathRefSettingConsumer, FileManagerItem item) {
        final FileType fileType = FileType.ofValueName(item.getFileType());
        if (fileType == FileType.DIRECTORY || fileType == FileType.PARENT_DIRECTORY) {
            pathRefSettingConsumer.accept(item.getPath());
            initViewCrumb();
            initViewItems();
        } else if (fileType == FileType.FILE) {
            final String fileItemInfo = View.getInstance().toFileItemInfo(item);
            View.getInstance().showFileInfoModal(fileItemInfo);
        } else {
            throw new IllegalArgumentException("Unsupported file type " + fileType);
        }
    }

    protected void initViewEnterKeySelection(Consumer<String> pathRefSettingConsumer) {
        getView().setOnKeyPressed(e -> {
            if (!getView().getSelectionModel().isEmpty() && e.getCode() == KeyCode.ENTER) {
                handleViewSelectionAction(pathRefSettingConsumer, getView().getSelectionModel().getSelectedItem());
                e.consume();
            }
        });
    }

    protected void initViewMouseSelection(Consumer<String> pathRefSettingConsumer) {
        getView().setRowFactory(tv -> {
            final TableRow<FileManagerItem> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    handleViewSelectionAction(pathRefSettingConsumer, row.getItem());
                }
            });
            return row;
        });
    }

    protected void initBreadCrumbListener() {
        getBreadcrumbBar().selectedCrumbProperty()
                .addListener((observable, oldValue, newValue) -> onBreadcrumb(getPathRefSettingConsumer(), newValue.getValue()));
    }

    protected void onBreadcrumb(Consumer<String> pathRefSettingConsumer, BreadCrumbFile selection) {
        pathRefSettingConsumer.accept(selection.getPath());
        initViewItems();
    }

    protected abstract void onParentBtn();

    protected abstract void onHomeBtn();

    protected abstract void onRootBtn();
}
