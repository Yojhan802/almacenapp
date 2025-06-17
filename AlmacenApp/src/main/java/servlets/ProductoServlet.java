package servlets;

import com.google.gson.Gson;
import dao.ProductoJpaController;
import dao.exceptions.NonexistentEntityException;
import dto.Producto;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/producto", "/producto/*"})
public class ProductoServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ProductoServlet.class.getName());

    private final Gson gson = new Gson();
    
  
    // Constructor alternativo para testing
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setContentType("application/json");
        ProductoJpaController dao = new ProductoJpaController();
        String pathInfo = req.getPathInfo();

        
        PrintWriter out = resp.getWriter();

        if (pathInfo == null || pathInfo.equals("/")) {
            List<Producto> productos = dao.findProductoEntities();
            out.print(new Gson().toJson(productos));
        } else {
            try {
                int id = Integer.parseInt(pathInfo.substring(1));
                Producto producto = dao.findProducto(id);
                if (producto != null) {
                    out.print(new Gson().toJson(producto));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        ProductoJpaController productoDao = new ProductoJpaController();
        try {
            Producto actualizado = gson.fromJson(req.getReader(), Producto.class);
            productoDao.edit(actualizado);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NonexistentEntityException e) {
            logger.log(Level.WARNING, "Intento de actualizar producto inexistente", e);
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, 
                            "El producto no existe");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en PUT /producto", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Error al actualizar el producto");
        }
    }

    
    
    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) 
            throws IOException {
        resp.setStatus(statusCode);
        resp.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}