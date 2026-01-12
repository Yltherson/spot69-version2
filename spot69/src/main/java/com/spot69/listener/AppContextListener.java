package com.spot69.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.spot69.dao.CreditDAO;

public class AppContextListener implements ServletContextListener {

    // Cette méthode est appelée au démarrage de l'application
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Application démarrée.");
        // Relancer les timers d'annulation des crédits non validés
        CreditDAO creditDAO = new CreditDAO();
        creditDAO.rechargerTasksAnnulation(); 
    }

    // Cette méthode est appelée à l'arrêt ou au redeploy de l'application
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application arrêtée. Fermeture des tasks programmés.");
        if (CreditDAO.scheduler != null && !CreditDAO.scheduler.isShutdown()) {
            CreditDAO.scheduler.shutdownNow(); // stoppe tous les tasks actifs
        }
    }
}
