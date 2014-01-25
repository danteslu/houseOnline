package com.houseOnline.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.Ognl;

import org.apache.commons.lang.StringUtils;

import com.houseOnline.common.query.Conditions;
import com.houseOnline.common.query.Conditions.Condition;
import com.houseOnline.common.query.CriteriaOperator;
import com.houseOnline.common.query.Sorts;
import com.houseOnline.common.query.Sorts.SortCond;
import com.houseOnline.common.xml.XSDNode;
import com.houseOnline.common.xml.XSDReader;
import com.houseOnline.model.Geometry;
import com.houseOnline.model.User;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

public class MongoDBHelper {

	private static Map<CriteriaOperator, String> operatorMap;

	static {
		operatorMap = new HashMap<CriteriaOperator, String>();
		operatorMap.put(CriteriaOperator.LT, QueryOperators.LT);
		operatorMap.put(CriteriaOperator.GT, QueryOperators.GT);

		operatorMap.put(CriteriaOperator.IN, QueryOperators.IN);
		operatorMap.put(CriteriaOperator.AND, QueryOperators.AND);
		operatorMap.put(CriteriaOperator.OR, QueryOperators.OR);
	}

	public static BasicDBObject getQueryConds(Conditions conditions) {

		BasicDBObject queryObject = new BasicDBObject();

		for (Condition condition : conditions.getConditions()) {

			String operator = operatorMap.get(condition.getOperator());

			Object value = getQueryCond(condition);

			if (StringUtils.isEmpty(operator)) {
				queryObject.put(condition.getField(), value);
			} else {
				queryObject.put(operator, value);
			}
		}

		return queryObject;
	}

	private static Object getQueryCond(Condition condition) {

		if (condition == null) {
			return null;
		}

		if (condition.isComplex()) {
			return new BasicDBObject(condition.getField(),
					getQueryCond((Condition) condition.getValue()));
		}

		return condition.getValue();
	}

	public static BasicDBObject getSortConds(Sorts sorts) {

		BasicDBObject sortObj = new BasicDBObject();

		Collection<SortCond> sortConds = sorts.getSortConds();

		for (SortCond sortCond : sortConds) {
			sortObj.append(sortCond.getField(), sortCond.getOrder());
		}

		return sortObj;
	}

	public static BasicDBObject getReturnFields(List<String> fields) {

		BasicDBObject fieldsObj = new BasicDBObject();

		fieldsObj.append("_id", 0);
		
		for (String field : fields) {
			fieldsObj.append(field, 1);
		}

		return fieldsObj;
	}

	/**
	 * 
	 * @param className
	 * @return
	 * @throws Exception
	 */
	private static Object getBeanInstance(String className) throws Exception {
		
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		
		return clazz.newInstance();
	}

	public static BasicDBObject bean2DBObj(Object bean) throws Exception {

		XSDNode xsdNode = XSDReader.getXSDForClass(bean.getClass().getName());

		return bean2DBObjWithXSDNode(bean, xsdNode);
	}

	private static BasicDBObject bean2DBObjWithXSDNode(Object bean,
			XSDNode xsdNode) throws Exception {

		BasicDBObject dbObject = new BasicDBObject();

		List<XSDNode> nodes = xsdNode.getNodes();

		for (XSDNode subNode : nodes) {

			String nodeName = subNode.getName();

			Object valueObj = Ognl.getValue(nodeName, bean);

			if (valueObj == null) {
				continue;
			}

			Object dbVal = null;

			if (subNode.isLeaf()) {
				dbVal = convertLeaf(valueObj, subNode.getIsUnbounded());
			} else {
				dbVal = convertComplex(valueObj, subNode);
			}

			if (dbVal != null) {
				dbObject.append(nodeName, dbVal);
			}
		}

		return dbObject;
	}

	public static Object dbObj2Bean(DBObject dbObject, String className)
			throws Exception {

		XSDNode xsdNode = XSDReader.getXSDForClass(className);

		return dbObj2BeanByXSDNode(dbObject, xsdNode);
	}

	private static Object dbObj2BeanByXSDNode(DBObject dbObject, XSDNode xsdNode)
			throws Exception {

		String className = xsdNode.getClassName();

		Object bean = getBeanInstance(className);

		Set<String> keys = dbObject.keySet();

		for (String key : keys) {

			Object value = dbObject.get(key);

			if (isBasicDBObject(value)) {

				Object subBean = dbObj2BeanByXSDNode((BasicDBObject) value,
						xsdNode.getSubNode(key));

				Ognl.setValue(key, bean, subBean);

			} else if (isBasicDBList(value)) {
				/*
				 * JAXB would not generate setter method for collection or array
				 */
				List<Object> beanList = (List<Object>) Ognl.getValue(key, bean);

				beanList.addAll(dbList2Bean((BasicDBList) value,
						xsdNode.getSubNode(key)));

			} else {
				
				Ognl.setValue(key, bean, value);

			}
		}

		return bean;
	}

	private static List<Object> dbList2Bean(BasicDBList basicDBList,
			XSDNode xsdNode) throws Exception {

		List<Object> beans = new ArrayList<Object>();

		for (Object dbObject : basicDBList) {
			if (isBasicDBObject(dbObject)) {
				beans.add(dbObj2BeanByXSDNode((BasicDBObject) dbObject, xsdNode));
			} else {
				beans.add(dbObject);
			}
		}

		return beans;
	}

	private static boolean isBasicDBObject(Object obj) {
		return obj != null && (obj instanceof BasicDBObject);
	}

	private static boolean isBasicDBList(Object obj) {
		return obj != null && (obj instanceof BasicDBList);
	}

	private static Object convertComplex(Object valueObj, XSDNode subNode)
			throws Exception {
		if (subNode.getIsUnbounded()) {
			BasicDBList dbList = new BasicDBList();
			Collection<? extends Object> coll = (Collection<? extends Object>) valueObj;
			
			if (coll.isEmpty()) {
				return null;
			}
			
			for (Object object : coll) {
				dbList.add(bean2DBObjWithXSDNode(object, subNode));
			}
			return dbList;
		} else {
			return bean2DBObjWithXSDNode(valueObj, subNode);
		}
	}

	private static Object convertLeaf(Object valueObj, boolean isUnbouded) {
		
		if (valueObj == null) {
			return null;
		}
		
		if (isUnbouded) {
			
			Collection<?> coll = (Collection<? extends Object>) valueObj;
			
			if (coll.isEmpty()) {
				return null;
			}
			
			BasicDBList dbList = new BasicDBList();
			dbList.addAll(coll);
			return dbList;
		} else {
			return valueObj;
		}
	}

	public static void main(String[] args) throws Exception {
		User user = new User();
		user.setEmail("100mouse@163.com");
		user.setName("dantes lu");
		user.setRegTime(new Date());

		Geometry geometry = new Geometry();
		
		geometry.setName("dlu1");
		
		user.getGeometry().add(geometry);
		
		DBObject dbObject = bean2DBObj(user);
		
		System.out.println(dbObject.toString());

		User bean = (User) dbObj2Bean(dbObject, User.class.getName());
		
		JAXBHelper.toString(bean);
	}
}
