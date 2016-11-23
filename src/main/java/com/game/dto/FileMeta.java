/*File Name	: FileMeta.java
 *Created By: PRATIK RANJANE
 *Purpose	: Storing details of uploaded file
 * */

package com.game.dto;

public class FileMeta {

	private String fileName;			//name of file uploaded
	private String fileType;			//type of file uploaded
	private byte[] bytes;				//uploaded file is stored in bytes
	private byte[] downBytes;			//JSOUP file is stored
	private String downloadFileName;	//JSOUP file name(i.e Download name)
	private int totalGames;				//no of games in file
	private int progress;				//no of game JSOUP done
	
	
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
	public String getDownloadFileName() {
		return downloadFileName;
	}
	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public byte[] getDownBytes() {
		return downBytes;
	}
	public void setDownBytes(byte[] downBytes) {
		this.downBytes = downBytes;
	}
	
	public int getTotalGames() {
		return totalGames;
	}
	public void setTotalGames(int totalGames) {
		this.totalGames = totalGames;
	}

}
