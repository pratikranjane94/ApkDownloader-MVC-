/*File Name		: ApkSiteDataFetching.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Getting game information from APK-DL.com such as Game name, Version, Size,
 *				  Publish date, APK Download Link and storing it into CSV file.
 * */

package com.game.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.game.dto.JsonInfo;
import com.game.dto.PlayStoreData;
import com.game.dto.ScrapedData;

public class ApkSiteDataFetching {

	Logger logger = Logger.getLogger("APKSITE");

	private JsonInfo jsonInfo;

	public void setJsonInfo(JsonInfo jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	public ScrapedData createApkSiteDetails(PlayStoreData playStoreData, int id, int no, String fileName) {

		ArrayList<String> s1 = new ArrayList<String>();
		ScrapedData count = new ScrapedData();

		String apk = "https://apk-dl.com/";
		String apkSite = apk.concat(playStoreData.getPackageName());
		String downloadLink = "";
		String dlTitle = "";
		try {
			// fetch the document over HTTP
			Document doc = Jsoup.connect(apkSite).userAgent("Chrome/47.0.2526.80").timeout(10000).get();

			logger.debug("Scraping of APK-DL data started");

			// getting info class to fetch genre title version and publish date
			Elements infoClass = doc.select("[class=info section]");
			String in = infoClass.text();

			// getting data from info class
			String[] s3 = in.split("App Name</div>");
			for (String string : s3) {
				s1.add(string);
			}
			String info = s1.toString();

			// getting title
			dlTitle = info.substring((info.indexOf("Name") + 4), (info.indexOf("Package Name") - 1)).trim();

			System.out.println(info);
			// getting genre
			String dlGenre = doc.getElementsByClass("category").text();

			// getting version
			String dlVersion = info.substring((info.indexOf("Version") + 7), (info.indexOf("Developer") - 1)).trim();

			// getting publish date
			String dlPublishDate = info.substring((info.indexOf("Updated") + 7), (info.indexOf("File") - 1)).trim();

			// replacing comma in date with space
			dlPublishDate = dlPublishDate.replace(",", " ");

			// getting size
			String size = info.substring((info.indexOf("Size") + 4), (info.indexOf("Requires") - 1)).trim();

			// getting download link
			String downUrl = doc.getElementsByClass("download-btn")
					.select("[class=mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect fixed-size mdl-button--primary]")
					.attr("href");

			// checking whether link contains "HTTP"
			if (downUrl.contains("http") == false)
				downUrl = ("http://apk-dl.com").concat(downUrl.trim());

			// scraping downLink to get download link
			Document downloadLinkDoc = Jsoup.connect(downUrl).userAgent("Chrome/47.0.2526.80").timeout(10000).get();
			downloadLink = downloadLinkDoc.getElementsByTag("p").select("a[href]").attr("href");

			if (downloadLink != "") {
				// adding "HTTP" to link if absent
				if (downloadLink.contains("http") == false) {
					downloadLink = ("http:").concat(downloadLink);
				}
			} else {
				// no download link present
				downloadLink = downloadLink.replaceAll(downloadLink, "No download Link or paid app");
			}

			// if no data fetched
			if (dlTitle.equals("asd") && dlGenre.equals("") && dlVersion.equals("") && size.equals("") && dlPublishDate.equals("")) {
				return null;
			} else {
				count.setDlTitle(dlTitle);
				count.setDlGenre(dlGenre);
				count.setDlSize(size);
				count.setDlVersion(dlVersion);
				count.setDlPublishDate(dlPublishDate);
				count.setDownloadLink(downloadLink);
				count.setPlayStoreData(playStoreData);
				count.setFileName(fileName);
				count.setId(id);
				count.setNo(no);

				// displaying game info
				System.out.println("----------Dl-apk site data--------------");
				System.out.println("Title: " + dlTitle);
				System.out.println("Apk Site genre: " + dlGenre);
				System.out.println("Version: " + dlVersion);
				System.out.println("Published Date: " + dlPublishDate);
				System.out.println("Size: " + size);
				System.out.println("Download Link:" + count.getDownloadLink());
				logger.debug("APK-DL Data Scraping completed");
			}
		} catch (UnknownHostException u) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
			logger.debug("UnknownHostException! Trying To Scraping Data Again");
			ApkSiteDataFetching as = new ApkSiteDataFetching();
			as.createApkSiteDetails(playStoreData, id, no, fileName);
		} catch (NullPointerException e) {
			System.out.println("Null exception");
			logger.debug("Null Pointer Exception",e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Exception",e);
			return null;
		}
		return count;
	}

	// Creating JSON file of fetched info
	public boolean createCsv(ScrapedData count, String downloadFileName) {
		try {
			
			logger.debug("Creating CSV of APK-DL Meta Data");
			
			boolean notFound = false;

			File file = new File(jsonInfo.getCsvDownloadFilePath() + "/" + downloadFileName);

			if (!file.exists())
				notFound = true;

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);

			// if file doesn't exists, then create it
			if (notFound) {
				file.createNewFile();
				bw.append("PlayStore Title,Genre,Size,Version,Publish Date,Package,Url,");
				bw.append("Apk Title,Genre,Size,Version,Publish Date,Download Link,");
				bw.newLine();
			}
			// appending data to CSV
			bw.append(count.getDlTitle());
			bw.append(",");
			bw.append(count.getDlGenre());
			bw.append(",");
			bw.append(count.getDlSize());
			bw.append(",");
			bw.append(count.getDlVersion());
			bw.append(",");
			bw.append(count.getDlPublishDate());
			bw.append(",");
			bw.append(count.getDownloadLink());
			bw.append(",");
			if (count.getDownloadLink().contains("http://dl3.apk-dl.com/store/download?id")) {
				System.out.println("inside if");
				bw.append("Broken Link");
			}
			bw.newLine();
			bw.close();
			System.out.println(count.getDlTitle() + " Apk-dl data Stored in csv");
			logger.debug("Apk-dl data Stored in csv");
			System.out.println("");
		}

		catch (Exception e) {
			logger.debug("Exception While Creating CSV",e);
			return false;
		}
		return true;
	}

}
