package mironec.jumpinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Random;

import mironec.jumpinggame.entities.Platform;
import mironec.jumpinggame.entities.Player;

public class Game{
	public static final float GRAVITY = 1.0f;
	public static final float FRICTION = 0.1f;
	public static final int PLAYER_WIDTH = 50;
	public static final int PLAYER_HEIGHT = 50;
	public static final int PLATFORM_WIDTH = 100;
	public static final int PLATFORM_HEIGHT = 30;
	public static final int MAX_NUMBER = 20;
	public static final int EXPANDING_TICKS = 25;
	public static final int DYING_TICKS = 500;
	private static final int MIN_SPACE = 30;
	private static final int HEIGHT_BETWEEN_PLATFORMS = 200;
	/**
	 * How many levels of platforms are visible on the screen at the same time.
	 */
	private static int LEVELS_VISIBLE = 3;
	
	private ArrayList<Platform> platforms;
	private ArrayList<Platform> platformsToAdd;
	private ArrayList<Platform> platformsToRemove;
	private Player player;
	private Main m;
	private int safeGround;
	private int oldSafeGround;
	private int viewPointX,viewPointY;
	private Random rand;
	private int ticksToExpand;
	private int ticksToDie;
	private Rectangle blackSpace;
	private int score;
	private int levelsDone;
	private int width, height;
	
	public Game(Main m){
		this.m=m;
	}
	
	/**
	 * Generates 2-4 platforms on the specified coordinates.
	 * 
	 * @param y The y level at which to generate the platforms. The top of the platforms will rest at this level.
	 */
	public void generateLevel(int y){
		int numOfPlatforms = rand.nextInt(3) + 2; //2-4
		
		int emptySpace = m.getWidth()-numOfPlatforms*PLATFORM_WIDTH - (numOfPlatforms+1)*MIN_SPACE;
		double weights[] = new double[numOfPlatforms+1];
		double spaces[] = new double[numOfPlatforms+1];
		double totalWeight = 0;
		for(int i = 0;i<numOfPlatforms+1;i++){
			weights[i] = rand.nextDouble();
			totalWeight += weights[i];
		}
		for(int i = 0;i<numOfPlatforms+1;i++){
			spaces[i] = MIN_SPACE + weights[i]/totalWeight*emptySpace;
		}
		int Xfar = (int)spaces[0];
		for(int i = 0;i<numOfPlatforms;i++){
			addPlatform(new Platform(Xfar, y, rand, MAX_NUMBER, this));
			Xfar+=(int)spaces[i+1]+PLATFORM_WIDTH;
		}
		
	}
	
	/**
	 * Raises the safeground to the given level.
	 * @param y The y coordinate where to raise the safe ground.
	 * @param p The platform that caused the expansion.
	 */
	public void raiseSafeGround(int y, Platform p){
		oldSafeGround=safeGround;
		safeGround=y;
		ArrayList<Platform> avail = new ArrayList<Platform>();
		for(Platform pe : platforms){
			if(pe.getY()>=y){removePlatform(pe);}
			if(pe.getY()>=y-HEIGHT_BETWEEN_PLATFORMS&&pe.getY()<y){avail.add(pe);}
		}
		player.setNumber(avail.get(rand.nextInt(avail.size())).getResult());
		generateLevel(y-HEIGHT_BETWEEN_PLATFORMS*LEVELS_VISIBLE);
		ticksToExpand = EXPANDING_TICKS;
		blackSpace = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
		levelsDone++;
		score=calcScore(score,levelsDone);
	}
	
	/**
	 * Calculates the score after clearing  a platform.
	 * 
	 * @param score Initial score
	 * @param levelsDone Levels done until now
	 * @return returns the new score
	 */
	public int calcScore(int score, int levelsDone){
		return score + 9 + levelsDone;
	}
	
