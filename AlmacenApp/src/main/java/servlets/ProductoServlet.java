package servlets;

import com.google.gson.Gson;
import dao.ProductoJpaController;

import dto.Producto;


import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/producto", "/producto/*"})
public class ProductoServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //ProductoDAO dao = new ProductoDAO();
        ProductoJpaController dao = new ProductoJpaController();
        String pathInfo = req.getPathInfo();

        resp.setContentType("application/json");
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
        ProductoJpaController dao = new ProductoJpaController();
        Producto actualizado = new Gson().fromJson(req.getReader(), Producto.class);

        try {
            dao.edit(actualizado);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}