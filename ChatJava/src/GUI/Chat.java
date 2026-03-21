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

public class Chat extends JFrame {
    private CardLayout cardLayout;
    private JPanel pContenedor, pantallaInicial, Separador, pChat, pChatInput, cabeceraChat, pMensajes;
    private JLabel MiPerfil, Titulo, Contactos;
    private JPopupMenu Opciones;
    private JButton Volver, Enviar;
    private JTextField Mensajes;
    private JScrollPane scrollContactos;
    
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
        
        // Cabecera superior (Verde estilo WhatsApp)
        JPanel Separador=new JPanel(new BorderLayout());
        Separador.setPreferredSize(new Dimension(1000, 70));
        Separador.setBackground(new Color(200, 162, 200)); 
        
        JLabel Titulo = new JLabel("  WhatsChafa");
        Titulo.setFont(new Font("Arial", Font.BOLD, 22));
        Titulo.setForeground(Color.WHITE);
        Separador.add(Titulo, BorderLayout.WEST);
        
        //MI PERFIL
        
        JLabel MiPerfil = new JLabel("Mi Perfil \u2630   "); // El símbolo es un icono de menú de 3 rayas
        MiPerfil.setFont(new Font("Arial", Font.BOLD, 25));
        MiPerfil.setForeground(Color.WHITE);
        Separador.add(MiPerfil, BorderLayout.EAST);
        
        // Creamos el menú pequeño que saldrá al dar clic
        JPopupMenu Opciones = new JPopupMenu();
        Opciones.add(new JMenuItem("Nuevo grupo"));
        Opciones.add(new JMenuItem("Ajustes"));
        Opciones.addSeparator(); // Una rayita divisoria
        Opciones.add(new JMenuItem("Cerrar sesión"));
        
        // Le decimos que muestre el menú cuando alguien haga clic en "Mi Perfil"
        MiPerfil.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Aparece justo debajo del texto
                Opciones.show(MiPerfil, e.getX() - 80, e.getY() + 20);
            }
        });
        
        pantallaInicial.add(Separador, BorderLayout.NORTH);
        
        // Lista central de contactos
        String[] contactos = {"América", "Alexa", "Ramón", "Erick", "Diego", "Paco", "Justin", "Dani", "Leti", "Zayra", "Linda"};
        JList<String> listaContactos = new JList<>(contactos);
        listaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaContactos.setFont(new Font("Arial", Font.PLAIN, 24));
        listaContactos.setFixedCellHeight(80); // Contactos más grandes y espaciados

        
        JScrollPane scrollContactos = new JScrollPane(listaContactos);
        pantallaInicial.add(scrollContactos, BorderLayout.CENTER);
        
        
        //Segunda pantalla para el chat :3
        JPanel pChat = new JPanel(new BorderLayout());
        
        // CABECERA 
        JPanel cabeceraChat = new JPanel(new BorderLayout());
        cabeceraChat.setPreferredSize(new Dimension(1000, 70));
        cabeceraChat.setBackground(new Color(200, 162, 200));
        
        // Botón para regresar a la lista de contactos
        JButton Volver = new JButton("<- Volver");
        Volver.setFont(new Font("Arial", Font.BOLD, 14));
        cabeceraChat.add(Volver, BorderLayout.WEST);
        
        JLabel Contactos = new JLabel("Nombre del Contacto", JLabel.CENTER);
        Contactos.setFont(new Font("Arial", Font.BOLD, 22));
        cabeceraChat.add(Contactos, BorderLayout.CENTER);
        
        pChat.add(cabeceraChat, BorderLayout.NORTH);
        
        // Área de los mensajes
        JPanel pMessages = new JPanel();
        pMessages.setBackground(new Color(229, 221, 213)); 
        pChat.add(pMessages, BorderLayout.CENTER);
        
        // Barra para escribir
        JPanel pChatInput = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pChatInput.setPreferredSize(new Dimension(1000, 80));
        pChatInput.setBackground(new Color(240, 242, 245));
        
        JTextField Mensaje = new JTextField(60);
        Mensaje.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton Enviar = new JButton("Enviar");
        
        pChatInput.add(Mensaje);
        pChatInput.add(Enviar);
        pChat.add(pChatInput, BorderLayout.SOUTH);
        
        
       // CAMBIO DE PANTALLA
       
        listaContactos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String seleccionado = listaContactos.getSelectedValue();
                if (seleccionado != null) {
                    Contactos.setText(seleccionado); 
                    cardLayout.show(pContenedor, "CHAT"); // Muestra la carta del chat
                }
            }
        });
        
        // BOTÓN VOLVER
        
        Volver.addActionListener(e -> {
            listaContactos.clearSelection(); 
            cardLayout.show(pContenedor, "LISTA"); // Muestra la carta de la lista principal
        });
        
        // Finalmente, agregamos las dos "cartas" a la ventana principal
        pContenedor.add(pantallaInicial, "LISTA");
        pContenedor.add(pChat, "CHAT");
        
        // Elegimos que la ventana arranque mostrando la carta "LISTA"
        cardLayout.show(pContenedor, "LISTA");
    }
    
    public static void main(String[] args) {
        new Chat();
    }
}