	/**
	 * Required to start the game. Initializes variables and generates starting platforms.
	 * 
	 * @param width The width of the screen where the game will be played.
	 * @param height The height of the screnn where the game will be played.
	 */
	public void start(int width, int height){
		platforms = new ArrayList<Platform>();
		platformsToAdd = new ArrayList<Platform>();
		platformsToRemove = new ArrayList<Platform>();
		
		this.width = width;
		this.height = height;
		
		player = new Player(width/2-PLAYER_WIDTH/2, -PLAYER_HEIGHT, this);
		viewPointX=0;
		viewPointY=-height+10;
		safeGround=0;
		blackSpace=null;
		
		rand = new Random();
		LEVELS_VISIBLE=0;
		for(int y=-HEIGHT_BETWEEN_PLATFORMS;y>-height-HEIGHT_BETWEEN_PLATFORMS;y-=HEIGHT_BETWEEN_PLATFORMS){
			LEVELS_VISIBLE++;
			generateLevel(y);
		}
		player.setNumber(platformsToAdd.get(0).getResult());
		ticksToExpand=-1;
		ticksToDie=-1;
		score = 0;
		levelsDone = 0;
	}
	
	/**
	 * All rendering concerning the game is handled here.
	 * @param g The graphics the game uses to render.
	 */
	public void paint(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		
		
		g.setColor(Color.black);
		if(blackSpace!=null){
			g.fillRect(blackSpace.x-viewPointX, blackSpace.y-viewPointY, blackSpace.width, blackSpace.height);
			g.fillRect(0, oldSafeGround-viewPointY, width, height-oldSafeGround+viewPointY);
		}
		else{
			g.fillRect(0, safeGround-viewPointY, width, height-safeGround+viewPointY);
		}
		
		g.setFont(new Font("Arial",Font.PLAIN,30));
		g.drawString(""+score, 10, g.getFontMetrics().getHeight()+5);
		
		for(Platform p : new ArrayList<Platform>(platforms)){
			p.paint(g, width, height, viewPointX, viewPointY);
		}
		player.paint(g, viewPointX, viewPointY);
		
	}
	
	/**
	 * All logic concerning the game is handled here.
	 */
	public void logic(){
		//Expanding of a platform
		if(ticksToExpand > 0){
			blackSpace.x -= (blackSpace.x)/ticksToExpand;
			blackSpace.width += (blackSpace.x)/ticksToExpand + (m.getWidth() - blackSpace.x-blackSpace.width)/ticksToExpand;
			blackSpace.height += (m.getHeight() + viewPointY - blackSpace.y - blackSpace.height)/ticksToExpand;
			viewPointY += (safeGround-m.getHeight()-viewPointY+50)/ticksToExpand;
			ticksToExpand--;
		}
		//Finished expanding
		if(ticksToExpand==0){
			blackSpace=null;
			viewPointY=safeGround-m.getHeight()+50;
			ticksToExpand=-1;
			ticksToDie=DYING_TICKS;
		}
		//Movement of the screen downwards with passing time
		if(ticksToDie > 0 && ticksToExpand == -1){
			viewPointY = safeGround-m.getHeight()+50-(int)(50f*(DYING_TICKS-ticksToDie)/DYING_TICKS);
			ticksToDie--;
		}
		//Dying by running out of time
		if(ticksToDie==0){
			lose();
		}
		//Logic of player and platforms
		player.logic(safeGround);
		for(Platform p : platformsToAdd){
			platforms.add(p);
		}
		platformsToAdd.clear();
		for(Platform p : platformsToRemove){
			platforms.remove(p);
		}
		platformsToRemove.clear();
		for(Platform p : platforms){
			p.logic(player);
		}
	}
	
	/**
	 * Adds a platform to the game.
	 * @param p The platform to add.
	 */
	public void addPlatform(Platform p){
		platformsToAdd.add(p);
	}
	
	/**
	 * Removes a platform from the game.
	 * @param p The platform to remove.
	 */
	public void removePlatform(Platform p){
		platformsToRemove.add(p);
	}
	
	/**
	 * Loses the game.
	 * Currently closes the window forcefully.
	 */
	public void lose(){
		System.exit(0);
	}
	
	public Main getMain(){
		return m;
	}
}