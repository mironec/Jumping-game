package mironec.jumpinggame;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class Main extends Applet implements KeyListener{
	
	private static final long serialVersionUID = 2L;
	
	private Image frontBuffer,backBuffer;
	private Graphics frontBufferG,backBufferG;
	int fps=0,frames=0;
	long lastSecond=0;
	long lastRenderTime;
	long lastLogicTime;
	
	private int renderMode;
	private static final int RENDER_MODE_GAME = 0;
	private static final int RENDER_MODE_PAUSED_GAME = 1;
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
						Thread.yield();
						try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			}
		};
		game = new Game(this);
		game.start(getWidth(), getHeight());
		t.start();
		logic.start();
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
		}
		
		frontBufferG.drawImage(backBuffer, 0, 0, this);
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
			renderMode= (renderMode==RENDER_MODE_GAME?RENDER_MODE_PAUSED_GAME:RENDER_MODE_GAME);
		}
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

}
