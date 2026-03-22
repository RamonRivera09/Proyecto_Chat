package SERVIDOR;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Servidor {
    // Definimos el "canal" o puerto de red por donde entrarán los mensajes
    private static final int PUERTO = 9090;
    
    // Aquí guardaremos las "tuberías de salida" de todos los usuarios conectados
    // Usamos Set para que no haya duplicados
    private static Set<PrintWriter> escritores = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("El servidor de WhatsChafa está iniciando...");

        // Creamos el servidor en el puerto que elegimos
        try (ServerSocket listener = new ServerSocket(PUERTO)) {
            System.out.println("Servidor a la escucha en el puerto " + PUERTO);

            // Ciclo infinito: El servidor nunca se apaga, siempre espera usuarios
            while (true) {
                // Se detiene aquí hasta que alguien se conecta
                Socket enchufeCliente = listener.accept(); 
                System.out.println("¡Un nuevo usuario se ha conectado al servidor!");

                // Le asignamos un trabajador (Hilo) exclusivo a este nuevo cliente
                Manejador_Cliente manejador = new Manejador_Cliente(enchufeCliente, escritores);
                new Thread(manejador).start();
            }
            
        } catch (IOException e) {
            System.out.println("Error fatal en el servidor: " + e.getMessage());
        }
    }
}