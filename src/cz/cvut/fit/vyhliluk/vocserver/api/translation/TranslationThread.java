package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.util.List;

import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;

public class TranslationThread implements Runnable {

	// ==================== STATIC ATTRIBUTES ==================

	// ==================== INSTANCE ATTRIBUTES ================

	private ITranslator translator;
	private Lang from;
	private Lang to;
	private String word;

	private Result state;
	private String error = null;
	private List<String> res = null;

	// ==================== STATIC METHODS =====================

	// ==================== CONSTRUCTORS =======================

	public TranslationThread(ITranslator translator, Lang from, Lang to, String word) {
		this.translator = translator;
		this.from = from;
		this.to = to;
		this.word = word;

		this.state = Result.NOT_DONE;
	}

	// ==================== OVERRIDEN METHODS ==================

	public void run() {
		try {
			this.res = translator.translate(from, to, word);
			this.setState(Result.DONE);
		} catch (TranslationException ex) {
			this.error = ex.getLocalizedMessage();
			this.setState(Result.ERROR);
		}
	}

	// ==================== INSTANCE METHODS ===================

	// ==================== PRIVATE METHODS ====================

	private void setState(Result state) {
		synchronized (this) {
			this.state = state;
		}
	}

	// ==================== GETTERS/SETTERS ====================

	public Result getState() {
		synchronized (this) {
			return this.state;
		}
	}

	public List<String> getResult() {
		return this.res;
	}

	public String getError() {
		return this.error;
	}

	public ITranslator getTranslator() {
		return translator;
	}

	// ==================== INNER CLASSES ======================

	public static enum Result {
		NOT_DONE, DONE, ERROR
	}
}
