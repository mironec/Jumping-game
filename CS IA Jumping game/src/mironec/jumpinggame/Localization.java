package mironec.jumpinggame;

import java.util.Hashtable;
import java.util.Map;

public class Localization {
	
	private static final String LOCALE_ENGLISH = "EN";
	private static final int LOCALE_ENGLISH_CODE = 1;
	private static final String LOCALE_SLOVAK = "SK";
	private static final int LOCALE_SLOVAK_CODE = 2;
	
	@SuppressWarnings("serial")
	public static final Map<String, String[]> words = new Hashtable<String, String[]>(){{
		put("resume", new String[]{"resume","Resume","Sp� do hry"});
		put("highscores", new String[]{"highscores","High scores","Vysok� sk�re"});
		put("exit", new String[]{"exit","Exit","Ukon�i�"});
		put("enteryourname", new String[]{"enteryourname","Enter your name:","Nap�te svoje meno:"});
		put("submit", new String[]{"submit","Submit","Odosla�"});
		put("return", new String[]{"return","Return","Vr�ti� sa"});
		put("reset", new String[]{"reset","Reset high scores","Vynulova� tabu�ku"});
		put("playAgain", new String[]{"playAgain","Play again","Hra� znovu"});
		put("gameOver", new String[]{"gameOver","Game over","Prehra"});
	}};
	
	private String locale;
	
	public Localization(String locale){
		this.locale = locale;
	}
	
	public static String getWord(String word, String locale){
		if(locale.equalsIgnoreCase(LOCALE_ENGLISH)){return words.get(word)[LOCALE_ENGLISH_CODE];}
		else if(locale.equalsIgnoreCase(LOCALE_SLOVAK)){return words.get(word)[LOCALE_SLOVAK_CODE];}
		else{return words.get(word)[0];}
	}
	
	public String getWord(String word){
		return getWord(word,this.locale);
	}
	
}
