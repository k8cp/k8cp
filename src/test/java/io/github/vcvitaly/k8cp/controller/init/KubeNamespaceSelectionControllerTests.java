package io.github.vcvitaly.k8cp.controller.init;

public class KubeNamespaceSelectionControllerTests {

    /*@Nested
    @ExtendWith(ApplicationExtension.class)
    class KubeNamespaceSelectionControllerSuccessTest {
        private final View viewMock = mock(View.class);

        private final K3sContainer k3s = new K3sContainer(DockerImageName.parse("rancher/k3s:v1.26.14-k3s1"));

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
    }*/
}