package negocio;

import java.util.List;
import modelo.Producto;
import datos.ProductoDAO;

public class ProductoServicio {
    
    private final ProductoDAO productoDAO;
    
    public ProductoServicio() {
        this.productoDAO = new ProductoDAO();
    }
    
    // [0] ya existe el producto [1] registro exitoso [2] error
    public int agregarProducto(Producto producto) {
        // Validaciones de negocio
        if(producto.getPrecio() <= 0) {
            return 2; // Precio inválido
        }
        if(producto.getStock() < 0) {
            return 2; // Stock inválido
        }
        
        // Normalizar datos
        producto.setNombre(producto.getNombre().trim());
        producto.setCodigo(producto.getCodigo().trim());
        
        if(producto.getDescripcion() != null) {
            producto.setDescripcion(producto.getDescripcion().trim());
        }
        
        return productoDAO.registrarProducto(producto);
    }
    
    public boolean actualizarProducto(Producto producto) {
        // Validaciones
        if(producto.getPrecio() <= 0 || producto.getStock() < 0) {
            return false;
        }
        
        // Normalizar datos
        producto.setNombre(producto.getNombre().trim());
        producto.setCodigo(producto.getCodigo().trim());
        
        if(producto.getDescripcion() != null) {
            producto.setDescripcion(producto.getDescripcion().trim());
        }
        
        return productoDAO.actualizarProducto(producto);
    }
    
    public boolean eliminarProducto(int id) {
        return productoDAO.eliminarProducto(id);
    }
    
    public List<Producto> obtenerTodosProductos() {
        return productoDAO.obtenerTodosLosProductos();
    }
    
    public List<Producto> buscarProductosPorNombre(String nombre) {
        return productoDAO.buscarPorNombre(nombre.trim());
    }
    
    public List<Producto> ordenarProductosPorPrecioDesc() {
        return productoDAO.ordenarPorPrecioDescendente();
    }
    
    public long contarTotalProductos() {
        return productoDAO.contarProductos();
    }
    
    public List<Producto> buscarProductosPorRangoPrecio(double min, double max) {
        if(min > max) {
            double temp = min;
            min = max;
            max = temp;
        }
        return productoDAO.productosPorRangoPrecio((float)min, (float)max);
    }
    
    public Producto buscarProductoPorCodigo(String codigo) {
        return productoDAO.buscarPorCodigo(codigo.trim());
    }
}