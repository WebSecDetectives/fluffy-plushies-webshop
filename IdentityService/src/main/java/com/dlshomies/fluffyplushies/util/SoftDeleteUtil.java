package com.dlshomies.fluffyplushies.util;

import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;

import java.util.function.Supplier;

public final class SoftDeleteUtil {
    private SoftDeleteUtil() { }

    public static <T> T executeWithoutSoftDeleteFilter(EntityManager entityManager, Supplier<T> supplier) {
        Session session = entityManager.unwrap(Session.class);
        // Check if the filter is enabled
        Filter filter = session.getEnabledFilter("softDeleteFilter");
        boolean wasEnabled = filter != null;
        if (wasEnabled) {
            session.disableFilter("softDeleteFilter");
        }
        try {
            return supplier.get();
        } finally {
            if (wasEnabled) {
                session.enableFilter("softDeleteFilter").setParameter("deleted", false);
            }
        }
    }

    public static void executeWithoutSoftDeleteFilter(EntityManager entityManager, Runnable runnable) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.getEnabledFilter("softDeleteFilter");
        boolean wasEnabled = filter != null;
        if (wasEnabled) {
            session.disableFilter("softDeleteFilter");
        }
        try {
            runnable.run();
        } finally {
            if (wasEnabled) {
                session.enableFilter("softDeleteFilter").setParameter("deleted", false);
            }
        }
    }
}