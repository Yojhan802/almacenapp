
//pestaña agregar//
function agregarProducto(formulario) {
      const formData = new FormData(formulario);
      const contexto = window.location.pathname.split("/")[1];

      fetch(`/${contexto}/AgregarProductoServlet`, {
        method: "POST",
        body: new URLSearchParams(formData)
      })
      .then(response => {
        if (response.ok) {
          alert("Producto agregado con éxito.");
          formulario.reset();
        } else {
          response.text().then(text => alert("Error al agregar producto: " + text));
        }
      })
      .catch(error => {
        console.error("Error:", error);
      });
    }

    document.getElementById("formAgregarProducto").addEventListener("submit", function (e) {
      e.preventDefault();
      agregarProducto(this);
    });
    
   //pestaña editarproducto
    
    function showMessage(message, isError = true) {
      const statusEl = document.getElementById('statusMessage');
      statusEl.textContent = message;
      statusEl.className = isError ? 'status-message error' : 'status-message success';
      
      if (!isError) {
        setTimeout(() => {
          statusEl.style.display = 'none';
        }, 3000);
      }
    }

    function buscarProductoPorIdEditar() {
    const id = document.getElementById('idBuscar').value;
    if (!id) {
        showMessage("❌ Por favor ingrese un ID válido");
        return false;
    }

    fetch(`producto/${id}`)
        .then(res => {
            if (!res.ok) {
                if (res.status >= 500) {
                    throw new Error("Error de Servidor");
                }
                alert("❌ Producto no encontrado");
            }
            return res.json();
        })
        .then(producto => {
            document.getElementById('erroreditar').style.display = 'none';
            showMessage("", false);
            mostrarFormularioConDatos(producto);
        })
            .catch(err => {
                if (err.message === "Error de Servidor" || err.message.includes("Failed to fetch")) {
                    document.getElementById('erroreditar').style.display = 'block';
                    setTimeout(buscarProductoPorIdEditar, 5000);
                }
                console.error("Error:", err);
            });
    
    return false;
}

    function mostrarFormularioConDatos(producto) {
      const contenedor = document.getElementById('editarFormulario');
      contenedor.style.display = 'block';
      contenedor.innerHTML = `
        <form onsubmit="return guardarCambiosProducto(${producto.id})">
          <label for="edit-nombre">Nombre:</label>
          <input type="text" id="edit-nombre" value="${producto.nombre}" required>

          <label for="edit-precio">Precio:</label>
          <input type="number" id="edit-precio" value="${producto.precio}" step="0.01" required>

          <label for="edit-stock">Stock:</label>
          <input type="number" id="edit-stock" value="${producto.stock}" required>

          <button type="submit">💾 Guardar Cambios</button>
        </form>
      `;
    }

    function guardarCambiosProducto(id) {
      const productoEditado = {
        id,
        nombre: document.getElementById('edit-nombre').value,
        precio: parseFloat(document.getElementById('edit-precio').value),
        stock: parseInt(document.getElementById('edit-stock').value)
      };

      fetch('producto', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(productoEditado)
      })
      .then(res => {
        if (!res.ok) throw new Error("Error al actualizar");
        alert("✅ Producto actualizado correctamente", false);
      })
      .catch(err => {
        alert("❌ No se pudo guardar los cambios");
        console.error(err);
      });

      return false;
    }
    
    //pestaña eliminar
