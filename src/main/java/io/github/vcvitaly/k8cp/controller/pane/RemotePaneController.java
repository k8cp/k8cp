package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.model.Mock;
import io.github.vcvitaly.k8cp.view.View;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.BreadCrumbBar;

@Slf4j
public class RemotePaneController extends PaneController {
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
    protected void initViewCrumb() {

    }

    @Override
    protected void initViewItems() {

    }

    @Override
    protected void initViewButtons() {

    }

    @Override
    protected void initViewMouseSelection() {

    }

    @Override
    protected void initViewEnterKeySelection() {

    }

    @Override
    protected void initBreadCrumbListener() {

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
