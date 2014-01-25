package com.houseOnline.common.query.impl;

import java.util.ArrayList;
import java.util.List;

import com.houseOnline.common.query.Conditions;
import com.houseOnline.common.query.IQuery;
import com.houseOnline.common.query.Page;
import com.houseOnline.common.query.Sorts;

/**
 * Contain following info:
 * 1. page
 * 2. sort
 * 3. search criteria
 * 4. return fields
 * @author dlu1
 *
 */
public abstract class AstractQuery<E> implements IQuery<E> {
	
	private String collName;
	
	private Page page;
	
	private Sorts sorts = new Sorts();
	
	private Conditions conditions = new Conditions();
	
	private List<String> fields = new ArrayList<String>();
	
	public AstractQuery() {
		
	}
	
	public AstractQuery(String collName) {
		this.collName = collName;
	}
	
	public AstractQuery(Page page, Sorts sorts, Conditions conditions, List<String> returnFields, String collName) {
		this.page = page;
		this.sorts = sorts;
		this.conditions = conditions;
		this.fields = returnFields;
	}

	@Override
	public E getQueryConds() {
		return null;
	}
	
	@Override
	public E orderBy() {
		return null;
	}
	
	@Override
	public String getFrom() {
		return collName;
	}

	@Override
	public boolean isPage() {
		return page != null;
	}

	@Override
	public int getLimit() {
		return page.getnPerPage();
	}

	@Override
	public int getSkip() {
		return page.getSkip();
	}

	public Page getPage() {
		return page;
	}

	public Sorts getSorts() {
		return sorts;
	}

	public Conditions getConditions() {
		return conditions;
	}
	
	public List<String> getFields() {
		return fields;
	}

	@Override
	public E getReturnFields() {
		return null;
	}
	
	
}
