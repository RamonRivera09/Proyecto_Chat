package GUI;

import INICIO_SESION.Conexion;
import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PantallaActualizar extends JPanel {

    private JTextField txtNombre;
    private JComboBox<String> cmbEstado;
    private JLabel lblRutaFoto;
    private String rutaFoto = "";
    private String usuarioActual;

    public PantallaActualizar(String nombreUsuario, CardLayout cl, JPanel contenedor) {

        this.usuarioActual = nombreUsuario;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // 🔹 Cabecera
        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("<- Volver");
        btnVolver.addActionListener(e -> cl.show(contenedor, "PERFIL"));
        cabecera.add(btnVolver);

        add(cabecera, BorderLayout.NORTH);

        // 🔹 Cuerpo
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // 📸 Foto
        JButton btnSeleccionarFoto = new JButton("Seleccionar Foto");
        lblRutaFoto = new JLabel("Ninguna imagen seleccionada");

        btnSeleccionarFoto.addActionListener(e -> seleccionarFoto());

        // 👤 Nombre de usuario
        txtNombre = new JTextField(15);
        txtNombre.setText(nombreUsuario);

        // 🔹 Estado
        String[] estados = {"Disponible", "Ocupado", "Ausente", "No Disponible", "Invisible"};
        cmbEstado = new JComboBox<>(estados);
        cmbEstado.setSelectedIndex(0); // Por defecto Disponible

        // 💾 Botón Guardar
        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.addActionListener(e -> actualizarPerfil());

        // Agregar componentes al panel
        cuerpo.add(new JLabel("Foto de perfil:"));
        cuerpo.add(btnSeleccionarFoto);
        cuerpo.add(lblRutaFoto);

        cuerpo.add(Box.createRigidArea(new Dimension(0, 20)));

        cuerpo.add(new JLabel("Nombre de usuario:"));
        cuerpo.add(txtNombre);

        cuerpo.add(Box.createRigidArea(new Dimension(0, 20)));

        cuerpo.add(new JLabel("Estado:"));
        cuerpo.add(cmbEstado);

        cuerpo.add(Box.createRigidArea(new Dimension(0, 20)));

        cuerpo.add(btnGuardar);

        add(cuerpo, BorderLayout.CENTER);
    }

    // 📂 Seleccionar imagen
    private void seleccionarFoto() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            rutaFoto = archivo.getAbsolutePath();
            lblRutaFoto.setText(rutaFoto);
        }
    }

    // 💾 Actualizar en la base de datos
    private void actualizarPerfil() {
        String nuevoNombre = txtNombre.getText();
        String nuevoEstado = (String) cmbEstado.getSelectedItem();

        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío");
            return;
        }

        try (Connection conn = Conexion.obtenerConexion()) {

            // Usamos el mismo enfoque que en registro
            String sql = "UPDATE usuarios SET usuario = ?, estado = ?, foto = ? WHERE usuario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, nuevoNombre);
            pstmt.setString(2, nuevoEstado);
            pstmt.setString(3, rutaFoto);
            pstmt.setString(4, usuarioActual);

            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente");

            // Actualizar referencia del usuario actual
            usuarioActual = nuevoNombre;

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
            }
        }
    }
}
