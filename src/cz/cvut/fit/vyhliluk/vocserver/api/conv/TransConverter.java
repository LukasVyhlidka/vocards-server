package cz.cvut.fit.vyhliluk.vocserver.api.conv;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="trans")
public class TransConverter {
	//================= STATIC ATTRIBUTES ======================

	//================= INSTANCE ATTRIBUTES ====================
	
	private String word;
	
	private List<String> translations = new ArrayList<String>();

	//================= STATIC METHODS =========================

	//================= CONSTRUCTORS ===========================
	
	public TransConverter(String word, List<String> translations) {
		super();
		this.word = word;
		this.translations = translations;
	}
	
	public TransConverter() {
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

//	@XmlElement
	@XmlElement
	public List<String> getTranslations() {
		return translations;
	}

	//================= INNER CLASSES ==========================

}
