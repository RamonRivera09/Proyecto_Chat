package GUI;

import INICIO_SESION.Conexion; // Asegúrate de que esta sea tu clase de conexión
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Correo extends JFrame implements ActionListener {

    private JPanel pPrincipal, pArriba, pCentro;
    private JTextField txtCorreo; // Le cambié el nombre de 'codigo' a 'txtCorreo' para ser más claros
    private JButton volver, agregar;
    private JLabel lblprincipal;
    private String usuarioActual; // Para saber a quién le estamos registrando el correo

    public Correo(String usuario) {
        this.usuarioActual = usuario;
        configFrame();
        initComponents();
        setVisible(true);
    }

    public void configFrame() {
        setSize(new Dimension(500, 220));
        setTitle("CHARLEMOS - Registrar Correo");
        // Nota: Asegúrate de que la ruta de la imagen sea correcta
        try {
            this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Logo_Chat.jpg")).getImage());
        } catch (Exception e) {
            System.out.println("No se encontró el logo");
        }
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void initComponents() {
        pPrincipal = new JPanel(new BorderLayout());

        // Panel superior
        pArriba = new JPanel(new GridBagLayout());
        GridBagConstraints d = new GridBagConstraints();
        pArriba.setBackground(new Color(200, 162, 200));

        volver = new JButton("<- Volver");
        volver.addActionListener(this);
        d.gridx = 0;
        d.gridy = 0;
        d.insets = new Insets(10, 10, 10, 10);
        pArriba.add(volver, d);

        lblprincipal = new JLabel("Ingresa el correo a registrar:");
        lblprincipal.setForeground(Color.WHITE);
        lblprincipal.setFont(new Font("Arial", Font.BOLD, 15));
        d.gridx = 1;
        pArriba.add(lblprincipal, d);

        pPrincipal.add(pArriba, BorderLayout.NORTH);

        // Panel central
        pCentro = new JPanel(new GridBagLayout());
        pCentro.setBackground(new Color(229, 221, 213));
        GridBagConstraints c = new GridBagConstraints();

        txtCorreo = new JTextField(20);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        pCentro.add(txtCorreo, c);

        agregar = new JButton("Registrar Correo");
        agregar.addActionListener(this);
        c.gridy = 1;
        pCentro.add(agregar, c);

        pPrincipal.add(pCentro, BorderLayout.CENTER);
        add(pPrincipal);
    }

    // --- MÉTODO DE VALIDACIÓN ---
    public boolean esCorreoValido(String correo) {
        // Expresión regular estándar para correos
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    // --- MÉTODO PARA GUARDAR EN BD ---
    public void registrarEnBD(String correo) {
        try (Connection conn = Conexion.obtenerConexion()) {
            // Suponiendo que tu tabla se llama 'usuarios' y tiene una columna 'correo'
            String sql = "UPDATE usuarios SET correo = ? WHERE usuario = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, usuarioActual);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "¡Correo registrado con éxito!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el usuario.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == volver) {
            this.dispose();
        }

        if (e.getSource() == agregar) {
            String correoIngresado = txtCorreo.getText().trim();

            // 1. Validar si está vacío
            if (correoIngresado.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo está vacío. Por favor ingresa un correo.");
                return;
            }

            // 2. Validar formato de correo
            if (esCorreoValido(correoIngresado)) {
                registrarEnBD(correoIngresado);
            } else {
                JOptionPane.showMessageDialog(this, "Formato de correo inválido. Ejemplo: usuario@gmail.com", 
                        "Error de formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}