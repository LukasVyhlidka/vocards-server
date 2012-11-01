package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.util.Const;

public class SlovnikScraperStrategy implements ScreenScraperTranslatorStrategy {

	// ================= STATIC ATTRIBUTES ======================

	private static final String URL_TEMPLATE = "http://slovnik.cz/bin/mld.fpl?vcb=%s&dictdir=%s.%s&lines=15";
	
	private static SlovnikScraperStrategy instance = null;

	// ================= INSTANCE ATTRIBUTES ====================

	// ================= STATIC METHODS =========================
	
	public static synchronized SlovnikScraperStrategy getInstance() {
		if (instance == null) {
			instance = new SlovnikScraperStrategy();
		}
		return instance;
	}

	// ================= CONSTRUCTORS ===========================
	
	private SlovnikScraperStrategy() {
		
	}

	// ================= OVERRIDEN METHODS ======================

	// ================= INSTANCE METHODS =======================

	public List<String> scrapeTranslations(String translatedWord, Document doc) {
		List<String> res = new ArrayList<String>();

		String word = translatedWord.toLowerCase();
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//div[@class='pair']";

		try {
			NodeList list = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				NodeList children = n.getChildNodes();
				Node l = children.item(0);
				Node r = children.item(2); // there is a text between them
				if (l.getTextContent().toLowerCase().equals(word)) {
					res.add(r.getTextContent());
				}
			}
		} catch (XPathExpressionException ex) {
			ex.printStackTrace();
		}

		return res;
	}

	public boolean isTransDirValid(Lang from, Lang to) {
		if (!Lang.CZECH.equals(from) && !Lang.CZECH.equals(to)) {
			return false;
		}

		Lang foreign = (Lang.CZECH.equals(from) ? to : from); // Non czech
		if (Lang.SLOVAK.equals(foreign)) {
			return false;
		}

		return true;
	}

	public String getUrl(Lang from, Lang to, String word) {
		try {
			String encWord = URLEncoder.encode(word, Const.TRANSLATION_CHARACTER_ENCODING);
			String langComb = this.getLangCombination(from, to);
			String url = String.format(URL_TEMPLATE, encWord, langComb, this.amendCode(from));

			return url;
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	// ================= PRIVATE METHODS ========================

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

	// ================= GETTERS/SETTERS ========================

	// ================= INNER CLASSES ==========================

}
