package negocio;

import datos.ClienteDAO;
import datos.EmpleadoDAO;
import modelo.Persona;
import datos.PersonaDAO;
import java.util.List;
import modelo.Cliente;
import modelo.Empleado;

public class PersonaServicio {
    private final PersonaDAO personaDAO;
    private final ClienteDAO clienteDAO;
    private final EmpleadoDAO empleadoDAO;
    
    public PersonaServicio() {
        this.personaDAO = new PersonaDAO();
        this.clienteDAO = new ClienteDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    public int AgregarPersona(Persona nuevaPersona) {
        nuevaPersona.CalcularEdad();
        if (nuevaPersona.getEdad() < 18) {
            return 3; // Menor de edad
        }

        nuevaPersona.setNombre(nuevaPersona.getNombre().toUpperCase());
        nuevaPersona.setApellido(nuevaPersona.getApellido().toUpperCase());

        try {
            if (nuevaPersona instanceof Cliente cliente) {
                return clienteDAO.RegistrarClienteDB(cliente);
            } else if (nuevaPersona instanceof Empleado empleado) {
                return empleadoDAO.RegistrarEmpleadoDB(empleado);
            }
            return 2; // Error: Tipo de persona no reconocido
        } catch (Exception e) {
            return 2; // Error interno
        }
    }
    

    public Persona BuscarPersonaPorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }
        Persona persona = clienteDAO.BuscarClientePorCedula(cedula);
        return (persona != null) ? persona : empleadoDAO.BuscarEmpleadoPorCedula(cedula);
    }

    public List<Persona> ListarPersonas() {
        return personaDAO.ListarPersonasRegistradas();
    }

    public boolean EliminarPersonaPorId(int numId) {
        return personaDAO.EliminarPersona(numId);
    }

    public boolean ActualizarPersona(int id, Persona persona) {
        return personaDAO.ActualizarPersona(id, persona);
    }
}