/*
 * InitServletCntxListener.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.online;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.houseOnline.common.LoggerFactory;
import com.houseOnline.common.MongoConnectionFactory;
import com.houseOnline.crud.Crud;
import com.houseOnline.crud.Model;
import com.houseOnline.crud.annotations.ModelConfig;
import com.houseOnline.crud.xsd.SchemaElement;
import com.houseOnline.crud.xsd.SchemaFactory;
import com.houseOnline.robot.RobotEngine;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * <p>
 * ClassName: InitServletCntxListener
 * </p>
 * 
 * <p>
 * Abstract:
 * </p>
 * <ul>
 * <li></li>
 * </ul>
 * 
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class InitServletCntxListener implements ServletContextListener {

	/**
	 * Perform some tasks when application starts
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		LoggerFactory.getSystemLogger().info(" ****** Project Starts ****** ");
		createDB();
		RobotEngine.getInstance().start();
		moreStart();
		LoggerFactory.getSystemLogger().info(" ****** Project Started ****** ");
	}

	/**
	 * Perform some tasks when application stops
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		LoggerFactory.getSystemLogger().info(" ****** Project Stops ****** ");
		RobotEngine.getInstance().shutdown();
		moreDestroyed();
		LoggerFactory.getSystemLogger().info(" ****** Project Stopped ****** ");
	}

	protected void moreStart() {
	}

	protected void moreDestroyed() {
	}

	private void createDB() {
		try {
			LoggerFactory.getSystemLogger().info("Try to init crud");
			// 创建collection并且创建索引
			// 自动创建的索引目前只支持一级的字段，而且是单键的索引，组合的索引涉及具体的用户需求，不能在xsd中配置，也不自动创建
			for (Field field : Model.class.getFields()) {
				ModelConfig mc = field.getAnnotation(ModelConfig.class);
				if (mc != null) {
					LoggerFactory.getSystemLogger().info(
							"Try to init collection " + field.getName() + "/"
									+ mc.xsd() + "/" + mc.collection() + "/"
									+ mc.indicator());

					String cn = mc.collection();

					DB db = MongoConnectionFactory.getConnection();
					// 如果集合不存在会自动创建
					DBCollection coll = db.getCollection(cn);
					// 当前的集合的索引
					List<DBObject> currentIndexes = coll.getIndexInfo();
					Set<String> currentIndexNames = new HashSet<String>();
					for (DBObject o : currentIndexes) {
						currentIndexNames.add(o.get("name").toString());
					}

					SchemaElement element = SchemaFactory.getInstance()
							.getSchemaElement(field.getName(), mc.xsd());
					Map<String, SchemaElement> indexes = element.getIndexes();
					LoggerFactory.getSystemLogger().info(
							"Found " + indexes.size() + " for this element");
					for (String key : indexes.keySet()) {
						SchemaElement indexElement = indexes.get(key);
						int order = 1;
						String indexName = Crud.ESCAPE
								+ indexElement.getLstName() + "_" + order;
						LoggerFactory.getSystemLogger().info(
								"Try to check index " + indexName);
						if (currentIndexNames.contains(indexName)) {
							LoggerFactory.getSystemLogger().info(
									"The index is created before");
						} else {
							coll.createIndex(new BasicDBObject(Crud.ESCAPE
									+ indexElement.getLstName(), order));
							LoggerFactory.getSystemLogger().info(
									"Create index " + db.getLastError());
						}
					}
				}
			}
		} catch (Exception e) {
			LoggerFactory.getSystemLogger().error(
					"Failed in Crud static block", e);
			throw new RuntimeException(e);
		}
	}
}
