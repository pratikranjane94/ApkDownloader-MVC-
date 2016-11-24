/*File Name		: PlayStoreDataFetching.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Getting game information from PlayStore such as Game name, Version, Size,
 *				  Publish date, Package name and storing it into CSV file.
 * */

package com.game.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.game.dto.JsonInfo;
import com.game.dto.PlayStoreData;

public class PlayStoreDataFetching {
	
	Logger logger=Logger.getLogger("PLAYSTORE");

	private JsonInfo jsonInfo;

	public void setJsonInfo(JsonInfo jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	/*-----------------------Scraping PlayStore site data---------------------------*/

	public PlayStoreData getPlayStoreData(String url) {

		
		PlayStoreData playStoreData=new PlayStoreData();
		// ArrayList<String> err=new ArrayList<String>();
		try {
			// fetching the document over HTTP
			Document doc = Jsoup.connect(url).userAgent("Chrome/47.0.2526.80").timeout(10000).get();
			
			logger.debug("Scraping of Play Store Data Started");

			// getting game title class to fetch title
			Elements t = doc.getElementsByClass("document-title");

			// getting game info class to fetch version size publish date
			Elements g = doc.getElementsByClass("document-subtitle");
			Elements info = doc.getElementsByClass("meta-info");

			// getting game package name
			String packageName = url.substring(url.indexOf("id=") + 3);

			// getting game info
			String title = t.select("[class=id-app-title]").text();
			String genre = g.select("[itemprop=genre]").text();
			String version = info.select("[itemprop=softwareVersion]").text();
			String size = info.select("[itemprop=fileSize]").text();
			String publishDate = info.select("[itemprop=datePublished]").text();

			// if no data fetched return null
			if (title.equals("") && genre.equals("") && version.equals("") && size.equals("") && publishDate.equals("")
					&& packageName.equals("")) {
				return null;
			} else {
				playStoreData.setTitle(title);
				playStoreData.setGenre(genre);
				playStoreData.setSize(size);
				playStoreData.setVersion(version);
				playStoreData.setPublishDate(publishDate);
				playStoreData.setPackageName(packageName);
				playStoreData.setUrl(url);

				System.out.println("----------Play Store Data--------------");

				// showing game details
				System.out.println("Title of Game: " + title);
				System.out.println("Genre:" + genre);
				System.out.println("Version: " + version);
				System.out.println("File Size: " + size);
				System.out.println("Update date: " + publishDate);
				System.out.println("Package Name:" + packageName);
				System.out.println("Play Store URL:" + url);
				logger.debug("PlayStore Data Scraping Completed");
			}

		} catch (UnknownHostException u) {
			try {
				Thread.sleep(1000); // wait for a second
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// if unknown host exception occurs call the same method again
			PlayStoreDataFetching asdf = new PlayStoreDataFetching();
			asdf.getPlayStoreData(url);

		}

		catch (Exception e) {

			return null;
		}
		return playStoreData;
	}

	// creating CSV file for play store data
	public boolean createCsv(PlayStoreData playStoreData, String downloadFileName) {

		boolean notFound = false;
		try {
			// adding data to CSV
			File file = new File(jsonInfo.getCsvDownloadFilePath() + "/" + downloadFileName);
			File dir = new File(jsonInfo.getCsvDownloadFilePath());
			
			if (!dir.exists()) {
				dir.mkdirs();
				System.out.println("directory created");
				logger.debug("Directory created "+dir.toString());
			}

			if (!file.exists())
				notFound = true;

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);

			// if file doesn't exists, then create it
			if (notFound) {
				file.createNewFile();
				logger.debug("New file is Created "+file.toString());
				bw.append("PlayStore Title,Genre,Size,Version,Publish Date,Package,Url,");
				bw.append("Apk Title,Genre,Size,Version,Publish Date,Download Link,");
				bw.newLine();
			}

			// appending data to CSV
			bw.append(playStoreData.getTitle());
			bw.append(",");
			bw.append(playStoreData.getGenre());
			bw.append(",");
			bw.append(playStoreData.getSize());
			bw.append(",");
			bw.append(playStoreData.getVersion());
			bw.append(",");
			bw.append(playStoreData.getPublishDate());
			bw.append(",");
			bw.append(playStoreData.getPackageName());
			bw.append(",");
			bw.append(playStoreData.getUrl());
			bw.append(",");
			bw.close();

			System.out.println(playStoreData.getTitle() + " Play store data Stored in csv");
			System.out.println("");
			logger.debug(playStoreData.getTitle() + " Play store data Stored in csv");
		} catch(IOException e){
			//ignore
		}
		catch (Exception e) {
			System.out.println("Exception while creating CSV "+e.toString());
			return false;
		}
		return true;

	}

}
