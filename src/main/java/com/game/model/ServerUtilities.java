package com.game.model;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import com.game.dto.FileMeta;
import com.game.dto.PlayStoreData;
import com.game.dto.ScrapedData;

public class ServerUtilities {
	Logger logger=Logger.getLogger("SERVERUTILITIES");
	public String databaseToCSVConverter(List<ScrapedData> list) {
		PlayStoreData playStoreData = new PlayStoreData();
		String data = "PlayStore Title,Genre,Size,Version,Publish Date,Package,Url,Apk Title,Genre,Size,Version,Publish Date,Download Link \n";
		for (ScrapedData scraperList : list) {
			// getting play store data
			playStoreData = scraperList.getPlayStoreData();
			data = data + playStoreData.getTitle();
			data = data + ",";
			data = data + playStoreData.getGenre();
			data = data + ",";
			data = data + playStoreData.getSize();
			data = data + ",";
			data = data + playStoreData.getVersion();
			data = data + ",";

			String date = playStoreData.getPublishDate();
			if (date.contains(","))
				date = date.replaceAll(",", "");

			data = data + date;
			data = data + ",";
			data = data + playStoreData.getPackageName();
			data = data + ",";
			data = data + playStoreData.getUrl();

			// getting DL-APK data
			data = data + ",";
			data = data + scraperList.getDlTitle();
			data = data + ",";
			data = data + scraperList.getDlGenre();
			data = data + ",";
			data = data + scraperList.getDlSize();
			data = data + ",";
			data = data + scraperList.getDlVersion();
			data = data + ",";
			data = data + scraperList.getDlPublishDate();
			data = data + ",";
			data = data + scraperList.getDownloadLink();
			data = data + "\n";
		}
		return data;
	}
	
	public HttpServletResponse setDataInResponse(FileMeta fileMeta, HttpServletResponse response, String data) {
		try {
			response.setContentType(fileMeta.getFileType());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileMeta.getFileName() + "\"");

			FileCopyUtils.copy(data.getBytes(), response.getOutputStream());

			logger.debug("Name of file to be downloaded:" + fileMeta.getFileName());
			System.out.println("Name of file to be downloaded:" + fileMeta.getFileName());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug("Exception while setting response",e);
		}
		return response;
	}

}
