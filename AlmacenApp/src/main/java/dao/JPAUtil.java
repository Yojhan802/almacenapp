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
    private static final Logger log = Logger.getLogger(JPAUtil.class.getName());
    private static volatile EntityManagerFactory emf;
    private static final Lock candado = new ReentrantLock();
    
    // Configuración de reintentos
    private static final int MAX_INTENTOS = 3;
    private static final long ESPERA_INICIAL = 1000;
    private static final long ESPERA_MAXIMA = 10000;
    
    // Circuit Breaker
    private static final int MAX_FALLOS = 3;
    private static final long TIEMPO_DESCANSO = 30000;
    private static final AtomicInteger contadorFallos = new AtomicInteger(0);
    private static volatile long ultimoFallo = 0;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (estaEnDescanso()) {
            throw new RuntimeException("Servicio echando la siesta (Circuit Breaker activado)");
        }
        
        if (emf != null && emf.isOpen()) {
            return emf;
        }
        
        candado.lock();
        try {
            if (emf != null && emf.isOpen()) {
                return emf;
            }
            
            int intentos = 0;
            long espera = ESPERA_INICIAL;
            
            while (intentos < MAX_INTENTOS) {
                try {
                    emf = Persistence.createEntityManagerFactory("com.mycompany_AlmacenApp_war_1.0-SNAPSHOTPU");
                    contadorFallos.set(0);
                    return emf;
                } catch (Exception e) {
                    intentos++;
                    int fallos = contadorFallos.incrementAndGet();
                    log.log(Level.WARNING, "Intento {0} fallido: {1}", 
                        new Object[]{intentos, e.getMessage()});
                    
                    if (fallos >= MAX_FALLOS) {
                        ultimoFallo = System.currentTimeMillis();
                        log.log(Level.SEVERE, "¡Se cansó! Circuit Breaker activado");
                    }
                    
                    if (intentos < MAX_INTENTOS) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(espera);
                            espera = Math.min(espera * 2, ESPERA_MAXIMA);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Lo cortaron", ie);
                        }
                    }
                }
            }
            throw new RuntimeException("No hubo caso después de " + MAX_INTENTOS + " intentos");
        } finally {
            candado.unlock();
        }
    }
    
    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    private static boolean estaEnDescanso() {
        if (contadorFallos.get() < MAX_FALLOS) {
            return false;
        }
        
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoFallo > TIEMPO_DESCANSO) {
            contadorFallos.set(0);
            return false;
        }
        
        return true;
    }
    
    public static void close() {
        candado.lock();
        try {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        } finally {
            candado.unlock();
        }
    }
}