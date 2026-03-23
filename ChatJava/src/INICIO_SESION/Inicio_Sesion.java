package INICIO_SESION;

import GUI.Chat;
import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;

public class Inicio_Sesion extends JFrame implements ActionListener {

    private JPanel central, sur;
    private JButton atras, iniciar;
    private JLabel tx1, tx2, tx3;
    private JPasswordField contra1, contra2;
    private JTextField user;

    public Inicio_Sesion() {
        configFrame();
        initComponents();
        setVisible(true);
    }

    public void configFrame() {
        setSize(new Dimension(400, 400));
        setLocationRelativeTo(null);
        setTitle("Iniciar Sesión al Chat");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    public void initComponents() {
        // Creamos un solo panel y le asignamos GridBagLayout
        central = new JPanel();
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
        tx2 = new JLabel("Ingresa tu contraseña:", JLabel.CENTER);
        tx2.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridy = 2; // Fila 2
        central.add(tx2, gbc);

        // 4. Ingresar contraseña
        contra1 = new JPasswordField();
        contra1.setPreferredSize(new Dimension(300, 30));
        gbc.gridy = 3; // Fila 3
        central.add(contra1, gbc);

        //5. Crear Panel para los botones
        sur = new JPanel();
        sur.setLayout(new BorderLayout());

        //6. Crear botón atrás
        atras = new JButton("Atrás");
        atras.setFont(new Font("Arial", Font.BOLD, 15));
        atras.setPreferredSize(new Dimension(100, 50));
        atras.addActionListener(this);
        sur.add(atras, BorderLayout.WEST);

        //7. Crar botón iniciar
        iniciar = new JButton("Iniciar");
        iniciar.setFont(new Font("Arial", Font.BOLD, 15));
        iniciar.setPreferredSize(new Dimension(100, 50));
        iniciar.addActionListener(this);
        sur.add(iniciar, BorderLayout.EAST);

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

            // Botón INICIAR SESIÓN (tu variable se llama "registrarse")
        } else if (e.getSource() == iniciar) {

            String nombreUsuario = user.getText();
            String password = new String(contra1.getPassword());

            // Validar campos vacíos
            if (nombreUsuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa tu usuario y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Usamos nuestra nueva clase ConexionDB
            try (Connection conn = Conexion.obtenerConexion()) {

                // Si la conexión fue exitosa (no es nula)
                if (conn != null) {
                    String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nombreUsuario);
                    pstmt.setString(2, password);

                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "¡Bienvenido al chat, " + nombreUsuario + "!", "Ingreso Exitoso", JOptionPane.INFORMATION_MESSAGE);

                        // AQUÍ ABRIREMOS LA VENTANA DEL CHAT PRÓXIMAMENTE
                        // new VentanaChat(nombreUsuario).setVisible(true);
                        // this.dispose();
                        new Chat();
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
                        contra1.setText(""); // Limpiamos la contraseña
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error en la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Inicio_Sesion());
    }

}
