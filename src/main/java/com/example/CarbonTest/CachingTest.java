package com.example.CarbonTest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.cache.CacheManager;
import javax.cache.Cache;
import javax.cache.Caching;

@WebServlet("/CachingTest")
public class CachingTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CacheManager cacheManager;
	private Cache<String, String> cache;

	public CachingTest() {
		// get the cache from javax.cache api
		cacheManager = Caching.getCacheManagerFactory().getCacheManager(
				"testCacheManager");
		cache = cacheManager.getCache("simpleCache");
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
		String item = req.getParameter("item");
		if (item == null) {
			resp.getWriter().write(
					"<html><body>Item cannot be empty!</body></html>");
			return;
		}
		System.out.println("Item " + item);

		// if new amount is defined add that to the cache
		String amount = req.getParameter("amount");
		if (amount != null) {
			cache.put(item, amount);
			System.out.println("Adding " + item + " : " + amount + " to cache.");
		}

		amount = cache.get(item);
		resp.getWriter().write(
				"<html><body>Item : " + item + "<br/>Amount : " + amount
						+ "</body></html>");
	}
}
