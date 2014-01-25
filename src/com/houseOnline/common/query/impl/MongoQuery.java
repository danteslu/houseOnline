package com.houseOnline.common.query.impl;

import java.util.List;

import com.houseOnline.common.MongoDBHelper;
import com.houseOnline.common.query.Conditions;
import com.houseOnline.common.query.Page;
import com.houseOnline.common.query.Sorts;
import com.mongodb.BasicDBObject;

public class MongoQuery extends AstractQuery<BasicDBObject> {
	
	public MongoQuery() {
		
	}
	
	public MongoQuery(String collName) {
		super(collName);
	}
	
	public MongoQuery(Page page, Sorts sorts, Conditions conditions, List<String> returnFields, String collName) {
		super(page, sorts, conditions, returnFields, collName);
	}

	@Override
	public BasicDBObject getQueryConds() {
		return MongoDBHelper.getQueryConds(getConditions());
	}

	@Override
	public BasicDBObject orderBy() {
		return MongoDBHelper.getSortConds(getSorts());
	}

	@Override
	public BasicDBObject getReturnFields() {
		return MongoDBHelper.getReturnFields(getFields());
	}
	
	
}
