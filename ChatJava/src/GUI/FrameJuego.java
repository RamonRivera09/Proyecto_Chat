package GUI;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
public class FrameJuego extends JFrame{
    
    PanelDelJuego obj = new PanelDelJuego();
    
    FrameJuego(){
            this.add(obj);
            this.setTitle("Juego de Snake");
            this.setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/Snake.png")).getImage());
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setResizable(false);
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new FrameJuego());
    }
}