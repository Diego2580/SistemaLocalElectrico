/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import javax.persistence.EntityManager;
import modelo.Empleado;
import util.PersistenceUtil;

/**
 *
 * @author diego
 */
public class EmpleadoDAO {
    
    // [0] ya existe el empleado  [1] registro de empleado exitoso
    // [2] Error interno
    // Se emplea este metodo para verificar si existe el cliente por su cedula
    // y si no existe se procede a registrarla
    public int RegistrarEmpleadoDB(Empleado nuevoEmpleado){
        // Inicia la sesion de trabajo con la base de datos
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(p) FROM Persona p WHERE p.numIdentificacion = :numId", Long.class)
            .setParameter("numId", nuevoEmpleado.getNumIdentificacion())
            .getSingleResult();
            
            // Existe la persona, porque el contador dio un resultado
            if(count > 0) return 0;
            
            // Se inicia la transicion
            em.getTransaction().begin();
            // Se inserta la persona
            em.persist(nuevoEmpleado);
            // Confirmar y guardar los cambios
            em.getTransaction().commit();
            return 1;
        }catch(Exception ex){
            // Revertir todo, no guardar nada
            if(em.getTransaction().isActive()){
               em.getTransaction().rollback(); 
            }
            return 2;
        }finally{
            em.close();
        }
    }
    
    public Empleado BuscarEmpleadoPorCedula(String cedula) {
    EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
    try {
        return em.createQuery(
            "SELECT e FROM Empleado e WHERE e.numIdentificacion = :cedula", Empleado.class)
            .setParameter("cedula", cedula)
            .getSingleResult();
    } catch (Exception ex) {
        // Si no se encuentra ningún resultado, devuelve null
        return null;
    } finally {
        em.close();
    }
}
    
    // Metodo que permite actulizar la persona
    public boolean ActualizarEmpleadoDB(int id, Empleado empleadoActualizar) {
        // Inicia la sesion de trabajo con la base de datos
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try {
            // Se verifica si la persona que se intenta actualizar existe
            Empleado existente = em.find(Empleado.class, id);
            if (existente == null) return false;

            // Se inicia la transicion
            em.getTransaction().begin();
            
            // Se actualiza cada parametro de la persona
            existente.setNombre(empleadoActualizar.getNombre());
            existente.setApellido(empleadoActualizar.getApellido());
            existente.setNumIdentificacion(empleadoActualizar.getNumIdentificacion());
            existente.setCorreo(empleadoActualizar.getCorreo());
            existente.setFechaNacimiento(empleadoActualizar.getFechaNacimiento());
            existente.setEdad(empleadoActualizar.getEdad());
            existente.setRol(empleadoActualizar.getRol());
            existente.setActivo(empleadoActualizar.isActivo());
            
            // em.merge(personaActualizar);
            // Confirmar y guardar los cambios
            em.getTransaction().commit();
            return true;

        } catch (Exception ex) {
            // Revertir todo, no guardar nada
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return false;
        } finally {
            em.close();
        }
    }
}