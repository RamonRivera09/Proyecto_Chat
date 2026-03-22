package INICIO_SESION;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    // Aquí definimos las credenciales de tu base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "MDmama09"; // Pon tu contraseña si usas una

    // Este método es el que llamaremos desde otras ventanas
    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            // Intentamos establecer la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (SQLException e) {
            // Si algo falla (ej. XAMPP está apagado), mostramos un error
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos:\n" + e.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return conexion; // Devuelve la conexión lista para usarse, o null si falló
    }
}
