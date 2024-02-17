module io.github.vcvitaly.k8cp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens io.github.vcvitaly.k8cp to javafx.fxml;
    exports io.github.vcvitaly.k8cp;
}