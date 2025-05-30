
package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexionDB {
    
    // Variables de la clase
    // 5432 para PostgreSQL
    private static final String URL = "jdbc:mysql://localhost:3306/persona_database";
    private static final String USER = "root";
    private static final String PASSWORD = "ucacue";
    
    // Se emplea esta metodo para poder abrir la conexion con la DB
    public static Connection AbrirConexion(){
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }catch(SQLException ex){
            System.out.println("Error de conexión: " + ex.getMessage());
            return null;
        }
    }
    
    
    // Se emplea esta metodo para poder cerrar la conexion con la DB
    public static void CerrarConexion(Connection conn){
        if(conn != null){
            try{
                conn.close();
            }catch(SQLException ex){
                System.out.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }
}