/*File Name		: PlayStoreUrlFetching.java
 *Created By	: RAJU SHINDE
 *Purpose		: finds PlayStore URL for given game name
 * */

package com.game.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PlayStoreUrlFetching {
	
	// find the PlayStore link for the game
	public String findUrl(String gname) {
		try {
			String eurl = encodeUrl(gname);
			Document doc = Jsoup.connect(eurl).userAgent("Chrome/50.0.2661.94").timeout(10000).get();
			Elements links = doc.select("a[href]");
			for (Element link : links) {
				String furl = link.absUrl("href");
				Pattern p = Pattern.compile("https://play.google.com/store/apps/details");
				Matcher m = p.matcher(furl);
				if (m.find()) {
					return furl;
				}
			}
		} catch (UnknownHostException e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("URL not fetched. Trying to fetch URL again");
			PlayStoreUrlFetching u = new PlayStoreUrlFetching();
			u.findUrl(gname);
		}catch (HttpStatusException e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.out.println("URL not fetched. Trying to fetch URL again");
			PlayStoreUrlFetching u = new PlayStoreUrlFetching();
			u.findUrl(gname);
		} catch (Exception e) {
			System.out.println("Exception occured in Play store URL fetching "+e.toString());
			return null;
		}

		return null;
	}

	// encode string to get proper URL
	public String encodeUrl(String gname) throws IOException {
		try {
			String es = URLEncoder.encode(gname, "UTF-8");
			String url = "https://play.google.com/store/search?q=";
			gname = url.concat(es);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return gname;
	}

}