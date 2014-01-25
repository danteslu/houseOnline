package test.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.houseOnline.common.JAXBHelper;
import com.houseOnline.common.query.IQuery;
import com.houseOnline.common.query.impl.MongoQuery;
import com.houseOnline.crud.MongoDao;
import com.houseOnline.model.Configuration;
import com.houseOnline.model.Facility;
import com.houseOnline.model.HouseDetail;
import com.houseOnline.model.Other;
import com.houseOnline.model.Service;
import com.houseOnline.service.IHouseService;
import com.houseOnline.service.impl.HouseServiceImpl;
import com.mongodb.BasicDBObject;

public class TestHouseDetail{

	MongoDao<HouseDetail> dao = new MongoDao<HouseDetail>();
	
	IHouseService houseService = new HouseServiceImpl();
	
	private HouseDetail houseDetail;
	
//	@Test
//	public void testCreate() throws Exception {
//		dao.create(houseDetail);
//	}
//	
	@Test
	public void testGetById() throws Exception {
		HouseDetail houseDetail = dao.getById("houseId", "52e3634f6e4720d26ccfa4b3", HouseDetail.class.getName());
		JAXBHelper.toString(houseDetail);
	}
	
//	@Test
//	public void testRemove() throws Exception {
//		
//		HouseDetail detail = new HouseDetail();
//		
//		detail.setHouseId("52e3442f6e476a65a95ad076");
//		
//		dao.findAndRemove(detail);
//	}
	
//	@Test
//	public void testQuery() throws Exception {
//		
//		IQuery<BasicDBObject> query = new MongoQuery("houseDetail");
//		
//		List<HouseDetail> houseDetails = dao.find(query, HouseDetail.class.getName());
//		
//		for (HouseDetail houseDetail : houseDetails) {
//			JAXBHelper.toString(houseDetail);
//		}
//	}
//	
//	@Test
//	public void testRemove() {
//		fail("Not yet implemented");
//	}

	@Before
	public void setUp() {
		
		houseDetail = new HouseDetail();
		
		houseDetail.setHouseId(new ObjectId().toString());
		
		houseDetail.setSubject("远洋公馆精品单卧园景套房");
		
		houseDetail.setAddress("三亚市榆亚大道508号远洋奥林匹克公馆小区");
		
		houseDetail.setVestAddr("前厅地址");
		
		houseDetail.setQuantity(2);
		
		houseDetail.setHouseType("平层");
		
		houseDetail.setRoomType("1室1厅1卫");
		
		houseDetail.setSize(70);
		
		houseDetail.setBedNum(2);
		
		houseDetail.setPreferredPerson(2);
		
		houseDetail.getPhotoPaths().add("/sanya_gongyu/dadonghai_361.png");
		houseDetail.getPhotoPaths().add("/sanya_gongyu/dadonghai_362.png");
		
		houseDetail.setHouseDesc("经典地中海 您的尊贵私人空间");
		
		Facility tv = new Facility();
		tv.setName("电视机");
		tv.setValue(Boolean.FALSE);
		
		Facility fridge = new Facility();
		fridge.setName("电冰箱");
		fridge.setValue(Boolean.TRUE);
		
		Facility towels = new Facility();
		towels.setName("牙具");
		towels.setValue(Boolean.TRUE);
		
		List<Facility> facilities = houseDetail.getFacilities();
		facilities.add(tv);
		facilities.add(fridge);
		facilities.add(towels);
		
		Configuration wifi = new Configuration();
		wifi.setName("WIFI");
		wifi.setValue(true);
		
		Configuration broadband = new Configuration();
		broadband.setName("宽带");
		broadband.setValue(true);
		
		List<Configuration> configurations = houseDetail.getConfigurations();
		configurations.add(broadband);
		
		Service extraBed = new Service();
		extraBed.setName("加床");
		extraBed.setValue(true);
		
		Service ticketService = new Service();
		ticketService.setName("票据服务");
		ticketService.setValue(true);
		
		List<Service> services = houseDetail.getServices();		
		services.add(extraBed);
		services.add(ticketService);
		
		Other smoke = new Other();
		smoke.setName("吸烟");
		smoke.setValue(true);
		
		List<Other> others = houseDetail.getOthers();
		others.add(smoke);
	}
}
