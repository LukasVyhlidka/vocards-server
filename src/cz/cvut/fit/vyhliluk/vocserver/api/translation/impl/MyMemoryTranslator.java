package cz.cvut.fit.vyhliluk.vocserver.api.translation.impl;

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

public class MyMemoryTranslator implements ITranslator {

	// ==================== STATIC ATTRIBUTES ==================

	private static MyMemoryTranslator instance = null;

	private static final String URL_TEMPLATE = "http://mymemory.translated.net/api/get?q=%s&langpair=%s%s%s";

	private static final String JSON_KEY_RESPONSE_DATA = "responseData";
	private static final String JSON_KEY_TRANSLATED_TEXT = "translatedText";

	// ==================== INSTANCE ATTRIBUTES ================

	// ==================== STATIC METHODS =====================

	public static MyMemoryTranslator getInstance() {
		if (instance == null) {
			instance = new MyMemoryTranslator();
		}
		return instance;
	}

	// ==================== CONSTRUCTORS =======================

	private MyMemoryTranslator() {

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
					URLEncoder.encode("|", "UTF-8"),
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
		List<String> res = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String response = reader.readLine();
		System.out.println(response);
		try {
			JSONObject root = new JSONObject(response);

			if (root.has(JSON_KEY_RESPONSE_DATA)) {
				JSONObject respData = root.getJSONObject(JSON_KEY_RESPONSE_DATA);
				if (respData.has(JSON_KEY_TRANSLATED_TEXT)) {
					res.add(respData.getString(JSON_KEY_TRANSLATED_TEXT));
				}
			}
		} catch (JSONException ex) {
			// TODO: handle
			ex.printStackTrace();
		}
		return res;
	}

	// ==================== GETTERS/SETTERS ====================

	// ==================== INNER CLASSES ======================
}
