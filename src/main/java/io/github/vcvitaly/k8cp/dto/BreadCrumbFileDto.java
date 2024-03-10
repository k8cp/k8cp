package io.github.vcvitaly.k8cp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreadCrumbFileDto {
    private String path;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
