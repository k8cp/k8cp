package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.util.FileUtil;
import io.github.vcvitaly.k8cp.util.ItemSelectionUtil;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.BreadCrumbBar;

@Slf4j
public class LocalPaneController extends PaneController {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initView();
            initLocalRootSelector();
        } catch (Exception e) {
            log.error("Could not init left view", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    @Override
    protected TableView<FileManagerItem> getView() {
        return leftView;
    }

    @Override
    protected void initViewCrumb() {
        final TreeItem<BreadCrumbFile> treeItem = View.getInstance().toTreeItem(Model.resolveLocalBreadcrumbTree());
        leftBreadcrumbBar.setSelectedCrumb(treeItem);
    }

    @Override
    protected void initViewItems() {
        try {
            final List<FileManagerItem> fileMangerItems = View.getInstance().toFileMangerItems(Model.listLocalFiles());
            leftView.setItems(FXCollections.observableList(fileMangerItems));
        } catch (IOOperationException e) {
            log.error("Could list local files", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    @Override
    protected void initViewButtons() {
        leftParentBtn.setOnAction(e -> onLeftParentBtn());
        leftHomeBtn.setOnAction(e -> onLeftHomeBtn());
        leftRootBtn.setOnAction(e -> onLeftRootBtn());
        leftRefreshBtn.setOnAction(e -> onLeftRefreshBtn());
    }

    @Override
    protected void initViewMouseSelection() {
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
            initViewCrumb();
            initViewItems();
        } else if (fileType == FileType.FILE) {
            final String fileItemInfo = View.getInstance().toFileItemInfo(item);
            View.getInstance().showFileInfoModal(fileItemInfo);
        } else {
            throw new IllegalArgumentException("Unsupported file type " + fileType);
        }
    }

    @Override
    protected void initViewEnterKeySelection() {
        leftView.setOnKeyPressed(e -> {
            if (!leftView.getSelectionModel().isEmpty() && e.getCode() == KeyCode.ENTER) {
                handleLeftViewSelectionAction(leftView.getSelectionModel().getSelectedItem());
                e.consume();
            }
        });
    }

    @Override
    protected void initBreadCrumbListener() {
        leftBreadcrumbBar.selectedCrumbProperty()
                .addListener((observable, oldValue, newValue) -> onLeftBreadcrumb(newValue.getValue()));
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
        initViewCrumb();
        initViewItems();
    }

    private void onLeftHomeBtn() {
        Model.setLocalPathRefToHome();
        localRootSelector.setValue(Model.getMainRoot());
        initViewCrumb();
        initViewItems();
    }

    private void onLeftRootBtn() {
        Model.setLocalPathRefToRoot();
        localRootSelector.setValue(Model.getMainRoot());
        initViewCrumb();
        initViewItems();
    }

    private void onLeftRefreshBtn() {
        initViewCrumb();
        initViewItems();
    }

    private void onLeftBreadcrumb(BreadCrumbFile selection) {
        Model.setLocalPathRef(selection.getPath());
        initViewItems();
    }

    private void onLocalRootSelection() {
        final RootInfoContainer root = localRootSelector.getValue();
        final String rootPath = root.path();
        if (!FileUtil.isInTheSameRoot(rootPath, Model.getLocalPathRef())) {
            Model.setLocalPathRef(rootPath);
            initViewCrumb();
            initViewItems();
        }
    }
}
