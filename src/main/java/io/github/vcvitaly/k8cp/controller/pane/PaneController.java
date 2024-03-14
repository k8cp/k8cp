package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.enumeration.FileManagerColumn;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import java.util.List;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    protected void initView() throws IOOperationException {
        getView().setPlaceholder(getNoRowsToDisplayLbl());
        getView().getColumns().addAll(getTableColumns());
        initViewCrumb();
        initViewItems();
        initViewButtons();
        initViewMouseSelection();
        initViewEnterKeySelection();
        initBreadCrumbListener();
    }

    protected abstract void initViewCrumb();

    protected abstract void initViewItems();

    protected abstract void initViewButtons();

    protected abstract void initViewMouseSelection();

    protected abstract void initViewEnterKeySelection();

    protected abstract void initBreadCrumbListener();
}
