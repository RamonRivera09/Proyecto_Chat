package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelFavoritos extends JPanel implements ActionListener{

    private JButton btnVolver, btnEliminarFav;
    private JTextField txtBuscarFav;
    private DefaultListModel<String> modeloFavoritos;
    private JList<String> listaFavoritos;
    private JButton BuscarFav;

    public PanelFavoritos(ActionListener accionVolver) {
        setLayout(new BorderLayout());
        setBackground(new Color(229, 221, 213));

        // --- PANEL SUPERIOR (Leyenda Favoritos y Volver) ---
        JPanel pArriba = new JPanel(new BorderLayout());
        pArriba.setBackground(new Color(200, 162, 200)); // Color turquesa
        pArriba.setPreferredSize(new Dimension(0, 60));

        JLabel lblTitulo = new JLabel("  Contactos Favoritos", JLabel.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        btnVolver = new JButton("Volver");
        btnVolver.setFocusPainted(false);
        btnVolver.addActionListener(accionVolver);

        pArriba.add(lblTitulo, BorderLayout.CENTER);
        pArriba.add(btnVolver, BorderLayout.EAST);

        // --- PANEL CENTRAL (Scroll idéntico al principal) ---
        modeloFavoritos = new DefaultListModel<>();
        listaFavoritos = new JList<>(modeloFavoritos);
        listaFavoritos.setFont(new Font("Arial", Font.PLAIN, 20));
        
        JScrollPane scroll = new JScrollPane(listaFavoritos);
        scroll.setBorder(null);

        // --- PANEL INFERIOR (Panel de búsqueda/acción) ---
        JPanel pAbajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pAbajo.setBackground(new Color(200, 162, 200));
        pAbajo.setPreferredSize(new Dimension(0, 50));
        
        txtBuscarFav = new JTextField(30);
        btnEliminarFav = new JButton("Quitar de Favoritos");
        BuscarFav=new JButton("Buscar");
        BuscarFav.addActionListener(this);
        pAbajo.add(BuscarFav);
        pAbajo.add(txtBuscarFav);
        pAbajo.add(btnEliminarFav);

        add(pArriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(pAbajo, BorderLayout.SOUTH);
    }
// --- GETTERS PARA QUE 'CHAT.JAVA' PUEDA MANEJAR LA LISTA ---
    public DefaultListModel<String> getModeloFavoritos() {
        return modeloFavoritos;
    }

    public JList<String> getListaFavoritos() {
        return listaFavoritos;
    }


    public JButton getBtnEliminarFav() {
        return btnEliminarFav;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==BuscarFav){
            String busqueda = txtBuscarFav.getText().trim().toLowerCase();

            if (busqueda.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Escribe un nombre");
                return;
            }

            ListModel<String> modelo = listaFavoritos.getModel();
            boolean encontrado = false;

            for (int i = 0; i < modelo.getSize(); i++) {
                String contacto = modelo.getElementAt(i).toLowerCase();

                if (contacto.contains(busqueda)) {
                    listaFavoritos.setSelectedIndex(i);
                    listaFavoritos.ensureIndexIsVisible(i);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "Contacto no registrado");
            }
        }
    }
    
} 