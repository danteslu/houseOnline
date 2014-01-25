/*
 * Dao.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.crud;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.houseOnline.common.MongoConnectionFactory;
import com.houseOnline.common.MongoDBHelper;
import com.houseOnline.common.query.IQuery;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * DAO implement to access MongoDB
 * @author Dantes Lu
 * @since Jan 01, 2014
 */
public class MongoDao<T> {
	
	private final static Properties CLASS_DBCOLLECTION_MAPPING = new Properties();
	
	private final static String CLASS_DBCOLLECTION_PROPERTIES = "Class_DBCollection.properties";
	
	private final static String MONGO_ID_FIELD = "_id";
	
	static {
		
		InputStream inStream = null;
		
		try {
			
			inStream = ClassLoader.getSystemResourceAsStream(CLASS_DBCOLLECTION_PROPERTIES);
			
			CLASS_DBCOLLECTION_MAPPING.load(inStream);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Support page query and order by specify conditions
	 * @param query
	 * @return: a list of String in XML format
	 * @throws Exception 
	 */
	public List<T> find(IQuery<BasicDBObject> query, String wrapperClass) throws Exception {
		
		DBCursor cursor = null;
		
		try {
			
			String collName = query.getFrom();
			
			BasicDBObject returnFields = query.getReturnFields();
			
			BasicDBObject queryObj = query.getQueryConds();
			
			BasicDBObject orderBy = query.orderBy();
			
			DB db = MongoConnectionFactory.getConnection();
			
			DBCollection dbCollection = db.getCollection(collName);
			
			cursor = dbCollection.find(queryObj, returnFields).sort(orderBy);
			
			if (query.isPage()) {
				cursor = cursor.skip(query.getSkip()).limit(query.getLimit());
			}
			
			return extractResult(cursor, wrapperClass);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public T getById(String key, String id, String wrapperClass) throws Exception {
		
		DBObject idObj = new BasicDBObject();
		
		idObj.put(key, id);
		
		String collName = CLASS_DBCOLLECTION_MAPPING.getProperty(wrapperClass);
		
		DBObject returnFields = new BasicDBObject();
		returnFields.put(MONGO_ID_FIELD, 0);
		
		DB db = MongoConnectionFactory.getConnection();
		
		DBCollection dbCollection = db.getCollection(collName);
		
		DBObject objToReturn = dbCollection.findOne(idObj, returnFields);
		
		return (T) MongoDBHelper.dbObj2Bean(objToReturn, wrapperClass);

	}

	public int size(IQuery<BasicDBObject> query) throws Exception {
		
		DBCursor cursor = null;
		
		try {
			
			BasicDBObject queryObj = query.getQueryConds();
			
			DB db = MongoConnectionFactory.getConnection();
			
			DBCollection dbCollection = db.getCollection(query.getFrom());
			
			return dbCollection.find(queryObj).size();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private List<T> extractResult(DBCursor cursor, String className) throws Exception {
		
		List<T> results = new ArrayList<T>();
		
		while (cursor.hasNext()) {
						
			DBObject dbObject = cursor.next();
			
			System.out.println(dbObject);
			
			T bean = (T)MongoDBHelper.dbObj2Bean(dbObject, className);
			
			results.add(bean);
		}
		
		return results;
	}
	
	public void create(Object bean) throws Exception {
		
		String collName = CLASS_DBCOLLECTION_MAPPING.getProperty(bean.getClass().getName());
		
		DBObject dbObject = MongoDBHelper.bean2DBObj(bean);
		
		DB db = MongoConnectionFactory.getConnection();
		
		DBCollection collection = db.getCollection(collName);
		
		collection.insert(dbObject);
	}
	
	public void update(Object bean, IQuery<BasicDBObject> query) throws Exception {
		
		DBObject updateObject = MongoDBHelper.bean2DBObj(bean);
		
		BasicDBObject objToUpdate = query.getQueryConds();
		
		String collName = CLASS_DBCOLLECTION_MAPPING.getProperty(bean.getClass().getName());
		
		DB db = MongoConnectionFactory.getConnection();
		
		DBCollection collection = db.getCollection(collName);
		
		collection.update(objToUpdate, updateObject);
		
	}
	
	public void findAndRemove(Object bean) throws Exception {
		
		DBObject query = MongoDBHelper.bean2DBObj(bean);
		
		String collName = CLASS_DBCOLLECTION_MAPPING.getProperty(bean.getClass().getName());
		
		DB db = MongoConnectionFactory.getConnection();
		
		DBCollection collection = db.getCollection(collName);
		
		collection.findAndRemove(query);
	}
}
