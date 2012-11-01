package cz.cvut.fit.vyhliluk.vocserver.core;

import cz.cvut.fit.vyhliluk.vocserver.util.Const;

public enum Lang {

	CZECH(Const.LANGUAGE_CZ, "cs"),
	SLOVAK(Const.LANGUAGE_SK, "sk"),
	ENGLISH(Const.LANGUAGE_EN, "en"),
	FRENCH(Const.LANGUAGE_FR, "fr"),
	GERMAN(Const.LANGUAGE_DE, "de");

	public static Lang getById(int id) {
		for (Lang l : Lang.values()) {
			if (l.getId() == id) {
				return l;
			}
		}
		return null;
	}
	
	private Lang(int id, String code) {
		this.id = id;
		this.code = code;
	}

	private int id;
	private String code;

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

}
