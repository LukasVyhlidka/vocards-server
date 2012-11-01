package cz.cvut.fit.vyhliluk.vocserver.api.translation.exc;

import cz.cvut.fit.vyhliluk.vocserver.core.VocardsException;

public class TranslationException extends VocardsException {

	// ================= STATIC ATTRIBUTES ======================
	
	private static final long serialVersionUID = 1L;

	// ================= INSTANCE ATTRIBUTES ====================

	// ================= STATIC METHODS =========================

	// ================= CONSTRUCTORS ===========================

	public TranslationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TranslationException(String message) {
		super(message);
	}

	// ================= OVERRIDEN METHODS ======================

	// ================= INSTANCE METHODS =======================

	// ================= PRIVATE METHODS ========================

	// ================= GETTERS/SETTERS ========================

	// ================= INNER CLASSES ==========================

}
