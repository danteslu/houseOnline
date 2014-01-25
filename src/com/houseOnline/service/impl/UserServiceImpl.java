package com.houseOnline.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.houseOnline.common.query.IQuery;
import com.houseOnline.crud.MongoDao;
import com.houseOnline.model.User;
import com.houseOnline.service.IUserService;
import com.mongodb.BasicDBObject;

public class UserServiceImpl implements IUserService {
	
	MongoDao<User> dao = new MongoDao<User>();
	
	private Logger logger;
	
	@Override
	public void createUser(User user) {
		try {
			dao.create(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateUser(User user, IQuery<BasicDBObject> query) {
		try {
			dao.update(user, query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public List<User> queryUser(IQuery<BasicDBObject> query) {
		
		try {
			return dao.find(query, User.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<User>();
	}
}
