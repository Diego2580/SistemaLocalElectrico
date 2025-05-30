package modelo;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "usuario")  // Asegúrate que esta tabla existe y tiene las columnas necesarias
public class Empleado extends Persona {

    @Column(nullable = false)
    private String rol;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column
    private boolean activo;

    @Column
    private String clave;

    // Constructor vacío
    public Empleado() {
    }

    // Constructor sin ID
    public Empleado(String nombre, String apellido, String numIdentificacion,
                    String correo, LocalDate fechaNacimiento, String rol,
                    String cargo, LocalDate fechaIngreso, boolean activo) {
        super(nombre, apellido, numIdentificacion, correo, fechaNacimiento);
        this.rol = rol;
        this.cargo = cargo;
        this.fechaIngreso = fechaIngreso;
        this.activo = activo;
        this.clave = numIdentificacion; // puedes ajustar esto si lo deseas
    }

    // Constructor con ID
    public Empleado(int id, String nombre, String apellido, String numIdentificacion,
                    String correo, LocalDate fechaNacimiento, String rol,
                    String cargo, LocalDate fechaIngreso, boolean activo, String clave) {
        super(nombre, apellido, numIdentificacion, correo, fechaNacimiento);
        this.rol = rol;
        this.cargo = cargo;
        this.fechaIngreso = fechaIngreso;
        this.activo = activo;
        this.clave = clave;
    }

    // Getters y Setters
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
