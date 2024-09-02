package io.github.vcvitaly.k8cp.domain;

public record SizeHolder(Long sizeBytes, String sizeRepresentation) implements Comparable<SizeHolder> {

    @Override
    public String toString() {
        return sizeRepresentation;
    }


    @Override
    public int compareTo(SizeHolder o) {
        return Long.compare(sizeBytes, o.sizeBytes);
    }
}
