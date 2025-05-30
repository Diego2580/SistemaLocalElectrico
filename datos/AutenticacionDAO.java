/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import modelo.Empleado;
import util.PersistenceUtil;

/**
 *
 * @author diego
 */
public class AutenticacionDAO {
        // Se emplea este metodo para poder iniciar sesion con usuario y clave
    public Empleado IniciarSesionUsuarioClave(String usuario, String clave){
        // Se inicia la sesion de trabajo
        EntityManager em = PersistenceUtil.getEntityManagerFactory().createEntityManager();
        try{
            return em.createQuery(
               "SELECT e FROM Empleado e WHERE e.numIdentificacion = :user AND e.clave = :contrasena", Empleado.class)
                .setParameter("user", usuario)
                .setParameter("contrasena", clave)
                .getSingleResult();
        }catch(NoResultException ex){
            return null;
        }finally{
            em.close();
        }
    }
}
