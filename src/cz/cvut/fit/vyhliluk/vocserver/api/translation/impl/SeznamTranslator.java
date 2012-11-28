package cz.cvut.fit.vyhliluk.vocserver.api.translation.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.htmlcleaner.TagNode;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.ITranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.util.HtmlCleanerUtil;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.util.Const;

public class SeznamTranslator implements ITranslator {

	// ==================== STATIC ATTRIBUTES ==================

	private static final String URL_TEMPLATE = "http://slovnik.seznam.cz/%s-%s/word/?q=%s&forceLang=1";

	private static SeznamTranslator instance = null;

	// ==================== INSTANCE ATTRIBUTES ================

	// ==================== STATIC METHODS =====================

	public static SeznamTranslator getInstance() {
		if (instance == null) {
			instance = new SeznamTranslator();
		}
		return instance;
	}

	// ==================== CONSTRUCTORS =======================

	private SeznamTranslator() {

	}

	// ==================== OVERRIDEN METHODS ==================

	public boolean isUsable(Lang from, Lang to, String word) {
		return (Lang.CZECH.equals(from) || Lang.CZECH.equals(to)) &&
				(!Lang.SLOVAK.equals(from)) && (!Lang.SLOVAK.equals(to));
	}

	public List<String> translate(Lang from, Lang to, String word) throws TranslationException {
		Set<String> res = new HashSet<String>();

		String translWord = word.toLowerCase();
		String url = this.getUrl(from, to, translWord);
		TagNode root = HtmlCleanerUtil.cleanUrl(url);

		TagNode results = root.findElementByAttValue("id", "results", true, true);
		TagNode fastMeanings = results.findElementByAttValue("id", "fastMeanings", true, true);

		TagNode[] children = fastMeanings.getChildTags();
		StringBuilder sb = new StringBuilder();
		for (TagNode child : children) {
			if (child.getName().equals("br") ||
					(child.getName().equals("span") &&
					"comma".equals(child.getAttributeByName("class")))) {
				res.add(sb.toString());
				sb = new StringBuilder();
			} else {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				sb.append(child.getText().toString());
			}
		}

		if (sb.length() > 0) {
			res.add(sb.toString().trim());
		}

		return new ArrayList<String>(res);
	}

	// ==================== INSTANCE METHODS ===================

	// ==================== PRIVATE METHODS ====================

	private String getUrl(Lang from, Lang to, String word) {
		try {
			String encWord = URLEncoder.encode(word, Const.TRANSLATION_CHARACTER_ENCODING);
			return String.format(URL_TEMPLATE, this.amendCode(from), this.amendCode(to), encWord);
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	private String amendCode(Lang l) {
		if (Lang.CZECH.equals(l)) {
			return "cz";
		}
		return l.getCode();
	}

	// ==================== GETTERS/SETTERS ====================

	// ==================== INNER CLASSES ======================
}
