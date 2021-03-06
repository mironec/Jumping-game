package mironec.jumpinggame.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import mironec.jumpinggame.Game;

public class Player {

	private int x, y;
	private float precX, precY;
	private boolean isJumping;
	private float velX, velY;
	private int width, height;
	private Game g;
	private int number;
	
	private static final float JUMPING_FORCE = 22.0F;
	private BufferedImage image;
	
	public Player(int x, int y, Game g) {
		this.x = x; precX = x;
		this.y = y; precY = y;
		
		this.width = Game.PLAYER_WIDTH;
		this.height = Game.PLAYER_HEIGHT;
		
		isJumping = false;
		velX = velY = 0;
		
		number = -1;
		
		this.g = g;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		constructImage();
	}
	
	//Tick 20ms
	/**
	 * Handles some of the logic that applies to the player.
	 * logic(int safeGround) handles all the logic.
	 * 
	 * Applies gravity, friction, jumps and moves to the sides.
	 */
	private void logic(){
		if(g.getMain().keys[KeyEvent.VK_RIGHT]||g.getMain().keys[KeyEvent.VK_D]){velX+=2.0;}
		if(g.getMain().keys[KeyEvent.VK_LEFT]||g.getMain().keys[KeyEvent.VK_A]){velX-=2.0;}
		if((g.getMain().keys[KeyEvent.VK_UP]||g.getMain().keys[KeyEvent.VK_SPACE]||g.getMain().keys[KeyEvent.VK_W])&&isJumping==false){velY=-JUMPING_FORCE; isJumping=true;}
		this.precX += velX - (velX*Game.FRICTION)/2;
		this.precY += velY + Game.GRAVITY/2;
		this.x = (int)precX;
		this.y = (int)precY;
		this.velY += Game.GRAVITY;
		this.velX = velX*(1-Game.FRICTION);
	}
	
	/**
	 * If the player is going in the Y+ direction, he is falling.
	 * 
	 * @return Returns true if the player is falling.
	 */
	public boolean isFalling(){
		return velY>0;
	}
	
	/**
	 * Construct the image for the player. Needed in the paint(g) function. Usually called in the constructor and when setting a new number.
	 */
	private void constructImage(){
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillOval(0, 0, width, height);
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial",Font.PLAIN,height-10));
		String str = ""+number;
		g.drawString(str, width/2-g.getFontMetrics().stringWidth(str)/2, height-10);
	}
	
	/**
	 * Handles all the rendering concerning the player
	 * @param g Graphics used to draw.
	 * @param viewPointX offset used in the X direction.
	 * @param viewPointY offset used in the Y direction.
	 */
	public void paint(Graphics2D g, int viewPointX, int viewPointY){
		g.drawImage(image, null, x-viewPointX, y-viewPointY);
	}
	
	/**
	 * Handles all the logic concerning the player.
	 * 
	 * @param safeGround the y coordinate of the safeGround used in Game.
	 */
	public void logic(int safeGround){
		logic();
		if(precY+height>safeGround){
			precY=y=safeGround-height;
			if(velY>5.0F){
				velY=-velY*0.5F;
			}
			else{
				velY=0;
				isJumping=false;
			}
		}
		if(precX<0){precX=x=0; velX=-velX;}
		if(precX+width>g.getMain().getWidth()){precX=x=g.getMain().getWidth()-width; velX=-velX;}
	}

	public Rectangle getRect(){
		return new Rectangle(x,y,width,height);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
		constructImage();
	}

}
