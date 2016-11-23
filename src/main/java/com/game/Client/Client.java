/*File Name		: Client.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Entry point of project, sends file to the server,download APK using SELENIUM 
 *				  and checks whether APK are download or not
 * */

package com.game.Client;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.openqa.selenium.chrome.ChromeDriver;

import com.game.controllers.Utility;
import com.game.dto.JsonInfo;
import com.game.model.ApkDownloadSelenium;
import com.game.model.ApkSiteDataFetching;
import com.game.model.IsDownloaded;
import com.game.model.PlayStoreDataFetching;

public class Client {	

	public static void main(String[] args) throws InterruptedException, IOException, ParseException {
		
		Logger logger=Logger.getLogger("CLIENT");
		
		PlayStoreDataFetching playStoreDataFetching = new PlayStoreDataFetching();
		ApkSiteDataFetching apkSiteDataFetching = new ApkSiteDataFetching();
		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		IsDownloaded isDownloaded = new IsDownloaded();
		Utility utility = new Utility();

		JsonInfo jsonInfo = new JsonInfo();

		JSONParser parser = new JSONParser();
		Scanner scanner = new Scanner(System.in);

		ArrayList<String> urlList = new ArrayList<>();
		ArrayList<ChromeDriver> driverList = new ArrayList<>();

		File file;
		FileBody fileBody;
		MultipartEntityBuilder builder;
		HttpResponse response;
		HttpEntity entity;

		String csvFilePath = "";

		String propertyJsonPath = "";
		String fileName = null;
		String filePath = null;

		int size = 0;

		System.out.println("Enter property.json file path:");
		propertyJsonPath = scanner.next();

		Object obj = parser.parse(new FileReader(propertyJsonPath));

		JSONObject jsonObject = (JSONObject) obj;

		// getting properties from JSON
		jsonInfo.setRestCall((String) jsonObject.get("restCall"));
		jsonInfo.setChromeDriverPath((String) jsonObject.get("chromeDriverPath"));
		jsonInfo.setCredentialsPath((String) jsonObject.get("credentialsPath"));
		jsonInfo.setChromeExtensionPath((String) jsonObject.get("chromeExtensionPath"));
		jsonInfo.setApkFileDownloadFolder((String) jsonObject.get("apkFileDownloadFolder") + "/");
		jsonInfo.setCsvDownloadFilePath((String) jsonObject.get("csvDownloadFilePath"));

		System.out.println("RestCall:" + jsonInfo.getRestCall() + 
				"\n Chrome driver path:"+ jsonInfo.getChromeDriverPath() + 
				"\n Login Credentials path:" + jsonInfo.getCredentialsPath()	+ 
				"\n Chrome extension path:" + jsonInfo.getChromeExtensionPath() + 
				"\n Apk Download folder:" + jsonInfo.getApkFileDownloadFolder());

		System.out.println("Enter csv file path:");
		csvFilePath = scanner.next();

		// passing properties from JSON to required class
		apkDownloadSelenium.setJsonInfo(jsonInfo);
		isDownloaded.setJsonInfo(jsonInfo);
		playStoreDataFetching.setJsonInfo(jsonInfo);
		apkSiteDataFetching.setJsonInfo(jsonInfo);

		// --------------- rest call to upload file-----------------

		HttpClient httpClient = HttpClientBuilder.create().build();

		// calling REST
		HttpPost httpPost = new HttpPost(jsonInfo.getRestCall());

		// attaching file with request
		file = new File(csvFilePath);
		fileBody = new FileBody(file);

		builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		builder.addPart("files", fileBody);
		entity = builder.build();
		httpPost.setEntity(entity);

		// execute HTTP post request
		response = httpClient.execute(httpPost);

		// ------------------end of rest call----------------------

		System.out.println("response:" + response.toString());
		// reading file name from response

		fileName = utility.getFileNameFromResponse(response);

		// end of reading file name

		// -----------------downloading file to client machine------------

		utility.writeResponseData(response, jsonInfo, fileName);
		// resEntity = response.getEntity();

		filePath = jsonInfo.getCsvDownloadFilePath() + "/" + fileName;

		// ---------------------end of downloading file-------------------

		System.out.println("File saved");
		logger.debug("File saved");

		// getting list of play store link by reading file
		urlList = apkDownloadSelenium.readFile(filePath);

		System.out.println("url list:" + urlList.toString());

		// -------opening play store links using SELENIUM--------
		size = urlList.size() / 5;
		size = size * 5;
		for (int i = 0; i < size; i++) {
			System.out.println("play store url:" + urlList.get(i));
			logger.debug("play store url:" + urlList.get(i));

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
			logger.debug("size:" + size + " Urlist size: " + urlList.size());
			// checking download completed or not
			isDownloaded.isDownloadCompleted(jsonInfo.getCsvDownloadFilePath(), fileName, urlList.size());
		}
		// closing all tabs
		for (ChromeDriver driver : driverList) {
			apkDownloadSelenium.closeTabs(driver);
		}

		System.out.println("Apk Downloading completed");
		logger.debug("Apk Downloading completed");

		try {
			scanner.close();
		} catch (Exception e) {
			// ignore
		}
	}// end of main
}
