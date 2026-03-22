package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

public class Chat extends JFrame {
    private CardLayout cardLayout;
    private JPanel pContenedor, pantallaInicial, Separador, pChat, pChatInput, cabeceraChat, pMensajes;
    private JLabel MiPerfil, Titulo, Contactos;
    private JPopupMenu Opciones;
    private JButton Volver, Enviar;
    private JTextField Mensajes;
    private JScrollPane scrollContactos;

    // Nuevo
    private JScrollPane scrollMensajes;
    private JComboBox<String> emojis;
    private JComboBox<String> fuentes;

    public Chat(){
        configFrame();
        initComponents();
        setVisible(true);
    }
    
    public void configFrame(){
        setSize(new Dimension(1000, 900));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        cardLayout = new CardLayout();
        pContenedor = new JPanel(cardLayout);
        setContentPane(pContenedor);
    }
    
    public void initComponents(){
        JPanel pantallaInicial = new JPanel(new BorderLayout());
        
        // Cabecera superior
        JPanel Separador=new JPanel(new BorderLayout());
        Separador.setPreferredSize(new Dimension(1000, 70));
        Separador.setBackground(new Color(200, 162, 200)); 
        
        JLabel Titulo = new JLabel("  WhatsChafa");
        Titulo.setFont(new Font("Arial", Font.BOLD, 22));
        Titulo.setForeground(Color.WHITE);
        Separador.add(Titulo, BorderLayout.WEST);
        
        JLabel MiPerfil = new JLabel("Mi Perfil \u2630   ");
        MiPerfil.setFont(new Font("Arial", Font.BOLD, 25));
        MiPerfil.setForeground(Color.WHITE);
        Separador.add(MiPerfil, BorderLayout.EAST);
        
        JPopupMenu Opciones = new JPopupMenu();
        Opciones.add(new JMenuItem("Nuevo grupo"));
        Opciones.add(new JMenuItem("Ajustes"));
        Opciones.addSeparator();
        Opciones.add(new JMenuItem("Cerrar sesión"));
        
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

        JScrollPane scrollContactos = new JScrollPane(listaContactos);
        pantallaInicial.add(scrollContactos, BorderLayout.CENTER);
        
        
        // Segunda pantalla para el chat
        JPanel pChat = new JPanel(new BorderLayout());
        
        JPanel cabeceraChat = new JPanel(new BorderLayout());
        cabeceraChat.setPreferredSize(new Dimension(1000, 70));
        cabeceraChat.setBackground(new Color(200, 162, 200));
        
        JButton Volver = new JButton("<- Volver");
        Volver.setFont(new Font("Arial", Font.BOLD, 14));
        cabeceraChat.add(Volver, BorderLayout.WEST);
        
        JLabel Contactos = new JLabel("Nombre del Contacto", JLabel.CENTER);
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
        JPanel pChatInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pChatInput.setPreferredSize(new Dimension(1000, 80));
        pChatInput.setBackground(new Color(240, 242, 245));
        
        JTextField Mensaje = new JTextField(30);
        Mensaje.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton Enviar = new JButton("Enviar");

        
        JButton Zumbido = new JButton("Zumbido");
        JButton Archivos = new JButton("Archivos");

        String[] listaEmojis = {"😊","😂","❤️","👍","😢"};
        emojis = new JComboBox<>(listaEmojis);

        String[] listaFuentes = {"Arial","Courier New","Times New Roman"};
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
            String codigo = "USR-" + nombre.substring(0,2).toUpperCase() + "01";

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Nombre: " + nombre +
                    "\nEstado: Disponible" +
                    "\nCódigo: " + codigo);
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
    
    // MENSAJE DERECHA
    public void enviarMensaje(JTextField Mensaje){
        String texto = Mensaje.getText();
        String tipoFuente = fuentes.getSelectedItem().toString();

        if (!texto.isEmpty()) {
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panel.setBackground(new Color(229,221,213));

            JPanel burbuja = new JPanel();
            burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
            burbuja.setBackground(new Color(220,248,198));

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

            JScrollBar vertical = scrollMensajes.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());

            Mensaje.setText("");

            // SOCKET
            // out.println(texto);
        }
    }
    
    // MENSAJE IZQUIERDA
    public void recibirMensaje(String texto){
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(229,221,213));

        JPanel burbuja = new JPanel();
        burbuja.setLayout(new BoxLayout(burbuja, BoxLayout.Y_AXIS));
        burbuja.setBackground(Color.WHITE);

        JLabel mensaje = new JLabel(texto);

        JLabel horaLabel = new JLabel(hora);
        horaLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        horaLabel.setForeground(Color.GRAY);

        burbuja.add(mensaje);
        burbuja.add(horaLabel);
        panel.add(burbuja);

        pMensajes.add(panel);
        pMensajes.revalidate();

        JScrollBar vertical = scrollMensajes.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    public static void main(String[] args) {
        new Chat();
    }
}