package io.github.vcvitaly.k8cp.controller.pane;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.model.Mock;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import org.controlsfx.control.BreadCrumbBar;

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
        mockRightView();
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
