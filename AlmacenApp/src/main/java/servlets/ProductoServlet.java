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
        List<Producto> productos = dao.findProductoEntities();

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (productos == null) {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            out.print("{\"status\":\"error\",\"message\":\"Base de datos no disponible\"}");
        } else {
            String json = new Gson().toJson(productos);
            out.print(json);
        }
    }
}