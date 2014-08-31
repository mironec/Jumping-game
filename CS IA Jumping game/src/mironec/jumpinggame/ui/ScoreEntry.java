package mironec.jumpinggame.ui;

public class ScoreEntry {

	private int score;
	private String name;
	
	public ScoreEntry(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public String getName(){
		return name;
	}
	
	public int getScore(){
		return score;
	}

}
