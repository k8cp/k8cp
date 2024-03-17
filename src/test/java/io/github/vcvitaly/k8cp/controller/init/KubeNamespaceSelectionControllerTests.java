package io.github.vcvitaly.k8cp.controller.init;

import io.github.vcvitaly.k8cp.context.ServiceLocator;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.view.View;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KubeNamespaceSelectionControllerTests {

    @Nested
    @ExtendWith(ApplicationExtension.class)
    class KubeNamespaceSelectionControllerSuccessTest {
        private final View viewMock = mock(View.class);

        private void mockViewGetStage() {
            when(viewMock.getCurrentStage()).thenReturn(mock(Stage.class));
        }

        @Start
        private void start(Stage stage) {
            mockViewGetStage();
            ServiceLocator.setView(viewMock);
            ServiceLocator.setModel(
                    Model.builder()
//                            .kubeServiceSupplier(new KubeServiceImpl(new KubeClientImpl()))
                            .build()
            );
            View.getInstance().showKubeNamespaceSelectionWindow();
        }

        @Test
        void namespaceChoiceBoxIsLoadedSuccessfully() {

        }
    }
}