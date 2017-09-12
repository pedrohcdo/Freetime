package com.createlier.freetime.localdb;

import java.util.List;

/**
 * Dao Structure
 * 
 * @author Pedro Henrique
 *
 */
public interface Dao<Type> {
	
	/**
	 * List All
	 *  
	 * @return
	 */
	public List<Type> listAll();
	
	/**
	 * Get Item
	 *
	 * @return
	 */
	public Type get(final int roll);
	
	/**
	 * Insert Item
	 *
	 */
	public long insert(final Type item);

	/**
	 * Delete Item
	 * 
	 * @param item
	 */
	public void delete(final Type item);

	/**
	 * Update Item
	 * 
	 * @param item
	 */
	public void update(final Type item);

	
	/**
	 * Delete all
	 */
	public void clearAll();
}
