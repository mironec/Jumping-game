package mironec.jumpinggame.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
	
	private static final float JUMPING_FORCE = 20.0F; 
	
	public Player(int x, int y, Game g) {
		this.x = x; precX = x;
		this.y = y; precY = y;
		
		this.width = Game.PLAYER_WIDTH;
		this.height = Game.PLAYER_HEIGHT;
		
		isJumping = false;
		velX = velY = 0;
		
		number = -1;
		
		this.g = g;
	}
	
	//Tick 20ms
	public void logic(){
		if(g.getMain().keys[KeyEvent.VK_RIGHT]){velX+=2.0;}
		if(g.getMain().keys[KeyEvent.VK_LEFT]){velX-=2.0;}
		if(g.getMain().keys[KeyEvent.VK_UP]&&isJumping==false){velY=-JUMPING_FORCE; isJumping=true;}
		this.precX += velX;
		this.precY += velY - Game.GRAVITY/2;
		this.x = (int)precX;
		this.y = (int)precY;
		this.velY += Game.GRAVITY;
		this.velX = velX*(1-Game.FRICTION);
	}
	
	public boolean isFalling(){
		return velY>0;
	}
	
	public void paint(Graphics2D g, BufferedImage img, int viewPointX, int viewPointY){
		g.setColor(Color.black);
		g.fillOval(x-viewPointX, y-viewPointY, width, height);
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial",Font.PLAIN,height-10));
		String str = ""+number;
		g.drawString(str, x-viewPointX+width/2-g.getFontMetrics().stringWidth(str)/2, y-viewPointY+height-10);
	}
	
	public void logic(int safeGround){
		logic();
		if(precY+height>safeGround){
			precY=y=safeGround-height;
			if(velY>10.0F){
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
	}

}
