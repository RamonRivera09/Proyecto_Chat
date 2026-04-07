package GUI;

import INICIO_SESION.Conexion;
import java.awt.*;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PantallaPerfil extends JPanel {

    private JLabel lblFoto, correo, estado, conectado;
    private ImageIcon perfil;

    public PantallaPerfil(String nombreUsuario, CardLayout cl, JPanel contenedor) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        // Cabecera sencilla
        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cabecera.setBackground(new Color(200, 162, 200));

        JButton btnVolver = new JButton("<- Volver al Chat");
        btnVolver.addActionListener(e -> cl.show(contenedor, "LISTA"));

        cabecera.add(btnVolver);
        add(cabecera, BorderLayout.NORTH);

        // Cuerpo del Perfil
        JPanel cuerpo = new JPanel();
        cuerpo.setBackground(new Color(229, 221, 213));
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        perfil = new ImageIcon(getClass().getResource("/IMAGENES/nuevo_perfil.png"));

        Image img = perfil.getImage();
        Image imgEscalada = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

        lblFoto = new JLabel(new ImageIcon(imgEscalada));
        JLabel lblNombre = new JLabel();
        JLabel lblCodigo = new JLabel();
        correo = new JLabel();
        estado = new JLabel();
        conectado = new JLabel();

        try (Connection conn = Conexion.obtenerConexion()) {

            String sql = "SELECT usuario,codigo, correo, estado, conectado, foto FROM usuarios WHERE usuario = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nombreUsuario);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nombreDB=rs.getString("usuario");
                String codigoDB = rs.getString("codigo");
                String correoDB = rs.getString("correo");
                String estadoDB = rs.getString("estado");
                String conectadoDB = rs.getString("conectado");
                String fotoDB = rs.getString("foto");

                // Asignar datos a labels
                lblNombre.setText("Usuario: "+nombreDB);
                lblCodigo.setText("ID: " + codigoDB);
                correo.setText("Correo: " + (correoDB != null ? correoDB : "No registrado"));
                estado.setText("Estado: " + estadoDB);
                conectado.setText("Conectado: " + conectadoDB);

                // Si tienes foto guardada como ruta
                if (fotoDB != null && !fotoDB.isEmpty()) {
                    ImageIcon img2 = new ImageIcon(fotoDB);
                    Image escalada = img2.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    lblFoto.setIcon(new ImageIcon(escalada));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar perfil: " + ex.getMessage());
        }
        lblFoto.setPreferredSize(new Dimension(200, 200));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel[] labels = new JLabel[]{lblNombre, lblCodigo, correo, estado, conectado};
        for (JLabel l : labels) {
            l.setFont(new Font("Arial", Font.BOLD, 24));
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        cuerpo.add(lblFoto);
        cuerpo.add(Box.createRigidArea(new Dimension(0, 20)));
        cuerpo.add(lblNombre);
        cuerpo.add(lblCodigo);
        cuerpo.add(correo);
        cuerpo.add(estado);
        cuerpo.add(conectado);

        add(cuerpo, BorderLayout.CENTER);
    }

}
