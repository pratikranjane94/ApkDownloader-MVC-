/*File Name		: IsDownloaded.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Checking each game from file is download or not, if download is completed it is moved into 
 *				  folder with same name as file name.
 * */

package com.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import com.game.dto.JsonInfo;

public class IsDownloaded {

	private JsonInfo jsonInfo;

	public JsonInfo getJsonInfo() {
		return jsonInfo;
	}

	public void setJsonInfo(JsonInfo jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	/*
	 * checks game is download or not logic:incomplete download file contains
	 * extension .crxdownload. if filename in directory/folder not end with .apk
	 * returns false, else file download is completed and will return true with
	 * actual file name
	 */
	public ArrayList<String> isFileDownloaded(String fileName) {
		String flag = "false";
		ArrayList<String> info = new ArrayList<>();

		File dir = new File("/home/bridgelabz6/Downloads/apk-downloader/");
		File[] dir_contents = dir.listFiles();

		for (int i = 0; i < dir_contents.length; i++) {
			// System.out.println(fileName + " is downloading");
			try {
				if (dir_contents[i].getName().contains(fileName) && dir_contents[i].getName().endsWith(".apk")) {
					flag = "true";
					info.add(dir_contents[i].getName());
					info.add(flag);
					return info;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		info.add("null");
		info.add("false");
		return info;
	}

	// reads the game name from file and checks whether it is download or not
	public void isDownloadCompleted(String downloadFilePath, String fileName, int last)
			throws IOException, InterruptedException {
		System.out.println("file path:" + downloadFilePath);
		FileReader st1 = new FileReader(downloadFilePath + fileName);
		BufferedReader st2 = new BufferedReader(st1);
		ArrayList<String> data = new ArrayList<>();

		int loop = 0;
		String folderName = fileName.replaceAll(".csv", "");

		System.out.println("File Name:" + fileName);

		String dowFileName;
		int progress;

		if (last % 5 == 4) {
			loop = last / 5;

			loop = loop * 5;
		} else {
			if (last > 10) {
				loop = last % 5;
				loop = loop * 5;
			}
			else
				loop=0;
		}
		if (loop % 5 == 0 && loop != 0)
			loop = loop + 1;

		System.out.println("Loop till:" + loop);

		for (int j = 0; j < loop - 1; j++) {
			st2.readLine();
		}

		dowFileName = st2.readLine();
		if (dowFileName == null) {
			System.out.println("file is empty");
		} else {
			for (progress = 0; progress < 5; progress++) {
				try {

					dowFileName = st2.readLine();

					String[] gname = dowFileName.split("\\,");
					dowFileName = gname[5];
					if (dowFileName.equals(null))
						break;
					System.out.println("Game name:" + dowFileName);
				} catch (Exception e) {
					break;
				}

				data = isFileDownloaded(dowFileName);

				if (data.get(1).equals("true")) {
					String movePath = jsonInfo.getApkFileDownloadFolder();
					String targetPath = movePath + folderName + "/";

					File file = new File(targetPath);
					if (!file.exists())
						file.mkdirs();

					movePath = movePath.concat(data.get(0));
					targetPath = targetPath.concat(data.get(0));

					Path movefrom = FileSystems.getDefault().getPath(movePath);
					Path target = FileSystems.getDefault().getPath(targetPath);

					// moving download file into new folder
					Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);

					System.out.println(dowFileName + " is downloaded");
				} else {
					data = isFileDownloaded(dowFileName);

					//if file is currently downloading, keeps checking until downloading is completed
					while (data.get(1) != "true") {
						Thread.sleep(1000);
						System.out.println("download file name:" + dowFileName);
						data = isFileDownloaded(dowFileName);
					}
					System.out.println(dowFileName + " is downloded");

					String movePath = jsonInfo.getApkFileDownloadFolder();
					String targetPath = movePath + folderName + "/";

					File file = new File(targetPath);
					if (!file.exists())
						file.mkdirs();

					movePath = movePath.concat(data.get(0));
					targetPath = targetPath.concat(data.get(0));

					Path movefrom = FileSystems.getDefault().getPath(movePath);
					Path target = FileSystems.getDefault().getPath(targetPath);

					// moving download file into new folder
					Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);

					// dowFileName = st2.readLine();

					/*
					 * if(dowFileName.equals(null)) break;
					 */

				} // end of inner for else

			} // end of for
			
			st2.close();
			System.out.println("completed for loop");
		}

	}
	
}
