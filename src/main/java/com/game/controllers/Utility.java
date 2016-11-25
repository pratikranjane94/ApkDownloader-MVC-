package com.game.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import com.game.dto.JsonInfo;

@Component
public class Utility {
	Logger logger = Logger.getLogger("UTILITIES");

	public void moveDataToFolder(JsonInfo jsonInfo, String folderName, String fileName) {
		String movePath = jsonInfo.getApkFileDownloadFolder();
		String targetPath = movePath + folderName + "/";

		File file = new File(targetPath);
		if (!file.exists())
			file.mkdirs();

		movePath = movePath.concat(fileName);
		targetPath = targetPath.concat(fileName);

		Path movefrom = FileSystems.getDefault().getPath(movePath);
		Path target = FileSystems.getDefault().getPath(targetPath);

		// moving download file into new folder
		try {
			Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
			logger.debug("APK file moved to new folder");
		} catch (IOException e) {
			System.out.println("Unable to move file");
			logger.debug("Excpetion in moving file");
		}
	}

	public String getFileNameFromResponse(HttpResponse response) {
		String fileName = null;
		Header[] header = response.getHeaders("Content-disposition");
		String cd = null;
		try {
			cd = header[0].getValue();
		String[] cds = cd.split(";");
		for (String string : cds) {
			if (string.trim().startsWith("filename")) {
				fileName = string.split("=")[1];
				fileName = fileName.replaceAll("\"", "");
				System.out.println("File name:" + fileName);
				break;
			}
		}
		} catch (Exception e) {
			System.out.println("Exception in getting file name from response"+e.toString());
		}
		return fileName;
	}

	public void writeResponseData(HttpResponse response, JsonInfo jsonInfo, String fileName) {
		HttpEntity resEntity;
		String filePath;
		resEntity = response.getEntity();

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		if (resEntity != null) {

			try {
				bis = new BufferedInputStream(resEntity.getContent());

				filePath = jsonInfo.getCsvDownloadFilePath() + "/" + fileName;

				bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));

				int inByte;

				while ((inByte = bis.read()) != -1)
					bos.write(inByte);
			} catch (FileNotFoundException e) {
				System.out.println("File not found !");
				logger.debug("File not found");
			} catch (IOException e) {
				System.out.println("IO exception occured");
				logger.debug("IO exception occured while writing data to response", e);
			} catch (UnsupportedOperationException e) {
				logger.debug("UnsupportedOperationException", e);
				e.printStackTrace();
			}

			try {
				bis.close();
				bos.close();
			} catch (IOException e) {
				// ignore
			}

		}

	}

}
