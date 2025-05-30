package modelo;

import java.time.LocalDate;
import java.time.Period;
import javax.persistence.*;

@Entity
@Table(name = "usuario")
public class Persona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(nullable = false, unique = true)
    private String numIdentificacion;
    
    @Column(nullable = false)
    private String correo;
    
    @Column(nullable = false)
    private LocalDate fechaNacimiento;
    
    @Column
    private int edad;

    public Persona() {
        // Constructor vacío requerido por JPA
    }
    
    public Persona(String nombre, String apellido, String numIdentificacion,
            String correo, LocalDate fechaNacimiento, int edad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numIdentificacion = numIdentificacion;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
    }
    
    public Persona(String nombre, String apellido, String numIdentificacion,
            String correo, LocalDate fechaNacimiento) {
        this(nombre, apellido, numIdentificacion, correo, fechaNacimiento, 0);
        this.CalcularEdad();
    }
    
    public Persona(int id, String nombre, String apellido, String numIdentificacion,
            String correo, LocalDate fechaNacimiento, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numIdentificacion = numIdentificacion;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumIdentificacion() {
        return numIdentificacion;
    }

    public void setNumIdentificacion(String numIdentificacion) {
        this.numIdentificacion = numIdentificacion;
    }

    // Métodos adicionales para compatibilidad con "cedula"
    public String getCedula() {
        return this.numIdentificacion;
    }

    public void setCedula(String cedula) {
        this.numIdentificacion = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
    
    // Método para calcular la edad automáticamente
    public void CalcularEdad() {
        if (this.fechaNacimiento != null) {
            Period periodo = Period.between(this.fechaNacimiento, LocalDate.now());
            this.edad = periodo.getYears();
        }
    }

    // Método toString para representación de objeto
    @Override
    public String toString() {
        return "Persona{" + 
               "id=" + id + 
               ", nombre=" + nombre + 
               ", apellido=" + apellido + 
               ", numIdentificacion=" + numIdentificacion + 
               ", correo=" + correo + 
               ", fechaNacimiento=" + fechaNacimiento + 
               ", edad=" + edad + '}';
    }
}