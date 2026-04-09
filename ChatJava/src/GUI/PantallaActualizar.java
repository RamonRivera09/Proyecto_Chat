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
    private JPasswordField contra;
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
        cabecera.setBackground(new Color(200, 162, 200));
        JButton btnVolver = new JButton("<- Volver");
        btnVolver.addActionListener(e -> cl.show(contenedor, "PERFIL"));
        cabecera.add(btnVolver);

        add(cabecera, BorderLayout.NORTH);

        // 🔹 Cuerpo
        JPanel cuerpo = new JPanel();
        cuerpo.setBackground(new Color(229, 221, 213));
        cuerpo.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // 📸 Foto
        JButton btnSeleccionarFoto = new JButton("Seleccionar Foto");
        lblRutaFoto = new JLabel("Ninguna imagen seleccionada");
        lblRutaFoto.setFont(new Font ("Arial", Font.BOLD, 20));

        btnSeleccionarFoto.addActionListener(e -> seleccionarFoto());

        // 👤 Nombre de usuario
        txtNombre = new JTextField(30);
        txtNombre.setText(nombreUsuario);

        // 🔹 Estado
        String[] estados = {"Disponible", "Ocupado", "Ausente", "No Disponible", "Invisible"};
        cmbEstado = new JComboBox<>(estados);
        cmbEstado.setSelectedIndex(0); // Por defecto Disponible

        // 💾 Botón Guardar
        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.addActionListener(e -> actualizarPerfil());
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(btnSeleccionarFoto, c);
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(lblRutaFoto, c);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        JLabel nomuser=new JLabel ("Ingresa el nuevo nombre: ");
        nomuser.setFont(new Font ("Arial", Font.BOLD, 20));
        cuerpo.add(nomuser, c);
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(txtNombre, c);
        JLabel contrasena=new JLabel ("Ingresa la nueva Contraseña: ");
        contrasena.setFont(new Font("Arial", Font.BOLD, 20));
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(contrasena, c);
        contra=new JPasswordField(30);
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(contra, c);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(10, 10, 10, 10);
        JLabel ponerEstado=new JLabel("Selecciona el estado: ");
        ponerEstado.setFont(new Font("Arial", Font.BOLD, 20));
        cuerpo.add(ponerEstado, c);
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(cmbEstado, c);
        c.gridx = 1;
        c.gridy = 4;
        c.insets = new Insets(10, 10, 10, 10);
        cuerpo.add(btnGuardar, c);
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
    // 💾 Actualizar en la base de datos
    private void actualizarPerfil() {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoEstado = (String) cmbEstado.getSelectedItem();
        // Obtener la contraseña desde el JPasswordField
        String nuevaContra = new String(contra.getPassword()).trim();

        try (Connection conn = Conexion.obtenerConexion()) {

            // Construimos el SQL dinámicamente
            StringBuilder sql = new StringBuilder("UPDATE usuarios SET ");
            boolean primerCampo = true;

            // --- Lógica para el Nombre ---
            if (!nuevoNombre.isEmpty()) {
                sql.append("usuario = ?");
                primerCampo = false;
            }

            // --- Lógica para el Estado ---
            if (nuevoEstado != null && !nuevoEstado.isEmpty()) {
                if (!primerCampo) sql.append(", ");
                sql.append("estado = ?");
                primerCampo = false;
            }

            // --- Lógica para la Foto ---
            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                if (!primerCampo) sql.append(", ");
                sql.append("foto = ?");
                primerCampo = false;
            }

            // --- NUEVA: Lógica para la Contraseña ---
            if (!nuevaContra.isEmpty()) {
                if (!primerCampo) sql.append(", ");
                sql.append("contrasena = ?"); // Asegúrate de que tu columna se llame así en la BD
                primerCampo = false;
            }

            sql.append(" WHERE usuario = ?");

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            // Asignar parámetros según el orden en que se agregaron al SQL
            int indice = 1;
            if (!nuevoNombre.isEmpty()) {
                pstmt.setString(indice++, nuevoNombre);
            }
            if (nuevoEstado != null && !nuevoEstado.isEmpty()) {
                pstmt.setString(indice++, nuevoEstado);
            }
            if (rutaFoto != null && !rutaFoto.isEmpty()) {
                pstmt.setString(indice++, rutaFoto);
            }
            // Asignar contraseña si no está vacía
            if (!nuevaContra.isEmpty()) {
                pstmt.setString(indice++, nuevaContra);
            }

            // El último parámetro siempre es el usuarioActual para el WHERE
            pstmt.setString(indice, usuarioActual);

            int filas = pstmt.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "Perfil actualizado correctamente");
                // Limpiar el campo de contraseña después de actualizar por seguridad
                contra.setText(""); 
                
                if (!nuevoNombre.isEmpty()) {
                    usuarioActual = nuevoNombre;
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se realizaron cambios.");
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this, "Ese nombre de usuario ya está en uso", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage());
            }
        }
    }
}
