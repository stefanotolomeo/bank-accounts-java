package com.company.bankaccounts.dao.manager;

import com.company.bankaccounts.dao.exceptions.InvalidInputException;

import java.util.Map;

public interface IManager<T> {

	T save(T item) throws Exception;

	T update(T item) throws Exception;

	T findById(String id) throws Exception;

	Map<String, T> findAll();

	T delete(String id) throws Exception;
}
