package cz.cvut.fit.vyhliluk.vocserver.api.translation.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class HtmlCleanerUtil {

	//==================== STATIC ATTRIBUTES ==================

	//==================== INSTANCE ATTRIBUTES ================

	//==================== STATIC METHODS =====================
	
	public static TagNode cleanUrl(String url) {
		try {
			HtmlCleaner cleaner = new HtmlCleaner();
			TagNode node = cleaner.clean(new URL(url));
			return node;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	//==================== CONSTRUCTORS =======================

	//==================== OVERRIDEN METHODS ==================

	//==================== INSTANCE METHODS ===================

	//==================== PRIVATE METHODS ====================

	//==================== GETTERS/SETTERS ====================

	//==================== INNER CLASSES ======================
}
