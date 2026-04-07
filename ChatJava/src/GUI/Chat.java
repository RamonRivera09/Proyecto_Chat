package GUI;

//import Snake.FrameJuego;
import INICIO_SESION.Inicio;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.nio.charset.StandardCharsets;
import javax.swing.ListModel;

public class Chat extends JFrame implements ActionListener {

    // --- Variables de Red ---
    private Socket socket;
    private BufferedReader entrada; // Para escuchar al servidor
    private PrintWriter salida;     // Para enviarle mensajes al servidor
    private CardLayout cardLayout;
    private JPanel pContenedor, pBuscar, pantallaInicial, Separador, pChat, pChatInput, cabeceraChat, pMensajes;
    private JLabel MiPerfil, Titulo, Contactos;
    private JPopupMenu Opciones;
    private JButton Volver, Enviar;
    private JTextField Mensajes;
    private JScrollPane scrollContactos;

    // Nuevo
    private JScrollPane scrollMensajes;
    private JComboBox<String> emojis;
    private JComboBox<String> fuentes;

    private JMenuItem juego, correo;
    private JTextField txtBuscar;
    private JButton btnBuscar;

    private String usuarioLogueado; // Variable para guardar el nombre

    public Chat(String usuario) {
        this.usuarioLogueado = usuario; // Guardamos el nombre
        configFrame();
        initComponents();

        // Opcional: Cambiar el título de la ventana con el nombre
        setTitle("WhatsChafa - Sesión de: " + usuarioLogueado);

        conectarAlServidor();
        setVisible(true);
    }

    public void configFrame() {
        setSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        pContenedor = new JPanel(cardLayout);
        setContentPane(pContenedor);
    }

    public void initComponents() {
        pantallaInicial = new JPanel(new BorderLayout());
        PantallaPerfil vistaPerfil = new PantallaPerfil(usuarioLogueado, cardLayout, pContenedor);
        pContenedor.add(vistaPerfil, "PERFIL");

        // Cabecera superior
        Separador = new JPanel(new BorderLayout());
        Separador.setPreferredSize(new Dimension(1000, 70));
        Separador.setBackground(new Color(200, 162, 200));

        Titulo = new JLabel("CHARLEMOS");
        Titulo.setFont(new Font("Arial", Font.BOLD, 22));
        Titulo.setForeground(Color.WHITE);
        Separador.add(Titulo, BorderLayout.WEST);

        MiPerfil = new JLabel(usuarioLogueado + " Conectad@ " + " \u2630   ");
        MiPerfil.setFont(new Font("Arial", Font.BOLD, 20));
        MiPerfil.setFont(new Font("Arial", Font.BOLD, 25));
        MiPerfil.setForeground(Color.WHITE);
        Separador.add(MiPerfil, BorderLayout.EAST);

        Opciones = new JPopupMenu();
        JMenuItem itemVerPerfil = new JMenuItem("Ver mi Perfil");
        //itemVerPerfil.setFont(new Font("Arial", Font.PLAIN, 14));
        itemVerPerfil.addActionListener(e -> {
            cardLayout.show(pContenedor, "PERFIL");
        });
        Opciones.add(itemVerPerfil);
        correo=new JMenuItem("Registrar correo");
        correo.addActionListener(this);
        Opciones.add(correo);
        juego = new JMenuItem("Jugar");
        juego.addActionListener(this);
        Opciones.add(new JMenuItem("Contacto"));
        Opciones.add(new JMenuItem("Ajustes"));
        Opciones.add(juego);
        Opciones.addSeparator();

        //Cerrar sesión
        JMenuItem itemCerrar = new JMenuItem("Cerrar sesión");
        itemCerrar.setForeground(Color.RED); // Un detalle visual de alerta

        itemCerrar.addActionListener(e -> {
            // Pedimos confirmación al usuario (esto le da puntos a tu interfaz)
            int confirmar = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres salir?",
                    "Cerrar Sesión",
                    JOptionPane.YES_NO_OPTION);

            if (confirmar == JOptionPane.YES_OPTION) {
                cerrarSesion();
            }
        });
        Opciones.addSeparator();
        Opciones.add(itemCerrar);
        MiPerfil.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Opciones.show(MiPerfil, e.getX() - 80, e.getY() + 20);
            }
        });

        pantallaInicial.add(Separador, BorderLayout.NORTH);

        // Lista contactos
        String[] contactos = {"América", "Alexa", "Ramón", "Erick", "Diego", "Paco", "Justin", "Dani", "Leti", "Zayra", "Linda"};
        JList<String> listaContactos = new JList<>(contactos);
        listaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaContactos.setFont(new Font("Arial", Font.PLAIN, 24));
        listaContactos.setFixedCellHeight(80);

        scrollContactos = new JScrollPane(listaContactos);
        pantallaInicial.add(scrollContactos, BorderLayout.CENTER);

        pBuscar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pBuscar.setBackground(new Color(200, 162, 200));

        txtBuscar = new JTextField(30); // tamaño en columnas
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> {
            String busqueda = txtBuscar.getText().trim().toLowerCase();

            if (busqueda.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Escribe un nombre");
                return;
            }

            ListModel<String> modelo = listaContactos.getModel();
            boolean encontrado = false;

            for (int i = 0; i < modelo.getSize(); i++) {
                String contacto = modelo.getElementAt(i).toLowerCase();

                if (contacto.contains(busqueda)) {
                    listaContactos.setSelectedIndex(i);
                    listaContactos.ensureIndexIsVisible(i);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Contacto no registrado");
            }
        });
        pBuscar.add(txtBuscar);
        pBuscar.add(btnBuscar);

        pantallaInicial.add(pBuscar, BorderLayout.SOUTH);

        // Segunda pantalla para el chat
        pChat = new JPanel(new BorderLayout());

        cabeceraChat = new JPanel(new BorderLayout());
        cabeceraChat.setPreferredSize(new Dimension(1000, 70));
        cabeceraChat.setBackground(new Color(200, 162, 200));

        Volver = new JButton("<- Volver");
        Volver.setFont(new Font("Arial", Font.BOLD, 14));
        cabeceraChat.add(Volver, BorderLayout.WEST);

        Contactos = new JLabel("Nombre del Contacto", JLabel.CENTER);
        Contactos.setFont(new Font("Arial", Font.BOLD, 22));
        cabeceraChat.add(Contactos, BorderLayout.CENTER);

        // Boton info
        JButton Info = new JButton("Info");
        cabeceraChat.add(Info, BorderLayout.EAST);

        pChat.add(cabeceraChat, BorderLayout.NORTH);

        // Área de los mensajes
        JPanel pMessages = new JPanel();
        pMessages.setBackground(new Color(229, 221, 213));

        // SCROLL MENSAJES
        pMessages.setLayout(new BoxLayout(pMessages, BoxLayout.Y_AXIS));
        scrollMensajes = new JScrollPane(pMessages);
        pChat.add(scrollMensajes, BorderLayout.CENTER);
        this.pMensajes = pMessages;

        // Barra para escribir
        pChatInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pChatInput.setPreferredSize(new Dimension(1000, 80));
        pChatInput.setBackground(new Color(240, 242, 245));

        // Crea una fuente específica para los emojis
        Font fuenteEmoji = new Font("Segoe UI Emoji", Font.PLAIN, 16);

