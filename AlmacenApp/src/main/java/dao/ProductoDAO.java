/*package dao;

import dto.Producto;
import javax.persistence.EntityManager;
import java.util.List;

public class ProductoDAO {
    public List<Producto> listarProductos() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            return em.createQuery("SELECT p FROM Producto p", Producto.class).getResultList();
        } catch (Exception e) {
            System.out.println("❌ Error de conexión o consulta: " + e.getMessage());
            return null;
        } finally {
            if (em != null) em.close();
        }
    }
}*/