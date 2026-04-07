package GUI;

import java.awt.*;
import javax.swing.*;

public class PantallaPerfil extends JPanel {
    private JLabel lblFoto;
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
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        perfil = new ImageIcon(getClass().getResource("/IMAGENES/nuevo_perfil.png"));

        Image img = perfil.getImage();
        Image imgEscalada = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);

        lblFoto = new JLabel(new ImageIcon(imgEscalada));
        lblFoto.setPreferredSize(new Dimension(200, 200));
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        

        JLabel lblNombre = new JLabel("Usuario: " + nombreUsuario);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 24));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        String codigo = "USR-" + nombreUsuario.substring(0, Math.min(2, nombreUsuario.length())).toUpperCase() + "01";
        JLabel lblCodigo = new JLabel("ID: " + codigo);
        lblCodigo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cuerpo.add(lblFoto);
        cuerpo.add(Box.createRigidArea(new Dimension(0, 20)));
        cuerpo.add(lblNombre);
        cuerpo.add(lblCodigo);

        add(cuerpo, BorderLayout.CENTER);
    }
    
}
