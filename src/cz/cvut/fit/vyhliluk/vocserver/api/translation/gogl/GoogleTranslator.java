package cz.cvut.fit.vyhliluk.vocserver.api.translation.gogl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.ITranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;

public class GoogleTranslator implements ITranslator {

	// ==================== STATIC ATTRIBUTES ==================

	private static final String URL_TEMPLATE = "http://translate.google.com/translate_a/t?" +
			"client=p&text=%S&hl=cs&sl=%s&tl=%s&ie=UTF-8&oe=UTF-8&multires=1&otf=1&ssel=0&tsel=0&sc=1";

	private static final String JSON_KEY_SENTENCES = "sentences";
	private static final String JSON_KEY_DICT = "dict";
	private static final String JSON_KEY_TRANS = "trans";
	private static final String JSON_KEY_WORD = "word";
	private static final String JSON_KEY_TERMS = "terms";
	private static final String JSON_KEY_ENTRY = "entry";

	private static GoogleTranslator instance = null;

	// ==================== INSTANCE ATTRIBUTES ================

	// ==================== STATIC METHODS =====================

	public static GoogleTranslator getInstance() {
		if (instance == null) {
			instance = new GoogleTranslator();
		}
		return instance;
	}

	// ==================== CONSTRUCTORS =======================

	private GoogleTranslator() {

	}

	// ==================== OVERRIDEN METHODS ==================

	public boolean isUsable(Lang from, Lang to, String word) {
		return true;
	}

	public List<String> translate(Lang from, Lang to, String word) throws TranslationException {
		List<String> res = new ArrayList<String>();
		try {
			String url = String.format(
					URL_TEMPLATE,
					URLEncoder.encode(word, "UTF-8"),
					from.getCode(),
					to.getCode());

			URL targetURL = new URL(url);
			URLConnection targetConnection = targetURL.openConnection();
			res = this.buildResponse(targetConnection);
		} catch (MalformedURLException ex) {
			// TODO: handle
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO: handle
			ex.printStackTrace();
		}

		return res;
	}

	// ==================== INSTANCE METHODS ===================

	// ==================== PRIVATE METHODS ====================

	private List<String> buildResponse(URLConnection conn) throws IOException {
		Set<String> res = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String response = reader.readLine();
		System.out.println(response);
		try {
			JSONObject root = new JSONObject(response);

			if (root.has(JSON_KEY_SENTENCES)) {
				JSONArray sentences = root.getJSONArray(JSON_KEY_SENTENCES);
				for (int i = 0; i < sentences.length(); i++) {
					JSONObject sentence = sentences.getJSONObject(i);
					res.add(sentence.getString(JSON_KEY_TRANS));
				}
			}

			if (root.has(JSON_KEY_DICT)) {
				JSONArray dicts = root.getJSONArray(JSON_KEY_DICT);
				for (int i = 0; i < dicts.length(); i++) {
					JSONObject dict = dicts.getJSONObject(i);
					if (dict.has(JSON_KEY_WORD)) {
						this.appendWord(dict, res);
					}
					if (dict.has(JSON_KEY_TERMS)) {
						JSONArray terms = dict.getJSONArray(JSON_KEY_TERMS);
						for (int j = 0; j < terms.length(); j++) {
							res.add(terms.getString(j));
						}
					}
					if (dict.has(JSON_KEY_ENTRY)) {
						JSONArray entries = dict.getJSONArray(JSON_KEY_ENTRY);
						for (int j = 0; j < entries.length(); j++) {
							this.appendWord(entries.getJSONObject(j), res);
						}
					}
				}
			}
		} catch (JSONException ex) {
			// TODO: handle
			ex.printStackTrace();
		}
		return new ArrayList<String>(res);
	}

	private void appendWord(JSONObject dict, Set<String> translations) throws JSONException {
		translations.add(dict.getString(JSON_KEY_WORD));
	}

	// ==================== GETTERS/SETTERS ====================

	// ==================== INNER CLASSES ======================
}
