package com.houseOnline.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

import com.houseOnline.common.query.Conditions;
import com.houseOnline.common.query.Page;
import com.houseOnline.common.query.Sorts;
import com.houseOnline.model.HouseDetail;
import com.houseOnline.service.IHouseService;
import com.houseOnline.service.impl.HouseServiceImpl;

public class HouseAction extends AbstractAction {
	
	private IHouseService houseService = new HouseServiceImpl();
	
	private HouseDetail houseDetail;
	
	private Conditions conditions;
	
	private Sorts sorts;
	
	private Page page;
	
	public HouseDetail getHouseDetails() {
		return null;
	}
	
	@Action(value="/houseDetail/", 
			results={
				@Result(name ="success", 
				type = "freemarker", 
				location = "/views/houseDetail.htm")})
	public String getHouseDetailById() {
		 houseDetail = houseService.getDetailById(houseDetail.getHouseId());
		 return SUCCESS;
	}
	
	@Action(value="/createHouse/", 
			results={
				@Result(name ="success", 
				type = "freemarker", 
				location = "/views/demo.htm")})
	public String createHouse() {
		houseService.createHouse(houseDetail);
		return SUCCESS;
	}
}
