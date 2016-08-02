import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private boolean isRunning = false;
	private Timer mainTimer = new Timer(50, null);
	private Timer ghostScareTimer = new Timer(8000, null);
	
	private ArrayList<String> gameMap = new ArrayList<String>();
	
	private ArrayList<RoundRectangle2D> gameTiles = new ArrayList<RoundRectangle2D>();
	private ArrayList<Rectangle2D> dotTiles = new ArrayList<Rectangle2D>();
	private ArrayList<Ellipse2D> powerTiles = new ArrayList<Ellipse2D>();
	
	private ArrayList<BufferedImage> pacImages = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> ghostImages = new ArrayList<BufferedImage>();
	private BufferedImage pacLogo = null;
	
	Pacman player = new Pacman(248, 218);
	Ghost [] ghosts = { new Ghost(238, 181), new Ghost(248, 181), new Ghost(258, 181), new Ghost(268, 181) };
	
	private int score = 0;
	private int pacLives = 0;
	
	
	String topScorer = "";
	String highScore = "";
	
	public class Pacman {
		
		private int xPos, yPos, size = 13;
		private int dirX = 0, dirY = 0;
		int frame = 0;
		
		public Pacman(int xPos, int yPos) {
			
			this.xPos = xPos;
			this.yPos = yPos;
		}
		
		public void setDirection(int dirX, int dirY) {
			
			this.dirX = dirX;
			this.dirY = dirY;
		}	
		
		public void movePacman() {
			
			frame = (frame + 1) % 3;
			
			Rectangle location = getBounds();
			
			if (location.x < 47) {
				xPos = 447;
			}
			
			if (location.x > 447) {
				xPos = 47;
			}
			
			if (dirX == 1 && dirY == 0) {
				if (isValidMove(location, 3, 0)) {
					xPos += 3;
				}
			} else if (dirX == -1 && dirY == 0) {
				if (isValidMove(location, -3, 0)) {
					xPos -= 3;
				}
			} else if (dirX == 0 && dirY == 1) {
				if (isValidMove(location, 0, 3)) {
					yPos += 3;
				}
			} else if (dirX == 0 && dirY == -1) {
				if (isValidMove(location, 0, -3)) {
					yPos -= 3;
				}
			}
		}
		
		public boolean isValidMove(Rectangle location, int xDir, int yDir) {
			
			location.x += xDir;
			location.y += yDir;
			
			for (RoundRectangle2D rect : gameTiles) {
				if (rect.intersects(location)) {
					return false;
				}
			}
			return true;
		}
		
		public int getImageNum() {
			
			int imageNum = 0;
			
			if (frame == 2 || dirX == 0 && dirY == 0) {
				
				imageNum = 8;
			} else {
				
				if (dirX == 1 && dirY == 0) {
					imageNum = frame;
				} else if (dirX == -1 && dirY == 0) {
					imageNum = 2 + frame;
				} else if (dirX == 0 && dirY == -1) {
					imageNum = 4 + frame;
				} else if (dirX == 0 && dirY == 1) {
					imageNum = 6 + frame;
				}
			}
			return imageNum;
		}
		
		public Rectangle getBounds() {
			return new Rectangle(xPos, yPos, size, size);
		}

	}
	
	public class Ghost {
		
		private int xPos, yPos;
		private int dirX = 0, dirY = 0, size = 14;
		private int frame = 0;
		private int speed = 3;
		
		boolean edible = false;
		boolean pathLeft = false, pathRight = false, pathDown = false, pathUp = false;
		
		public Ghost(int xPos, int yPos) {
			
			this.xPos = xPos;
			this.yPos = yPos;
		}
		
		public void checkDirections() {
			
			pathLeft = true;
			pathRight = true;
			pathDown = true;
			pathUp = true;
			
			Rectangle location = getBounds();
			
			Rectangle checkLeft = new Rectangle(location.x - 10, location.y, location.width, location.height);
			Rectangle checkRight = new Rectangle(location.x + 10, location.y, location.width, location.height);
			Rectangle checkDown = new Rectangle(location.x, location.y + 10, location.width, location.height);
			Rectangle checkUp = new Rectangle(location.x, location.y - 10, location.width, location.height);
			
			for (RoundRectangle2D rect : gameTiles) {
				
				if (rect.intersects(checkLeft)) {
					pathLeft = false;
				}
				
				if (rect.intersects(checkRight)) {
					pathRight = false;
				}
				
				if (rect.intersects(checkDown)) {
					pathDown = false;
				}
				
				if (rect.intersects(checkUp)) {
					pathUp = false;
				}
			}
		}
		
		public void calcMoves() {
			
			int xDiff = getXDifference();
			int yDiff = getYDifference();
		
			int rand = (int)(Math.random() * 4) + 1;
			
			if (!edible) {
				if (pathLeft && dirX != 1 && (rand == 1 || xDiff > 0)) {
					dirX = -1;
					dirY = 0;
				} else if (pathRight && dirX != -1 && (rand == 2 || xDiff < 0)) {
					dirX = 1;
					dirY = 0;
				} else if (pathDown && dirY != -1 && (rand == 3 || yDiff < 0)) {
					dirX = 0;
					dirY = 1;
				} else if (pathUp && dirY != 1 && (rand == 4 || yDiff > 0)) {
					dirX = 0;
					dirY = -1;
				}
			} else {
				
				if (pathLeft && dirX != 1 && (rand == 1 || xDiff < 0)) {
					dirX = -1;
					dirY = 0;
				} else if (pathRight && dirX != -1 && (rand == 2 || xDiff > 0)) {
					dirX = 1;
					dirY = 0;
				} else if (pathDown && dirY != -1 && (rand == 3 || yDiff > 0)) {
					dirX = 0;
					dirY = 1;
				} else if (pathUp && dirY != 1 && (rand == 4 || yDiff < 0)) {
					dirX = 0;
					dirY = -1;
				}
			}
			
		}
		
		public void getRandDirection() {
			
			int direction = (int)(Math.random() * 4 + 1);
			
			switch (direction) {
				case 1 : 
					dirX = 1;
					dirY = 0;
				break;
				case 2 : 
					dirX = -1; 
					dirY = 0;
				break;
				case 3 : 
					dirX = 0;
					dirY = 1;
				break;
				case 4 : 
					dirX = 0; 
					dirY = -1;
				break;
			}
		}
		
		public void moveGhost() {
			
			frame ^= 1;
			
			Rectangle location = getBounds();
			
			if (location.x < 47) {
				xPos = 447;
			}
			
			if (location.x > 447) {
				xPos = 47;
			}
			
			if (dirX == 1 && dirY == 0) {
				if (isValidMove(location, 3, 0)) {
					xPos += speed;
				} else {
					getRandDirection();
				}
			} else if (dirX == -1 && dirY == 0) {
				if (isValidMove(location, -3, 0)) {
					xPos -= speed;
				} else {
					getRandDirection();
				}
			} else if (dirX == 0 && dirY == 1) {
				if (isValidMove(location, 0, 3)) {
					yPos += speed;
				} else {
					getRandDirection();
				}
			} else if (dirX == 0 && dirY == -1) {
				if (isValidMove(location, 0, -3)) {
					yPos -= speed;
				} else {
					getRandDirection();
				}
			}
		}
		
		public void getDirections() {
			
			pathLeft = true;
			pathRight = true;
			pathDown = true;
			pathUp = true;

			Rectangle location = getBounds();
			
			Rectangle checkLeft = new Rectangle(location.x - 10, location.y, location.width, location.height);
			Rectangle checkRight = new Rectangle(location.x + 10, location.y, location.width, location.height);
			Rectangle checkDown = new Rectangle(location.x, location.y + 10, location.width, location.height);
			Rectangle checkUp = new Rectangle(location.x, location.y - 10, location.width, location.height);
			
			for (RoundRectangle2D rect : gameTiles) {
				
				if (rect.intersects(checkLeft)) {
					pathLeft = false;
				}
				
				if (rect.intersects(checkRight)) {
					pathRight = false;
				}
				
				if (rect.intersects(checkDown)) {
					pathDown = false;
				}
				
				if (rect.intersects(checkUp)) {
					pathUp = false;
				}
			}
		}
		
		public boolean isValidMove(Rectangle location, int xDir, int yDir) {
			
			location.x += xDir;
			location.y += yDir;
			
			for (RoundRectangle2D rect : gameTiles) {
				
				if (rect.intersects(location)) {
					return false;
				}
			}
			return true;
		}
		
		public int getImageNum() {
			return frame;
		}
		
		public Rectangle getBounds() {
			return new Rectangle(xPos, yPos, size, size);
		}
		
		public int getXDifference() {
			return this.xPos - player.xPos;
		}
		
		public int getYDifference() {
			return this.yPos - player.yPos;
		}
	}
	
	public Board() throws IOException {
		
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		
		addKeyListener(new GameKeys());
		setFocusable(true);
		
		mainTimer.addActionListener(setGameFrameRate);
		
		loadImages();
		initMap();
		loadHighScore();
	}
	
	private void startGame() {
		score = 0;
		pacLives = 1;
		isRunning = true;
		startTimer();
	}
	
	public void endGame() {
		
		isRunning = false;
		
		if (score > Integer.parseInt(highScore)) {
			setHighScore();
		}
	}
	
	private void startTimer() {
		mainTimer.start();
	}
	
	private void stopTimer() {
		mainTimer.stop();
	}
	
	private void loadHighScore() throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader("src/high-score.txt"));
		
		topScorer = reader.readLine();
		highScore = reader.readLine();
		
		reader.close();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		try {
			runGame(g);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void runGame(Graphics g) throws InterruptedException {
		
		Graphics2D g2d = (Graphics2D) g;
		
		if (isRunning) {
			
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			
			drawMap(g2d);
			
			player.movePacman();
			
			for (Ghost ghost : ghosts) {
				ghost.getDirections();
				ghost.calcMoves();
				ghost.moveGhost();
			}
			
			drawPacman(g2d);
			drawGhosts(g2d);
			
			if (checkGhostPacCollision()) {
				
				pacLives -= 1;
				
				if (pacLives == 0 || checkWinCond()) {
					endGame();
					resetTiles();
					initMap();
				}
				resetGamePieces();
			}
			drawScore(g2d);
			
		} else {
			drawMap(g2d);
			drawIntroScreen(g2d);
			stopTimer();
		}
	}
	
	private void resetTiles() {
		
		gameTiles.clear();
		dotTiles.clear();
		powerTiles.clear();
	}
	
	private void initMap() {
		
		if (gameMap.isEmpty()) {
			gameMap.add("00000000000000000000000");
			gameMap.add("0+-------0-----------+0");
			gameMap.add("0-00-0-0-0-0-00-00-00-0");
			gameMap.add("0-00-0-0-0-0-0-----00-0");
			gameMap.add("0--------0-0-0-000----0");
			gameMap.add("0-00-0-0-------0---00-0");
			gameMap.add("0------0-00 00---0-0--0");
			gameMap.add("0000-0-0-0   0-000-0-00");
			gameMap.add("-----0---00000-00------");
			gameMap.add("000-00-0--- ------0-000");
			gameMap.add("0----0-0-0-0-00-0-0---0");
			gameMap.add("0-00-------0----0---0-0");
			gameMap.add("0-+0-0-0-0-00-0---000-0");
			gameMap.add("00---0---0------0----+0");
			gameMap.add("00000000000000000000000");
		}
		for (int i = 0; i < gameMap.size(); i++) {
			for (int j = 0; j < gameMap.get(i).length(); j++) {
				
				char currChar = gameMap.get(i).charAt(j);
				
				if (currChar == '0') {
					
					gameTiles.add(new RoundRectangle2D.Double(47 + j * 18, 52 + i * 18, 18, 18, 5, 5));
				}
				if (currChar == '-') {
					dotTiles.add(new Rectangle2D.Double(56 + j * 18, 62 + i * 18, 1, 1));
				}
				if (currChar == '+') {
					powerTiles.add(new Ellipse2D.Double(53 + j * 18, 59 + i * 18, 6, 6));
				}
			}
		}
	}

	private void drawMap(Graphics2D g2d) {
		
		Rectangle location = player.getBounds();
		
		for (RoundRectangle2D rect : gameTiles) {
			
			g2d.setColor(new Color(0, 35, 255));
			g2d.setStroke(new BasicStroke(1.0f));
			g2d.draw(rect);
		}
		
		for (Iterator<Rectangle2D> iterator = dotTiles.iterator(); iterator.hasNext();) {
			
			Rectangle2D dot = iterator.next();
			
			if (dot.intersects(location)) {
				iterator.remove();
				score += 50;
			} else {
				g2d.setColor(new Color(255, 184, 174));
				g2d.setStroke(new BasicStroke(2.0f));
				g2d.draw(dot);
			}	
		}
		
		for (Iterator<Ellipse2D> iterator = powerTiles.iterator(); iterator.hasNext();) {
			
			Ellipse2D power = iterator.next();
			
			if (power.intersects(location)) {
				
				iterator.remove();
				
				if (ghostScareTimer.isRunning()) {
					ghostScareTimer.restart();
				} else {
					ghostScareTimer.addActionListener(unscareGhosts);
					ghostScareTimer.start();
				}

				for (Ghost ghost : ghosts) {
					ghost.edible = true;
					ghost.speed = 2;
				}
				
			} else {
				g2d.setPaint(new Color(255, 184, 174));
				g2d.fill(power);
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.draw(power);
			}	
		}
	}
	
	private void drawScore(Graphics2D g2d) {
		
		g2d.setFont(new Font("Serif", Font.BOLD, 16));
		g2d.setColor(new Color(255, 255, 255));
		g2d.drawString("Score: " + score, 45, 30);
		g2d.drawString("Lives: " + pacLives, 410, 30);
		g2d.drawString("High-Score: " + topScorer + " " + highScore, 45, 355);
	}
	
	private boolean checkWinCond() {
		return dotTiles.isEmpty();
	}
	
	private boolean checkGhostPacCollision() {
		
		Rectangle pacLoc = player.getBounds();
		
		for (Ghost ghost : ghosts) {
			
			Rectangle ghostLoc = ghost.getBounds();
			
			if (pacLoc.intersects(ghostLoc)) {
				
				if (ghost.edible) {
					
					score += 250;
					ghost.xPos = 248;
					ghost.yPos = 181;
					ghost.speed = 3;
					ghost.edible = false;
					
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	private void resetGamePieces() {
		
		player.dirX = 0;
		player.dirY = 0;
		player.xPos = 248;
		player.yPos = 218;
		
		for (int i = 0; i < ghosts.length; i++) {
			ghosts[i].dirX = 0;
			ghosts[i].dirY = 0;
			ghosts[i].xPos = 238 + i * 10; 
			ghosts[i].yPos = 181;
		}
	}
	
	private void loadImages() throws IOException  {
		
		BufferedImage spriteSheet = ImageIO.read(new File("src/pacman.png"));
		int width = 13;
		int height = 13;
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 2; j++) {
				pacImages.add(spriteSheet.getSubimage(j * width, i * height, width, height));
			}
		}
		
		spriteSheet = ImageIO.read(new File("src/ghosts.png"));
		width = 14;
		height = 14;
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 2; j++) {
				ghostImages.add(spriteSheet.getSubimage(j * width, i * height, width, height));
			}
		}
		
		pacLogo = ImageIO.read(new File("src/pac-logo.png"));
	}
	
	public class GameKeys implements KeyListener {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			e.consume();
			
			int key = e.getKeyCode();
			
			switch(key) {
			case KeyEvent.VK_LEFT : player.setDirection(-1, 0);
				break;
			case KeyEvent.VK_RIGHT : player.setDirection(1, 0);
				break;
			case KeyEvent.VK_DOWN : player.setDirection(0, 1);
				break;
			case KeyEvent.VK_UP : player.setDirection(0, -1);
				break;
			case KeyEvent.VK_ENTER : startGame();
				break;
			case KeyEvent.VK_ESCAPE : isRunning = false;
				break;
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
	private void drawIntroScreen(Graphics2D g2d) {
        
        String title = "PA  MAN";

        g2d.setColor(new Color(255, 255, 11));
        g2d.setFont(new Font("Monospaced", Font.BOLD, 42));
        g2d.drawString(title, 165, 200);
        
        String action = "Press enter to start";
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g2d.drawString(action, 145, 350);
        
        g2d.drawImage(pacLogo, 220, 168, null);
	}
	
	private void drawPacman(Graphics2D g2d) {
		
		g2d.drawImage(pacImages.get(player.getImageNum()), player.xPos, player.yPos, null);
	}
	
	private void drawGhosts(Graphics2D g2d) {
		
		for (int i = 0; i < ghosts.length; i++) {
			
			if (ghosts[i].edible) {
				g2d.drawImage(ghostImages.get(8 + ghosts[i].getImageNum()), ghosts[i].xPos, ghosts[i].yPos, null);
			} else {
				g2d.drawImage(ghostImages.get(i * 2 + ghosts[i].getImageNum()), ghosts[i].xPos, ghosts[i].yPos, null);
			}
		}
	}
	
	public void setHighScore() {
		
		String input = JOptionPane.showInputDialog("New high-score! Enter your initials: ");
		String initials = input.length() > 3 ? input.substring(0, 3).toUpperCase() : input.toUpperCase();
		
		PrintWriter writer;
		
		try {
			writer = new PrintWriter("src/high-score.txt", "UTF-8");
			writer.println(initials);
			writer.println(score);
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	ActionListener setGameFrameRate = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	};
	
	ActionListener unscareGhosts = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			for (Ghost ghost : ghosts) {
				ghost.edible = false;
				ghost.speed = 3;
			}
			ghostScareTimer.stop();
		}
	};

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}
