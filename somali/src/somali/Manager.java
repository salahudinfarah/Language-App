package somali;
import java.util.*;

public final class Manager {
	// HashMap to hold each language object
	private static HashMap<String, Language> map = new HashMap<String, Language>();
	private static String primary_language;
	private static String secondary_language;
	
	/* Constructor creates and initializes the language objects
	 * @param primary -> language to be tested on 
	 * @param secondary -> language to be tested with
	 */
	public static boolean setLanguage(String primary, String secondary) {
		primary_language = primary.toLowerCase();
		secondary_language = secondary.toLowerCase();
		if (primary_language.equals(secondary_language)) {
			return false;
		}
		// Initialize language objects
		Language key_language = new Language(primary_language);
		Language answer_language = new Language(secondary_language);
		key_language.setWords();
		answer_language.setWords();
		// If languages contain no words, return
		if (key_language.isEmpty() || answer_language.isEmpty()) {
			return false;
		} else {
			map.put(primary_language, key_language);
			map.put(secondary_language, answer_language);
		}
		return true;
	}
	
	public static String getPrimary() {
		return primary_language;
	}

	public static String getSecondary() {
		return secondary_language;
	}
	
	// Returns the key word that will be tested on
	public static Word getKeyWord() {
		if (primary_language == null) {
			return null;
		}
		return map.get(primary_language).getRandomWord();
	}
	
	// Get equivalent word in the secondary language
	public static Word getEquivalent(Word word) {
		return map.get(secondary_language).getWords().get(word.getIndex());
	}
	
	// Return a random answer option
	public static Word getAnswer() {
		if (secondary_language == null) {
			return null;
		}
		return map.get(secondary_language).getRandomWord();
	}
}
