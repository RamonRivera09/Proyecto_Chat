package Snake;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class PanelDelJuego extends JPanel implements ActionListener{
        private JButton reiniciar;
	static final int SCREEN_WIDTH = 1000;
	static final int SCREEN_HEIGHT = 650;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	static final int DELAY = 100;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 3;
	int manzanasComidas;
	int manzanasX;
	int manzanasY;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
        
	PanelDelJuego(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.DARK_GRAY);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		inicioJuego();
	}
        
	public void inicioJuego() {
		ManzanaN();
		running = true;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dibujo(g);
	}
	public void Dibujo(Graphics g) {
		if(running) {
			g.setColor(Color.white);
			g.fillOval(manzanasX, manzanasY, UNIT_SIZE, UNIT_SIZE);
		
			for(int i = 0; i< bodyParts;i++) {
				if(i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}			
			}
			g.setColor(Color.red);
			g.setFont( new Font("RVV",Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Puntaje:"+manzanasComidas, (SCREEN_WIDTH - metrics.stringWidth("Puntaje: "+manzanasComidas))/2, g.getFont().getSize());
		}
		else {
			finJuego(g);
		}
		
	}
	public void ManzanaN(){
		manzanasX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		manzanasY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}
	public void movimientos(){
		for(int i = bodyParts;i>0;i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
		
	}
	public void checkManzanas() {
		if((x[0] == manzanasX) && (y[0] == manzanasY)) {
			bodyParts++;
			manzanasComidas++;
			ManzanaN();
		}
	}
	public void checkChoques() {
		for(int i = bodyParts;i>0;i--) {
			if((x[0] == x[i])&& (y[0] == y[i])) {
				running = false;
			}
		}
		
		if(x[0] < 0) {
			running = false;
		}
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		
		if(y[0] < 0) {
			running = false;
		}
		
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	public void finJuego(Graphics g) {
		//Puntos totales
		g.setColor(Color.white);
		g.setFont( new Font("RVV",Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Puntaje: "+manzanasComidas, (SCREEN_WIDTH - metrics1.stringWidth("Puntaje: "+manzanasComidas))/2, g.getFont().getSize());
		//Cuando la persona pierde entonces:
		g.setColor(Color.white);
		g.setFont( new Font("Ink Free",Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Usted ha perdido:-(", (SCREEN_WIDTH - metrics2.stringWidth("Usted ha perdido:-("))/2, SCREEN_HEIGHT/2);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(running) {
			movimientos();
			checkManzanas();
			checkChoques();
		}
                if (e.getSource() == reiniciar){
                   inicioJuego(); 
                }
		repaint();
	}
	public class MyKeyAdapter extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}
}
