package cz.cvut.fit.vyhliluk.vocserver.api.conv;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="translations")
public class TransListConverter {
	//================= STATIC ATTRIBUTES ======================

	//================= INSTANCE ATTRIBUTES ====================
	
	private String word;
	
	private List<TransConverter> translations = new ArrayList<TransConverter>();

	//================= STATIC METHODS =========================

	//================= CONSTRUCTORS ===========================
	
	public TransListConverter(String word, List<TransConverter> translations) {
		super();
		this.word = word;
		this.translations = translations;
	}
	
	public TransListConverter() {
		super();
	}

	//================= OVERRIDEN METHODS ======================

	//================= INSTANCE METHODS =======================

	//================= PRIVATE METHODS ========================

	//================= GETTERS/SETTERS ========================
	
	@XmlElement
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	@XmlElement
	public List<TransConverter> getTranslations() {
		return translations;
	}

	//================= INNER CLASSES ==========================

}
