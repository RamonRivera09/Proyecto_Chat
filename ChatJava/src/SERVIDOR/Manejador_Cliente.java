package SERVIDOR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Manejador_Cliente implements Runnable {

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Map<String, PrintWriter> usuariosConectados;
    private String codigoUsuario; // Agregar la variable para almacenar el código de usuario

    public Manejador_Cliente(Socket socket, Map<String, PrintWriter> usuariosConectados) {
        this.socket = socket;
        this.usuariosConectados = usuariosConectados;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            salida = new PrintWriter(new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // Recibimos el primer mensaje, que es el código de usuario
            codigoUsuario = entrada.readLine();
            if (codigoUsuario != null) {
                synchronized (usuariosConectados) {
                    usuariosConectados.put(codigoUsuario, salida); // Registramos el código de usuario
                    System.out.println("Usuario registrado en servidor: " + codigoUsuario);
                }
            } else {
                // Si no se recibe un código válido, cerramos la conexión
                System.out.println("No se recibió un código de usuario. Desconectando...");
                socket.close();
                return;
            }

            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Servidor recibió: " + mensaje);

                // Verificamos si es un mensaje de notificación
                if (mensaje.startsWith("NOTIF||")) {
                    // Formato: NOTIF||codigoEmisor||codigoReceptor||mensaje
                    String[] partes = mensaje.split("\\|\\|");
                    if (partes.length >= 4) {
                        String codigoReceptor = partes[2];
                        PrintWriter receptor;
                        synchronized (usuariosConectados) {
                            receptor = usuariosConectados.get(codigoReceptor); // Encontramos al receptor
                        }
                        if (receptor != null) {
                            receptor.println(mensaje); // Enviamos la notificación solo al receptor
                        } else {
                            System.out.println("Receptor no conectado: " + codigoReceptor);
                        }
                    }
                } else {
                    // Mensaje general a todos los usuarios conectados, excepto al emisor
                    synchronized (usuariosConectados) {
                        for (Map.Entry<String, PrintWriter> entry : usuariosConectados.entrySet()) {
                            if (!entry.getKey().equals(codigoUsuario)) {
                                entry.getValue().println(mensaje); // Enviamos el mensaje a todos
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Usuario desconectado: " + codigoUsuario);
        } finally {
            // Limpiar usuario de la lista al desconectarse
            if (codigoUsuario != null) {
                synchronized (usuariosConectados) {
                    usuariosConectados.remove(codigoUsuario); // Quitamos al usuario de la lista
                    System.out.println("Usuario removido: " + codigoUsuario);
                }
            }
            try {
                socket.close(); // Cerramos la conexión
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}