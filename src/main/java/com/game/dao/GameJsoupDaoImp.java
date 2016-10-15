/*File Name	: GameJsoupDaoImp.java
 *Created By: Pratik Ranjane
 *Purpose	: Creating database related operation for JSOUP data.  
 * */

package com.game.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.game.dto.Count;

@Repository("gameJsoupDao")
public class GameJsoupDaoImp implements GameJsoupDao {

	// creating session factory
	@Resource(name = "sessionFactory")
	SessionFactory sessionFactory;

	// creating session
	Session session;

	// Inserting JSOUP data into database
	public void insert(int id, int no, String fileName, ArrayList<String> playStoreDetails,
			ArrayList<String> apkSiteDetails) {

		session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();

		Count count = new Count();

		count.setId(id);
		count.setNo(no);
		count.setFileName(fileName);
		count.setTitle(playStoreDetails.get(0));
		count.setGenre(playStoreDetails.get(1));
		count.setSize(playStoreDetails.get(2));
		count.setVersion(playStoreDetails.get(3));
		count.setPublishDate(playStoreDetails.get(4));
		count.setPackageName(playStoreDetails.get(5));
		count.setUrl(playStoreDetails.get(6));
		if (apkSiteDetails != null) {
			try {
				count.setDlTitle(apkSiteDetails.get(0));
				count.setDlGenre(apkSiteDetails.get(1));
				count.setDlSize(apkSiteDetails.get(2));
				count.setDlVersion(apkSiteDetails.get(3));
				count.setDlPublishDate(apkSiteDetails.get(4));
				count.setDownloadLink(apkSiteDetails.get(5));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Storing data into database
		session.save(count);
		tr.commit();
		session.close();
		System.out.println("Jsoup data stored in database");
	}

	// Returns whether database is empty or not
	@SuppressWarnings("rawtypes")
	public boolean isEmpty() {
		session = sessionFactory.openSession();

		Query query = session.createQuery("from Count");
		List list = query.list();

		System.out.println("Is empty:" + list.size());
		session.close();
		if (list.size() <= 0)
			return true;
		else
			return false;
	}

	// Returns last file name from database
	@SuppressWarnings("rawtypes")
	public String checkLastFileName() {
		Count count = new Count();
		String lastFileName;

		session = sessionFactory.openSession();

		Query query = session.createQuery("from Count order by id DESC");
		query.setMaxResults(1);

		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}
		session.close();
		lastFileName = count.getFileName();
		System.out.println("last file Name:" + lastFileName);
		return lastFileName;
	}

	// Returns last id of given file
	@SuppressWarnings("rawtypes")
	public int checkId(String fileName) {
		Count count = new Count();
		int id = 0;

		session = sessionFactory.openSession();

		Query query = session.createQuery("from Count where fileName=:fileName order by no DESC");
		query.setString("fileName", fileName);
		query.setMaxResults(1);

		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}

		id = count.getId();
		System.out.println("Last id(db):" + id);
		session.close();
		return id;
	}

	// Returns last id in database
	@SuppressWarnings("rawtypes")
	public int checkLastId() {
		Count count = new Count();
		int id = 0;

		session = sessionFactory.openSession();

		Query query = session.createQuery("from Count order by id DESC");
		query.setMaxResults(1);

		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
			count.toString();
		}

		id = count.getId();
		System.out.println("last id in database regardless of filename:" + id);
		session.close();
		return id;
	}

	// Returns last progress of given file
	@SuppressWarnings("rawtypes")
	public int checkProgress(String fileName, int id) {
		Count count = new Count();
		int progress = 0;

		session = sessionFactory.openSession();

		// Returns data in descending order
		Query query = session.createQuery("from Count where fileName=:fileName and id=:id order by no DESC");
		query.setString("fileName", fileName);
		query.setInteger("id", id);

		// Restricting no of records to one to get last progress
		query.setMaxResults(1);

		List list = query.list();
		for (int i = 0; i < list.size(); i++) {
			count = (Count) list.get(i);
		}
		// getting last progress
		progress = count.getNo();
		session.close();
		System.out.println("total Progress(db):" + progress);
		return progress;
	}

	// Update the filename to filename concatenated with id
	public void update(String fileName, int id) {
		session = sessionFactory.openSession();

		Query update = session.createQuery("update Count set fileName=:filename where id=:id");
		update.setString("filename", fileName);
		update.setInteger("id", id);
		update.executeUpdate();
		session.close();
		System.out.println("Updated");
	}

	@SuppressWarnings("unchecked")
	public String getFileRecords(String fileName) {
		session = sessionFactory.openSession();
		String data = "PlayStore Title,Genre,Size,Version,Publish Date,Package,Url,Apk Title,Genre,Size,Version,Publish Date,Download Link \n";

		Query query = session.createQuery("from Count where fileName=:fileName");
		query.setString("fileName", fileName);

		List<Count> list = query.list();
		for (Count count2 : list) {
			data = data + count2.getTitle();
			data = data + ",";
			data = data + count2.getGenre();
			data = data + ",";
			data = data + count2.getSize();
			data = data + ",";
			data = data + count2.getVersion();
			data = data + ",";
			data = data + count2.getPublishDate();
			data = data + ",";
			data = data + count2.getPackageName();
			data = data + ",";
			data = data + count2.getUrl();
			data = data + ",";
			data = data + count2.getDlTitle();
			data = data + ",";
			data = data + count2.getDlGenre();
			data = data + ",";
			data = data + count2.getDlSize();
			data = data + ",";
			data = data + count2.getDlVersion();
			data = data + ",";
			data = data + count2.getDlPublishDate();
			data = data + ",";
			data = data + count2.getDownloadLink();
			data = data + "\n";
		}
		System.out.println(data);

		return data;

	}
}
