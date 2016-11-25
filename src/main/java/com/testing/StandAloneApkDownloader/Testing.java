package com.testing.StandAloneApkDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.chrome.ChromeDriver;


import com.game.dto.JsonInfo;
import com.game.dto.PlayStoreData;
import com.game.dto.ScrapedData;
import com.game.model.ApkDownloadSelenium;
import com.game.model.ApkSiteDataFetching;
import com.game.model.GameNotFound;
import com.game.model.IsDownloaded;
import com.game.model.PlayStoreDataFetching;
import com.game.model.PlayStoreUrlFetching;

public class Testing {
	public static void main(String[] args) throws IOException, InterruptedException, ParseException {
		PlayStoreUrlFetching playStoreUrlFetching = new PlayStoreUrlFetching();
		PlayStoreDataFetching playStoreDataFetching = new PlayStoreDataFetching();
		ApkSiteDataFetching apkSiteDataFetching = new ApkSiteDataFetching();
		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		IsDownloaded isDownloaded = new IsDownloaded();
		GameNotFound gameNotFound = new GameNotFound();
		JsonInfo jsonInfo = new JsonInfo();
		
		PlayStoreData playStoreData=new PlayStoreData();
		ScrapedData scrapedData=new ScrapedData();

		JSONParser parser = new JSONParser();

		ArrayList<String> playStoreDetails = new ArrayList<String>();
		ArrayList<String> apkSiteDetails = new ArrayList<String>();
		ArrayList<String> urlList = new ArrayList<String>();
		ArrayList<ChromeDriver> driverList = new ArrayList<ChromeDriver>();
		//ArrayList<String> arrayList = new ArrayList<String>();

		Scanner scanner = new Scanner(System.in);

		String url;
		String line;
		String temp;
		//String csvFilePath = "";
		String downloadFilePath = "";
		String fileName = null;
		int size = 0;

		int count = 0;
		int totalGames = 0;
		int progress = 0;

		boolean status = true;
		boolean psStatus = true;

		try {
			
			Object obj = parser.parse(new FileReader(new File("properties.json").getAbsolutePath()));

			JSONObject jsonObject = (JSONObject) obj;


			// getting properties from JSON

			jsonInfo.setChromeDriverPath((String) jsonObject.get("chromeDriverPath"));
			jsonInfo.setCredentialsPath((String) jsonObject.get("credentialsPath"));
			jsonInfo.setChromeExtensionPath((String) jsonObject.get("chromeExtensionPath"));
			jsonInfo.setApkFileDownloadFolder((String) jsonObject.get("apkFileDownloadFolder")+"/");
			jsonInfo.setCsvFilePath((String) jsonObject.get("csvFilePath"));
			jsonInfo.setPropertyJsonFilePath((String) jsonObject.get("propertyJsonFilePath"));
			downloadFilePath = (String) jsonObject.get("csvDownloadFilePath");
			fileName = jsonInfo.getCsvFilePath();
			
			System.out.println("Path of Csv File:"+jsonInfo.getCsvFilePath());
			System.out.println("Path of Property.json file:"+jsonInfo.getPropertyJsonFilePath());
			System.out.println("Chrome Driver path:" + jsonInfo.getChromeDriverPath());
			System.out.println("Chrome Extension path:" + jsonInfo.getChromeExtensionPath());
			System.out.println("Extension Credentials path:" + jsonInfo.getCredentialsPath());
			System.out.println("Download folder of APK:" + jsonInfo.getApkFileDownloadFolder());
			System.out.println("Path to store Scraped data file:"+downloadFilePath);
			System.out.println("-------------------------------------");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//setting properties to respective classes
		gameNotFound.setFilePath(downloadFilePath);
		jsonInfo.setCsvDownloadFilePath(downloadFilePath);

		apkDownloadSelenium.setJsonInfo(jsonInfo);
		isDownloaded.setJsonInfo(jsonInfo);
		playStoreDataFetching.setJsonInfo(jsonInfo);
		apkSiteDataFetching.setJsonInfo(jsonInfo);
		//end of setting properties
		
		//deleting file if already exist
		File fileCsv=new File(downloadFilePath+"/"+fileName);
		if(fileCsv.exists()){
			fileCsv.delete();
			System.out.println("Previous "+fileName+" deleted");
		}
		File filePlayStoreCsv=new File(downloadFilePath+"/"+"PlayStoreNotFetched.csv");
		if(filePlayStoreCsv.exists()){
			filePlayStoreCsv.delete();
			System.out.println("Previous PlayStoreNotFetched.csv deleted");
		}
		File fileApkCsv=new File(downloadFilePath+"/"+"DlApkStoreNotFetched.csv");
		if(fileApkCsv.exists()){
			fileApkCsv.delete();
			System.out.println("Previous DlApkStoreNotFetched.csv deleted");
		}
		File fileUrlCsv=new File(downloadFilePath+"/"+"UrlNotFetched.csv");
		if(fileUrlCsv.exists()){
			fileUrlCsv.delete();
			System.out.println("Previous UrlNotFetched.csv deleted");
		}
		
		System.out.println("-------------------------------------");
		
		// counting no of games from file
		BufferedReader brCount = new BufferedReader(new FileReader(jsonInfo.getCsvFilePath()));
		while (brCount.readLine() != null) {
			count++;
		}
		totalGames = count - 1;
		brCount.close();
		// end of counting games

		// ***********scraping play store and DL-APK data and storing it in CSV*********

		FileReader fr = new FileReader(jsonInfo.getCsvFilePath());
		BufferedReader br = new BufferedReader(fr);
		line = br.readLine();
		if (line == null) {
			System.out.println("file is empty");
		} else {
			line = br.readLine();

			for (progress = 0; progress < totalGames; progress++) {

				temp = line;
				String[] gname = line.split("\\,");

				// Separates the game name from line read from file
				line = gname[1];
				System.out.println("Game Name= " + line);

				// getting URL for game
				url = playStoreUrlFetching.findUrl(line);

				line = br.readLine();

				// exception handling if URL not found
				if (url == null) {
					gameNotFound.addGameNotFound("Url", temp);
					System.err.println("URL Not Found");
					continue;
				} // end of handling in URL fetching

				// getting play store site data
				playStoreData = playStoreDataFetching.getPlayStoreData(url);

				// handling exception in play store details
				if (playStoreData.equals(null)) {
					gameNotFound.addGameNotFound("PlayStore", temp);
					System.err.println("PlayStore Data Not Found");
				} // end of handling in play store details

				// creating CSV file of play store data
				psStatus = playStoreDataFetching.createCsv(playStoreData, fileName);

				// handling exception in creating play store data CSV
				// file

				if (psStatus == false) {
					//gameNotFound.addGameNotFoundInFile("PlayStore", temp, fileName);
					System.err.println("PlayStore CSV is not created \n");
				}

				// getting play store package name
				//String pack = playStoreDataFetching.getPackage(playStoreData);

				// getting APK-DL site data
				scrapedData = apkSiteDataFetching.createApkSiteDetails(playStoreData, 0, 0, "");

				// handling exception in APK site details
				if (scrapedData == null) {
					gameNotFound.addGameNotFoundInFile("DlApk", temp,fileName);
					System.err.println("DL-APK Data Not Found");
				} // end of handling in APK-DL

				// creating CSV file of APK-DL site details
				status = apkSiteDataFetching.createCsv(scrapedData, fileName);

				// handling exception in creating APK-DL data CSV file if
				if (status == false) { //
					//gameNotFound.addGameNotFoundInFile("DlApk", temp,fileName);
					System.err.println("Dl-APK CSV is not created \n");
				}

			}
			urlList = apkDownloadSelenium.readFile(downloadFilePath+"/"+jsonInfo.getCsvFilePath());

			System.out.println("url list:" + urlList.toString());

			// -------opening play store links using SELENIUM--------
			size = urlList.size() / 5;
			size = size * 5;
			for (int i = 0; i < size; i++) {
				System.out.println("play store url:" + urlList.get(i));
				//logger.debug("play store url:" + urlList.get(i));

				// downloading APK using SELENIUM
				driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(i)));

				if (i % 5 == 4) {

					// checking download completed or not
					isDownloaded.isDownloadCompleted(jsonInfo.getCsvDownloadFilePath(), fileName, i);

					// closing all tabs
					for (ChromeDriver driver : driverList) {
						apkDownloadSelenium.closeTabs(driver);
					}
					driverList.clear();
				}

			}

			if (urlList.size() != size) {
				for (int j = size; j < urlList.size(); j++) {
					driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(j)));
				}
				System.out.println("size:" + size + " Urlist size: " + urlList.size());
				//logger.debug("size:" + size + " Urlist size: " + urlList.size());
				// checking download completed or not
				isDownloaded.isDownloadCompleted(jsonInfo.getCsvDownloadFilePath(), fileName, urlList.size());
			}
			// closing all tabs
			for (ChromeDriver driver : driverList) {
				apkDownloadSelenium.closeTabs(driver);
			}

			System.out.println("Apk Downloading completed");
			//logger.debug("Apk Downloading completed");

			try {
				scanner.close();
			} catch (Exception e) {
				// ignore
			}
		}// end of main
	}
}
