package com.houseOnline.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.houseOnline.common.query.Conditions;
import com.houseOnline.common.query.IQuery;
import com.houseOnline.common.query.Page;
import com.houseOnline.common.query.Sorts;
import com.houseOnline.common.query.impl.MongoQuery;
import com.houseOnline.crud.MongoDao;
import com.houseOnline.model.HouseDetail;
import com.houseOnline.service.IHouseService;
import com.mongodb.BasicDBObject;

public class HouseServiceImpl implements IHouseService {
	
	private final static String COLL_HOUSE_DETAIL = "houstDetail";
	
	private final static String HOUSE_DETAIL_ID = "houseId";
	
	private MongoDao<HouseDetail> dao = new MongoDao<HouseDetail>();

	@Override
	public String createHouse(HouseDetail houseDetail) {
		
		try {
			
			//generate house id
			
			dao.create(houseDetail);
			
			return "success";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "error";

	}

	@Override
	public List<HouseDetail> queryHouse(Page page, Sorts sorts, Conditions conditions) {
		
		IQuery<BasicDBObject> query = new MongoQuery(page, sorts, conditions, null, COLL_HOUSE_DETAIL);
		
		try {
			return dao.find(query, HouseDetail.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<HouseDetail>();
	}

	@Override
	public HouseDetail getDetailById(String id) {
		
		try {
			return dao.getById(HOUSE_DETAIL_ID, id, HouseDetail.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
