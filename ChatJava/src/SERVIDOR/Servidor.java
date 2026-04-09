package SERVIDOR;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private static final int PUERTO = 9090;

    // Map de usuarios conectados: códigoUsuario -> PrintWriter
    private static Map<String, PrintWriter> usuariosConectados = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor CHARLEMOS iniciado en puerto " + PUERTO);

        try (ServerSocket listener = new ServerSocket(PUERTO)) {

            while (true) {
                Socket enchufeCliente = listener.accept();
                System.out.println("¡Nuevo usuario conectado!");
                
                // Le damos un hilo exclusivo a este cliente
                Manejador_Cliente manejador = new Manejador_Cliente(enchufeCliente, usuariosConectados);
                new Thread(manejador).start();
            }

        } catch (IOException e) {
            System.out.println("Error fatal en el servidor: " + e.getMessage());
        }
    }
}