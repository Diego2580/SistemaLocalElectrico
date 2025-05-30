package datos;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import modelo.Cliente;
import modelo.Empleado;
import modelo.Persona;
import util.PersistenceUtil;

public class PersonaDAO {
    
    public int registarPersona(Persona persona) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(p) FROM Persona p WHERE p.numIdentificacion = :numId", Long.class)
                .setParameter("numId", persona.getNumIdentificacion())
                .getSingleResult();
            
            if(count > 0) {
                return 0; // Ya existe
            }
            
            em.getTransaction().begin();
            em.persist(persona);
            em.getTransaction().commit();
            return 1; // Éxito
        } catch(Exception ex) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al registrar persona: " + ex.getMessage());
            return 2; // Error
        } finally {
            em.close();
        }
    }
    
    public List<Persona> ListarPersonasRegistradas() {
    EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
    try {
        // Obtener todos los clientes
        List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
        
        // Obtener todos los empleados
        List<Empleado> empleados = em.createQuery("SELECT e FROM Empleado e", Empleado.class).getResultList();
        
        // Combinar las listas
        List<Persona> personas = new ArrayList<>();
        personas.addAll(clientes);
        personas.addAll(empleados);
        
        return personas;
    } finally {
        em.close();
    }
}
    
    public boolean eliminarPersona(int id) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Persona persona = em.find(Persona.class, id);
            if(persona == null) {
                return false;
            }
            
            em.getTransaction().begin();
            em.remove(persona);
            em.getTransaction().commit();
            return true;
        } catch(Exception ex) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al eliminar persona: " + ex.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    public boolean actualizarPersona(Persona persona) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Persona existente = em.find(Persona.class, persona.getId());
            if(existente == null) {
                return false;
            }
            
            em.getTransaction().begin();
            existente.setNombre(persona.getNombre());
            existente.setApellido(persona.getApellido());
            existente.setNumIdentificacion(persona.getNumIdentificacion());
            existente.setCorreo(persona.getCorreo());
            existente.setFechaNacimiento(persona.getFechaNacimiento());
            existente.setEdad(persona.getEdad());
            em.getTransaction().commit();
            return true;
        } catch(Exception ex) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error al actualizar persona: " + ex.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    public Persona buscarPorCedula(String cedula) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Persona p WHERE p.numIdentificacion = :cedula", Persona.class)
                .setParameter("cedula", cedula)
                .getSingleResult();
        } catch(NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
    
    public Persona buscarPorCorreo(String correo) {
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery(
                "SELECT p FROM Persona p WHERE p.correo = :correo", Persona.class)
                .setParameter("correo", correo)
                .getSingleResult();
        } catch(NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public int RegistrarPersona(Persona persona) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    public boolean EliminarPersona(int numId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public boolean ActualizarPersona(int id, Persona persona) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}