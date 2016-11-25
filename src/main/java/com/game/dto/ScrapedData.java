package com.game.dto;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class ScrapedData {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int key;
	@Column
	private int id;
	@Column
	private int no;
	@Column
	private String fileName;
	@Column
	private String dlTitle;
	@Column
	private String dlGenre;
	@Column
	private String dlSize;
	@Column
	private String dlVersion;
	@Column
	private String dlPublishDate;
	@Column
	private String downloadLink;
	@Embedded
	private PlayStoreData playStoreData;
	
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDlTitle() {
		return dlTitle;
	}
	public void setDlTitle(String dlTitle) {
		this.dlTitle = dlTitle;
	}
	public String getDlGenre() {
		return dlGenre;
	}
	public void setDlGenre(String dlGenre) {
		this.dlGenre = dlGenre;
	}
	public String getDlSize() {
		return dlSize;
	}
	public void setDlSize(String dlSize) {
		this.dlSize = dlSize;
	}
	public String getDlVersion() {
		return dlVersion;
	}
	public void setDlVersion(String dlVersion) {
		this.dlVersion = dlVersion;
	}
	public String getDlPublishDate() {
		return dlPublishDate;
	}
	public void setDlPublishDate(String dlPublishDate) {
		this.dlPublishDate = dlPublishDate;
	}
	public String getDownloadLink() {
		return downloadLink;
	}
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	public PlayStoreData getPlayStoreData() {
		return playStoreData;
	}
	public void setPlayStoreData(PlayStoreData playStoreData) {
		this.playStoreData = playStoreData;
	}
	@Override
	public String toString() {
		return "ScrapedData [key=" + key + ", id=" + id + ", no=" + no + ", fileName=" + fileName + ", dlTitle="
				+ dlTitle + ", dlGenre=" + dlGenre + ", dlSize=" + dlSize + ", dlVersion=" + dlVersion
				+ ", dlPublishDate=" + dlPublishDate + ", downloadLink=" + downloadLink + ", playStoreData="
				+ playStoreData + "]";
	}	
	
}
