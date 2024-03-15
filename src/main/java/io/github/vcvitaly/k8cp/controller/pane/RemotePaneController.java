package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.exception.IOOperationException;
import io.github.vcvitaly.k8cp.model.Mock;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
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
    protected Consumer<String> getPathRefSettingConsumer() {
        return Model::setRemotePathRef;
    }

    @Override
    protected void initViewCrumb() {
        initViewCrumb(Model.resolveRemoteBreadcrumbTree());
    }

    @Override
    protected void initViewItems() {
        initViewItems(Model::listRemoteFiles, "remote");
    }

    @Override
    protected void onParentBtn() {
        Model.setRemotePathRefToParent();
        onRefreshBtn();
    }

    @Override
    protected void onHomeBtn() {
        try {
            Model.setRemotePathRefToHome();
            onRefreshBtn();
        } catch (IOOperationException e) {
            log.error("Could not set the remote path to home", e);
            View.getInstance().showErrorModal(e.getMessage());
        }
    }

    @Override
    protected void onRootBtn() {
        Model.setRemotePathRefToRoot();
        onRefreshBtn();
    }

    private void mockRightView() {
        mockRightBreadcrumbBar();
        rightView.setPlaceholder(getNoRowsToDisplayLbl());
        rightView.getColumns().addAll(getTableColumns());
        rightView.setItems(Mock.rightViewItems());
    }

    private void mockRightBreadcrumbBar() {
        rightBreadcrumbBar.setSelectedCrumb(Mock.rightBreadcrumbItem());
    }
}
