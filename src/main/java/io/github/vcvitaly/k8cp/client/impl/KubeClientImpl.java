package io.github.vcvitaly.k8cp.client.impl;

import io.github.vcvitaly.k8cp.client.KubeClient;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.emptyList;

@Slf4j
public class KubeClientImpl implements KubeClient {

    private static final int WAIT_TIMEOUT_MS = 250;
    private final Exec exec;
    private final CoreV1Api api;

    public KubeClientImpl(String kubeConfigPath) {
        ApiClient client;
        try (final FileReader fr = new FileReader(kubeConfigPath)) {
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(fr)).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Configuration.setDefaultApiClient(client);
        exec = new Exec(client);
        api = new CoreV1Api(client);
    }

    @Override
    public List<String> execAndReturnOut(String namespace, String podName, String[] cmdParts) {
        try {
            return executeAndReturnOutInternal(namespace, podName, cmdParts);
        } catch (IOException | ApiException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> executeAndReturnOutInternal(String namespace, String podName, String[] cmdParts) throws IOException, ApiException, InterruptedException {
        boolean tty = System.console() != null;
        final Process proc =
                exec.exec(namespace, podName, cmdParts, true, tty);
        final var ref = new Object() {
            List<String> outLines = new ArrayList<>();
            List<String> errLines = new ArrayList<>();
        };

        final Thread out = Thread.ofVirtual().start(() -> {
            ref.outLines.addAll(readStream(proc.getInputStream(), "output"));
        });

        final Thread err = Thread.ofVirtual().start(() -> {
            ref.errLines.addAll(readStream(proc.getErrorStream(), "error"));
        });

        proc.waitFor();

        out.join();
        err.join();

        proc.destroy();

        final int exitValue = proc.exitValue();
        if (exitValue != 0) {
            log.error("Exit code: " + exitValue);
        }

        return ref.outLines;
    }

    private List<String> readStream(InputStream is, String streamType) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return readStream(br);
        } catch (IOException | InterruptedException ex) {
            log.error("An error while reading %s stream: ".formatted(streamType), ex);
        }
        return emptyList();
    }

    private List<String> readStream(BufferedReader br) throws IOException, InterruptedException {
        List<String> out = new ArrayList<>();

        int i = 0;

        while (true) {
            if (br.ready()) {
                String line = br.readLine();
                out.add(line);
            } else {
                Thread.sleep(1);
                i++;
                if (i >= WAIT_TIMEOUT_MS) {
                    break;
                }
            }
        }

        return out;
    }
}
