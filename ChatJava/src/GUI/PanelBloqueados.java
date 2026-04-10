package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelBloqueados extends JPanel {

    private JButton btnVolver, btnDesbloquear;
    private JTextField txtBuscar;
    private DefaultListModel<String> modeloBloqueados;
    private JList<String> listaBloqueados;

    public PanelBloqueados(ActionListener accionVolver) {
        setLayout(new BorderLayout());
        setBackground(new Color(229, 221, 213)); // Mismo fondo que pPrincipal

        // --- PANEL SUPERIOR (Leyenda y Volver) ---
        JPanel pArriba = new JPanel(new BorderLayout());
        pArriba.setBackground(new Color(200, 162, 200)); // Color verde oscuro Chat
        pArriba.setPreferredSize(new Dimension(0, 60));

        JLabel lblTitulo = new JLabel("  Usuarios Bloqueados", JLabel.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        btnVolver = new JButton("Volver");
        btnVolver.addActionListener(accionVolver);

        pArriba.add(lblTitulo, BorderLayout.CENTER);
        pArriba.add(btnVolver, BorderLayout.EAST);

        // --- PANEL CENTRAL (Lista con Scroll) ---
        modeloBloqueados = new DefaultListModel<>();
        listaBloqueados = new JList<>(modeloBloqueados);
        listaBloqueados.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane scroll = new JScrollPane(listaBloqueados);
        scroll.setBorder(null);

        // --- PANEL INFERIOR (Búsqueda/Acciones) ---
        JPanel pAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pAbajo.setBackground(new Color(200, 162, 200));
        
        txtBuscar = new JTextField(30);
        btnDesbloquear = new JButton("Desbloquear Selección");
        JLabel Buscar=new JLabel("Buscar: ");
        Buscar.setForeground(Color.WHITE);
        pAbajo.add(Buscar);
        pAbajo.add(txtBuscar);
        pAbajo.add(btnDesbloquear);

        // Agregar al panel principal
        add(pArriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pAbajo, BorderLayout.SOUTH);
    }
}