package com.houseOnline.service;

import java.util.List;

import com.houseOnline.common.query.IQuery;
import com.houseOnline.model.User;
import com.mongodb.BasicDBObject;

public interface IUserService {
	
	void createUser(User user);
	
	void updateUser(User user, IQuery<BasicDBObject> query);
	
	List<User> queryUser(IQuery<BasicDBObject> query);
}
