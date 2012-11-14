package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cvut.fit.vyhliluk.vocserver.api.conv.ErrorConverter;
import cz.cvut.fit.vyhliluk.vocserver.api.conv.TransConverter;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.BadLangCombinationException;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.TranslationException;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.CentrumTranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.GoogleTranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.SeznamTranslator;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.core.VocardsException;
import cz.cvut.fit.vyhliluk.vocserver.util.Const;
import cz.cvut.fit.vyhliluk.vocserver.util.StringUtil;

@Path("v1/trans")
public class TranslationApi {
	// ================= STATIC ATTRIBUTES ======================

	private static final Logger LOG = LoggerFactory.getLogger(TranslationApi.class);

	// ================= INSTANCE ATTRIBUTES ====================

	// ================= STATIC METHODS =========================

	// ================= CONSTRUCTORS ===========================

	// ================= OVERRIDEN METHODS ======================

	// ================= INSTANCE METHODS =======================

	@GET
	@Produces(value = "application/json")
	public Response translate(@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("word") String word) {
		ResponseBuilder rb = Response.ok();

		if (from == null || to == null || StringUtil.isEmpty(word)) {
			rb = Response.status(Response.Status.BAD_REQUEST);
			rb.entity(new ErrorConverter(Const.ERROR_CODE_ERROR_PARAMS, "from, to and word parameters have to be provided."));
			return rb.build();
		}

		Lang fromLang = Lang.getByCode(from);
		Lang toLang = Lang.getByCode(to);
		if (fromLang == null || toLang == null) {
			rb = Response.status(Response.Status.BAD_REQUEST);
			rb.entity(new ErrorConverter(Const.ERROR_CODE_UNKNOWN_LANG, "Unsupported Language."));
			return rb.build();
		}

		if (fromLang.equals(toLang)) {
			rb = Response.status(Response.Status.BAD_REQUEST);
			rb.entity(new ErrorConverter(Const.ERROR_CODE_EQUAL_LANG, "Languages are the same."));
			return rb.build();
		}

		try {
			TransConverter conv = this.createTranslation(fromLang, toLang, word);
			rb.entity(conv);
		} catch (BadLangCombinationException ex) {
			rb = Response.status(Response.Status.BAD_REQUEST);
			rb.entity(new ErrorConverter(Const.ERROR_CODE_UNSUPPORTED_LANG_COMB, "Unsupported Laguage Combination"));
			return rb.build();
		} catch (VocardsException ex) {
			rb = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
			rb.entity(new ErrorConverter(Const.ERROR_CODE_ERROR, "Error during data retrieval"));
			return rb.build();
		}

		return rb.build();
	}

	// ================= PRIVATE METHODS ========================

	private TransConverter createTranslation(Lang from, Lang to, String word) throws VocardsException {
		List<String> translations = new ArrayList<String>();
		try {
//			ITranslator transl = SlovnikCzTranslator.getInstance();
//			if (transl.isUsable(from, to, word)) {
//				translations = transl.translate(from, to, word);
//			}
			
//			ITranslator gTrans = GoogleTranslator.getInstance();
//			if (gTrans.isUsable(from, to, word)) {
//				translations = gTrans.translate(from, to, word);
//			}
			
//			ITranslator seznamTrans = SeznamTranslator.getInstance();
//			if (seznamTrans.isUsable(from, to, word)) {
//				translations = seznamTrans.translate(from, to, word);
//			}
			
			ITranslator centrumTrans = CentrumTranslator.getInstance();
			if (centrumTrans.isUsable(from, to, word)) {
				translations = centrumTrans.translate(from, to, word);
			}
		} catch (TranslationException ex) {
			LOG.error("Slovnik scraper error!", ex);
		}

		TransConverter res = new TransConverter(word, translations);
		return res;
	}

	// ================= GETTERS/SETTERS ========================

	// ================= INNER CLASSES ==========================

}
