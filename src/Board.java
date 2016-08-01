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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	boolean isRunning = true;
	private Timer timer;
	ArrayList<String> gameMap = new ArrayList<String>();
	ArrayList<RoundRectangle2D> gameTiles = new ArrayList<RoundRectangle2D>();
	ArrayList<Rectangle2D> dotTiles = new ArrayList<Rectangle2D>();
	public ArrayList<BufferedImage> pacImages = new ArrayList<BufferedImage>();
	public ArrayList<BufferedImage> ghostImages = new ArrayList<BufferedImage>();
	Pacman player = new Pacman(68, 72);
	Ghost [] ghosts = { new Ghost(238, 181), new Ghost(248, 181), new Ghost(258, 181), new Ghost(268, 181) };
	int score = 0;
	int pacLives = 3;
	
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
			
			if (dirX == 1 && dirY == 0) {
				if (isValidMove(2, 0)) {
					xPos += 2;
				}
			} else if (dirX == -1 && dirY == 0) {
				if (isValidMove(-2, 0)) {
					xPos -= 2;
				}
			} else if (dirX == 0 && dirY == 1) {
				if (isValidMove(0, 2)) {
					yPos += 2;
				}
			} else if (dirX == 0 && dirY == -1) {
				if (isValidMove(0, -2)) {
					yPos -= 2;
				}
			}
		}
		
		public boolean isValidMove(int xDir, int yDir) {
			
			Rectangle location = player.getBounds();
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
		
		int xPos, yPos, isValidMove;
		int dirX = 0, dirY = 0, size = 14;
		int frame = 0;
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
		
			int rand = (int)(Math.random() * 2) + 1;
			
			if (pathLeft && xDiff > 0 && dirX != 1 && rand == 1) {
				dirX = -1;
				dirY = 0;
			} else if (pathRight && xDiff < 0 && dirX != -1 && rand == 2) {
				dirX = 1;
				dirY = 0;
			} else if (pathDown && yDiff < 0 && dirY != -1 && rand == 1) {
				dirX = 0;
				dirY = 1;
			} else if (pathUp && yDiff > 0 && dirY != 1 && rand == 2) {
				dirX = 0;
				dirY = -1;
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
			
			if (dirX == 1 && dirY == 0) {
				if (isValidMove(2, 0)) {
					xPos += 2;
				} else {
					getRandDirection();
				}
			} else if (dirX == -1 && dirY == 0) {
				if (isValidMove(-2, 0)) {
					xPos -= 2;
				} else {
					getRandDirection();
				}
			} else if (dirX == 0 && dirY == 1) {
				if (isValidMove(0, 2)) {
					yPos += 2;
				} else {
					getRandDirection();
				}
			} else if (dirX == 0 && dirY == -1) {
				if (isValidMove(0, -2)) {
					yPos -= 2;
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
		
		public boolean isValidMove(int xDir, int yDir) {
			
			Rectangle location = getBounds();
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
		
		startTimer();
		loadImages();
		initMap();
	}
	
	private void startTimer() {
		timer = new Timer(40, this);
		timer.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		runGame(g);
	}
	
	private void runGame(Graphics g) {
		
		if (isRunning) {
			
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			
			drawMap(g2d);
			
			player.movePacman();
			
			for (Ghost ghost : ghosts) {
				ghost.getDirections();
				ghost.calcMoves();
				ghost.moveGhost();
			}
			
			if (checkLoseLife()) {
				pacLives--;
				resetGamePieces();
			}
			
			drawPacman(g2d);
			drawGhosts(g2d);
			drawScore(g2d);
		}
	}
	
	private void initMap() {
		
		gameMap.add("00000000000000000000000");
		gameMap.add("0+-------0------------0");
		gameMap.add("0-00-0-0-0-0-00-00-00-0");
		gameMap.add("0-00-0-0-0-0-0-----00-0");
		gameMap.add("0--------0-0-0-000----0");
		gameMap.add("0-00-0-0-------0---00-0");
		gameMap.add("0------0-00+00---0-0--0");
		gameMap.add("0000-0-0-0+++0-000-0-00");
		gameMap.add("-----0---00000-00------");
		gameMap.add("000-00-0----------0-000");
		gameMap.add("0----0-0-0-0-00-0-0---0");
		gameMap.add("0-00-------0----0---0-0");
		gameMap.add("0--0-0-0-0-00-0---000-0");
		gameMap.add("00---0---0------0-----0");
		gameMap.add("00000000000000000000000");
		
		for (int i = 0; i < gameMap.size(); i++) {
			for (int j = 0; j < gameMap.get(i).length(); j++) {
				
				char currChar = gameMap.get(i).charAt(j);
				
				if (currChar == '0') {
					
					gameTiles.add(new RoundRectangle2D.Double(47 + j * 18, 52 + i * 18, 18, 18, 5, 5));
				}
				if (currChar == '-') {
					dotTiles.add(new Rectangle2D.Double(56 + j * 18, 62 + i * 18, 1, 1));
				}
			}
		}
	}

	private void drawMap(Graphics2D g2d) {
		
		Rectangle location = player.getBounds();
		
		for (RoundRectangle2D rect : gameTiles) {
			
			g2d.setColor(new Color(0, 35, 255));
			g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2d.draw(rect);
		}
		
		for (Iterator<Rectangle2D> iterator = dotTiles.iterator(); iterator.hasNext();) {
			
			Rectangle2D dot = iterator.next();
			
			if (dot.intersects(location)) {
				iterator.remove();
				score += 50;
			} else {
				g2d.setColor(new Color(255, 184, 174));
				g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
				g2d.draw(dot);
			}	
		}
	}
	
	private void drawScore(Graphics2D g2d) {
		
		g2d.setFont(new Font("Serif", Font.BOLD, 16));
		g2d.setColor(new Color(255, 255, 255));
		g2d.drawString("Score: " + score, 45, 30);
		g2d.drawString("Lives: " + pacLives, 410, 30);
	}
	
	private void checkWinGame() {
		
	}
	
	private boolean checkLoseLife() {
		
		Rectangle pacLoc = player.getBounds();
		
		for (Ghost ghost : ghosts) {
			
			Rectangle ghostLoc = ghost.getBounds();
			
			if (pacLoc.intersects(ghostLoc)) {
				return true;
			}
		}
		return false;
	}
	
	private void resetGamePieces() {
		
		player.dirX = 0;
		player.dirY = 0;
		player.xPos = 68;
		player.yPos = 72;
		
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
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 2; j++) {
				ghostImages.add(spriteSheet.getSubimage(j * width, i * height, width, height));
			}
		}
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
			case KeyEvent.VK_ESCAPE :
				isRunning = false;
			}
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
	
	private void drawPacman(Graphics2D g2d) {
		
		g2d.drawImage(pacImages.get(player.getImageNum()), player.xPos, player.yPos, null);
	}
	
	private void drawGhosts(Graphics2D g2d) {
		
		for (int i = 0; i < ghosts.length; i++) {
			g2d.drawImage(ghostImages.get(i * 2 + ghosts[i].getImageNum()), ghosts[i].xPos, ghosts[i].yPos, null);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
