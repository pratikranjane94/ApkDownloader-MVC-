/*File Name	: GameJsoupDao.java
 *Created By: Pratik Ranjane
 *Purpose	: Creating interface to declare operations to be performed on database.
 * */

package com.game.dao;

import java.util.List;

import com.game.dto.ScrapedData;

public interface GameJsoupDao {

	// Inserting JSOUP data into database
	public void insert(ScrapedData count);
	
	// Returns whether database is empty or not
	public boolean isEmpty();
	
	// Returns last file name from database
	public String checkLastFileName();

	// Returns last id of given file
	public int checkId(String fileName);
	
	// Returns last id in database
	public int checkLastId();
	
	// Returns last progress of given file
	public int checkProgress(String fileName, int id);

	//Update the filename to filename concatenated with id
	public void update(String fileName, int id);
	
	public List<ScrapedData> getFileRecords(String fileName);
}
