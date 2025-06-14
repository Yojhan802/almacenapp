package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.concurrent.TimeUnit;

public class JPAUtil {
    private static EntityManagerFactory emf;
    private static final int MAX_RETRIES = 5;

    public static EntityManager getEntityManager() {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                if (emf == null || !emf.isOpen()) {
                    emf = Persistence.createEntityManagerFactory("com.mycompany_AlmacenApp_war_1.0-SNAPSHOTPU");
                }
                return emf.createEntityManager();
            } catch (Exception e) {
                attempts++;
                System.err.println("🔁 Reintento (" + attempts + "): " + e.getMessage());
                System.out.println(attempts);
                try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException ignored) {}
            }
        }
        throw new RuntimeException("No se pudo obtener EntityManager");
    }
}