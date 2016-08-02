import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;


public class Game extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final int WIDTH = 500, HEIGHT = 500;
	
	public Game(String name) throws IOException {
		
		add(new Board());
		
		setTitle(name);
		setSize(WIDTH, HEIGHT);
		setBackground(Color.BLACK);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	public static void main(String[] args) throws IOException {
		
		Game pacman = new Game("Pacman");
		pacman.setVisible(true);
	}
	
}
