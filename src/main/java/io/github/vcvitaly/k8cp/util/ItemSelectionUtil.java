package io.github.vcvitaly.k8cp.util;

import java.util.List;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemSelectionUtil {

    public static <T> T getSelectionItem(List<T> items, Predicate<T> predicate) {
        return items.stream()
                .filter(predicate)
                .findFirst()
                .orElseGet(
                        () -> items.stream().findFirst().orElseThrow(
                                () -> new IllegalArgumentException("Please check that the list is not empty first")
                        )
                );
    }
}
