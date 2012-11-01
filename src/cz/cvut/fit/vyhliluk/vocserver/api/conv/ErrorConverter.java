package cz.cvut.fit.vyhliluk.vocserver.api.conv;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="error")
public class ErrorConverter {
	//================= STATIC ATTRIBUTES ======================

	//================= INSTANCE ATTRIBUTES ====================
	
	private int code;
	
	private String msg;

	//================= STATIC METHODS =========================

	//================= CONSTRUCTORS ===========================
	
	public ErrorConverter(int code, String msg) {
		super();
		this.code = code;
		this.msg = msg;
	}
	
	public ErrorConverter() {
		super();
	}

	//================= OVERRIDEN METHODS ======================

	//================= INSTANCE METHODS =======================

	//================= PRIVATE METHODS ========================

	//================= GETTERS/SETTERS ========================
	
	@XmlElement
	public Integer getCode() {
		return code;
	}

	

	public void setCode(int code) {
		this.code = code;
	}

	@XmlElement
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	//================= INNER CLASSES ==========================

}
