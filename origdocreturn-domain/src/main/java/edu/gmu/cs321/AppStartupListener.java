package edu.gmu.cs321;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseInitializer.initialize();
            System.out.println("Application startup completed successfully.");
        } catch (RuntimeException e) {
            System.err.println("Application startup failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application shutting down.");
    }
}