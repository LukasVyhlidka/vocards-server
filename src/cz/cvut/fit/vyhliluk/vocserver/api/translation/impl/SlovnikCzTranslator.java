package cz.cvut.fit.vyhliluk.vocserver.api.translation.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.ITranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.util.HtmlCleanerUtil;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.util.Const;

public class SlovnikCzTranslator implements ITranslator {

	// ==================== STATIC ATTRIBUTES ==================

	private static final String URL_TEMPLATE = "http://slovnik.cz/bin/mld.fpl?vcb=%s&dictdir=%s.%s&lines=15";

	private static SlovnikCzTranslator instance = null;

	// ==================== INSTANCE ATTRIBUTES ================

	// ==================== STATIC METHODS =====================

	public static SlovnikCzTranslator getInstance() {
		if (instance == null) {
			instance = new SlovnikCzTranslator();
		}
		return instance;
	}

	// ==================== CONSTRUCTORS =======================

	private SlovnikCzTranslator() {
	}

	// ==================== OVERRIDEN METHODS ==================

	// ==================== INSTANCE METHODS ===================

	public boolean isUsable(Lang from, Lang to, String word) {
		if (!Lang.CZECH.equals(from) && !Lang.CZECH.equals(to)) {
			return false;
		}

		Lang foreign = (Lang.CZECH.equals(from) ? to : from); // Non czech
		if (Lang.SLOVAK.equals(foreign)) {
			return false;
		}

		return true;
	}

	public List<String> translate(Lang from, Lang to, String word) throws TranslationException {
		List<String> res = new ArrayList<String>();
		String translWord = word.toLowerCase();
		String url = this.getUrl(from, to, translWord);
		TagNode root = HtmlCleanerUtil.cleanUrl(url);

		try {
			Object[] elems = root.evaluateXPath("//div[@class='pair']");
			for (Object element : elems) {
				TagNode el = (TagNode) element;
				TagNode[] childrens = el.getChildTags();
				TagNode l = childrens[0];
				TagNode r = childrens[1];
				
				String lText = l.getText().toString();
				if (l.getText().toString().toLowerCase().equals(translWord)) {
					res.add(r.getText().toString());
				}
			}
		} catch (XPatherException ex) {
			ex.printStackTrace();
			throw new TranslationException("slovnik.cz translation error", ex);
		}
		
		return res;
	}

	// ==================== PRIVATE METHODS ====================

	private String getUrl(Lang from, Lang to, String word) {
		try {
			String encWord = URLEncoder.encode(word, Const.TRANSLATION_CHARACTER_ENCODING);
			String langComb = this.getLangCombination(from, to);
			String url = String.format(URL_TEMPLATE, encWord, langComb, this.amendCode(from));

			return url;
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	private String getLangCombination(Lang from, Lang to) {
		if (Lang.CZECH.equals(from)) {
			return this.amendCode(to) + this.amendCode(from);
		} else if (Lang.CZECH.equals(to)) {
			return this.amendCode(from) + this.amendCode(to);
		}
		return null;
	}

	private String amendCode(Lang lang) {
		if (Lang.GERMAN.equals(lang)) {
			return "ge";
		} else if (Lang.CZECH.equals(lang)) {
			return "cz";
		}
		return lang.getCode();
	}

	// ==================== GETTERS/SETTERS ====================

	// ==================== INNER CLASSES ======================
}
