/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package negocio;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import modelo.Empleado;

/**
 *
 * @author diego
 */
public class LoginServicio {

   private EntityManagerFactory emf;
    private EntityManager em;

    public LoginServicio() {
        // El nombre debe coincidir con el persistence-unit de persistence.xml
        emf = Persistence.createEntityManagerFactory("tuUnidadDePersistencia");
        em = emf.createEntityManager();
    }

    public boolean LoginUsuarioClave(String usuario, String clave) {
        try {
            // Consulta por numIdentificacion (usuario)
            Empleado empleado = em.createQuery(
                "SELECT e FROM Empleado e WHERE e.numIdentificacion = :numId", Empleado.class)
                .setParameter("numId", usuario)
                .getSingleResult();

            // Compara la clave
            if (empleado != null && empleado.getClave() != null && empleado.getClave().equals(clave)) {
                return true;
            }
        } catch (Exception e) {
            // Puede ser NoResultException si no encuentra empleado
            // Aquí puedes registrar error o manejar excepción según convenga
        }
        return false;
    }

    // Cerramos los recursos cuando no se necesiten más
    public void cerrar() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

}
