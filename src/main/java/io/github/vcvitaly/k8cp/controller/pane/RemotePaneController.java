package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileInfoContainer;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.domain.PathRefreshEvent;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.util.BoolStatusReturningConsumer;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.BreadCrumbBar;
import org.slf4j.Logger;

@Slf4j
public class RemotePaneController extends PaneController {
    public Button rightParentBtn;
    public Button rightRootBtn;
    public Button rightHomeBtn;
    public Button rightRefreshBtn;
    public Button rightCopyBtn;
    public Button rightMoveBtn;
    public Button rightDeleteBtn;
    public Button rightRenameBtn;
    public BreadCrumbBar<BreadCrumbFile> rightBreadcrumbBar;
    public TableView<FileManagerItem> rightView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            resolveFilesAndBreadcrumbs();
            initView();
        } catch (Exception e) {
            log.error("Could not init the remote view", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    @Override
    protected TableView<FileManagerItem> getView() {
        return rightView;
    }

    @Override
    protected BreadCrumbBar<BreadCrumbFile> getBreadcrumbBar() {
        return rightBreadcrumbBar;
    }

    @Override
    protected Button getParentBtn() {
        return rightParentBtn;
    }

    @Override
    protected Button getRootBtn() {
        return rightRootBtn;
    }

    @Override
    protected Button getHomeBtn() {
        return rightHomeBtn;
    }

    @Override
    protected Button getRefreshBtn() {
        return rightRefreshBtn;
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected BoolStatusReturningConsumer<PathRefreshEvent> getPathEventRefSettingConsumer() {
        return Model::setRemotePathEventRef;
    }

    @Override
    protected void initViewCrumb() {
        final List<BreadCrumbFile> remoteBreadcrumbTree = Model.getRemoteBreadcrumbTree();
        initViewCrumb(remoteBreadcrumbTree);
    }

    @Override
    protected void initViewItems() {
        final List<FileInfoContainer> remoteFiles = Model.getRemoteFiles();
        initViewItems(remoteFiles);
    }

    @Override
    protected void onParentBtn() {
        onNavigationBtn(() -> {
            if (Model.setRemotePathRefToParent()) {
                resolveFilesAndBreadcrumbs();
            }
        });
    }

    @Override
    protected void onHomeBtn() {
        onNavigationBtn(() -> {
            Model.setRemotePathRefToHome();
            resolveFilesAndBreadcrumbs();
        });
    }

    @Override
    protected void onRootBtn() {
        onNavigationBtn(() -> {
            Model.setRemotePathRefToRoot();
            resolveFilesAndBreadcrumbs();
        });
    }

    @Override
    protected void resolveFilesAndBreadcrumbs() throws IOOperationException {
        Model.resolveRemoteBreadcrumbTree();
        Model.resolveRemoteFiles();
    }

    @Override
    protected void onBreadcrumb(BoolStatusReturningConsumer<PathRefreshEvent> pathRefSettingConsumer, BreadCrumbFile selection) {
        onBreadcrumbInternal(pathRefSettingConsumer, selection, Model::resolveRemoteFiles);
    }
}
