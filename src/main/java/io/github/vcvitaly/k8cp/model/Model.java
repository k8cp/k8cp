package io.github.vcvitaly.k8cp.model;

import io.github.vcvitaly.k8cp.view.ViewFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Model {

    @Getter
    @Setter
    private String podName;

    @Getter
    private final ViewFactory viewFactory;

    private Model() {
        viewFactory = new ViewFactory();
    }

    public static Model getInstance() {
        return ModelHolder.model;
    }

    private static class ModelHolder {
        private static final Model model = new Model();
    }
}
