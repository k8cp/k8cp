package io.github.vcvitaly.k8cp.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtil {

    public static boolean hasProperty(Class<?> clazz, String propertyName) {
        final Set<String> fields = Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
        return fields.contains(propertyName);
    }
}
