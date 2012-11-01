package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.core.VocardsException;

public class ScreenScraperTranslator {
	// ================= STATIC ATTRIBUTES ======================

	// ================= INSTANCE ATTRIBUTES ====================

	private ScreenScraperTranslatorStrategy strategy;

	// ================= STATIC METHODS =========================

	// ================= CONSTRUCTORS ===========================

	public ScreenScraperTranslator(ScreenScraperTranslatorStrategy strategy) {
		super();
		this.strategy = strategy;
	}

	// ================= OVERRIDEN METHODS ======================

	// ================= INSTANCE METHODS =======================

	public List<String> translate(Lang from, Lang to, String word) throws TranslationException, VocardsException {
		if (!this.isTransDirValid(from, to)) {
			throw new VocardsException("Translation on unsupported language combination.");
		}

		try {
			String url = this.strategy.getUrl(from, to, word);
			URL targetURL = new URL(url);
			URLConnection targetConnection = targetURL.openConnection();
			Document xmlResponse = this.buildDoc(targetConnection.getInputStream());
			
			return this.strategy.scrapeTranslations(word, xmlResponse);
		} catch (IOException ex) {
			throw new TranslationException("Error during data retrieval.", ex);
		}
	}

	public boolean isTransDirValid(Lang from, Lang to) {
		return this.strategy.isTransDirValid(from, to);
	}

	// ================= PRIVATE METHODS ========================

	private Document buildDoc(InputStream inputStrm) throws TranslationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setNamespaceAware(false);
			factory.setValidating(false);
			factory.setSchema(null);
			factory.setExpandEntityReferences(false);
			factory.setFeature("http://xml.org/sax/features/namespaces", false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			inputStrm.close();

			return builder.parse(inputStrm);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new TranslationException("Error during data retrieval.", ex);
		}
	}

	// ================= GETTERS/SETTERS ========================

	// ================= INNER CLASSES ==========================

}
