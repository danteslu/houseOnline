package com.houseOnline.service;

import java.util.List;

import com.houseOnline.common.query.Conditions;
import com.houseOnline.common.query.Page;
import com.houseOnline.common.query.Sorts;
import com.houseOnline.model.HouseDetail;

public interface IHouseService {
	
	String createHouse(HouseDetail houseDetail);
	
	List<HouseDetail> queryHouse(Page page, Sorts sorts, Conditions conditions);
	
	HouseDetail getDetailById(String id);
}
