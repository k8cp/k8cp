package io.github.vcvitaly.k8cp.controller.init;

import io.github.vcvitaly.k8cp.TestUtil;
import io.github.vcvitaly.k8cp.client.impl.LocalFsClientImpl;
import io.github.vcvitaly.k8cp.context.ServiceLocator;
import io.github.vcvitaly.k8cp.domain.KubeConfigContainer;
import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.service.PathProvider;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigHelperImpl;
import io.github.vcvitaly.k8cp.service.impl.KubeConfigSelectionServiceImpl;
import io.github.vcvitaly.k8cp.view.View;
import java.nio.file.Path;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class KubeConfigSelectionControllerTest {

    @BeforeAll
    static void beforeAll() {
        final PathProvider pathProvider = mock(PathProvider.class);
        final Path homePath = TestUtil.getPath("/kubeconfig/ok");
        when(pathProvider.provideLocalHomePath()).thenReturn(homePath.toString());
        ServiceLocator.setModel(
                Model.builder()
                        .pathProvider(pathProvider)
                        .kubeConfigSelectionService(new KubeConfigSelectionServiceImpl(
                                new LocalFsClientImpl(), new KubeConfigHelperImpl()
                        ))
                        .build()
        );
    }

    @Start
    private void start(Stage stage) {
        View.getInstance().showKubeConfigSelectionWindow();
    }

    @Test
    void name(FxRobot robot) {
        final ChoiceBox<KubeConfigContainer> choiceBox = robot.lookup(".choice-box").queryAs(ChoiceBox.class);
        assertThat(choiceBox.getValue())
                .usingRecursiveComparison()
                .ignoringFields("path")
                .isEqualTo(new KubeConfigContainer("kind-kind", "kube_config.yml", null));

    }
}