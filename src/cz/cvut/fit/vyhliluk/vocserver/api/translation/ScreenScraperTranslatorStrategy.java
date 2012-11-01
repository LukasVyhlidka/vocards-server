package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.util.List;

import org.w3c.dom.Document;

import cz.cvut.fit.vyhliluk.vocserver.core.Lang;

public interface ScreenScraperTranslatorStrategy {
	
	public List<String> scrapeTranslations(String translatedWord, Document doc);
	
	public boolean isTransDirValid(Lang from, Lang to);
	
	public String getUrl(Lang from, Lang to, String word);

}
