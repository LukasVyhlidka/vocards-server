package cz.cvut.fit.vyhliluk.vocserver.api.translation.gogl;

public class GoogleRespBlockLeaf implements IGoogleResponseBlock {

	// ==================== STATIC ATTRIBUTES ==================

	// ==================== INSTANCE ATTRIBUTES ================
	
	private String value;

	// ==================== STATIC METHODS =====================

	// ==================== CONSTRUCTORS =======================
	
	public GoogleRespBlockLeaf(String value) {
		this.value = value;
	}

	// ==================== OVERRIDEN METHODS ==================

	public GoogleRespBlockComposite getComposite() {
		return null;
	}

	// ==================== INSTANCE METHODS ===================

	// ==================== PRIVATE METHODS ====================

	// ==================== GETTERS/SETTERS ====================
	
	public String getValue() {
		return this.value;
	}

	// ==================== INNER CLASSES ======================
}
