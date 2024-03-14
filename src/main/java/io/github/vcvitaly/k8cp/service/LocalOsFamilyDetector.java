package io.github.vcvitaly.k8cp.service;

import io.github.vcvitaly.k8cp.enumeration.OsFamily;

public interface LocalOsFamilyDetector {

    OsFamily detectOsFamily();
}
