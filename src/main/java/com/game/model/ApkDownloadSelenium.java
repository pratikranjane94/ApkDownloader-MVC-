/*File Name		: ApkDownloadSelenium.java
 *Created By	: PRATIK RANJANE
 *Purpose		: 
 * */

package com.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.game.dto.JsonInfo;

public class ApkDownloadSelenium {

	Logger logger = Logger.getLogger("APKSELENIUM");

	private JsonInfo jsonInfo;

	public void setJsonInfo(JsonInfo jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	public ChromeDriver downloadApkUsingSelenium(String playStoreUrl)
			throws InterruptedException, IOException, ParseException {

		ChromeDriver driver = null;
		String username = null;
		String password = null;
		String androidId = null;

		// getting credentials

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(jsonInfo.getCredentialsPath()));

			JSONObject jsonObject = (JSONObject) obj;

			username = (String) jsonObject.get("username");

			password = (String) jsonObject.get("password");

			androidId = (String) jsonObject.get("androidId");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// end of getting credentials

		try {
			// login URL
			String chromeUrl = "chrome-extension://bifidglkmlbfohchohkkpdkjokajibgg/login.html";

			System.setProperty("webdriver.chrome.driver", jsonInfo.getChromeDriverPath());

			ChromeOptions options = new ChromeOptions();

			// adding extension in CHROME
			options.addExtensions(new File(jsonInfo.getChromeExtensionPath()));

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new ChromeDriver(capabilities);

			System.out.println("Opening page:" + playStoreUrl);
			logger.debug("Opening page:" + playStoreUrl);

			// opening login page
			driver.get(chromeUrl);

			// setting email id
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			driver.findElement(By.id("inp-email")).sendKeys(username);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// setting password
			driver.findElement(By.id("inp-password")).sendKeys(password);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// setting android id
			driver.findElement(By.id("inp-gsf-id")).sendKeys(androidId);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// clicking login button
			driver.findElement(By.id("inp-gsf-id")).sendKeys(Keys.ENTER);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// opening play store tab in new window
			String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, "t");
			driver.findElement(By.id("inp-gsf-id")).sendKeys(selectLinkOpeninNewTab);

			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

			// switches to new tab
			driver.switchTo().window(tabs.get(2));
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// opening game's play store page
			driver.get(playStoreUrl);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// clicking download button
			driver.findElement(By.cssSelector("span.large.play-button.download-apk-button.apps button")).click();
			// Maximize the window.

			// driver.manage().window().maximize();
			System.out.println("Automation Completed");
			logger.debug("Automation Completed");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Selenium exception! Opening page again");
			logger.debug("Selenium Exception! Opening page again", e);
			driver.quit();
			downloadApkUsingSelenium(playStoreUrl);
		}

		return driver;
	}

	// logging out and closing all CHROME windows
	public void closeTabs(ChromeDriver driver) {

		try {
			// listing all tabs
			ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs2.get(1));
			driver.close();

			driver.switchTo().window(tabs2.get(2));
			driver.close();

			driver.switchTo().window(tabs2.get(0));

			WebElement element = driver.findElement(By.id("btn-logout"));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			driver.switchTo().alert().accept();
			driver.quit();
		} catch (Exception e) {
			System.err.println("Exception in closing tab");
			logger.debug("Exception in closing tab", e);
		}

	}

	// reading download file to get play store URL
	public ArrayList<String> readFile(String filePath) throws IOException {

		String url = null;
		ArrayList<String> urlList = new ArrayList<>();

		System.out.println("Filepath:" + filePath);

		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		url = br.readLine();

		while (url != null) {
			url = br.readLine();
			try {
				System.out.println("URL:"+url);
				if (url.trim().equals(null) || url.trim().equals("") ) {
					continue;
				}
			} catch (Exception e) {
				logger.debug("Excepion in isEmpty", e);
				// ignore
			}
			System.out.println("url:" + url);
			try {
				String[] gname = url.split("\\,");
				url = gname[6];

				if (!url.equals(null))
					urlList.add(url);

				
			} catch (NullPointerException e) {
				System.out.println("Unable to read URL");
				logger.debug("Unable to read URL", e);
			} catch (Exception e) {
				// ignore
			}
		}
		br.close();
		return urlList;
	}

}
