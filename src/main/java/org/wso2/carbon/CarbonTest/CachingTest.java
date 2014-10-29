/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.CarbonTest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.cache.CacheManager;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.CacheConfiguration;

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
		String cacheName = "expiringCache";
		String cacheManagerName = "testCacheManager";
		// get the cache from javax.cache api
		cacheManager = Caching.getCacheManagerFactory().getCacheManager(
				cacheManagerName);
//		cache = cacheManager.getCache(cacheName);
		// set default expiry time of the cache
		cache = cacheManager
				.<String, String> createCacheBuilder(cacheName)
				.setExpiry(CacheConfiguration.ExpiryType.MODIFIED,
						new CacheConfiguration.Duration(TimeUnit.SECONDS, 2))
				.setStoreByValue(false).build();
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
