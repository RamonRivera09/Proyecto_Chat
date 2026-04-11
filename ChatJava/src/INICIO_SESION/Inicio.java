package INICIO_SESION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Inicio extends JFrame implements ActionListener {
    
    private JLabel bienvenida;
    private JButton registrar, iniciar;
    private JPanel panelCentral, panelPrimero;
    
    public Inicio() {
        configFrame();
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Logo_Chat.jpg")).getImage());
        setVisible(true);
    }
    
    public void configFrame() {
        setSize(new Dimension(400, 400));
        setLocationRelativeTo(null);
        setTitle("Charlemos-Inicio");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // El JFrame mantiene BorderLayout, lo cual está perfecto
        setLayout(new BorderLayout());
    }
    
    public void initComponents() {
        // Creamos un solo panel y le asignamos GridBagLayout
        panelPrimero=new JPanel();
        panelPrimero.setLayout(new BorderLayout());
        panelPrimero.setBackground(new Color(200, 162, 200));
        panelCentral = new JPanel();
        panelCentral.setLayout(new GridBagLayout());
        panelCentral.setBackground(new Color(229, 221, 213));
        

        // GridBagConstraints nos permite controlar la posición y márgenes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Todo irá en la misma columna (columna 0)

        // Insets(arriba, izquierda, abajo, derecha) -> Controla el espacio entre elementos
        gbc.insets = new Insets(10, 0, 10, 0);

        // 1. Etiqueta de Bienvenida
        bienvenida = new JLabel("Bienvenido a CHARLEMOS.", JLabel.CENTER);
        bienvenida.setFont(new Font("Arial", Font.BOLD, 20));
        bienvenida.setForeground(Color.WHITE);
        
        panelPrimero.add(bienvenida);
        /*gbc.gridy = 0; // Fila 0
        panelCentral.add(bienvenida, gbc);*/

        // 2. Botón Iniciar Sesión
        iniciar = new JButton("Iniciar Sesión");
        iniciar.setFont(new Font("Arial", Font.BOLD, 15));
        iniciar.setPreferredSize(new Dimension(300, 50));
        iniciar.addActionListener(this);
        gbc.gridy = 0; // Fila 1
        panelCentral.add(iniciar, gbc);

        // 3. Botón Registrarte
        registrar = new JButton("Regístrate");
        registrar.setFont(new Font("Arial", Font.BOLD, 15));
        registrar.setPreferredSize(new Dimension(300, 50));
        registrar.addActionListener(this);
        gbc.gridy = 1; // Fila 2
        panelCentral.add(registrar, gbc);

        // Finalmente, agregamos este bloque centralizado al centro de la ventana
        
        add(panelPrimero, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == iniciar) {
            new Inicio_Sesion(); // Abre la ventana de iniciar sesión
            this.dispose();      // Cierra la ventana actual
        }        
        if (e.getSource() == registrar) {
            new Registro();      // Abre la ventana de registro
            this.dispose();      // Cierra la ventana actual
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Inicio());
    }
    
}
