package GUI;

//import Snake.FrameJuego;
import INICIO_SESION.Conexion;
import INICIO_SESION.Inicio;
import SERVIDOR.Manejador_Cliente;
import static SERVIDOR.Manejador_Cliente.usuariosConectados;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStreamWriter;
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
import javax.swing.ImageIcon;
import javax.swing.ListModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;

public class Chat extends JFrame implements ActionListener {

    // --- Variables de Red ---
    private Socket socket;
    private BufferedReader entrada; // Para escuchar al servidor
    public PrintWriter salida;     // Para enviarle mensajes al servidor
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

    private JMenuItem juego, correo, contacto;
    private JTextField txtBuscar;
    private JButton btnBuscar;

    private String usuarioLogueado; // Variable para guardar el nombre
    DefaultListModel<String> modeloContactos = new DefaultListModel<>();
    private DefaultListModel<String> modeloNotificaciones = new DefaultListModel<>();
    private JList<String> listaNotificaciones = new JList<>(modeloNotificaciones);

    public Chat(String usuario) {
        this.usuarioLogueado = usuario; // Guardamos el nombre
        configFrame();
        initComponents();
        cargarContactosDesdeBD();
        cargarContactos();
        cargarNotificacionesDesdeBD();

        // Opcional: Cambiar el título de la ventana con el nombre
        setTitle("CHARLEMOS - Sesión de: " + usuarioLogueado);

        conectarAlServidor();
        this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Logo_Chat.jpg")).getImage());
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
        PantallaActualizar ajuste = new PantallaActualizar(usuarioLogueado, cardLayout, pContenedor);
        pContenedor.add(vistaPerfil, "PERFIL");
        pContenedor.add(ajuste, "AJUSTE");

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
        JMenuItem notificaciones = new JMenuItem("Notificaciones");
        Opciones.add(notificaciones);
        notificaciones.addActionListener(e -> mostrarNotificaciones());
        correo = new JMenuItem("Registrar correo");
        correo.addActionListener(this);
        Opciones.add(correo);
        juego = new JMenuItem("Jugar");
        juego.addActionListener(this);
        contacto = new JMenuItem("Contacto");
        contacto.addActionListener(e -> {
            try (Connection conn = Conexion.obtenerConexion()) {
                String sql = "SELECT codigo FROM usuarios WHERE usuario = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, usuarioLogueado);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String codigoActual = rs.getString("codigo");
                    AgregarContacto ventanacontacto = new AgregarContacto(codigoActual, modeloContactos, this);
                    ventanacontacto.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Error: no se pudo obtener tu código de usuario");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + ex.getMessage());
            }
        });
        Opciones.add(contacto);
        JMenuItem itemAjustes = new JMenuItem("Ajustes");
        itemAjustes.addActionListener(e -> {
            cardLayout.show(pContenedor, "AJUSTE");
        });
        Opciones.add(itemAjustes);
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
        JList<String> listaContactos = new JList<>(modeloContactos);
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
            String nombreSeleccionado = Contactos.getText();

            // Si no hay contacto seleccionado, no buscamos nada
            if (nombreSeleccionado.equals("Selecciona un contacto") || nombreSeleccionado.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(null, "Por favor, selecciona un contacto primero.");
                return;
            }

            // Consultamos la base de datos
            String sql = "SELECT usuario, codigo, correo FROM usuarios WHERE usuario = ?";

            try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, nombreSeleccionado);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String nombreBD = rs.getString("usuario");
                    String codigoBD = rs.getString("codigo");
                    String correoBD = rs.getString("correo");

                    // Validar si el correo es nulo o vacío
                    if (correoBD == null || correoBD.trim().isEmpty()) {
                        correoBD = "No registrado";
                    }

                    // Determinar estado (Si está en el mapa de conectados del servidor)
                    // Nota: Como estamos en el cliente, el cliente no conoce el mapa 'usuariosConectados' 
                    // directamente porque eso vive en el Servidor. 
                    // Por ahora lo pondremos como "Cargado" o puedes dejarlo fijo como "Disponible".
                    String estado = "Disponible";

