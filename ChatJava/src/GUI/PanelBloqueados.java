package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelBloqueados extends JPanel implements ActionListener{

    private JButton btnVolver, btnDesbloquear;
    private JTextField txtBuscar;
    private DefaultListModel<String> modeloBloqueados;
    private JList<String> listaBloqueados;
    private JButton BuscarBloq;

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
        listaBloqueados.setFont(new Font("Arial", Font.PLAIN, 20));
        
        JScrollPane scroll = new JScrollPane(listaBloqueados);
        scroll.setBorder(null);

        // --- PANEL INFERIOR (Búsqueda/Acciones) ---
        JPanel pAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pAbajo.setBackground(new Color(200, 162, 200));
        
        txtBuscar = new JTextField(30);
        btnDesbloquear = new JButton("Desbloquear Selección");
        BuscarBloq=new JButton("Buscar");
        BuscarBloq.addActionListener(this);
        pAbajo.add(BuscarBloq);
        pAbajo.add(txtBuscar);
        pAbajo.add(btnDesbloquear);

        // Agregar al panel principal
        add(pArriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pAbajo, BorderLayout.SOUTH);
    }
// --- GETTERS ---
    public DefaultListModel<String> getModeloBloqueados() {
        return modeloBloqueados;
    }

    public JList<String> getListaBloqueados() {
        return listaBloqueados;
    }

    public JButton getBtnDesbloquear() {
        return btnDesbloquear;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Comparamos usando .equals() o e.getActionCommand() por si el objeto se reinició
        if (e.getSource() == BuscarBloq || e.getActionCommand().equals("Buscar")) {
            
            String busqueda = txtBuscar.getText().trim().toLowerCase();

            if (busqueda.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Escribe un nombre para buscar.");
                return;
            }

            ListModel<String> modelo = listaBloqueados.getModel();
            boolean encontrado = false;

            // Recorremos la lista para buscar al contacto
            for (int i = 0; i < modelo.getSize(); i++) {
                String contacto = modelo.getElementAt(i).toLowerCase();

                if (contacto.contains(busqueda)) {
                    listaBloqueados.setSelectedIndex(i);
                    listaBloqueados.ensureIndexIsVisible(i);
                    
                    // IMPORTANTE: Le devolvemos el foco a la lista para que la selección no se vea gris
                    listaBloqueados.requestFocus(); 
                    
                    encontrado = true;
                    break;
                }
            }

            // Si terminó de revisar toda la lista y no lo encontró
            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "El contacto '" + busqueda + "' no está registrado en bloqueados.");
            }
        }
    }
    
}