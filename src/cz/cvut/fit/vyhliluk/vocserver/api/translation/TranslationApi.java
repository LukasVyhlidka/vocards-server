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
	public Response translate(@QueryParam("from") Integer from, @QueryParam("to") Integer to, @QueryParam("word") String word) {
		ResponseBuilder rb = Response.ok();

		if (from == null || to == null || StringUtil.isEmpty(word)) {
			rb = Response.status(Response.Status.BAD_REQUEST);
			rb.entity(new ErrorConverter(Const.ERROR_CODE_ERROR_PARAMS, "from, to and word parameters have to be provided."));
			return rb.build();
		}

		Lang fromLang = Lang.getById(from);
		Lang toLang = Lang.getById(to);
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
			ScreenScraperTranslator slovnikTrans = new ScreenScraperTranslator(SlovnikScraperStrategy.getInstance());
			if (slovnikTrans.isTransDirValid(from, to)) {
				List<String> trans = slovnikTrans.translate(from, to, word);
				translations.addAll(trans);
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
