package INICIO_SESION;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Registro extends JFrame implements ActionListener {

    private JPanel central, sur;
    private JButton atras, registrarse;
    private JLabel tx1, tx2, tx3;
    private JPasswordField contra1, contra2;
    private JTextField user;

    public Registro() {
        configFrame();
        initComponents();
	this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Logo_Chat.jpg")).getImage());
        setVisible(true);
    }

    public void configFrame() {
        setSize(new Dimension(400, 400));
        setLocationRelativeTo(null);
        setTitle("CHARLEMOS-Registro");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void initComponents() {
        // Creamos un solo panel y le asignamos GridBagLayout
        central = new JPanel();
        central.setBackground(new Color(229, 221, 213));
        central.setLayout(new GridBagLayout());

        // GridBagConstraints nos permite controlar la posición y márgenes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Todo irá en la misma columna (columna 0)

        // Insets(arriba, izquierda, abajo, derecha) -> Controla el espacio entre elementos
        gbc.insets = new Insets(10, 0, 10, 0);

        // 1. Etiqueta de Ingresa tu usuario
        tx1 = new JLabel("Ingresa tu usuario:", JLabel.CENTER);
        tx1.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridy = 0; // Fila 0
        central.add(tx1, gbc);

        // 2. Ingresar espacio para usuarios
        user = new JTextField();
        user.setPreferredSize(new Dimension(300, 30));
        gbc.gridy = 1; // Fila 1
        central.add(user, gbc);

        //3. Etiqueta para contraseña
        tx2 = new JLabel("Ingresa la contraseña a utilizar:", JLabel.CENTER);
        tx2.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridy = 2; // Fila 2
        central.add(tx2, gbc);

        // 4. Ingresar contraseña
        contra1 = new JPasswordField();
        contra1.setPreferredSize(new Dimension(300, 30));
        gbc.gridy = 3; // Fila 3
        central.add(contra1, gbc);

        //5. Etiqueta de confirmación
        tx3 = new JLabel("Confirmar contraseña:", JLabel.CENTER);
        tx3.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridy = 4; // Fila 4
        central.add(tx3, gbc);

        // 6. Ingresar contraseña confirmada
        contra2 = new JPasswordField();
        contra2.setPreferredSize(new Dimension(300, 30));
        gbc.gridy = 5; // Fila 5
        central.add(contra2, gbc);

        //7. Crear Panel para los botones
        sur = new JPanel();
        sur.setBackground(new Color(200, 162, 200));
        sur.setLayout(new BorderLayout());

        //8. Crear botón atrás
        atras = new JButton("Atrás");
        atras.setFont(new Font("Arial", Font.BOLD, 15));
        atras.setPreferredSize(new Dimension(100, 50));
        atras.addActionListener(this);
        sur.add(atras, BorderLayout.WEST);

        //9. Crar botón iniciar
        registrarse = new JButton("Registrar");
        registrarse.setFont(new Font("Arial", Font.BOLD, 15));
        registrarse.setPreferredSize(new Dimension(100, 50));
        registrarse.addActionListener(this);
        sur.add(registrarse, BorderLayout.EAST);

        // Finalmente, agregamos este bloque centralizado al centro de la ventana
        add(central, BorderLayout.CENTER);
        add(sur, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Botón ATRÁS
        if (e.getSource() == atras) {
            new Inicio();
            this.dispose();

            // Botón REGISTRAR
        } else if (e.getSource() == registrarse) {

            String nombreUsuario = user.getText();
            String pass1 = new String(contra1.getPassword());
            String pass2 = new String(contra2.getPassword());

            // Generar código automático
            String codigo = "USR-"
                    + nombreUsuario.substring(0, Math.min(2, nombreUsuario.length())).toUpperCase()
                    + (int) (Math.random() * (999 - 1) + 1);

            // Validar campos vacíos
            if (nombreUsuario.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, llena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar que las contraseñas coincidan
            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = Conexion.obtenerConexion()) {

                if (conn != null) {
                    String sql = "INSERT INTO usuarios (usuario, contrasena, codigo, estado, conectado) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);

                    pstmt.setString(1, nombreUsuario);
                    pstmt.setString(2, pass1);
                    pstmt.setString(3, codigo);
                    pstmt.setString(4, "Disponible");
                    pstmt.setString(5, "Conectado");

                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "¡Usuario registrado con éxito!");

                    // Limpiar campos
                    user.setText("");
                    contra1.setText("");
                    contra2.setText("");
                }

            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) {
                    JOptionPane.showMessageDialog(this, "Usuario o código ya existente.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            new Inicio();
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Registro());
    }

}
