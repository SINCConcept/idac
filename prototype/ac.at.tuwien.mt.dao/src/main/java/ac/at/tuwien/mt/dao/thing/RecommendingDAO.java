/*
 * Copyright (c) 2016. All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS".
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
 * 
 * Author: Florin Bogdan Balint
 * 
 */
package ac.at.tuwien.mt.dao.thing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.at.tuwien.mt.model.thing.Thing;

/**
 * @author Florin Bogdan Balint
 *
 */
public interface RecommendingDAO {

	/**
	 * Recommend a Thing for the user with the specified userId. <br/>
	 * <br/>
	 * The recommendation is computed as follows:<br/>
	 * <ul>
	 * <li>In case there are no ratings at all: random items are proposed.</li>
	 * <li>In case there are ratings for things, but the user has no
	 * neighborhood, the top sold item is recommended.</li>
	 * <li>In case there are ratings for things and the user also has a
	 * neighborhood: the item with the highest computed rating is proposed.</li>
	 * </ul>
	 * 
	 * @param userId
	 * @return Thing
	 */
	public Thing recommend(String userId);

	/**
	 * Recommends a Thing based on a specific tag. <br/>
	 * <br/>
	 * The recommendation is computed as follows:<br/>
	 * <ul>
	 * <li>In case there are no ratings at all: random items are proposed.</li>
	 * <li>In case there are ratings for things: the highest computed rating is
	 * proposed.</li>
	 * </ul>
	 * 
	 * @param tag
	 * @return Thing
	 */
	public Thing recommendForTag(String tag);

	/**
	 * A map with the euclidean distance to the neighbors, with which the user
	 * has the most common rated items.
	 * 
	 * @param userId
	 * @return Map<String, Double>
	 */
	public Map<String, Double> getEuclideanNeighborhood(String userId);

	/**
	 * Computes the neighborhood of a user.
	 * 
	 * @param userId
	 * @return List<String>
	 */
	public Set<String> getNeighborhood(String userId);

	/**
	 * Get all things which were rated by this user.
	 * 
	 * @param userId
	 * @return
	 */
	public List<String> getAllUserRatedThings(String userId);

	/**
	 * Returns a list containing all thingIDs, which both users have commonly
	 * rated.
	 * 
	 * @param userId
	 * @param neighborId
	 * @return List<String>
	 */
	public List<String> getAllCommonRatedThings(String userId, String neighborId);

	/**
	 * Returns a list containing all thingIDs, which the neighbor rated, but the
	 * user didn't.
	 * 
	 * @param userId
	 * @param neighborId
	 * @return
	 */
	public List<String> getAllNeighborButNotUserRatedThings(String userId, String neighborId);

	/**
	 * Returns the top rated item. If no ratings can be found, NULL is returned.
	 * The owner of the top rated thing is not allowed to have the provided
	 * userId.
	 * 
	 * @param userId
	 * @return Thing
	 */
	public Thing getTopRatedThing(String userId);

	/**
	 * Returns the top rated item based on the specified tag.
	 * 
	 * @param tag
	 * @return Thing
	 */
	public Thing getTopRatedThingForTag(String tag);

	/**
	 * Returns a random item. If no items can be found, NULL is returned. The
	 * owner of the random thing is not allowed to have the provided userId.
	 * 
	 * @param userId
	 * @return Thing
	 */
	public Thing getRandomThing(String userId);

	/**
	 * Returns a random item based on a tag.
	 * 
	 * @param tag
	 * @return Thing
	 */
	public Thing getRandomThingForTag(String tag);

}
