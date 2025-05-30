package datos;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import modelo.Producto;
import util.PersistenceUtil;

public class ProductoDAO {
    
    public int registrarProducto(Producto producto) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(p) FROM Producto p WHERE p.codigo = :codigo", Long.class)
                .setParameter("codigo", producto.getCodigo())
                .getSingleResult();
            
            if(count > 0) {
                return 0; // Ya existe
            }
            
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            return 1; // Éxito
        } catch(Exception ex) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al registrar producto: " + ex.getMessage());
            return 2; // Error
        } finally {
            em.close();
        }
    }
    
    public boolean actualizarProducto(Producto producto) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Producto existente = em.find(Producto.class, producto.getId());
            if(existente == null) {
                return false;
            }
            
            em.getTransaction().begin();
            existente.setNombre(producto.getNombre());
            existente.setCodigo(producto.getCodigo());
            existente.setPrecio(producto.getPrecio());
            existente.setDescripcion(producto.getDescripcion());
            existente.setStock(producto.getStock());
            em.getTransaction().commit();
            return true;
        } catch(Exception ex) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al actualizar producto: " + ex.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean eliminarProducto(int id) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Producto producto = em.find(Producto.class, id);
            if(producto == null) {
                return false;
            }
            
            em.getTransaction().begin();
            em.remove(producto);
            em.getTransaction().commit();
            return true;
        } catch(Exception ex) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar producto: " + ex.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    public List<Producto> obtenerTodosLosProductos() {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p ORDER BY p.nombre", Producto.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Producto> buscarPorNombre(String nombre) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)", Producto.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Producto> ordenarPorPrecioDescendente() {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p ORDER BY p.precio DESC", Producto.class)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public long contarProductos() {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(p) FROM Producto p", Long.class)
                .getSingleResult();
        } finally {
            em.close();
        }
    }
    
    public List<Producto> productosPorRangoPrecio(float min, float max) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max", Producto.class)
                .setParameter("min", min)
                .setParameter("max", max)
                .getResultList();
        } finally {
            em.close();
        }
    }
    
    public Producto buscarPorCodigo(String codigo) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Producto p WHERE p.codigo = :codigo", Producto.class)
                .setParameter("codigo", codigo)
                .getSingleResult();
        } catch(NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
}