package cz.cvut.fit.vyhliluk.vocserver.api.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.ThreadManager;

import cz.cvut.fit.vyhliluk.vocserver.api.conv.ErrorConverter;
import cz.cvut.fit.vyhliluk.vocserver.api.conv.TransConverter;
import cz.cvut.fit.vyhliluk.vocserver.api.conv.TransListConverter;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.TranslationThread.Result;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.exc.BadLangCombinationException;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.CentrumTranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.GoogleTranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.MyMemoryTranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.SeznamTranslator;
import cz.cvut.fit.vyhliluk.vocserver.api.translation.impl.SlovnikCzTranslator;
import cz.cvut.fit.vyhliluk.vocserver.core.Lang;
import cz.cvut.fit.vyhliluk.vocserver.core.VocardsException;
import cz.cvut.fit.vyhliluk.vocserver.util.Const;
import cz.cvut.fit.vyhliluk.vocserver.util.StringUtil;

@Path("v1/trans")
public class TranslationApi {
	// ================= STATIC ATTRIBUTES ======================

	private static final Logger LOG = LoggerFactory.getLogger(TranslationApi.class);

	private static List<ITranslator> translators = new ArrayList<ITranslator>();
	static {
		translators.add(CentrumTranslator.getInstance());
		translators.add(GoogleTranslator.getInstance());
		translators.add(MyMemoryTranslator.getInstance());
		translators.add(SeznamTranslator.getInstance());
		translators.add(SlovnikCzTranslator.getInstance());
	}

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
			TransListConverter conv = this.createTranslation(fromLang, toLang, word);
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

	private TransListConverter createTranslation(Lang from, Lang to, String word) throws VocardsException {
		List<String> translations = new ArrayList<String>();

		ThreadFactory factory = ThreadManager.currentRequestThreadFactory();

		List<ThreadCrate> threads = new ArrayList<ThreadCrate>();
		for (ITranslator trans : translators) {
			if (trans.isUsable(from, to, word)) {
				TranslationThread runnable = new TranslationThread(trans, from, to, word);
				Thread t = factory.newThread(runnable);
				t.start();
				threads.add(new ThreadCrate(runnable, t));
			}
		}

		long start = System.currentTimeMillis();
		long limit = 4000;
		for (ThreadCrate tc : threads) {
			long wait = System.currentTimeMillis() - start;
			if (wait >= limit) {
				tc.thread.interrupt();
			} else {
				try {
					tc.thread.join(limit - wait);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

		for (ThreadCrate tc : threads) {
			if (!tc.thread.isAlive()) {
				if (tc.runnable.getState().equals(Result.DONE)) {
					translations.addAll(tc.runnable.getResult());
					LOG.info(tc.runnable.getTranslator().toString() + " OK");
				} else {
					LOG.warn(tc.runnable.getTranslator().toString() + " ended with state: " + tc.runnable.getState().toString());
				}
			}
		}

		TransListConverter res = this.createRes(word, translations);
		return res;
	}
	
	private TransListConverter createRes(String word, List<String> translations) {
		Collections.sort(translations);
		
		List<TransConverter> tcList = new ArrayList<TransConverter>();
		
		Iterator<String> it = translations.iterator();
		TransConverter tc = null;
		while (it.hasNext()) {
			String t = it.next();
			if (tc == null || ! tc.getTranslation().equals(t)) {
				tc = new TransConverter(t, 1);
				tcList.add(tc);
			} else {
				tc.setCount(tc.getCount() + 1);
			}
		}
		
		Collections.sort(tcList, new Comparator<TransConverter>() {
			public int compare(TransConverter o1, TransConverter o2) {
				return o2.getCount() - o1.getCount();
			}
		});
		
		return new TransListConverter(word, tcList);
	}

	// ================= GETTERS/SETTERS ========================

	// ================= INNER CLASSES ==========================

	private static class ThreadCrate {

		public ThreadCrate(TranslationThread r, Thread t) {
			this.runnable = r;
			this.thread = t;
		}

		public TranslationThread runnable;
		public Thread thread;
	}

}
