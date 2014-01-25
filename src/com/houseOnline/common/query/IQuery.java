package com.houseOnline.common.query;


/**
 * 
 * @author dlu1
 *
 */
public interface IQuery<E> {
	
	public E getQueryConds();
	
	public E orderBy();
	
	public E getReturnFields();
	
	public String getFrom();
	
	public boolean isPage();
	
	public int getLimit();
	
	public int getSkip();
}
