package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.util.List;

import org.w3c.dom.Document;

import cz.cvut.fit.vyhliluk.vocserver.core.Lang;

public class GoogleScraperStrategy implements ScreenScraperTranslatorStrategy {

	// ================= STATIC ATTRIBUTES ======================

	private static GoogleScraperStrategy instance = null;

	// ================= INSTANCE ATTRIBUTES ====================

	// ================= STATIC METHODS =========================

	public static synchronized GoogleScraperStrategy getInstance() {
		if (instance == null) {
			instance = new GoogleScraperStrategy();
		}
		return instance;
	}

	// ================= CONSTRUCTORS ===========================

	private GoogleScraperStrategy() {

	}

	// ================= OVERRIDEN METHODS ======================

	// ================= INSTANCE METHODS =======================

	public List<String> scrapeTranslations(String translatedWord, Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isTransDirValid(Lang from, Lang to) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getUrl(Lang from, Lang to, String word) {
		// TODO Auto-generated method stub
		return null;
	}

	// ================= PRIVATE METHODS ========================

	// ================= GETTERS/SETTERS ========================

	// ================= INNER CLASSES ==========================

}
