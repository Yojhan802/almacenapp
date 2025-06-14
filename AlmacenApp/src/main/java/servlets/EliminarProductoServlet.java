package servlets;

import dao.ProductoJpaController;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "EliminarProductoServlet", urlPatterns = {"/EliminarProducto/*"})
public class EliminarProductoServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el ID desde la URL: /EliminarProducto/5
        String pathInfo = request.getPathInfo(); // Ej: "/5"
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));

                ProductoJpaController ctrl = new ProductoJpaController();
                ctrl.destroy(id);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Producto eliminado correctamente.");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error al eliminar: " + e.getMessage());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID no válido.");
        }
    }
}
