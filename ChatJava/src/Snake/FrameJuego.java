package Snake;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
public class FrameJuego extends JFrame{
    
    PanelDelJuego obj = new PanelDelJuego();
    
    FrameJuego(){
            this.add(obj);
            this.setTitle("Juego de Snake");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.pack();
            this.setVisible(true);
            this.setLocationRelativeTo(null);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new FrameJuego());
    }
}