/*
    const estadoDiv = document.getElementById("estadoConexion");

    function mostrarEstado(texto, tipo = "info") {
        if (tipo === "info") {
            estadoDiv.className = "loading-bar";
            estadoDiv.innerHTML = `<div class="spinner"></div><span>${texto}</span>`;
        } else if (tipo === "error") {
            estadoDiv.className = "error-bar";
            estadoDiv.textContent = texto;
        } else {
            estadoDiv.className = "";
            estadoDiv.textContent = "";
        }
    }

    function ocultarEstado() {
        estadoDiv.className = "";
        estadoDiv.textContent = "";
    }

    function buscarProductoPorId() {
        const id = document.getElementById('idInput').value;
        if (!id) {
            alert("Ingrese un ID válido.");
            return;
        }

        mostrarEstado("Buscando producto...", "info");

        fetch(`buscar-producto?id=${id}`)
            .then(response => {
                if (!response.ok) throw new Error("Producto no encontrado.");
                return response.json();
            })
            .then(producto => {
                ocultarEstado();
                const resultadoDiv = document.getElementById('resultadoProducto');
                resultadoDiv.innerHTML = `
                    <table class="product-table">
                        <thead>
                            <tr>
                                <th>ID</th><th>Nombre</th><th>Precio</th><th>Stock</th><th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>${producto.id}</td>
                                <td>${producto.nombre}</td>
                                <td>${producto.precio}</td>
                                <td>${producto.stock}</td>
                                <td><button onclick="eliminarProducto(${producto.id})">Eliminar</button></td>
                            </tr>
                        </tbody>
                    </table>
                `;
            })
            .catch(error => {
                mostrarEstado(error.message, "error");
                document.getElementById('resultadoProducto').innerHTML = 
                    `<div class="error-message">${error.message}</div>`;
            });
    }

    function eliminarProducto(id, intento = 1) {
        if (!confirm("¿Estás seguro de que deseas eliminar este producto?")) {
            return;
        }

        mostrarEstado("Eliminando producto...", "info");

        fetch(`eliminar-producto?id=${id}`, {
            method: "DELETE"
        })
        .then(response => {
            if (response.ok) {
                ocultarEstado();
                alert("Producto eliminado correctamente.");
                document.getElementById('resultadoProducto').innerHTML = '';
            } else {
                return response.text().then(text => {
                    throw new Error(text || "No se pudo eliminar el producto.");
                });
            }
        })
        .catch(error => {
            console.error("Error al eliminar producto:", error);
            if (intento < 3) {
                mostrarEstado(`Fallo al eliminar (intento ${intento}/3). Reintentando...`, "error");
                setTimeout(() => eliminarProducto(id, intento + 1), 2000);
            } else {
                mostrarEstado("No se pudo conectar al servidor. Intente más tarde.", "error");
            }
        });
    }*/


        
        //pestaña index
        
        function mostrarFormularioEdicion(id,url) {
  mostrarSeccion(id); // Muestra la sección específica
  const contenedor = document.getElementById(id);
  contenedor.innerHTML = `
    
    <iframe src="${url}" style="width: 100%; height: 100vh; border: none;"></iframe>
  `;
}
    // Función para mostrar/ocultar secciones
    function mostrarSeccion(id) {
      const secciones = ['dashboard', 'productos', 'inventario', 'contenedor-tabla','editarproducto'];
      secciones.forEach(sec => {
        const elemento = document.getElementById(sec);
        if (elemento) {
          elemento.style.display = (sec === id) ? 'block' : 'none';
        }
      });
      
      if (id==='contenedor-tabla') {
          const inventario = document.getElementById('inventario');
        inventario.innerHTML = '';
}
      // Limpia el contenido del iframe al cambiar de sección
      if (id !== 'inventario') {
        const inventario = document.getElementById('inventario');
        inventario.innerHTML = '';
      }
      
      
      if (id === 'inventario') {
        const div = document.getElementById('main-content');
        div.style.padding = "0px";
      } 

      if (id !== 'productos') {
        document.getElementById('contenedor-tabla').innerHTML = '';
      }
    }

    // Función para mostrar/ocultar el menú desplegables
    function toggleDropdown() {
      const dropdown = document.getElementById("dropdownProductos");
      dropdown.classList.toggle("show");
    }

 

    // Función para eliminar un producto
   

    // Función para cargar el iframe del inventario
    function cargarIframe() {
      mostrarSeccion('inventario');
      const contenedor = document.getElementById('inventario');
      contenedor.innerHTML = '';
      contenedor.style.padding = "0px";

      const iframe = document.createElement('iframe');
      iframe.id = 'visor';
      iframe.setAttribute("scrolling", "no");
      iframe.style.overflow = "hidden";
      iframe.style.border = "none";
      iframe.width = "100%";
      iframe.height = "100%";
      iframe.src = 'inventario.html';

      contenedor.appendChild(iframe);
    }
    
    //pestaña inventario
    
    function cargarProductos() {
      fetch("producto")
        .then(res => {
          if (!res.ok) throw new Error("Error conexión");
          return res.json();
        })
        .then(data => {
          document.getElementById("error").style.display = "none";
          const tbody = document.querySelector("#tablaProductos tbody");
          tbody.innerHTML = "";
          data.forEach(p => {
            const row = document.createElement("tr");
            row.innerHTML = `
              <td>${p.id}</td>
              <td>${p.nombre}</td>
              <td>${p.stock}</td>
              <td>${p.precio}</td>
            `;
            tbody.appendChild(row);
          });
        })
        .catch(() => {
          document.getElementById("error").style.display = "block";
          setTimeout(cargarProductos, 5000);
        });
    }

    // Efecto de carga inicial
    document.addEventListener('DOMContentLoaded', () => {
      document.querySelector('.container').style.opacity = '0';
      document.querySelector('.container').style.transform = 'translateY(20px)';
      
      setTimeout(() => {
        document.querySelector('.container').style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        document.querySelector('.container').style.opacity = '1';
        document.querySelector('.container').style.transform = 'translateY(0)';
        cargarProductos();
      }, 100);
    });
    
    function verInventario() {
  // Guardar indicador para mostrar la sección inventario
  localStorage.setItem("mostrarInventario", "true");
  // Redirigir al dashboard
  window.location.href = "inventario.html"; // Ajusta el nombre si tu archivo se llama distinto
}
    
    