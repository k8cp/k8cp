package io.github.vcvitaly.k8cp.util;

import io.github.vcvitaly.k8cp.enumeration.FxmlView;
import javafx.fxml.FXMLLoader;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FxmlLoaderUtil {

    public static FXMLLoader createFxmlLoader(FxmlView fxmlView) {
        return new FXMLLoader(ResourceUtil.getResource(fxmlView.getResourcePath()));
    }
}
