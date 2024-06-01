package com.cadre.server.core.persistence.query;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.cadre.server.core.entity.POModel;
import com.cadre.server.core.persistence.exception.DBNoResultException;
import com.cadre.server.core.persistence.exception.DBNonUniqueResultException;

public class SearchResult<T extends POModel> {

	private List<T> resultList;

	public SearchResult(List<T> list) {
		this.resultList = list;
		
	}

	public List<T> getResultList() {
		return this.getResultList(true);
	}

	public boolean isNotEmpty() {
		return CollectionUtils.isNotEmpty(resultList);
	}
	
	public List<T> getResultList(boolean throwException){
		if (resultList!=null && resultList.size() > 0) {
			return resultList;			
		}else {
			if (throwException) {
				throw new DBNoResultException();				
			}else {
				return Collections.emptyList();
			}
		}
	}

	public T getSingleResult() {
		return this.getSingleResult(true);
	}
	
	public T getSingleResult(boolean throwException) {
		if (resultList!=null && resultList.size() > 0) {
			if (resultList.size() > 1 && throwException) {
				throw new DBNonUniqueResultException();
			}else {
				return resultList.get(0);
			}
		}else {
			if (throwException) {
				throw new DBNoResultException();				
			}else {
				return null;
			}
		}
	}

}
