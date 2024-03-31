module io.github.vcvitaly.k8cp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;

    requires static lombok;
    requires org.yaml.snakeyaml;
    requires kubernetes.client;
    requires kubernetes.client.api;
    /*requires kubernetes.model.admissionregistration;
    requires kubernetes.model.apiextensions;
    requires kubernetes.model.apps;
    requires kubernetes.model.autoscaling;
    requires kubernetes.model.batch;
    requires kubernetes.model.certificates;
    requires kubernetes.model.common;
    requires kubernetes.model.coordination;
    requires kubernetes.model.core;
    requires kubernetes.model.discovery;
    requires kubernetes.model.events;
    requires kubernetes.model.extensions;
    requires kubernetes.model.flowcontrol;
    requires kubernetes.model.gatewayapi;
    requires kubernetes.model.metrics;
    requires kubernetes.model.networking;
    requires kubernetes.model.node;
    requires kubernetes.model.policy;
    requires kubernetes.model.rbac;
    requires kubernetes.model.resource;
    requires kubernetes.model.scheduling;
    requires kubernetes.model.storageclass;*/
    requires org.apache.commons.io;
    requires com.fasterxml.jackson.annotation;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome;
    requires org.kordamp.ikonli.material2;

//    requires jdk.jdwp.agent;

    opens io.github.vcvitaly.k8cp to javafx.fxml;
    exports io.github.vcvitaly.k8cp;
    exports io.github.vcvitaly.k8cp.controller;
    exports io.github.vcvitaly.k8cp.controller.menu;
    exports io.github.vcvitaly.k8cp.controller.pane;
    exports io.github.vcvitaly.k8cp.controller.init;
    exports io.github.vcvitaly.k8cp.domain;
    exports io.github.vcvitaly.k8cp.enumeration;
    exports io.github.vcvitaly.k8cp.util;
    exports io.github.vcvitaly.k8cp.exception;
    exports io.github.vcvitaly.k8cp.context;
    opens io.github.vcvitaly.k8cp.controller to javafx.fxml;
    opens io.github.vcvitaly.k8cp.controller.menu to javafx.fxml;
    opens io.github.vcvitaly.k8cp.controller.init to javafx.fxml;
    opens io.github.vcvitaly.k8cp.controller.pane to javafx.fxml;
    opens io.github.vcvitaly.k8cp.util to javafx.fxml;
}