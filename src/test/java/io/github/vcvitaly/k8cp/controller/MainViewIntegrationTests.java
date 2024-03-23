package io.github.vcvitaly.k8cp.controller;

import io.github.vcvitaly.k8cp.model.Model;
import io.github.vcvitaly.k8cp.view.View;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.mockito.Mockito.mock;

class MainViewIntegrationTests {


    @Nested
    @ExtendWith(ApplicationExtension.class)
    class KubePodSelectionControllerSuccessTest extends TestFxTest {
        private final View viewMock = mock(View.class);
        private Model model;

        @Start
        private void start(Stage stage) throws Exception {
            /*ServiceLocator.setView(viewMock);
            final LocalOsFamilyDetectorImpl localOsFamilyDetector = new LocalOsFamilyDetectorImpl();
            final PathProviderImpl pathProvider = new PathProviderImpl(localOsFamilyDetector);
            final LocalFsClientImpl localFsClient = new LocalFsClientImpl();
            final SizeConverterImpl sizeConverter = new SizeConverterImpl();
            final LocalFsServiceImpl localFsService = new LocalFsServiceImpl(localFsClient, sizeConverter);
            model = spy(
                    Model.builder()
                            .pathProvider(pathProvider)
                            .localFsService(localFsService)
                            .localOsFamilyDetector(localOsFamilyDetector)
                            .build()
            );
            ServiceLocator.setModel(model);
            View.getInstance().showMainWindow();*/
        }
    }
}