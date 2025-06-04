package negocio;

import datos.FacturaDAO;
import modelo.Factura;
//
///**
// *
// * @author byron
// */
public class FacturaServicio {
   
    private final FacturaDAO facturaDAO;
    
    
    public FacturaServicio(){
        this.facturaDAO = new FacturaDAO();
    }
    
    
    public void RegistrarNuevaFactura(Factura nuevaFactura){
        facturaDAO.RegistrarFactura(nuevaFactura);
    }
}