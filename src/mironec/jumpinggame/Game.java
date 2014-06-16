package mironec.jumpinggame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
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
	
	private ArrayList<Platform> platforms;
	private ArrayList<Platform> platformsToAdd;
	private ArrayList<Platform> platformsToRemove;
	private Player player;
	private Main m;
	private int safeGround;
	private int viewPointX,viewPointY;
	private Random rand;
	private int ticksToExpand;
	private Rectangle blackSpace;
	
	public Game(Main m){
		this.m=m;
	}
	
	public void generateLevel(int y){
		
	}
	
	public void raiseSafeGround(int y, Platform p){
		safeGround=y;
		for(Platform pe : platforms){
			if(pe.getY()>=y){removePlatform(pe);}
		}
		ticksToExpand = EXPANDING_TICKS;
		blackSpace = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());
	}
	
	public void start(int width, int height){
		platforms = new ArrayList<Platform>();
		platformsToAdd = new ArrayList<Platform>();
		platformsToRemove = new ArrayList<Platform>();
		
		player = new Player(width/2-PLAYER_WIDTH/2, -PLAYER_HEIGHT, this);
		viewPointX=0;
		viewPointY=-height+10;
		safeGround=0;
		
		rand = new Random();
		addPlatform(new Platform(100, -200, rand, MAX_NUMBER, this));
		player.setNumber(platformsToAdd.get(0).getResult());
		ticksToExpand=-1;
	}
	
	public void paint(Graphics2D g, BufferedImage img){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		g.setColor(Color.black);
		g.fillRect(0, safeGround-viewPointY, img.getWidth(), img.getHeight()-safeGround+viewPointY);
		
		if(blackSpace!=null) g.fill(blackSpace);
		
		for(Platform p : platforms){
			p.paint(g, img, viewPointX, viewPointY);
		}
		player.paint(g, img, viewPointX, viewPointY);
		
	}
	
	public void logic(){
		if(ticksToExpand > 0){
			blackSpace.x += (blackSpace.x)/ticksToExpand;
			blackSpace.width += (blackSpace.x)/ticksToExpand + (m.getWidth() - blackSpace.x-blackSpace.width)/ticksToExpand;
			blackSpace.height += (m.getHeight() + viewPointY - blackSpace.y - blackSpace.height)/ticksToExpand;
			ticksToExpand--;
		}
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
	
	public void addPlatform(Platform p){
		platformsToAdd.add(p);
	}
	
	public void removePlatform(Platform p){
		platformsToRemove.add(p);
	}
	
	public void lose(){
		System.exit(0);
	}
	
	public Main getMain(){
		return m;
	}
}