// Aplícala al JTextField donde escribes
        JTextField Mensaje = new JTextField(30);
        Mensaje.setFont(fuenteEmoji);
        Enviar = new JButton("Enviar");

        JButton Zumbido = new JButton("Zumbido");
        JButton Archivos = new JButton("Archivos");

        String[] listaEmojis = {"😊", "😂", "❤️", "👍", "😢"};
        emojis = new JComboBox<>(listaEmojis);

        String[] listaFuentes = {"Arial", "Courier New", "Times New Roman"};
        fuentes = new JComboBox<>(listaFuentes);

        pChatInput.add(emojis);
        pChatInput.add(Mensaje);
        pChatInput.add(fuentes);
        pChatInput.add(Zumbido);
        pChatInput.add(Archivos);
        pChatInput.add(Enviar);
        pChat.add(pChatInput, BorderLayout.SOUTH);

        // EMOJIS
        emojis.addActionListener(e -> {
            String emoji = emojis.getSelectedItem().toString();
            Mensaje.setText(Mensaje.getText() + emoji);
            Mensaje.requestFocus();
        });

        // ENVIAR MENSAJE
        Enviar.addActionListener(e -> enviarMensaje(Mensaje));
        Mensaje.addActionListener(e -> enviarMensaje(Mensaje));

        // INFO CONTACTO
        Info.addActionListener(e -> {
            String nombre = Contactos.getText();
            String codigo = "USR-" + nombre.substring(0, 2).toUpperCase() + "01";

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Nombre: " + nombre
                    + "\nEstado: Disponible"
                    + "\nCódigo: " + codigo);
        });

        // CAMBIO DE PANTALLA
        listaContactos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String seleccionado = listaContactos.getSelectedValue();
                if (seleccionado != null) {
                    Contactos.setText(seleccionado);
                    cardLayout.show(pContenedor, "CHAT");
                }
            }
        });

        Volver.addActionListener(e -> {
            listaContactos.clearSelection();
            cardLayout.show(pContenedor, "LISTA");
        });

        pContenedor.add(pantallaInicial, "LISTA");
        pContenedor.add(pChat, "CHAT");

        cardLayout.show(pContenedor, "LISTA");
    }

    public void conectarAlServidor() {
        try {
            // 1. Nos conectamos al "puerto" de tu computadora (localhost) donde vive el Servidor
            socket = new Socket("localhost", 9090);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            salida = new PrintWriter(new java.io.OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // 2. Creamos un Hilo (Thread) que estará siempre escuchando si llega un mensaje
            Thread hiloEscucha = new Thread(() -> {
                String mensajeRecibido;
                try {
                    // Mientras el servidor nos siga mandando cosas...
                    while ((mensajeRecibido = entrada.readLine()) != null) {
                        // ... mandamos ese texto a tu método para dibujar la burbuja izquierda
                        recibirMensaje(mensajeRecibido);
                    }
                } catch (IOException ex) {
                    System.out.println("Desconectado del servidor.");
                }
            });
            hiloEscucha.start(); // Arrancamos la oreja virtual

        } catch (IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor. ¿Está encendido?", "Error de red", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void enviarMensaje(JTextField Mensaje) {
        String texto = Mensaje.getText();
        String tipoFuente = fuentes.getSelectedItem().toString();

        // Lista de emojis disponibles
        String[] listaEmojis = {"😊", "😂", "❤️", "👍", "😢"};

        // 👇 Detectar si el texto contiene algún emoji
        for (String emoji : listaEmojis) {
            if (texto.contains(emoji)) {
                tipoFuente = "Segoe UI Emoji";
                break;
            }
        }

        if (!texto.isEmpty()) {
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel.setBackground(new Color(229, 221, 213));

            JPanel burbuja = new JPanel();
            burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
            burbuja.setBackground(new Color(220, 248, 198));

            JLabel mensaje = new JLabel(texto);
            mensaje.setFont(new Font(tipoFuente, Font.PLAIN, 16));

            JLabel horaLabel = new JLabel(hora);
            horaLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            horaLabel.setForeground(Color.GRAY);

            burbuja.add(mensaje);
            burbuja.add(horaLabel);
            panel.add(burbuja);

            pMensajes.add(panel);
            pMensajes.revalidate();
            pMensajes.repaint();

            SwingUtilities.invokeLater(() -> {
                JScrollBar vertical = scrollMensajes.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });

            Mensaje.setText("");

            // 👇 Enviar texto + fuente
            if (salida != null) {
                salida.println(texto + "||" + tipoFuente);
            }
        }
    }

    
    public void recibirMensaje(String textoRecibido) {

        // Separar texto y fuente
        String[] partes = textoRecibido.split("\\|\\|");

        String texto = partes[0];
        String fuente = "Arial"; // por defecto

        if (partes.length > 1) {
            fuente = partes[1];
        }

        // Lista de emojis
        String[] listaEmojis = {"😊", "😂", "❤️", "👍", "😢"};

        // 👇 Forzar fuente emoji si el texto contiene alguno
        for (String emoji : listaEmojis) {
            if (texto.contains(emoji)) {
                fuente = "Segoe UI Emoji";
                break;
            }
        }

        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(229, 221, 213));

        JPanel burbuja = new JPanel();
        burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
        burbuja.setBackground(Color.WHITE);

        JLabel mensaje = new JLabel(texto);
        mensaje.setFont(new Font(fuente, Font.PLAIN, 16));

        JLabel horaLabel = new JLabel(hora);
        horaLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        horaLabel.setForeground(Color.GRAY);

        burbuja.add(mensaje);
        burbuja.add(horaLabel);
        panel.add(burbuja);

        pMensajes.add(panel);
        pMensajes.revalidate();
        pMensajes.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollMensajes.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    
    private void cerrarSesion() {
        try {
            // 1. Avisar al servidor o cerrar los flujos
            if (salida != null) {
                salida.close();
            }
            if (entrada != null) {
                entrada.close();
            }
            if (socket != null) {
                socket.close();
            }

            System.out.println("Conexiones cerradas correctamente.");
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        } finally {
            // 2. Regresar a la pantalla de Inicio
            new Inicio();

            // 3. Destruir la ventana de Chat por completo
            this.dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == juego) {
            new FrameJuego();
        }
    }
    /*public static void main(String[] args) {
        new Chat();
    }*/
}
