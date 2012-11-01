package cz.cvut.fit.vyhliluk.vocserver.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("v1/warm")
public class WarmApi {
	//================= STATIC ATTRIBUTES ======================

	//================= INSTANCE ATTRIBUTES ====================

	//================= STATIC METHODS =========================

	//================= CONSTRUCTORS ===========================

	//================= OVERRIDEN METHODS ======================

	//================= INSTANCE METHODS =======================
	
	@GET
	@Produces("text/plain")
	public String warm() {
		return "warmed";
	}

	//================= PRIVATE METHODS ========================

	//================= GETTERS/SETTERS ========================

	//================= INNER CLASSES ==========================

}
