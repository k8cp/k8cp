package io.github.vcvitaly.k8cp.enumeration;

import io.github.vcvitaly.k8cp.util.Constants;
import io.github.vcvitaly.k8cp.util.ReflectionUtil;
import lombok.Getter;

@Getter
public enum FileManagerColumn {
    NAME("Name", "name"),
    SIZE("Size", "size"),
    TYPE("Type", "fileType"),
    CHANGED("Changed", "changedAt");

    private final String colName;
    private final String fileManagerItemFieldName;

    FileManagerColumn(String colName, String fileManagerItemFieldName) {
        this.colName = colName;
        if (!ReflectionUtil.hasProperty(Constants.FILE_MANAGER_ITEM_CLAZZ, fileManagerItemFieldName)) {
            throw new IllegalArgumentException(
                    "%s has no such property: %s".formatted(Constants.FILE_MANAGER_ITEM_CLAZZ, fileManagerItemFieldName)
            );
        }
        this.fileManagerItemFieldName = fileManagerItemFieldName;
    }
}
