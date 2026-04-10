package SERVIDOR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class Manejador_Cliente extends Thread {

    private Socket socket;
    public PrintWriter salida;
    private BufferedReader entrada;
    private String codigoUsuario; // Usaremos esta variable para saber que guarda el CÓDIGO

    // Mapa de todos los clientes conectados (Llave: Código, Valor: Manejador)
    public static HashMap<String, Manejador_Cliente> usuariosConectados = new HashMap<>();

    public Manejador_Cliente(Socket socket) {
        this.socket = socket;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   @Override
    public void run() {
        try {
            // 1️⃣ Recibir CÓDIGO de usuario y registrar
            codigoUsuario = entrada.readLine(); 
            if (codigoUsuario != null) {
                // Eliminar el BOM y caracteres invisibles
                if (codigoUsuario.startsWith("\uFEFF")) {
                    codigoUsuario = codigoUsuario.substring(1);
                }
                codigoUsuario = codigoUsuario.replaceAll("[^a-zA-Z0-9-]", "").trim();
                
                usuariosConectados.put(codigoUsuario, this);
                System.out.println("DEBUG: Servidor registró a: [" + codigoUsuario + "]");
            }

            // 2️⃣ Escuchar mensajes infinitamente
            String mensajeRecibido;
            while ((mensajeRecibido = entrada.readLine()) != null) {
                procesarMensaje(mensajeRecibido);
            }

        } catch (IOException e) {
            System.out.println(codigoUsuario + " se ha desconectado de forma abrupta.");
        } finally {
            // 3️⃣ Limpiar al desconectarse
            if (codigoUsuario != null) {
                usuariosConectados.remove(codigoUsuario);
                System.out.println("Usuario eliminado del mapa: [" + codigoUsuario + "]");
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

    // 🚀 AQUÍ ESTÁ EL CÓDIGO QUE ME PREGUNTASTE DÓNDE IBA
    private void procesarMensaje(String mensajeRecibido) {
        String[] partes = mensajeRecibido.split("\\|\\|");
        if (partes.length < 4) return; 

        String tipo = partes[0];
        String codigoEmisor = partes[1];
        String codigoReceptor = partes[2].replaceAll("[^a-zA-Z0-9-]", "").trim(); 
        String contenido = partes[3];

        if (usuariosConectados.containsKey(codigoReceptor)) {
            Manejador_Cliente destino = usuariosConectados.get(codigoReceptor);
            destino.salida.println(mensajeRecibido); 
            System.out.println("Servidor: [" + tipo + "] entregado a [" + codigoReceptor + "]");
        } else {
            System.out.println("Servidor: Receptor [" + codigoReceptor + "] offline.");
            
            // 🚀 Avisar al emisor que el receptor no está conectado
            if (usuariosConectados.containsKey(codigoEmisor)) {
                Manejador_Cliente origen = usuariosConectados.get(codigoEmisor);
                origen.salida.println("OFFLINE||" + codigoReceptor);
            }
        }
    }
}