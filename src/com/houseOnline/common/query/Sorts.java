package com.houseOnline.common.query;

import java.util.ArrayList;
import java.util.Collection;

public class Sorts {
	
	public enum Order {
		DESC(-1),
		ASC(1);
		
		private int value;
		private Order(int value) {
			this.value = value;
		}
		
		private static Order valueOf(boolean bVal) {
			if (bVal) {
				return Order.ASC;
			}
			
			return Order.DESC;
		}
	}
	
	public class SortCond {
		private String field;
		private Order order;
		
		private SortCond(String field, boolean bVal) {
			this.field = field;
			this.order = Order.valueOf(bVal);
		}

		public String getField() {
			return field;
		}

		public int getOrder() {
			return order.value;
		}
	}
	
	private Collection<SortCond> sortConds = new ArrayList<SortCond>();
	
	public void addCond(String field, boolean bVal) {
		SortCond sortCond = new SortCond(field, bVal);
		sortConds.add(sortCond);
	}
	
	public Collection<SortCond> getSortConds() {
		return sortConds;
	}
}
