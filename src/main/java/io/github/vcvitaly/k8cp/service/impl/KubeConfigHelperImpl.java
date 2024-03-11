package io.github.vcvitaly.k8cp.service.impl;

import io.github.vcvitaly.k8cp.exception.FileSystemException;
import io.github.vcvitaly.k8cp.exception.KubeConfigLoadingException;
import io.github.vcvitaly.k8cp.service.KubeConfigHelper;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class KubeConfigHelperImpl implements KubeConfigHelper {

    private static final String ERROR_MSG = "An error while loading a kube config from path ";
    private static final String CLUSTERS_KEY = "clusters";
    private static final String CONTEXTS_KEY = "contexts";

    @Override
    public boolean validate(String path) throws FileSystemException {
        try {
            final Map<String, Object> configMap = getConfigMap(path);
            return configMap.containsKey(CLUSTERS_KEY) && configMap.containsKey(CONTEXTS_KEY);
        } catch (KubeConfigLoadingException e) {
            return false;
        }
    }

    @Override
    public String extractContextName(String path) throws FileSystemException, KubeConfigLoadingException {
        final Object contexts = getConfigMap(path).get(CONTEXTS_KEY);
        return contexts.toString();
    }

    private Map<String, Object> getConfigMap(String path) throws KubeConfigLoadingException, FileSystemException {
        try (final FileReader fr = new FileReader(path)) {
            Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
            return yaml.load(fr);
        } catch (IOException e) {
            throw new FileSystemException(ERROR_MSG + path, e);
        } catch (Exception e) {
            throw new KubeConfigLoadingException(ERROR_MSG + path, e);
        }
    }
}
