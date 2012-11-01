package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.util.List;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;

public interface ITranslator {
	
	boolean isUsable(Lang from, Lang to, String word);
	
	List<String> translate(Lang from, Lang to, String word) throws TranslationException;

}
