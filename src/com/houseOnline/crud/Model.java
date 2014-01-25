/*
 * Model.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.crud;

import java.lang.reflect.Field;

import com.fengweimin.common.LoggerFactory;
import com.fengweimin.crud.annotations.ModelConfig;
import com.fengweimin.model.User;

/**
 * <p>
 * ClassName: Model
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
public class Model {

	public static final String MODEL_USER = "user";
	
	private String id;
	private String name;

	@ModelConfig(xsd = "/schemas/bo.xsd", indicator = "u", collection = "users")
	public User user;

	public Model() {}
	
	public Model(String name) {
		super();
		this.name = name;
	}

	/*
	 * Methods
	 */
	/**
	 * 指定了当前model的名字后，查找该model的定义
	 * 
	 * @return
	 */
	public ModelConfig currentModelCongfig() {
		try {
			Field field = Model.class.getField(name);
			ModelConfig mc = field.getAnnotation(ModelConfig.class);
			return mc;
		} catch (Exception e) {
			LoggerFactory.getSystemLogger().error(
					"Failed in currentModelCongfig", e);
			return null;
		}
	}

	public Class currentModelClass() {
		try {
			Field field = Model.class.getField(name);
			return field.getType();
		} catch (Exception e) {
			LoggerFactory.getSystemLogger().error(
					"Failed in currentModelCongfig", e);
			return null;
		}
	}

	/*
	 * Get & Set
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
