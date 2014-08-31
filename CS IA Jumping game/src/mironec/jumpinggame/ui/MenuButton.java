package mironec.jumpinggame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class MenuButton implements MouseListener {
	
	private String text;
	private int x,y;
	private int width,height;
	private BufferedImage image;
	
	public MenuButton(String text, int x, int y, int width, int height){
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		constructImage();
	}
	
	/**
	 * Constructs the image of the menuButton. Required to properly paint(g) it.
	 */
	private void constructImage(){
		Graphics2D g = (Graphics2D)image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.PLAIN, 40));
		g.drawString(text, width/2-g.getFontMetrics().stringWidth(text)/2, height/2+g.getFontMetrics().getHeight()/4);
	}
	
	/**
	 * Paints the menuButton on the specified graphics.
	 * @param g The graphics to paint it on
	 */
	public void paint(Graphics2D g){
		g.drawImage(image, null, x, y);
	}
	
	/**
	 * What will happen when the button is pressed.
	 */
	public void buttonAction(){}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(getBounds().contains(e.getPoint())) buttonAction();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
	private Rectangle getBounds() {
		return new Rectangle(x,y,width,height);
	}
	
}
