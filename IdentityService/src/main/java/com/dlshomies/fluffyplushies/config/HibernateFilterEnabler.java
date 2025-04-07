package com.dlshomies.fluffyplushies.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.*;
import org.hibernate.Session;

import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HibernateFilterEnabler implements Filter {

    @PersistenceContext
    private EntityManager entityManager;

    // Sets up a global filter so that entities with deleted=true flag are ignored per default
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("softDeleteFilter").setParameter("deleted", false);
        chain.doFilter(request, response);
    }
}
