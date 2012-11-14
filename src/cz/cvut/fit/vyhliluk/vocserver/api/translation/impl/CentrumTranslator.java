package cz.cvut.fit.vyhliluk.vocserver.api.translation.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.ITranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.util.HtmlCleanerUtil;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.util.Const;

public class CentrumTranslator implements ITranslator {

	// ==================== STATIC ATTRIBUTES ==================

	private static CentrumTranslator instance = null;

	private static final String URL_TEMPLATE = "http://slovniky.centrum.cz/?q=%s&lang=%s";

	// ==================== INSTANCE ATTRIBUTES ================

	private List<TranslateDirection> translations = new ArrayList<TranslateDirection>();

	// ==================== STATIC METHODS =====================

	public static CentrumTranslator getInstance() {
		if (instance == null) {
			instance = new CentrumTranslator();
		}
		return instance;
	}

	// ==================== CONSTRUCTORS =======================

	private CentrumTranslator() {
		translations.add(new TranslateDirection(Lang.CZECH, Lang.ENGLISH, 2));
		translations.add(new TranslateDirection(Lang.ENGLISH, Lang.CZECH, 1));
		translations.add(new TranslateDirection(Lang.CZECH, Lang.GERMAN, 4));
		translations.add(new TranslateDirection(Lang.GERMAN, Lang.CZECH, 3));
		translations.add(new TranslateDirection(Lang.CZECH, Lang.FRENCH, 8));
		translations.add(new TranslateDirection(Lang.FRENCH, Lang.CZECH, 7));
	}

	// ==================== OVERRIDEN METHODS ==================

	public boolean isUsable(Lang from, Lang to, String word) {
		return getTranslDir(from, to) != null;
	}

	public List<String> translate(Lang from, Lang to, String word) throws TranslationException {
		Set<String> res = new HashSet<String>();

		String translWord = word.toLowerCase();
		String url = this.getUrl(from, to, translWord);
		TagNode root = HtmlCleanerUtil.cleanUrl(url);

		TagNode table = root.findElementByAttValue("id", "results", true, false);
		if (table != null) {
			try {
				Object[] trs = table.evaluateXPath("./*/tr");
				for (Object trObject : trs) {
					TagNode tr = (TagNode) trObject;
					TagNode th = tr.findElementByName("th", false);
					String thText = th.getText().toString().trim();
					if (translWord.equalsIgnoreCase(thText)) {
						TagNode[] lis = tr.getElementsByName("li", true);
						for (TagNode li : lis) {
							res.add(li.getText().toString());
						}
					}
				}
			} catch (XPatherException ex) {
				// TODO: handle
			}
		}

		return new ArrayList<String>(res);
	}

	// ==================== INSTANCE METHODS ===================

	// ==================== PRIVATE METHODS ====================

	private TranslateDirection getTranslDir(Lang from, Lang to) {
		for (TranslateDirection td : this.translations) {
			if (from.equals(td.from) && to.equals(td.to)) {
				return td;
			}
		}
		return null;
	}

	private String getUrl(Lang from, Lang to, String word) {
		try {
			String encWord = URLEncoder.encode(word, Const.TRANSLATION_CHARACTER_ENCODING);
			TranslateDirection tranDir = this.getTranslDir(from, to);
			return String.format(URL_TEMPLATE, encWord, tranDir.number);
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	// ==================== GETTERS/SETTERS ====================

	// ==================== INNER CLASSES ======================

	private static class TranslateDirection {

		public Lang from;
		public Lang to;
		public int number;

		public TranslateDirection(Lang from, Lang to, int number) {
			this.from = from;
			this.to = to;
			this.number = number;
		}
	}
}
