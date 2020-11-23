package com.company.bankaccounts.dao.manager;

import java.util.Map;

public interface IManager<T> {

	String save(T item) throws Exception;

	T update(T item) throws Exception;

	T findById(String id);

	Map<String, T> findAll();

	T delete(String id) throws Exception;
}
