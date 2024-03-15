package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.domain.RootInfoContainer;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.util.LocalFileUtil;
import io.github.vcvitaly.k8cp.util.ItemSelectionUtil;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.BreadCrumbBar;
import org.slf4j.Logger;

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
            log.error("Could not init the local view", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    @Override
    protected TableView<FileManagerItem> getView() {
        return leftView;
    }

    @Override
    protected BreadCrumbBar<BreadCrumbFile> getBreadcrumbBar() {
        return leftBreadcrumbBar;
    }

    @Override
    protected Button getParentBtn() {
        return leftParentBtn;
    }

    @Override
    protected Button getRootBtn() {
        return leftRootBtn;
    }

    @Override
    protected Button getHomeBtn() {
        return leftHomeBtn;
    }

    @Override
    protected Button getRefreshBtn() {
        return leftRefreshBtn;
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected Consumer<String> getPathRefSettingConsumer() {
        return Model::setLocalPathRef;
    }

    @Override
    protected void initViewCrumb() {
        initViewCrumb(Model.resolveLocalBreadcrumbTree());
    }

    @Override
    protected void initViewItems() {
        initViewItems(Model::listLocalFiles, "local");
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

    @Override
    protected void onParentBtn() {
        Model.setLocalPathRefToParent();
        localRootSelector.setValue(Model.getMainRoot());
        onRefreshBtn();
    }

    @Override
    protected void onHomeBtn() {
        Model.setLocalPathRefToHome();
        localRootSelector.setValue(Model.getMainRoot());
        onRefreshBtn();
    }

    @Override
    protected void onRootBtn() {
        Model.setLocalPathRefToRoot();
        localRootSelector.setValue(Model.getMainRoot());
        onRefreshBtn();
    }

    private void onLocalRootSelection() {
        final RootInfoContainer root = localRootSelector.getValue();
        final String rootPath = root.path();
        if (!LocalFileUtil.isInTheSameRoot(rootPath, Model.getLocalPath())) {
            Model.setLocalPathRef(rootPath);
            initViewCrumb();
            initViewItems();
        }
    }
}
