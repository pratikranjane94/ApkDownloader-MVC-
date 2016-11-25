/*File Name	: GameJsoupDaoImp.java
 *Created By: Pratik Ranjane
 *Purpose	: Creating database related operation for JSOUP data.  
 * */

package com.game.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.game.dto.ScrapedData;

@Repository("gameJsoupDao")
public class GameJsoupDaoImp implements GameJsoupDao {

	// creating session factory
	// @Resource(name = "sessionFactory")
	@Resource
	SessionFactory sessionFactory;

	// creating session
	Session session;
	// info -> debug -> warning - error
	Logger logger = Logger.getLogger("DAO");

	// Inserting JSOUP data into database
	public void insert(ScrapedData scrapedData) {
		logger.debug("insert start");
		session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		try {
			// Storing data into database
			session.save(scrapedData);
			tr.commit();
		} catch (Exception exp) {
			tr.rollback();
		}
		session.close();
		System.out.println("Jsoup data stored in database");
		logger.debug("Jsoup data stored in database");
	}

	// Returns whether database is empty or not
	@SuppressWarnings("rawtypes")
	public boolean isEmpty() {
		List list = null;
		try {
			session = sessionFactory.openSession();

			Query query = session.createQuery("from ScrapedData");
			list = query.list();

			System.out.println("Is empty:" + list.size());
			session.close();
		} catch (Exception e) {
			logger.debug("Exception in isEmpty", e);
		}
		if (list.size() <= 0) {
			logger.debug("Database is empty");
			return true;
		} else {
			logger.debug("Database is not empty");
			return false;
		}

	}

	// Returns last file name from database
	@SuppressWarnings("rawtypes")
	public String checkLastFileName() {
		ScrapedData count = new ScrapedData();
		String lastFileName = null;

		try {
			session = sessionFactory.openSession();

			Query query = session.createQuery("from ScrapedData order by id DESC");
			query.setMaxResults(1);

			List list = query.list();
			for (int i = 0; i < list.size(); i++) {
				count = (ScrapedData) list.get(i);
			}
			lastFileName = count.getFileName();
			System.out.println("last file Name:" + lastFileName);
		} catch (Exception e) {
			logger.debug("Exception in checking last file name", e);
		}
		session.close();
		return lastFileName;
	}

	// Returns last id of given file
	@SuppressWarnings("rawtypes")
	public int checkId(String fileName) {
		ScrapedData count = new ScrapedData();
		int id = 0;
		try {
			session = sessionFactory.openSession();

			Query query = session.createQuery("from ScrapedData where fileName=:fileName order by no DESC");
			/*
			 * List<ScrapedData> sdLis = (List<ScrapedData>)
			 * session.createCriteria(ScrapedData.class)
			 * .add(Restrictions.eq(fileName, fileName) )
			 * .addOrder(Order.desc("no"));
			 */
			query.setString("fileName", fileName);
			query.setMaxResults(1);

			List list = query.list();
			for (int i = 0; i < list.size(); i++) {
				count = (ScrapedData) list.get(i);
			}

			id = count.getId();
			System.out.println("Last id(db):" + id);
		} catch (Exception e) {
			logger.debug("Exception in checking id", e);
		}
		session.close();

		return id;
	}

	// Returns last id in database
	@SuppressWarnings("rawtypes")
	public int checkLastId() {
		ScrapedData count = new ScrapedData();
		int id = 0;
		try {
			session = sessionFactory.openSession();

			Query query = session.createQuery("from ScrapedData order by id DESC");
			query.setMaxResults(1);

			List list = query.list();
			for (int i = 0; i < list.size(); i++) {
				count = (ScrapedData) list.get(i);
				count.toString();
			}

			id = count.getId();
			System.out.println("last id in database regardless of filename:" + id);
		} catch (Exception e) {
			logger.debug("Exception in checking last id from database", e);
		}
		session.close();

		return id;
	}

	// Returns last progress of given file
	@SuppressWarnings("rawtypes")
	public int checkProgress(String fileName, int id) {
		ScrapedData count = new ScrapedData();
		int progress = 0;
		try {
			session = sessionFactory.openSession();

			// Returns data in descending order
			Query query = session.createQuery("from ScrapedData where fileName=:fileName and id=:id order by no DESC");
			query.setString("fileName", fileName);
			query.setInteger("id", id);

			// Restricting no of records to one to get last progress
			query.setMaxResults(1);

			List list = query.list();
			for (int i = 0; i < list.size(); i++) {
				count = (ScrapedData) list.get(i);
			}
			// getting last progress
			progress = count.getNo();
			System.out.println("total Progress(db):" + progress);
		} catch (Exception e) {
			logger.debug("Exception in checking progress", e);
		}
		session.close();

		return progress;
	}

	// Update the filename to filename concatenated with id
	public void update(String fileName, int id) {
		try {
			session = sessionFactory.openSession();
			Query update = session.createQuery("update ScrapedData set fileName=:filename where id=:id");
			update.setString("filename", fileName);
			update.setInteger("id", id);
			update.executeUpdate();
			logger.debug("File name is updated");
		} catch (Exception e) {
			logger.debug("Exception while updating file name", e);
		}
		session.close();

	}

	@SuppressWarnings("unchecked")
	public List<ScrapedData> getFileRecords(String fileName) {
		List<ScrapedData> list = null;
		try {
			session = sessionFactory.openSession();

			Query query = session.createQuery("from ScrapedData where fileName=:fileName");
			query.setString("fileName", fileName);

			list = query.list();
		} catch (Exception e) {
			logger.debug("Exception in getting file records");
		}
		session.close();
		return list;

	}
}