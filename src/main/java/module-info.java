module io.github.vcvitaly.k8cp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;

    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;

    requires static lombok;

    opens io.github.vcvitaly.k8cp to javafx.fxml;
    exports io.github.vcvitaly.k8cp;
    exports io.github.vcvitaly.k8cp.controller;
    exports io.github.vcvitaly.k8cp.controller.menu;
    opens io.github.vcvitaly.k8cp.controller to javafx.fxml;
    opens io.github.vcvitaly.k8cp.controller.menu to javafx.fxml;
}