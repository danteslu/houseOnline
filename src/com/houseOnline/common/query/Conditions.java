package com.houseOnline.common.query;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Conditions {
	
	private Collection<Condition> queryConds = new LinkedHashSet<Condition>();
	
	public class Condition {
		private String field;
		private CriteriaOperator operator;
		private Object value;
		
		public Condition (String field, CriteriaOperator operator, Object value) {
			this.field = field;
			this.operator = operator;
			this.value = value;
		}

		public String getField() {
			return field;
		}
		
		public Object getValue() {
			return value;
		}
		
		public CriteriaOperator getOperator() {
			return operator;
		}

		public boolean isComplex() {
			return (value instanceof Condition);
		}
	}
	
	public void addCondition(String field, CriteriaOperator operator, Object value) {
		queryConds.add(new Condition(field, operator, value));
	}
	
	public Collection<Condition> getConditions() {
		return queryConds;
	}
}