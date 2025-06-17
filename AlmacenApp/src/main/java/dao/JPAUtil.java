package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JPAUtil {
    private static final Logger logger = Logger.getLogger(JPAUtil.class.getName());
    private static volatile EntityManagerFactory emf;
    private static final Lock initLock = new ReentrantLock();
    
    // Configuración de reintentos
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_DELAY_MS = 1000;
    private static final long MAX_DELAY_MS = 10000;
    
    // Circuit Breaker
    private static final int FAILURE_THRESHOLD = 3;
    private static final long CIRCUIT_BREAKER_TIMEOUT_MS = 30000;
    private static final AtomicInteger failureCount = new AtomicInteger(0);
    private static volatile long lastFailureTime = 0;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (isCircuitOpen()) {
            throw new RuntimeException("Servicio no disponible temporalmente (Circuit Breaker activado)");
        }
        
        if (emf != null && emf.isOpen()) {
            return emf;
        }
        
        initLock.lock();
        try {
            if (emf != null && emf.isOpen()) {
                return emf;
            }
            
            int attempts = 0;
            long delay = INITIAL_DELAY_MS;
            
            while (attempts < MAX_RETRIES) {
                try {
                    emf = Persistence.createEntityManagerFactory("com.mycompany_AlmacenApp_war_1.0-SNAPSHOTPU");
                    failureCount.set(0);
                    return emf;
                } catch (Exception e) {
                    attempts++;
                    int currentFailures = failureCount.incrementAndGet();
                    logger.log(Level.WARNING, "Intento {0} fallido al crear EMF: {1}", 
                        new Object[]{attempts, e.getMessage()});
                    
                    if (currentFailures >= FAILURE_THRESHOLD) {
                        lastFailureTime = System.currentTimeMillis();
                        logger.log(Level.SEVERE, "Circuit Breaker activado por múltiples fallos");
                    }
                    
                    if (attempts < MAX_RETRIES) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(delay);
                            delay = Math.min(delay * 2, MAX_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Operación interrumpida", ie);
                        }
                    }
                }
            }
            throw new RuntimeException("No se pudo crear EntityManagerFactory después de " + MAX_RETRIES + " intentos");
        } finally {
            initLock.unlock();
        }
    }
    
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    private static boolean isCircuitOpen() {
        if (failureCount.get() < FAILURE_THRESHOLD) {
            return false;
        }
        
        long now = System.currentTimeMillis();
        if (now - lastFailureTime > CIRCUIT_BREAKER_TIMEOUT_MS) {
            failureCount.set(0);
            return false;
        }
        
        return true;
    }
    
    public static void close() {
        initLock.lock();
        try {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        } finally {
            initLock.unlock();
        }
    }
}