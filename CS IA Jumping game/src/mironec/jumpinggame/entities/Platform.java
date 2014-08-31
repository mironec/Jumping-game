package mironec.jumpinggame.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import mironec.jumpinggame.Game;

public class Platform {

	private int x,y,width,height;
	private int number1,number2,operation,result;
	
	private static final int OPERATION_PLUS = 0;
	private static final int OPERATION_MINUS = 1;
	
	private Game g;
	
	private BufferedImage image;
	
	/**
	 * 
	 * @param x Where the platform will be on the X axis
	 * @param y Where the platform will be on the Y axis
	 * @param rand Random number generator, to keep the games the same if the same seed is used
	 * @param maxNumber Max number for all the numbers on the platform (number1, number2 and result)
	 * @param g Game owning this platform.
	 */
	public Platform(int x, int y, Random rand, int maxNumber, Game g) {
		this.x = x;
		this.y = y;
		
		this.width = Game.PLATFORM_WIDTH;
		this.height = Game.PLATFORM_HEIGHT;
		
		number1 = rand.nextInt(maxNumber) + 1;
		if(number1==20){operation=OPERATION_MINUS;}
		else if(number1==1){operation=OPERATION_PLUS;}
		else{operation = rand.nextInt(2)==1?OPERATION_MINUS:OPERATION_PLUS;}
		if(operation==OPERATION_MINUS){
			number2 = rand.nextInt(number1-1)  + 1;
		}
		if(operation==OPERATION_PLUS){
			number2 = rand.nextInt(20-number1) + 1;
		}
		result = calcResult();
		this.g = g;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		constructImage();
	}
	
	/**
	 * Constructs the image for this platform. Needed for paint(g). Called in the constructor.
	 */
	private void constructImage(){
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.white);
		g.setFont(new Font("Arial",Font.PLAIN,height-4));
		String str = number1+(operation==OPERATION_MINUS?"-":"+")+number2;
		g.drawString(str, width/2-g.getFontMetrics().stringWidth(str)/2, height-4);
	}
	
	/**
	 * 
	 * @param g The graphics supplied from the Game, used to paint into its canvas
	 * @param gwidth The width of the Game canvas.
	 * @param gheight The height of the Game canvas.
	 * @param viewPointX The viewpoint of the Game, to offset the drawing
	 * @param viewPointY The viewpoint of the Game, to offset the drawing
	 */
	public void paint(Graphics2D g, int gwidth, int gheight, int viewPointX, int viewPointY){
		if(x>gwidth+viewPointX){return;}
		if(x+width<viewPointX){return;}
		if(y>gheight+viewPointY){return;}
		if(y+height<viewPointY){return;}
		
		g.drawImage(image, null, x-viewPointX, y-viewPointY);
	}
	
	/**
	 * Detects collisions with the player and determines whether to break or to expand.
	 * 
	 * @param p - The player to detect collisions with
	 * 
	 */
	public void logic(Player p){
		if(!p.isFalling()){return;}
		if(new Rectangle(x,y,width,height).intersects(p.getRect())){
			if(p.getNumber()!=result){
				g.lose();
			}
			else{
				g.raiseSafeGround(y,this);
			}
		}
	}

	/**
	 * Calculates and returns the result on this platform.
	 * Should be only used in the constructor and then it should be stored in platform's result variable.
	 * 
	 * @return returns the result of this platform
	 */
	private int calcResult(){
		return operation==OPERATION_MINUS?number1-number2:number1+number2;
	}
	
	public int getResult(){
		return result;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
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
	
}
