package mironec.jumpinggame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import mironec.jumpinggame.Game;
import mironec.jumpinggame.Main;

public class Highscores implements KeyListener, MouseListener {
	
	private Main m;
	private ScoreEntry[] scores;
	private String currentString;
	private int currentScore;
	
	private MenuButton submit, returnu, reset;
	
	/**
	 * Number of entries in the high scores.
	 */
	private static final int numOfScores = 5;
	/**
	 * Name of the file to store highscores.
	 */
	private static final String highscoresFileName = "highscores";
	
	public Highscores(Main m){
		this.m = m;
		currentString = "";
		currentScore = 0;
		scores = loadHighscores();
	}
	
	/**
	 * Paints the highscores.
	 * @param g The graphics to paint with.
	 */
	public void paint(Graphics2D g){
		if(m.getRenderMode() == Main.RENDER_MODE_HIGHSCORES){
			g.setColor(Color.black);
			g.fillRect(0, 0, m.getWidth(), m.getHeight());
			
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.PLAIN, Game.PLATFORM_HEIGHT*2));
			String str = m.getLocalization().getWord("highscores");
			g.drawString(str, m.getWidth()/2-g.getFontMetrics().stringWidth(str)/2, g.getFontMetrics().getHeight());
			for(int x=0;x<scores.length;x++){
				g.drawString((x+1)+". "+scores[x].getName(), 50, 150+x*50);
				g.drawString(""+scores[x].getScore(), m.getWidth()-50-g.getFontMetrics().stringWidth(""+scores[x].getScore()), 150+x*50);
			}
			reset.paint(g);
			returnu.paint(g);
		}
		if(m.getRenderMode() == Main.RENDER_MODE_HIGHSCORE_ENTER){
			g.setColor(Color.white);
			g.fillRect(0, 0, m.getWidth(), m.getHeight());
			
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.PLAIN, Game.PLATFORM_HEIGHT*2));
			String str = m.getLocalization().getWord("enteryourname");
			g.drawString(str, m.getWidth()/2-g.getFontMetrics().stringWidth(str)/2, m.getHeight()/2-g.getFontMetrics().getHeight());
			g.drawString(currentString, m.getWidth()/2-g.getFontMetrics().stringWidth(currentString)/2, m.getHeight()/2+g.getFontMetrics().getHeight());
			g.fillRect(m.getWidth()/2-Game.PLATFORM_WIDTH*3, m.getHeight()/2+g.getFontMetrics().getHeight()+2, Game.PLATFORM_WIDTH*6, 4);
			submit.paint(g);
			returnu.paint(g);
		}
	}
	
	public void showHighscores(){
		scores = loadHighscores();
		
		reset = new MenuButton(m.getLocalization().getWord("reset"), m.getWidth()/2-Game.PLATFORM_WIDTH*2, Game.PLATFORM_HEIGHT*13, Game.PLATFORM_WIDTH*4, Game.PLATFORM_HEIGHT*2){
			@Override
			public void buttonAction() {
				resetHighscores();
				saveHighscores();
			}
		};
		reset.setDrawBorder(true);
		reset.setWhiteText(true);
		
		returnu = new MenuButton(m.getLocalization().getWord("return"), m.getWidth()/2-Game.PLATFORM_WIDTH*2, Game.PLATFORM_HEIGHT*16, Game.PLATFORM_WIDTH*4, Game.PLATFORM_HEIGHT*2){
			@Override
			public void buttonAction() {
				m.unpauseGame();
			}
		};
		returnu.setDrawBorder(true);
		returnu.setWhiteText(true);
	}
	
	public void showHighscoreEnter(int score){
		currentString = "";
		currentScore = score;
		
		submit = new MenuButton(m.getLocalization().getWord("submit"), m.getWidth()/2-Game.PLATFORM_WIDTH*2, m.getHeight()/2+Game.PLATFORM_HEIGHT*4, Game.PLATFORM_WIDTH*4, Game.PLATFORM_HEIGHT*2){
			@Override
			public void buttonAction() {
				enterScore(currentString, currentScore);
				saveHighscores();
				m.showHighscores();
			}
		};
		submit.setDrawBorder(true);
		submit.setWhiteText(false);
		
		returnu = new MenuButton(m.getLocalization().getWord("return"), m.getWidth()/2-Game.PLATFORM_WIDTH*2, m.getHeight()/2+Game.PLATFORM_HEIGHT*7, Game.PLATFORM_WIDTH*4, Game.PLATFORM_HEIGHT*2){
			@Override
			public void buttonAction() {
				m.unpauseGame();
			}
		};
		returnu.setDrawBorder(true);
		returnu.setWhiteText(false);
	}
	
	/**
	 * Loads the highscores from the highscores file.
	 * @return Returns an array of score entries parsed from the highscores file.
	 */
	private ScoreEntry[] loadHighscores(){
		ScoreEntry[] scores;
		scores = new ScoreEntry[5];
		File f = new File(highscoresFileName);
		try {
		if(f.exists()&&f.isFile()){
				BufferedReader br = new BufferedReader(new FileReader(f));
				for(int x=0;br.ready();x++){
					String[] str = br.readLine().split(";");
					if(str.length!=2) continue;
					scores[x] = new ScoreEntry(str[0], Integer.parseInt(str[1]));
				}
				br.close();
			}
			else{
				resetHighscores();
			}
		}
		catch (IOException e) {
			resetHighscores();
		}
		return scores;
	}
	
	public void resetHighscores(){
		for(int x=0;x<numOfScores;x++)
			scores[x] = new ScoreEntry("Nobody", 0);
	}
	
	public void saveHighscores(){
		saveHighscores(scores);
	}
	
	/**
	 * Saves the scores to the highscores file.
	 * @param scores the scores to be saved
	 */
	public void saveHighscores(ScoreEntry[] scores){
		File f = new File(highscoresFileName);
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for(ScoreEntry score : scores){
				bw.write(score.getName()+";"+score.getScore()+System.lineSeparator());
			}
			bw.close();
		}
		catch(IOException e){}
	}
	
	public boolean isScoreEligibleForHighscores(int score){
		return score>scores[numOfScores-1].getScore();
	}
	
	public void enterScore(String name, int score){
		enterScore(new ScoreEntry(name, score));
	}
	
	public void enterScore(ScoreEntry scoreEntry){
		int position = numOfScores;
		while(scoreEntry.getScore() > scores[position-1].getScore()){
			position--;
			if(position == 0) break; //Top of highscores
		}
		
		for(int x = numOfScores-1;x>position;x--){
			scores[x]=scores[x-1];
		}
		scores[position] = scoreEntry;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(m.getRenderMode() == Main.RENDER_MODE_HIGHSCORE_ENTER){
			if((e.getKeyChar()+"").matches("[A-Za-z0-9_ ]")&&currentString.length()<10){
				currentString += e.getKeyChar();
			}
			else if(e.getKeyChar()==KeyEvent.VK_BACK_SPACE){
				if(currentString.length()>0)
					currentString = currentString.substring(0, currentString.length()-1);
			}
			else if(e.getKeyChar()==KeyEvent.VK_ENTER){
				enterScore(currentString, currentScore);
				saveHighscores();
				m.showHighscores();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(m.getRenderMode() == Main.RENDER_MODE_HIGHSCORES){
			reset.mouseClicked(e);
			returnu.mouseClicked(e);
		}
		if(m.getRenderMode() == Main.RENDER_MODE_HIGHSCORE_ENTER){
			submit.mouseClicked(e);
			returnu.mouseClicked(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
