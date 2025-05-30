
package modelo;

import javax.persistence.*;
import java.util.List;

///**
// *
// * @author byron
// */
@Entity
@Table(name = "factura")
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
@JoinColumn(name = "persona_id")  // Cambiado de "id" a "persona_id"
private Persona persona;
    
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    private List<DetalleFactura> detalles;
    
    
    public Factura (){
        
    }
    
    public Factura (Persona persona, List<DetalleFactura> detalles){
        this.persona = persona;
        this.detalles = detalles;
    }
    
    
    public Factura (int id, Persona persona, List<DetalleFactura> detalles){
        this.id = id;
        this.persona = persona;
        this.detalles = detalles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }
}