                    javax.swing.JOptionPane.showMessageDialog(null,
                            "📋 INFORMACIÓN DEL CONTACTO\n"
                            + "------------------------------------------\n"
                            + "Nombre: " + nombreBD + "\n"
                            + "Código: " + codigoBD + "\n"
                            + "Correo: " + correoBD + "\n"
                            + "Estado: " + estado,
                            "Detalles de Usuario",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(null, "No se encontró información del usuario.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null, "Error al acceder a la base de datos.");
            }
        });

        // CAMBIO DE PANTALLA
        listaContactos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String seleccionado = listaContactos.getSelectedValue();
                if (seleccionado != null) {
                    Contactos.setText(seleccionado);

                    // 🚀 REEMPLAZA EL REMOVEALL() POR ESTO:
                    cargarHistorialMensajes(seleccionado);
                    // -------------------------------------------------------------

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
            socket = new Socket("localhost", 9090);
            // Usamos UTF_8 para que los emojis no se rompan
            // Al crear la entrada:
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

// Al crear la salida (fíjate en el OutputStreamWriter):
            salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // 1️⃣ IDENTIFICACIÓN: Enviamos el CÓDIGO, no el nombre.
            // Esto soluciona que las notificaciones no lleguen en tiempo real.
            String miCodigo = obtenerCodigoUsuarioActual();
            salida.println(miCodigo);
            System.out.println("Identificado en servidor con código: " + miCodigo);

            // 2️⃣ HILO DE ESCUCHA: El "oído" del cliente
            Thread hiloEscucha = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = entrada.readLine()) != null) {
                        final String mensajeRecibido = linea;

                        // Ejecutamos en el hilo de la interfaz (EDT) para evitar errores visuales
                        SwingUtilities.invokeLater(() -> {
                            // ... dentro del thread de escucha ...
                            if (mensajeRecibido.startsWith("MSG||")) {
                                String[] partes = mensajeRecibido.split("\\|\\|");
                                if (partes.length >= 4) {
                                    String emisorCod = partes[1];
                                    String texto = partes[3];
                                    String nombreEmisor = obtenerNombreDesdeCodigo(emisorCod);

                                    // Si tengo abierto el chat con esa persona, lo muestro
                                    if (Contactos.getText().equals(nombreEmisor)) {
                                        mostrarMensajeEnChat(nombreEmisor, texto, "Arial", false);
                                    }
                                    // Siempre lanzamos notificación visual
                                    agregarNotificacion(nombreEmisor, "te envió un mensaje");
                                }
                            } else if (mensajeRecibido.startsWith("NOTIF||")) {
                                String[] partes = mensajeRecibido.split("\\|\\|");
                                if (partes.length >= 4) {
                                    String nombreEmisor = obtenerNombreDesdeCodigo(partes[1]);
                                    agregarNotificacion(nombreEmisor, partes[3]);
                                    cargarContactos(); // Por si nos agregaron
                                }
                            } else if (mensajeRecibido.startsWith("OFFLINE||")) {
                                // 🚀 Manejo de usuario desconectado
                                String[] partes = mensajeRecibido.split("\\|\\|");
                                String nombreReceptor = obtenerNombreDesdeCodigo(partes[1]);

                                JOptionPane.showMessageDialog(Chat.this,
                                        "El usuario " + nombreReceptor + " está desconectado.\nEl mensaje se guardó en su historial.",
                                        "Usuario Offline", JOptionPane.INFORMATION_MESSAGE);

                                // Guardamos una notificación para que el otro la vea al volver
                                guardarNotificacionOfflineEnBD(nombreReceptor, usuarioLogueado, "te dejó un mensaje pendiente.");
                            }
                        });
                    }
                } catch (IOException ex) {
                    System.out.println("Conexión con el servidor perdida.");
                }
            });
            hiloEscucha.start();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: El servidor no responde.", "Error de Red", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarMensaje(JTextField Mensaje) {
        String texto = Mensaje.getText();
        if (texto.isEmpty()) {
            return;
        }

        String tipoFuente = fuentes.getSelectedItem().toString();

        // Detectar emojis y usar fuente específica
        String[] listaEmojis = {"😊", "😂", "❤️", "👍", "😢"};
        for (String emoji : listaEmojis) {
            if (texto.contains(emoji)) {
                tipoFuente = "Segoe UI Emoji";
            }
        }

        // Mostrar en pantalla
        mostrarMensajeEnChat(usuarioLogueado, texto, tipoFuente, true);

        // --------------- Enviar al servidor ---------------
        String receptor = Contactos.getText(); // nombre del contacto seleccionado
        String codigoReceptor = obtenerCodigoDesdeNombre(receptor);

        if (codigoReceptor != null && salida != null) {
            salida.println("MSG||" + obtenerCodigoUsuarioActual() + "||" + codigoReceptor + "||" + texto);
            guardarMensajeEnBD(usuarioLogueado, receptor, texto);
        }

        Mensaje.setText("");
    }

    private void mostrarMensajeEnChat(String emisor, String texto, String fuente, boolean esMio) {
        // 1. Crear el contenedor del mensaje (el renglón)
        JPanel panelFila = new JPanel(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT));
        panelFila.setBackground(new Color(229, 221, 213)); // Color de fondo del chat

        // 2. Crear la burbuja
        JPanel burbuja = new JPanel();
        burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
        burbuja.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Color: Verde si es mío, Blanco si es de otros
        burbuja.setBackground(esMio ? new Color(220, 248, 198) : Color.WHITE);

        // 3. Formatear el contenido
        String hora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        JLabel lblNombre = new JLabel(esMio ? "Tú" : emisor);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 10));
        lblNombre.setForeground(new Color(100, 100, 100));

        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font(fuente, Font.PLAIN, 16));

        JLabel lblHora = new JLabel(hora);
        lblHora.setFont(new Font("Arial", Font.ITALIC, 9));
        lblHora.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // 4. Armar la burbuja y añadirla al chat
        burbuja.add(lblNombre);
        burbuja.add(lblTexto);
        burbuja.add(lblHora);

        panelFila.add(burbuja);

        // 5. Actualizar la interfaz
        pMensajes.add(panelFila);
        pMensajes.revalidate();
        pMensajes.repaint();

        // Auto-scroll hacia abajo
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

    private void mostrarNotificaciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(listaNotificaciones), BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver a Contactos");
        btnVolver.addActionListener(e -> cardLayout.show(pContenedor, "LISTA"));
        panel.add(btnVolver, BorderLayout.SOUTH);

        pContenedor.add(panel, "NOTIFICACIONES");
        cardLayout.show(pContenedor, "NOTIFICACIONES");

        // Doble clic sobre la notificación abre chat
        listaNotificaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = listaNotificaciones.locationToIndex(evt.getPoint());
                    if (index >= 0) {
                        String item = modeloNotificaciones.get(index);
                        String nombreContacto = item.split(" - ")[0]; // extrae el nombre
                        abrirChatCon(nombreContacto);
                    }
                }
            }
        });
    }

    private void abrirChatCon(String nombreContacto) {
        Contactos.setText(nombreContacto);
        cardLayout.show(pContenedor, "CHAT");
    }

    public void enviarNotificacion(String codigoEmisor, String codigoReceptor, String mensaje) {
        // YA NO NECESITAS BUSCAR EL NOMBRE EN LA BD AQUÍ
        // Porque el mapa 'usuariosConectados' ahora usa CÓDIGOS como llave.

        if (usuariosConectados.containsKey(codigoReceptor)) {
            Manejador_Cliente mc = usuariosConectados.get(codigoReceptor);
            mc.salida.println("NOTIF||" + codigoEmisor + "||" + codigoReceptor + "||" + mensaje);
            System.out.println("Notificación enviada con éxito al código: " + codigoReceptor);
        } else {
            System.out.println("Usuario desconectado o código no encontrado: " + codigoReceptor);
        }
    }

    private String obtenerCodigoUsuarioActual() {
        try (Connection conn = Conexion.obtenerConexion()) {
            String sql = "SELECT codigo FROM usuarios WHERE usuario = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usuarioLogueado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("codigo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void cargarContactos() {
        try (Connection conn = Conexion.obtenerConexion()) {

            // Obtener ID del usuario actual
            String sqlId = "SELECT id FROM usuarios WHERE usuario = ?";
            PreparedStatement psId = conn.prepareStatement(sqlId);
            psId.setString(1, usuarioLogueado);
            ResultSet rsId = psId.executeQuery();

            if (!rsId.next()) {
                return;
            }

            int idUsuario = rsId.getInt("id");

            // Obtener contactos
            String sql = "SELECT u.usuario FROM contactos c "
                    + "JOIN usuarios u ON c.contacto_id = u.id "
                    + "WHERE c.usuario_id = ? AND c.estado = 'aceptado'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            modeloContactos.clear(); // limpiar lista antes

            while (rs.next()) {
                modeloContactos.addElement(rs.getString("usuario"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar contactos: " + e.getMessage());
        }
    }

    private String obtenerCodigoDesdeNombre(String nombre) {
        try (Connection conn = Conexion.obtenerConexion()) {
            String sql = "SELECT codigo FROM usuarios WHERE usuario = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("codigo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String obtenerNombreDesdeCodigo(String codigo) {
        try (Connection conn = Conexion.obtenerConexion()) {
            String sql = "SELECT usuario FROM usuarios WHERE codigo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("usuario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codigo;
    }

    // --- 1. SE ARREGLÓ PARA QUE GUARDE EN LA BASE DE DATOS ---
    private void agregarNotificacion(String nombreEmisor, String mensaje) {
        String hora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        String contenidoCompleto = nombreEmisor + " - " + hora + " : " + mensaje;

        // Lo mostramos en pantalla
        modeloNotificaciones.addElement(contenidoCompleto);

        // LO GUARDAMOS EN LA BASE DE DATOS PARA QUE NO SE BORRE
        String sql = "INSERT INTO notificaciones (receptor_id, contenido) VALUES ((SELECT id FROM usuarios WHERE usuario = ?), ?)";
        try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogueado);
            ps.setString(2, contenidoCompleto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al guardar notificación: " + e.getMessage());
        }
    }

    private void guardarMensajeEnBD(String emisor, String receptor, String contenido) {
        String sql = "INSERT INTO mensajes (emisor_id, receptor_id, contenido) VALUES ("
                + "(SELECT id FROM usuarios WHERE usuario = ?), "
                + "(SELECT id FROM usuarios WHERE usuario = ?), ?)";
        try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emisor);
            ps.setString(2, receptor);
            ps.setString(3, contenido);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void guardarNotificacionOfflineEnBD(String receptorNom, String emisorNom, String accion) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String contenido = emisorNom + " - " + hora + " : " + accion;

        String sql = "INSERT INTO notificaciones (receptor_id, contenido) VALUES "
                + "((SELECT id FROM usuarios WHERE usuario = ?), ?)";
        try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, receptorNom);
            ps.setString(2, contenido);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarHistorialMensajes(String contactoNombre) {
        pMensajes.removeAll();
        String sql = "SELECT m.contenido, u_e.usuario as emisor_nom FROM mensajes m "
                + "JOIN usuarios u_e ON m.emisor_id = u_e.id "
                + "JOIN usuarios u_r ON m.receptor_id = u_r.id "
                + "WHERE (u_e.usuario = ? AND u_r.usuario = ?) OR (u_e.usuario = ? AND u_r.usuario = ?) "
                + "ORDER BY m.fecha ASC";
        try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogueado);
            ps.setString(2, contactoNombre);
            ps.setString(3, contactoNombre);
            ps.setString(4, usuarioLogueado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boolean esMio = rs.getString("emisor_nom").equals(usuarioLogueado);
                mostrarMensajeEnChat(rs.getString("emisor_nom"), rs.getString("contenido"), "Arial", esMio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pMensajes.revalidate();
        pMensajes.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == juego) {
            new FrameJuego();
        }
        if (e.getSource() == correo) {
            Correo ventanaCorreo = new Correo(usuarioLogueado);
            ventanaCorreo.setVisible(true);
        }
    }

    private void cargarContactosDesdeBD() {
        // 1. Limpiamos la lista actual para no duplicar si se recarga
        modeloContactos.clear();

        String sql = "SELECT u.usuario FROM contactos c "
                + "JOIN usuarios u ON c.contacto_id = u.id "
                + "WHERE c.usuario_id = (SELECT id FROM usuarios WHERE codigo = ?) "
                + "AND c.estado = 'aceptado'";

        try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuarioLogueado); // usuarioLogueado es tu código (USR-XXX)
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nombreContacto = rs.getString("usuario");
                modeloContactos.addElement(nombreContacto);
            }
            System.out.println("Contactos cargados desde BD para: " + usuarioLogueado);

        } catch (SQLException e) {
            System.err.println("Error al cargar contactos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarNotificacionesDesdeBD() {
        modeloNotificaciones.clear();

        // Buscamos las notificaciones donde tú eres el receptor
        String sql = "SELECT contenido FROM notificaciones "
                + "WHERE receptor_id = (SELECT id FROM usuarios WHERE usuario = ?) "
                + "ORDER BY fecha ASC";

        try (Connection conn = Conexion.obtenerConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuarioLogueado);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Agregamos cada aviso directamente a la lista visual
                modeloNotificaciones.addElement(rs.getString("contenido"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar notificaciones: " + e.getMessage());
        }
    }
    /*public static void main(String[] args) {
        new Chat();
    }*/
}
