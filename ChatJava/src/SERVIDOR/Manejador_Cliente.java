package SERVIDOR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

// Implementamos Runnable para que funcione como un Hilo (Thread) independiente
public class Manejador_Cliente implements Runnable {
    
    private Socket socket;
    private BufferedReader entrada; // Para leer lo que envía el usuario
    private PrintWriter salida;     // Para enviarle cosas a este usuario
    private Set<PrintWriter> escritores; // La lista de todos los usuarios

    // Constructor que recibe la conexión de Servidor.java
    public Manejador_Cliente(Socket socket, Set<PrintWriter> escritores) {
        this.socket = socket;
        this.escritores = escritores;
    }

    @Override
    public void run() {
        try {
            // 1. Preparamos las herramientas para leer y escribir
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

            // 2. Agregamos a este cliente a la lista general del servidor
            // synchronized evita que dos usuarios se agreguen al mismo milisegundo y choquen
            synchronized (escritores) {
                escritores.add(salida);
            }

            // 3. Ciclo infinito: Escuchamos todo el tiempo lo que escriba ESTE cliente
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                System.out.println("Servidor recibió: " + mensaje);

                // 4. Repartimos el mensaje a TODOS los clientes conectados
                synchronized (escritores) {
                    for (PrintWriter escritor : escritores) {
                        escritor.println(mensaje);
                    }
                }
            }
            
        } catch (IOException e) {
            System.out.println("Un usuario se desconectó o tuvo un problema de red.");
        } finally {
            // 5. Si el cliente cierra el chat, lo quitamos de la lista para no enviarle mensajes a la nada
            if (salida != null) {
                synchronized (escritores) {
                    escritores.remove(salida);
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
