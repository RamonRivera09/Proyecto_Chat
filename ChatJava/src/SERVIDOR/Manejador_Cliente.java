package SERVIDOR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import INICIO_SESION.Conexion;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class Manejador_Cliente extends Thread {

    private Socket socket;
    public PrintWriter salida;
    private BufferedReader entrada;
    private String usuario; // Nombre del usuario conectado

    // Mapa de todos los clientes conectados
    public static HashMap<String, Manejador_Cliente> usuariosConectados = new HashMap<>();

    public Manejador_Cliente(Socket socket) {
        this.socket = socket;
        try {
            // Al crear la entrada:
entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

// Al crear la salida (fíjate en el OutputStreamWriter):
salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   @Override
public void run() {
    try {
        // 1️⃣ Recibir CÓDIGO de usuario y registrar
        // En Manejador_Cliente.java, dentro del método run()
usuario = entrada.readLine(); 
if (usuario != null) {
    // 1. Eliminar el BOM de UTF-8 (el carácter invisible del inicio)
    if (usuario.startsWith("\uFEFF")) {
        usuario = usuario.substring(1);
    }
    // 2. Limpiar espacios y cualquier carácter de control invisible
    usuario = usuario.replaceAll("[^a-zA-Z0-9-]", "").trim();
    
    usuariosConectados.put(usuario, this);
    System.out.println("DEBUG: Servidor registró a: [" + usuario + "] (Largo: " + usuario.length() + ")");
}


            String mensajeRecibido;
            while ((mensajeRecibido = entrada.readLine()) != null) {
                procesarMensaje(mensajeRecibido);
            }

        } catch (IOException e) {
            System.out.println(usuario + " se ha desconectado.");
        } finally {
            // 2️⃣ Limpiar al desconectarse
            if (usuario != null) {
                usuariosConectados.remove(usuario);
                System.out.println("Usuario eliminado del mapa: " + usuario);
            }
            try {
                if (entrada != null) entrada.close();
                if (salida != null) salida.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Procesa mensajes normales o notificaciones
    private void procesarMensaje(String mensajeRecibido) {
        // Mensaje privado
        if (mensajeRecibido.startsWith("MSG||")) {
            String[] partes = mensajeRecibido.split("\\|\\|");
            if (partes.length >= 4) {
                String codigoEmisor = partes[1];
                String codigoReceptor = partes[2];
                String mensaje = partes[3];
                enviarMensajePrivado(codigoEmisor, codigoReceptor, mensaje);
            }
        }
        // Notificación
        else if (mensajeRecibido.startsWith("NOTIF||")) {
            String[] partes = mensajeRecibido.split("\\|\\|");
            if (partes.length >= 4) {
                String codigoEmisor = partes[1];
                String codigoReceptor = partes[2];
                String mensaje = partes[3];
                enviarNotificacion(codigoEmisor, codigoReceptor, mensaje);
            }
        } else {
            System.out.println("Mensaje recibido sin formato: " + mensajeRecibido);
        }
    }

    // Enviar mensaje privado solo al receptor conectado
    private void enviarMensajePrivado(String codigoEmisor, String codigoReceptor, String mensaje) {
        try (Connection conn = Conexion.obtenerConexion()) {
            // Obtener usuario receptor desde código
            String sql = "SELECT usuario FROM usuarios WHERE codigo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, codigoReceptor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String usuarioReceptor = rs.getString("usuario");

                // Verificar si el usuario está conectado
                if (usuariosConectados.containsKey(usuarioReceptor)) {
                    Manejador_Cliente mc = usuariosConectados.get(usuarioReceptor);
                    mc.salida.println("MSG||" + codigoEmisor + "||" + codigoReceptor + "||" + mensaje);
                    System.out.println("Mensaje enviado a: " + usuarioReceptor);
                } else {
                    System.out.println("Usuario desconectado: " + usuarioReceptor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Enviar notificación solo al receptor conectado
    private void enviarNotificacion(String codigoEmisor, String codigoReceptor, String mensaje) {
        try (Connection conn = Conexion.obtenerConexion()) {
            // Obtener usuario receptor desde código
            String sql = "SELECT usuario FROM usuarios WHERE codigo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, codigoReceptor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String usuarioReceptor = rs.getString("usuario");

                // Verificar si el usuario está conectado
                if (usuariosConectados.containsKey(usuarioReceptor)) {
                    Manejador_Cliente mc = usuariosConectados.get(usuarioReceptor);
                    mc.salida.println("NOTIF||" + codigoEmisor + "||" + codigoReceptor + "||" + mensaje);
                    System.out.println("Notificación enviada a: " + usuarioReceptor);
                } else {
                    System.out.println("Usuario desconectado: " + usuarioReceptor);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}