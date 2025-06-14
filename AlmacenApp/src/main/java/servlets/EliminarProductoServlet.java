package servlets;

import dao.ProductoJpaController;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "EliminarProductoServlet", urlPatterns = {"/eliminar-producto"})
public class EliminarProductoServlet extends HttpServlet {

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("ID no proporcionado.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            ProductoJpaController ctrl = new ProductoJpaController();
            ctrl.destroy(id);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Producto eliminado correctamente.");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al eliminar: " + e.getMessage());
        }
    }
}
