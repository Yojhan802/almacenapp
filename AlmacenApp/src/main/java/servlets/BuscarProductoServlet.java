package servlets;

import com.google.gson.Gson;
import dao.ProductoJpaController;
import dto.Producto;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns = "/buscar-producto") // NOTA: singular y coincide con tu HTML
public class BuscarProductoServlet extends HttpServlet {

    private EntityManagerFactory emf;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductoJpaController pro = new ProductoJpaController();
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID no proporcionado.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            
            Producto producto = pro.findProducto(id);

            if (producto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado.");
                return;
            }

            String json = new Gson().toJson(producto);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido.");
        }
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
