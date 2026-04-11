package SERVIDOR;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private static final int PUERTO = 9090;

    public static void main(String[] args) {
        System.out.println("Servidor iniciado...");
        try (ServerSocket listener = new ServerSocket(PUERTO)) {
            while (true) {
                Socket enchufeCliente = listener.accept();
                // El Manejador_Cliente se encarga de todo lo demás
                new Manejador_Cliente(enchufeCliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}