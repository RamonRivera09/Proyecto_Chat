package GUI;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AgregarContacto extends JFrame implements ActionListener{
    private JPanel pPrincipal, pArriba, pCentro, pSur;
    private JTextField codigo;
    private JButton volver, agregar;
    private JLabel lblprincipal;
    public AgregarContacto(){
        configFrame();
        initComponents();
        setVisible(true);
    }
    public void configFrame(){
        setSize(new Dimension(500,200));
        setTitle("CHARLEMOS - Agregar contacto");
        this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Logo_Chat.jpg")).getImage());
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public void initComponents(){
        pPrincipal=new JPanel();
        pPrincipal.setLayout(new BorderLayout());
        pArriba=new JPanel();
        pArriba.setLayout(new GridBagLayout());
        GridBagConstraints d=new GridBagConstraints();
        pArriba.setBackground(new Color(200, 162, 200));
        volver=new JButton("<- Volver");
        volver.addActionListener(this);
        d.gridx=0;
        d.gridy=0;
        d.anchor=GridBagConstraints.CENTER;
        d.fill=GridBagConstraints.NONE;
        d.insets=new Insets(10,10,10,10);
        pArriba.add(volver, d);
        lblprincipal=new JLabel ("Ingresa el código de usuario de tu contacto:");
        lblprincipal.setForeground(Color.WHITE);
        lblprincipal.setFont(new Font("Arial", Font.BOLD, 15));
        d.gridx=1;
        d.gridy=0;
        d.anchor=GridBagConstraints.CENTER;
        d.fill=GridBagConstraints.NONE;
        d.insets=new Insets(10,10,10,10);
        pArriba.add(lblprincipal, d);
        pPrincipal.add(pArriba, BorderLayout.NORTH);
        pCentro=new JPanel (new GridBagLayout());
        pCentro.setBackground(new Color(229, 221, 213));
        GridBagConstraints c=new GridBagConstraints();
        codigo=new JTextField(20);
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.NONE;
        c.insets=new Insets(10,10,10,10);
        pCentro.add(codigo, c);
        agregar=new JButton("Agregar contacto");
        c.gridx=0;
        c.gridy=1;
        c.anchor=GridBagConstraints.CENTER;
        c.fill=GridBagConstraints.NONE;
        c.insets=new Insets(10,10,10,10);
        pCentro.add(agregar, c);
        pPrincipal.add(pArriba, BorderLayout.NORTH);
        pPrincipal.add(pCentro, BorderLayout.CENTER);
        add(pPrincipal);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==volver){
            this.dispose();
        }
    }
}
