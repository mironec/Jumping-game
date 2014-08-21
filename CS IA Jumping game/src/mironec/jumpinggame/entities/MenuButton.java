package mironec.jumpinggame.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class MenuButton {
	
	private String text;
	private int x,y;
	private int width,height;
	
	public MenuButton(String text, int x, int y, int width, int height){
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void paint(Graphics2D g){
		g.setColor(Color.black);
		g.fillRect(x, y, width, height);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 40));
		g.drawString(text, x+width/2-g.getFontMetrics().stringWidth(text)/2, y+height/2+g.getFontMetrics().getHeight()/4);
	}
	
}
