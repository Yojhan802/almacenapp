/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;





import dao.ProductoJpaController;
import dto.Producto;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AgregarProductoServlet")
public class AgregarProductoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    String nombre = request.getParameter("nombre");
    String precioStr = request.getParameter("precio");
    String stockStr = request.getParameter("stock");

    try {
        if (nombre == null || nombre.isEmpty() || 
            precioStr == null || precioStr.isEmpty() || 
            stockStr == null || stockStr.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos son obligatorios.");
        }

        double precio = Double.parseDouble(precioStr);
        int stock = Integer.parseInt(stockStr);

        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);

        ProductoJpaController dao = new ProductoJpaController(); // Asegúrate que este constructor funciona
        dao.create(producto);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        response.getWriter().write("Producto agregado correctamente");

    } catch (NumberFormatException nfe) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Precio o stock no son válidos.");
    } catch (IllegalArgumentException iae) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al agregar el producto.");
    }
}

}
