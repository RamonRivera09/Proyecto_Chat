package GUI;

import INICIO_SESION.Conexion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AgregarContacto extends JFrame implements ActionListener {

    private JPanel pPrincipal, pArriba, pCentro;
    private JTextField codigo;
    private JButton volver, agregar;
    private JLabel lblprincipal;
    private String codigoUsuarioActual; // código del usuario logueado

    private DefaultListModel<String> modeloContactos;
    private Chat chat;

    public AgregarContacto(String codigoUsuarioActual, DefaultListModel<String> modeloContactos, Chat chat) {
        this.codigoUsuarioActual = codigoUsuarioActual;
        this.modeloContactos = modeloContactos;
        this.chat = chat;
        configFrame();
        initComponents();
        setVisible(true);
    }

    public void configFrame() {
        setSize(new Dimension(500, 200));
        setTitle("CHARLEMOS - Agregar contacto");
        this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Logo_Chat.jpg")).getImage());
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

        lblprincipal = new JLabel("Ingresa el código de usuario de tu contacto:");
        lblprincipal.setForeground(Color.WHITE);
        lblprincipal.setFont(new Font("Arial", Font.BOLD, 15));
        d.gridx = 1;
        pArriba.add(lblprincipal, d);

        pPrincipal.add(pArriba, BorderLayout.NORTH);

        // Panel central
        pCentro = new JPanel(new GridBagLayout());
        pCentro.setBackground(new Color(229, 221, 213));
        GridBagConstraints c = new GridBagConstraints();

        codigo = new JTextField(20);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        pCentro.add(codigo, c);

        agregar = new JButton("Agregar contacto");
        agregar.addActionListener(this);
        c.gridy = 1;
        pCentro.add(agregar, c);

        pPrincipal.add(pCentro, BorderLayout.CENTER);
        add(pPrincipal);
    }

    private String obtenerNombreDesdeCodigo(String codigo, Connection conn) throws SQLException {
        String sql = "SELECT usuario FROM usuarios WHERE codigo = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, codigo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("usuario");
        }
        return codigo; // fallback
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == volver) {
            this.dispose();
        } else if (e.getSource() == agregar) {
            agregarContacto();
        }
    }

    private void agregarContacto() {
        String codigoIngresado = codigo.getText().trim();

        if (codigoIngresado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes ingresar el código del usuario");
            return;
        }

        if (codigoIngresado.equals(codigoUsuarioActual)) {
            JOptionPane.showMessageDialog(this, "No puedes agregarte a ti mismo");
            return;
        }

        try (Connection conn = Conexion.obtenerConexion()) {
            // Verificar que el usuario existe
            String sqlVerificar = "SELECT id FROM usuarios WHERE codigo = ?";
            PreparedStatement psVerificar = conn.prepareStatement(sqlVerificar);
            psVerificar.setString(1, codigoIngresado);
            ResultSet rs = psVerificar.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "El usuario no existe");
                return;
            }

            int idContacto = rs.getInt("id");

            // Obtener id del usuario actual
            String sqlIdActual = "SELECT id FROM usuarios WHERE codigo = ?";
            PreparedStatement psIdActual = conn.prepareStatement(sqlIdActual);
            psIdActual.setString(1, codigoUsuarioActual);
            ResultSet rsActual = psIdActual.executeQuery();
            if (!rsActual.next()) {
                JOptionPane.showMessageDialog(this, "Error: usuario actual no encontrado");
                return;
            }
            int idUsuarioActual = rsActual.getInt("id");

            // Insertar en contactos: usuario -> contacto
            String sqlInsert = "INSERT IGNORE INTO contactos (usuario_id, contacto_id, estado) VALUES (?, ?, 'aceptado')";
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setInt(1, idUsuarioActual);
            psInsert.setInt(2, idContacto);
            psInsert.executeUpdate();

            // Insertar en contactos: contacto -> usuario (para que ambos se vean)
            psInsert.setInt(1, idContacto);
            psInsert.setInt(2, idUsuarioActual);
            psInsert.executeUpdate();
            String nombreNuevoContacto = obtenerNombreDesdeCodigo(codigoIngresado, conn); // obtiene nombre del usuario agregado
            modeloContactos.addElement(nombreNuevoContacto); // actualiza la lista de contactos en la ventana actual

            // Llamada a método de notificación en Chat
            chat.enviarNotificacion(codigoUsuarioActual, codigoIngresado, "te ha agregado como contacto");

            // Después de insertar en contactos
            JOptionPane.showMessageDialog(this, "Contacto agregado correctamente");
            this.dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar contacto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}