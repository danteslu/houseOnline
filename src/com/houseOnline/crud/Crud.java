/*
 * Crud.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.crud;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.fengweimin.action.AbstractAction;
import com.fengweimin.common.CastorHelper;
import com.fengweimin.common.IDGenerator;
import com.fengweimin.common.MongoConnectionFactory;
import com.fengweimin.crud.xsd.SchemaElement;
import com.fengweimin.crud.xsd.SchemaFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.opensymphony.xwork2.Action;

/**
 * <p>
 * ClassName: Crud
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
 * @since Dec 22, 2013
 */
public class Crud extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	public final static String ESCAPE = "_";
	public final static String MONGO_FIELD_ID = "_id";
	public final static String MONGO_FIELD_DOCUMENT = "_document";
	
	private Model model;
	private SchemaElement schema;

	private BasicDBObject query;
	
	private int limit = 50;
	
	private List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
	
	public Crud() {}
	
	public Crud(Logger logger) {
		this.logger = logger;
	}

	/**
	 * 1. Convert the object to xml and saved in _document field
	 * 2. update the related index field in corresponding table
	 * 
	 * @return
	 */
	public String create() {
		try {
			String name = model.getName();

			// The model object to create
			Object modelObject = Ognl.getValue("model." + name, this);
			String cn = model.currentModelCongfig().collection();

			String id = model.getId();
			if (StringUtils.isBlank(id)) {
				id = IDGenerator.next();
				model.setId(id);
			}
			
			logger.info("Try to create model " + name + " with id " + id);

			BasicDBObject dbObj = buildDBObj(name, modelObject);
			
			DB db = MongoConnectionFactory.getConnection();
			
			DBCollection coll = db.getCollection(cn);
			
			coll.insert(dbObj);
			
			CommandResult result = db.getLastError();
			
			logger.info("Insert result is " + result);
			
			if (result.ok()) {
				SetSuccessResult("Success create object(" + name + ")。");
			} else {
				SetFailResult("Failed to create object: " + name);
			}
		} catch (Exception e) {
			logger.error("Exception occured when create object", e);
			SetFailResult();
		}
		
		return SUCCESS;
	}

	private BasicDBObject buildDBObj(String name, Object modelObject)
			throws Exception, OgnlException {
		String document = CastorHelper.toString(modelObject);
		BasicDBObject doc = new BasicDBObject(MONGO_FIELD_DOCUMENT,
				document).append(MONGO_FIELD_ID, model.getId());
		SchemaElement element = SchemaFactory.getInstance()
				.getSchemaElement(name, model.currentModelCongfig().xsd());
		Map<String, SchemaElement> indexes = element.getIndexes();
		
		for (String key : indexes.keySet()) {
			SchemaElement indexElement = indexes.get(key);
			Object value = Ognl.getValue("model." + name + "."
					+ indexElement.getLstName(), this);
			logger.info("Try to set index " + indexElement.getLstName()
					+ " to " + value);
			doc.append(ESCAPE + indexElement.getLstName(), value);
		}
		
		return doc;
	}

	/**
	 * @return the _document field
	 */
	public String read() {
		try {
			String name = model.getName();
			String id = model.getId();

			DB db = MongoConnectionFactory.getConnection();
			String cn = model.currentModelCongfig().collection();
			DBCollection coll = db.getCollection(cn);

			BasicDBObject queryById = new BasicDBObject(MONGO_FIELD_ID, id);
			DBObject object = coll.findOne(queryById);
			if (object == null) {
				SetFailResult("无法找到您要读取的对象(" + id + ")。");
				return Action.SUCCESS;
			}

			String document = (String) object.get(MONGO_FIELD_DOCUMENT);

			StringReader sr = null;
			try {
				sr = new StringReader(document);
				Object data = CastorHelper.getUnMarshaller().unmarshal(
						model.currentModelClass(), sr);
				Ognl.setValue("model." + name, this, data);
			} finally {
				if (sr != null) {
					sr.close();
					sr = null;
				}
			}
		} catch (Exception e) {
			logger.error("Failed in read", e);
			SetFailResult();
		}
		
		return SUCCESS;
	}

	/**
	 * 根据ID完整更新某个model
	 */
	public String update() {
		try {
			String name = model.getName();
			String id = model.getId();

			Object modelObject = Ognl.getValue("model." + name, this);

			DB db = MongoConnectionFactory.getConnection();
			String cn = model.currentModelCongfig().collection();
			DBCollection coll = db.getCollection(cn);

			BasicDBObject queryById = new BasicDBObject(MONGO_FIELD_ID, id);

			String document = CastorHelper.toString(modelObject);
			BasicDBObject doc = new BasicDBObject(MONGO_FIELD_DOCUMENT,
					document);
			SchemaElement element = SchemaFactory.getInstance()
					.getSchemaElement(name, model.currentModelCongfig().xsd());
			Map<String, SchemaElement> indexes = element.getIndexes();
			for (String key : indexes.keySet()) {
				SchemaElement indexElement = indexes.get(key);
				Object value = Ognl.getValue("model." + name + "."
						+ indexElement.getLstName(), this);
				logger.info("Try to set index " + indexElement.getLstName()
						+ " to " + value);
				doc.append(ESCAPE + indexElement.getLstName(), value);
			}

			coll.update(queryById, doc);
			CommandResult result = db.getLastError();
			logger.info("Update result is " + result);
			if (result.ok()) {
				SetSuccessResult("成功更新了对象(" + name + ")。");
			} else {
				SetFailResult("更新时发生错误，请重试或联系管理员。");
			}

		} catch (Exception e) {
			logger.error("Failed in update", e);
			SetFailResult();
		}
		return SUCCESS;
	}

	/**
	 * 根据ID删除某个model
	 */
	public String delete() {
		try {
			String name = model.getName();
			String id = model.getId();

			DB db = MongoConnectionFactory.getConnection();
			String cn = model.currentModelCongfig().collection();
			DBCollection coll = db.getCollection(cn);

			BasicDBObject queryById = new BasicDBObject(MONGO_FIELD_ID, id);
			coll.remove(queryById);
			CommandResult result = db.getLastError();
			logger.info("Delete result is " + result);
			if (result.ok()) {
				SetSuccessResult("成功删除了对象(" + name + ")。");
			} else {
				SetFailResult("删除时发生错误，请重试或联系管理员。");
			}

		} catch (Exception e) {
			logger.error("Failed in delete", e);
			SetFailResult();
		}
		return SUCCESS;
	}

	/**
	 * 根据条件查询 1）所有查询条件必须有索引，否则找不到相应的字段；当前设计是有索引的字段才会抽取出来作为单独字段。
	 * 2）组合索引不会自动生成，需要人工根据实际情况创建 3）TODO: 支持排序条件
	 * 
	 * 条件的定义参考
	 * http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java
	 * -driver/#getting-started-with-java-driver
	 * http://docs.mongodb.org/manual/reference/operator/query/
	 * 
	 * @return
	 */
	public String query() {
		try {
			String name = model.getName();

			Set<String> set = new HashSet<String>();
			SchemaElement element = SchemaFactory.getInstance()
					.getSchemaElement(name, model.currentModelCongfig().xsd());
			Map<String, SchemaElement> indexes = element.getIndexes();
			for (String key : indexes.keySet()) {
				set.add(indexes.get(key).getLstName());
			}

			BasicDBObject realQuery = null;
			if (query != null) {
				logger.info("Try to query by " + query);
				for (String field : query.keySet()) {
					// Check if the field is in index
					if (!set.contains(field)) {
						logger.info("Field " + field + " is not defined");
						SetFailResult("该查询使用了没有被索引的键。");
						return Action.SUCCESS;
					}
				}
				// Escapte the fileds
				realQuery = new BasicDBObject();
				for (String field : query.keySet()) {
					realQuery.append(ESCAPE + field, query.get(field));
				}
			}

			DB db = MongoConnectionFactory.getConnection();
			String cn = model.currentModelCongfig().collection();
			DBCollection coll = db.getCollection(cn);
			DBCursor cursor = coll.find(realQuery);

			int count = 0;
			StringReader sr = null;
			try {
				while (cursor.hasNext() && count++ < limit) {
					DBObject object = cursor.next();
					String id;
					if (object.get(MONGO_FIELD_ID) instanceof ObjectId) {
						id = ((ObjectId) object.get(MONGO_FIELD_ID)).toString();
					} else {
						id = (String) object.get(MONGO_FIELD_ID);
					}

					String document = (String) object.get(MONGO_FIELD_DOCUMENT);
					sr = new StringReader(document);
					Object data = CastorHelper.getUnMarshaller().unmarshal(
							model.currentModelClass(), sr);

					Map<String, Object> map = new HashMap<String, Object>();
					map.put(id, data);

					queryResult.add(map);
				}
			} finally {
				cursor.close();
				if (sr != null) {
					sr.close();
					sr = null;
				}
			}

			logger.info("Found " + queryResult.size() + " records successfully");
			SetSuccessResult("成功处理了您的查询。");

		} catch (Exception e) {
			logger.error("Failed in query", e);
			SetFailResult();
		}
		return SUCCESS;
	}
	
	/*
	 * Get & Set
	 */
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public SchemaElement getSchema() {
		return schema;
	}

	public void setSchema(SchemaElement schema) {
		this.schema = schema;
	}

	public BasicDBObject getQuery() {
		return query;
	}

	public void setQuery(BasicDBObject query) {
		this.query = query;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<Map<String, Object>> getQueryResult() {
		return queryResult;
	}

	public void setQueryResult(List<Map<String, Object>> queryResult) {
		this.queryResult = queryResult;
	}

}
