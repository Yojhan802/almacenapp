package servlets;

import dto.Producto;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.google.gson.Gson;

@WebServlet("/buscar-productos")
public class BuscarProductoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar EntityManager
        EntityManager em = Persistence.createEntityManagerFactory("com.mycompany_AlmacenApp_war_1.0-SNAPSHOTPU").createEntityManager();

        // Consulta a la base de datos
        TypedQuery<Producto> query = em.createNamedQuery("Producto.findAll", Producto.class);
        List<Producto> listaProductos = query.getResultList();

        // Convertir la lista a JSON
        String json = new Gson().toJson(listaProductos);

        // Configurar respuesta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
