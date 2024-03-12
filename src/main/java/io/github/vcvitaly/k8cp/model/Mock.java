package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.domain.BreadCrumbFile;
import io.github.vcvitaly.k8cp.domain.FileManagerItem;
import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.experimental.UtilityClass;
import org.controlsfx.control.BreadCrumbBar;

@UtilityClass
public class Mock {

    public static TreeItem<BreadCrumbFile> leftBreadcrumbItem() {
        BreadCrumbFile dto1 = new BreadCrumbFile("C:\\", "C");
        BreadCrumbFile dto2 = new BreadCrumbFile("C:\\Users\\", "Users");
        return BreadCrumbBar.buildTreeModel(dto1, dto2);
    }

    public static TreeItem<BreadCrumbFile> rightBreadcrumbItem() {
        BreadCrumbFile dto1 = new BreadCrumbFile("/home", "home");
        BreadCrumbFile dto2 = new BreadCrumbFile("/home/user", "user");
        return BreadCrumbBar.buildTreeModel(dto1, dto2);
    }

    public static ObservableList<FileManagerItem> leftViewItems() {
        final String parentDirName = "..";
        FileManagerItem parentDirDto = FileManagerItem.builder()
                .path("C:\\Users\\")
                .name(parentDirName)
                .size("")
                .sizeUnit("")
                .fileType(FileType.PARENT_DIRECTORY.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        final String fileName = "file.txt";
        FileManagerItem fileDto = FileManagerItem.builder()
                .path("C:\\Users\\" + fileName)
                .name(fileName)
                .size(String.valueOf(1))
                .sizeUnit(FileSizeUnit.KB.toString())
                .fileType(FileType.FILE.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        final String dirName = "some_dir";
        FileManagerItem dirDto = FileManagerItem.builder()
                .path("C:\\Users\\" + dirName)
                .name(dirName)
                .size(String.valueOf(4))
                .sizeUnit(FileSizeUnit.KB.toString())
                .fileType(FileType.DIRECTORY.toString())
                .changedAt("2024-03-10 08:34")
                .build();
        return FXCollections.observableArrayList(parentDirDto, dirDto, fileDto);
    }

    public static ObservableList<FileManagerItem> rightViewItems() {
        final String parentDirName = "..";
        FileManagerItem parentDirDto = FileManagerItem.builder()
                .path("/home")
                .name(parentDirName)
                .size("")
                .sizeUnit("")
                .fileType(FileType.PARENT_DIRECTORY.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        final String fileName = "file.txt";
        FileManagerItem fileDto = FileManagerItem.builder()
                .path("/home/user/" + fileName)
                .name(fileName)
                .size(String.valueOf(1))
                .sizeUnit(FileSizeUnit.KB.toString())
                .fileType(FileType.FILE.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        return FXCollections.observableArrayList(parentDirDto, fileDto);
    }
}
