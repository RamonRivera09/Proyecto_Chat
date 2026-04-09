package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Correo extends JFrame implements ActionListener {

    private JPanel pPrincipal, pArriba, pCentro;
    private JTextField codigo;
    private JButton volver, agregar;
    private JLabel lblprincipal;

    public Correo() {
        configFrame();
        initComponents();
        setVisible(true);
    }

    public void configFrame() {
        setSize(new Dimension(500, 200));
        setTitle("CHARLEMOS - Registrar Correo");
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

        codigo = new JTextField(20);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        pCentro.add(codigo, c);

        agregar = new JButton("Registrar Correo");
        agregar.addActionListener(this);
        c.gridy = 1;
        pCentro.add(agregar, c);

        pPrincipal.add(pCentro, BorderLayout.CENTER);
        add(pPrincipal);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == volver) {
            this.dispose();
        }

    }
}
