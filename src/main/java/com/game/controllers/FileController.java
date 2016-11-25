/*File Name	: FileController.java
 *Created By: PRATIK RANJANE
 *Purpose	: Storing the uploaded file, creating JOUSP of games presents in files,
 *			  creating CSV file of details of games using and displaying on web page using Socket,
 *			  Downloading the file. 
 * */
package com.game.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.game.dao.GameJsoupDao;
import com.game.dto.FileMeta;
import com.game.dto.PlayStoreData;
import com.game.dto.ScrapedData;
import com.game.model.ApkDownloadSelenium;
import com.game.model.ApkSiteDataFetching;
import com.game.model.ServerUtilities;
import com.game.model.GameNotFound;
import com.game.model.PlayStoreDataFetching;
import com.game.model.PlayStoreUrlFetching;

@RestController
@EnableWebMvc
@RequestMapping("/controller")
public class FileController {

	@Resource(name = "gameJsoupDao")
	private GameJsoupDao gameJsoupDao;

	Logger logger = Logger.getLogger("CONTROLLER");

	/*-------------------------------------------Creating JSOUP of Uploaded File-------------------------------------------*/

	@SuppressWarnings("unused")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload(@RequestParam("files") MultipartFile mpf, HttpServletResponse response)
			throws IOException, InterruptedException {

		// Model Class objects
		PlayStoreUrlFetching playStoreUrlFetching = new PlayStoreUrlFetching();
		PlayStoreDataFetching playStoreDataFetching = new PlayStoreDataFetching();
		ApkSiteDataFetching apkSiteDataFetching = new ApkSiteDataFetching();
		ServerUtilities serverUtilities = new ServerUtilities();

		GameNotFound gameNotFound = new GameNotFound();
		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		Utility utility = new Utility();

		// DTO class objects
		FileMeta fileMeta = new FileMeta();
		PlayStoreData playStoreData = new PlayStoreData();
		ScrapedData scrapedData = new ScrapedData();

		ArrayList<ChromeDriver> closeTabs = new ArrayList<>();

		String url = ""; // Play Store URL
		String temp = ""; // game name temporary
		String fileName; // name of uploaded file
		String downloadFileName; // downloading name for file.
		String fileNameID = null; // file name with id
		int progress = 0; // no of game's data is scraped
		int noOfLines = 0; // total no of game in file
		int id = 0; // unique id for each uploaded file
		int totalGames = 0; // total games in file
		boolean status = true; // status of APK-DL CSV created or not
		boolean psStatus = true; // status of PlayStore CSV created or not

		System.out.println("-----------------* Game Scraping Controller *-----------------");

		// getting uploaded MULTIPART file and its information
		System.out.println(mpf.getOriginalFilename() + " uploaded! ");

		// getting original filename and setting download filename
		fileName = mpf.getOriginalFilename();
		downloadFileName = mpf.getOriginalFilename().replace(".", "Download.");

		// storing data in file meta class
		fileMeta.setFileName(fileName);
		fileMeta.setFileType(mpf.getContentType());
		fileMeta.setBytes(mpf.getBytes());

		// getting data from uploaded file
		InputStreamReader reader = new InputStreamReader(mpf.getInputStream());

		BufferedReader buffReader = new BufferedReader(reader);

		buffReader.readLine();

		String line = null;
		while ((line = buffReader.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue;
			}

			// Database entry
			if (gameJsoupDao.isEmpty()) {
				id = 0;
				logger.info("Database is Empty");
			} else {
				// if last filename is same as current filename
				// assign same id to game data
				try {
					if (gameJsoupDao.checkLastFileName().equals(fileName)) {
						id = gameJsoupDao.checkId(fileName);
					} else {
						/*
						 * if filename not matched, different(i.e new) file is
						 * uploaded assign new id(i.e increase id)
						 */
						id = gameJsoupDao.checkLastId();
						id = id + 1;
					}
				} catch (Exception e) {
					logger.debug("Exception while database opertions");
				}

			}

			totalGames = noOfLines++;

			fileMeta.setProgress(progress);
			String[] gname = line.split("\\,");

			// Separates the game name from line read from file
			String gameName = gname[1];
			System.out.println("Game Name:" + gameName);
			logger.debug("Game name" + gameName);

			// getting URL for game
			url = playStoreUrlFetching.findUrl(gameName);

			// exception handling if URL not found
			if (url == null) {
				System.err.println("URL Not Found");
				logger.debug("URL Not Found");
				continue;
			} // end of handling in URL fetching

			// getting play store site data
			playStoreData = playStoreDataFetching.getPlayStoreData(url);

			// handling exception in play store details
			if (playStoreData.equals(null)) {
				System.err.println("PlayStore Data Not Found");
				logger.debug("PlayStore Data Not Found");
			} // end of handling in play store details

			// getting APK-DL site data
			scrapedData = apkSiteDataFetching.createApkSiteDetails(playStoreData, id, noOfLines, fileName);

			// handling exception in APK site details
			if (scrapedData == null) {
				System.err.println("DL-APK Data Not Found");
				logger.debug("DL-APK Data Not Found");
			} // end of handling in APK-DL

			// data is inserted into database
			gameJsoupDao.insert(scrapedData);
			// end of database entry

		} // end of while

		fileNameID = fileName.replace(".", Integer.toString(id) + ".");
		fileMeta.setFileName(fileNameID);

		// updating new file name in database
		gameJsoupDao.update(fileMeta.getFileName(), id);

		try {
			buffReader.close();
		} catch (IOException io) {
			// Ignore
		}

		System.out.println("-----------End Of Program-----------");

		// end of scraping function

		// getting scraped data from database
		List<ScrapedData> list = gameJsoupDao.getFileRecords(fileMeta.getFileName());

		// getting file data from database and converting into CSV format
		String data = serverUtilities.databaseToCSVConverter(list);

		// setting data from database to HTTP SERVLET Response
		response = serverUtilities.setDataInResponse(fileMeta, response, data);

	}// End of sending file data in response
	
	@RequestMapping(value="/test",method=RequestMethod.POST)
	public String test(@RequestParam("gameName") String gameName){
		System.out.println("game name"+gameName);
		return gameName;
	}
	

}// End of class
