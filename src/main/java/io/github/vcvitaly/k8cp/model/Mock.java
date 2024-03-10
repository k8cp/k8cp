package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.dto.BreadCrumbFileDto;
import io.github.vcvitaly.k8cp.dto.FileItemDto;
import io.github.vcvitaly.k8cp.enumeration.FileSizeUnit;
import io.github.vcvitaly.k8cp.enumeration.FileType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import lombok.experimental.UtilityClass;
import org.controlsfx.control.BreadCrumbBar;

@UtilityClass
public class Mock {

    public static TreeItem<BreadCrumbFileDto> leftBreadcrumbItem() {
        BreadCrumbFileDto dto1 = new BreadCrumbFileDto("C:\\", "C");
        BreadCrumbFileDto dto2 = new BreadCrumbFileDto("C:\\Users\\", "Users");
        return BreadCrumbBar.buildTreeModel(dto1, dto2);
    }

    public static TreeItem<BreadCrumbFileDto> rightBreadcrumbItem() {
        BreadCrumbFileDto dto1 = new BreadCrumbFileDto("/home", "home");
        BreadCrumbFileDto dto2 = new BreadCrumbFileDto("/home/user", "user");
        return BreadCrumbBar.buildTreeModel(dto1, dto2);
    }

    public static ObservableList<FileItemDto> leftViewItems() {
        final String parentDirName = "C";
        FileItemDto parentDirDto = FileItemDto.builder()
                .path("C:\\Users\\" + parentDirName)
                .name(parentDirName)
                .size("")
                .sizeUnit("")
                .fileType(FileType.PARENT_DIRECTORY.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        final String fileName = "file.txt";
        FileItemDto fileDto = FileItemDto.builder()
                .path("C:\\Users\\" + fileName)
                .name(fileName)
                .size(String.valueOf(1))
                .sizeUnit(FileSizeUnit.KB.toString())
                .fileType(FileType.FILE.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        final String dirName = "some_dir";
        FileItemDto dirDto = FileItemDto.builder()
                .path("C:\\Users\\" + fileName)
                .name(fileName)
                .size(String.valueOf(4))
                .sizeUnit(FileSizeUnit.KB.toString())
                .fileType(FileType.DIRECTORY.toString())
                .changedAt("2024-03-10 08:34")
                .build();
        return FXCollections.observableArrayList(parentDirDto, dirDto, fileDto);
    }

    public static ObservableList<FileItemDto> rightViewItems() {
        final String parentDirName = "home";
        FileItemDto parentDirDto = FileItemDto.builder()
                .path("/home" + parentDirName)
                .name(parentDirName)
                .size("")
                .sizeUnit("")
                .fileType(FileType.PARENT_DIRECTORY.toString())
                .changedAt("2024-03-10 08:35")
                .build();
        final String fileName = "file.txt";
        FileItemDto fileDto = FileItemDto.builder()
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
