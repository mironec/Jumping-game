package mironec.jumpinggame;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import mironec.jumpinggame.ui.MenuButton;
import mironec.jumpinggame.ui.ScoreEntry;

public class Main extends Applet implements KeyListener, MouseListener {
	
	private static final long serialVersionUID = 2L;
	
	private Image frontBuffer,backBuffer;
	private Graphics frontBufferG,backBufferG;
	int fps=0,frames=0;
	long lastSecond=0;
	long lastRenderTime;
	long lastLogicTime;
	private Localization locale;
	private MenuButton resume,highscores,exit;
	
	private int renderMode;
	private static final int RENDER_MODE_GAME = 0;
	private static final int RENDER_MODE_PAUSED_GAME = 1;
	private static final int RENDER_MODE_HIGHSCORES = 2;
	/**
	 * Time for one logic cycle measured in miliseconds.
	 */
	private static final int TICK_MS = 20;
	private Game game;
	
	/**
	 * Keys[keyCode] is true if the key with the corresponding keyCode is held down.
	 * Used to tell which keys are currently pressed.
	 */
	public boolean keys[];

	/**
	 * Creates a frame with the applet inside.
	 * @param args do nothing
	 */
	public static void main(String[] args) {
		Frame f = new Frame("Jumping game");
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		f.setSize(800,600);
		Main m = new Main();
		f.add(m);
		f.setVisible(true);
		m.init();
	}
	
	@Override
	public void init() {
		frontBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		backBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		frontBufferG = frontBuffer.getGraphics();
		backBufferG = backBuffer.getGraphics();
		
		keys = new boolean[256];
		addKeyListener(this);
		addMouseListener(this);
		
		locale = new Localization("EN");
		
		Thread t = new Thread(){
			@Override
			public void run() {
				while(true){
					lastRenderTime = System.nanoTime();
					render();
					repaint();
					frames++;
					if(System.currentTimeMillis()/1000 != lastSecond){
						fps=(int)(frames*(System.currentTimeMillis()/1000D-lastSecond));
						lastSecond=System.currentTimeMillis()/1000;
						frames=0;
					}
					while(System.nanoTime() - lastRenderTime < 15660 * 1000){
						//Thread.yield();
						try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			}
		};
		Thread logic = new Thread(){
			@Override
			public void run() {
				while(true){
					if(System.nanoTime()-lastLogicTime > TICK_MS * 1000 * 1000 * 5){lastLogicTime = System.nanoTime();}
					else{lastLogicTime+=TICK_MS*1000*1000;}
					logic();
					while(System.nanoTime() - lastLogicTime < TICK_MS * 1000 * 1000){
						//Thread.yield();
						try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			}
		};
		game = new Game(this);
		game.start(getWidth(), getHeight());
		t.start();
		logic.start();
		//pauseGame();
	}
	
	//Tick 20ms
	/**
	 * Handles the logic of the application, usually just calls game logic.
	 */
	private void logic(){
		if(renderMode == RENDER_MODE_GAME && game != null){
			game.logic();
		}
	}
	
	/**
	 * Handles the render of the application, usually just calls game render and handles backbuffers.
	 */
	private void render(){
		if(renderMode == RENDER_MODE_GAME && game != null){
			game.paint((Graphics2D)backBufferG);
		}
		if(renderMode == RENDER_MODE_PAUSED_GAME && game != null){
			game.paint((Graphics2D)backBufferG);
			backBufferG.setColor(new Color(1.0f, 1.0f, 1.0f, 0.8f));
			backBufferG.fillRect(0, 0, getWidth(), getHeight());
			 
			resume.paint((Graphics2D) backBufferG);
			highscores.paint((Graphics2D) backBufferG);
			exit.paint((Graphics2D) backBufferG);
		}
		if(renderMode == RENDER_MODE_HIGHSCORES){
			backBufferG.setColor(Color.black);
			backBufferG.fillRect(0, 0, getWidth(), getHeight());
			
			backBufferG.setColor(Color.white);
			backBufferG.setFont(new Font("Arial", Font.PLAIN, Game.PLATFORM_HEIGHT*2));
			String str = locale.getWord("highscores");
			backBufferG.drawString(str, getWidth()/2-backBufferG.getFontMetrics().stringWidth(str)/2, backBufferG.getFontMetrics().getHeight());
			ScoreEntry[] scores = loadHighscores();
			for(int x=0;x<scores.length;x++){
				backBufferG.drawString((x+1)+". "+scores[x].getName(), 50, 150+x*50);
				backBufferG.drawString(""+scores[x].getScore(), getWidth()-50-backBufferG.getFontMetrics().stringWidth(""+scores[x].getScore()), 150+x*50);
			}
		}
		
		frontBufferG.drawImage(backBuffer, 0, 0, this);
	}
	
	private ScoreEntry[] loadHighscores(){
		ScoreEntry[] scores = {new ScoreEntry("Miron", 1000),
				new ScoreEntry("Miron", 700),
				new ScoreEntry("Miron", 250),
				new ScoreEntry("NieMiron", 150),
				new ScoreEntry("AnoMiron", 50)};
		return scores;
	}
	
	@Override
	public void paint(Graphics g) {
		update(g);
	}
	
	@Override
	public void update(Graphics g) {
		g.drawImage(frontBuffer,0,0,this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()>=0&&e.getKeyCode()<keys.length){
			keys[e.getKeyCode()]=true;
		}
		//Pausing the game by pressing escape
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
			if(renderMode==RENDER_MODE_GAME) pauseGame();
			else if(renderMode==RENDER_MODE_PAUSED_GAME) unpauseGame();
			else if(renderMode==RENDER_MODE_HIGHSCORES) unpauseGame();
		}
	}

	private void showHighscores(){
		renderMode = RENDER_MODE_HIGHSCORES;
	}
	
	/**
	 * Pauses the game.
	 * Creates the menu and stops all timers.
	 */
	private void pauseGame(){
		resume = new MenuButton(locale.getWord("resume"),getWidth()/5,getHeight()/9*2,getWidth()/5*3,getHeight()/9){
			public void buttonAction(){
				unpauseGame();
			}
		};
		highscores = new MenuButton(locale.getWord("highscores"),getWidth()/5,getHeight()/9*4,getWidth()/5*3,getHeight()/9){
			public void buttonAction(){
				showHighscores();
			}
		};
		exit = new MenuButton(locale.getWord("exit"),getWidth()/5,getHeight()/9*6,getWidth()/5*3,getHeight()/9){
			public void buttonAction() {
				System.exit(0);
			}
		};
		renderMode = RENDER_MODE_PAUSED_GAME;
	}
	
	/**
	 * Unpauses the game.
	 * Hides the menu and resumes all timers.
	 */
	private void unpauseGame(){
		renderMode = RENDER_MODE_GAME;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()>=0&&e.getKeyCode()<keys.length){
			keys[e.getKeyCode()]=false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(renderMode == RENDER_MODE_PAUSED_GAME){
			resume.mouseClicked(e);
			highscores.mouseClicked(e);
			exit.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
