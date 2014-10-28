package org.wso2.carbon.CarbonTest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.cache.CacheManager;
import javax.cache.Cache;
import javax.cache.Caching;

import org.apache.log4j.Logger;

@WebServlet("/CachingTest")
public class CachingTest extends HttpServlet {
	private static Logger log = Logger.getLogger(CachingTest.class);
	private static final long serialVersionUID = 1L;
	private CacheManager cacheManager;
	private Cache<String, String> cache;

	public CachingTest() {
	}

	@Override
	public void init() throws ServletException {
		// get the cache from javax.cache api
		cacheManager = Caching.getCacheManagerFactory().getCacheManager(
				"testCacheManager");
		cache = cacheManager.getCache("simpleCache");
		// cache = cacheManager.<String,
		// Integer>createCacheBuilder(cacheName).setExpiry(CacheConfiguration.ExpiryType.MODIFIED,
		// new CacheConfiguration.Duration(TimeUnit.SECONDS,
		// 10)).setStoreByValue(false).build();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.info("Starting");
		String item;
		String amount;

		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();

		item = req.getParameter("item");
		if (item == null) {
			out.println("Item cannot be empty!");
			log.warn("Item cannot be empty!");
			return;
		}

		log.info("Received item: " + item);
		amount = req.getParameter("amount");
		if (amount != null) {
			// if a new amount is defined add that to the cache
			cache.put(item, amount);
			log.info("Added " + item + " : " + amount + " to cache");
		}
		// try to access the cache more than needed
		amount = cache.get(item);
		log.info("Amount : " + amount);
		
		out.println("Item : " + item);
		out.println("Amount : " + amount);
		log.info("Finished processing");
	}
}
