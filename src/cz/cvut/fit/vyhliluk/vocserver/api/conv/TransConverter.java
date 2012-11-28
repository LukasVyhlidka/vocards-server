package cz.cvut.fit.vyhliluk.vocserver.api.conv;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "trans")
public class TransConverter {

	// ==================== STATIC ATTRIBUTES ==================

	// ==================== INSTANCE ATTRIBUTES ================

	private String translation;

	private int count;

	// ==================== STATIC METHODS =====================

	// ==================== CONSTRUCTORS =======================

	public TransConverter(String translation, int count) {
		super();
		this.translation = translation;
		this.count = count;
	}

	// ==================== OVERRIDEN METHODS ==================

	// ==================== INSTANCE METHODS ===================

	// ==================== PRIVATE METHODS ====================

	// ==================== GETTERS/SETTERS ====================

	@XmlElement
	public String getTranslation() {
		return translation;
	}

	@XmlElement
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	// ==================== INNER CLASSES ======================
}
