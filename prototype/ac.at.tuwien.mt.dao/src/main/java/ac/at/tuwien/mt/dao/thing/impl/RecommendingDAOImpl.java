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
package ac.at.tuwien.mt.dao.thing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import ac.at.tuwien.mt.dao.thing.RecommendingDAO;
import ac.at.tuwien.mt.dao.thing.ThingDAO;
import ac.at.tuwien.mt.model.thing.Thing;
import ac.at.tuwien.mt.model.thing.rating.Rating;

/**
 * @author Florin Bogdan Balint
 *
 */
public class RecommendingDAOImpl implements RecommendingDAO {

	private static final Logger LOGGER = LogManager.getLogger(ThingDAOImpl.class);

	private MongoClient mongoClient;
	private String database;
	private String thingCollection;
	private ThingDAO thingDAO;

	public RecommendingDAOImpl(MongoClient mongoClient, String database, String thingCollection) {
		this.mongoClient = mongoClient;
		this.database = database;
		this.thingCollection = thingCollection;
		thingDAO = new ThingDAOImpl(mongoClient, database, thingCollection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.thing.RecommendingDAO#recommend(java.lang.String)
	 */
	@Override
	public Thing recommend(String userId) {
		LOGGER.debug("Recommending Thing for user: " + userId);

		// see if there are any things in the database
		Map<String, Double> map = getEuclideanNeighborhood(userId);
		if (map.isEmpty() == false) {
			// sort map by value
			Map<String, Double> result = new LinkedHashMap<>();
			Stream<Map.Entry<String, Double>> st = map.entrySet().stream();

			st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

			// try to find a thing which one neighbour bought and the user did
			// not
			// buy
			Set<Entry<String, Double>> entrySet = result.entrySet();
			for (Entry<String, Double> entry : entrySet) {
				String neighborId = entry.getKey();
				List<String> allUnratedThings = getAllNeighborButNotUserRatedThings(userId, neighborId);
				if (!allUnratedThings.isEmpty()) {
					String thingId = allUnratedThings.get(0);
					Thing thing = thingDAO.getThing(thingId);
					return thing;
				}
			}
		}

		Thing thing = getTopRatedThing(userId);
		if (thing != null) {
			return thing;
		}

		thing = getRandomThing(userId);
		if (thing != null) {
			return thing;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.thing.RecommendingDAO#getNeighborhood(java.lang.
	 * String)
	 */
	@Override
	public Set<String> getNeighborhood(String userId) {
		List<String> allUserRatings = getAllUserRatedThings(userId);
		List<String> possibleUsers = new ArrayList<String>();
		for (String thingId : allUserRatings) {
			// add all users which rated this thing to the possibleUsers
			// but filter the above userId out (we don't want to add ourselves
			// to the neighborhood
			thingDAO.getThing(thingId).getRatings().stream().filter(r -> !r.getUserId().equals(userId)).forEach(r -> possibleUsers.add(r.getUserId()));
		}
		// now group them by the common items we rated
		Map<String, Long> counts = possibleUsers.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));

		// now save them into this map:
		// <k,v> = <how many common items do we have, who are these users>
		Map<Long, Set<String>> map = new HashMap<>();

		Long max = null;
		Set<Entry<String, Long>> entrySet = counts.entrySet();
		for (Entry<String, Long> entry : entrySet) {
			String key = entry.getKey();
			Long value = entry.getValue();
			Set<String> set = map.get(value);
			if (set == null) {
				map.put(value, new HashSet<String>());
			}
			map.get(value).add(key);

			// set max
			if (max == null) {
				max = value;
			}
			if (value > max) {
				max = value;
			}
		}

		// return the users with which I have the maximum nr of common items,
		// the whole neighborhood.

		return map.get(max);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.thing.RecommendingDAO#getAllUserRatedThings(java.
	 * lang. String)
	 */
	@Override
	public List<String> getAllUserRatedThings(String userId) {
		List<String> allUserRatings = new ArrayList<String>();
		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);
		FindIterable<Document> results = collection.find(Filters.elemMatch("ratings", Filters.eq("userId", userId)));
		for (Document document : results) {
			Thing thing = new Thing(document);
			allUserRatings.add(thing.getThingId());
		}
		return allUserRatings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.thing.RecommendingDAO#getEuclideanNeighborhood(java.
	 * lang. String)
	 */
	@Override
	public Map<String, Double> getEuclideanNeighborhood(String userId) {

		Set<String> neighborhood = getNeighborhood(userId);

		Map<String, Double> neighborDistanceMap = new HashMap<String, Double>();

		if (neighborhood == null) {
			return neighborDistanceMap;
		}

		// for each neighbor
		for (String neighborId : neighborhood) {
			// get all common rated things with this neighbor
			List<String> commonRatedThings = getAllCommonRatedThings(userId, neighborId);

			double internal = 0;

			for (String commonRatedThing : commonRatedThings) {
				List<Rating> ratings = thingDAO.getThing(commonRatedThing).getRatings();
				Rating userRating = null;
				Rating neighborRating = null;
				for (Rating rating : ratings) {
					if (rating.getUserId().equals(userId)) {
						userRating = rating;
					}
					if (rating.getUserId().equals(neighborId)) {
						neighborRating = rating;
					}
				}
				double internalSum = (userRating.getRating() - neighborRating.getRating());
				internal += Math.pow(internalSum, 2);
			}

			double euclideanDistanceToUser = Math.sqrt(internal);
			// String debugMsg = String.format("Calculated euclidean distance
			// \nfrom user: %s, \nto user: %s \ndistance: %s", userId,
			// neighborId, euclideanDistanceToUser);
			// LOGGER.debug(debugMsg);
			neighborDistanceMap.put(neighborId, euclideanDistanceToUser);
		}
		return neighborDistanceMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.thing.RecommendingDAO#
	 * getAllNeighborButNotUserRatedThings(java. lang. String)
	 */
	public List<String> getAllNeighborButNotUserRatedThings(String userId, String neighborId) {
		List<String> allUserRatings = getAllUserRatedThings(userId);
		List<String> allNeighborRatings = getAllUserRatedThings(neighborId);

		List<String> thingIDs = new ArrayList<String>();
		for (String neighborRatedThingId : allNeighborRatings) {
			boolean common = false;
			for (String userRatedThingId : allUserRatings) {
				if (neighborRatedThingId.equals(userRatedThingId)) {
					common = true;
				}
			}

			if (!thingIDs.contains(neighborRatedThingId) && !common) {
				thingIDs.add(neighborRatedThingId);
			}
		}
		return thingIDs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.at.tuwien.mt.dao.thing.RecommendingDAO#getAllCommonRatedThings(java.
	 * lang. String)
	 */
	public List<String> getAllCommonRatedThings(String userId, String neighborId) {
		List<String> allUserRatings = getAllUserRatedThings(userId);
		List<String> allNeighborRatings = getAllUserRatedThings(neighborId);

		List<String> commonRatedThingIds = new ArrayList<String>();
		for (String userRatedThingId : allUserRatings) {
			for (String neighborRatedThingId : allNeighborRatings) {
				if (userRatedThingId.equals(neighborRatedThingId)) {
					if (!commonRatedThingIds.contains(userRatedThingId)) {
						commonRatedThingIds.add(userRatedThingId);
					}
				}
			}
		}
		return commonRatedThingIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.thing.RecommendingDAO#getTopRatedThing(java.
	 * lang. String)
	 */
	@Override
	public Thing getTopRatedThing(String userId) {
		Document toSearchFor = new Document();
		toSearchFor.put("rating", -1);

		FindIterable<Document> list = mongoClient.getDatabase(database).getCollection(thingCollection).find(Filters.ne("ownerId", userId)).sort(toSearchFor).limit(1);
		for (Document document : list) {
			Thing thing = new Thing(document);
			if (thing.getRating() != null && thing.getRating() != 0) {
				return thing;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.at.tuwien.mt.dao.thing.RecommendingDAO#getRandomThing(java. lang.
	 * String)
	 */
	@Override
	public Thing getRandomThing(String userId) {
		// get the number of documents
		int count = (int) mongoClient.getDatabase(database).getCollection(thingCollection).count(Filters.ne("ownerId", userId));
		if (count == 0) {
			return null;
		}

		// generate a random number
		Random random = new Random();
		int low = 0;

		int nextLong = random.nextInt(count - low);
		FindIterable<Document> thingsList = mongoClient.getDatabase(database).getCollection(thingCollection).find(Filters.ne("ownerId", userId)).skip(nextLong);
		for (Document document : thingsList) {
			Thing thing = new Thing(document);
			return thing;
		}

		return null;
	}

	@Override
	public Thing recommendForTag(String tag) {
		Thing thing = getTopRatedThingForTag(tag);

		if (thing != null) {
			return thing;
		}

		thing = getRandomThingForTag(tag);
		if (thing != null) {
			return thing;
		}

		return null;
	}

	@Override
	public Thing getTopRatedThingForTag(String tag) {
		Document toSearchFor = new Document();
		toSearchFor.put("rating", -1);

		MongoCollection<Document> collection = mongoClient.getDatabase(database).getCollection(thingCollection);
		FindIterable<Document> list = collection.find(Filters.eq("tags", tag)).sort(toSearchFor).limit(1);
		for (Document document : list) {
			Thing thing = new Thing(document);
			if (thing.getRating() != null && thing.getRating() != 0) {
				return thing;
			}
		}

		return null;
	}

	@Override
	public Thing getRandomThingForTag(String tag) {
		int count = (int) mongoClient.getDatabase(database).getCollection(thingCollection).count(Filters.eq("tags", tag));
		if (count == 0) {
			return null;
		}

		// generate a random number
		Random random = new Random();
		int low = 0;

		int nextLong = random.nextInt(count - low);
		FindIterable<Document> thingsList = mongoClient.getDatabase(database).getCollection(thingCollection).find(Filters.eq("tags", tag)).skip(nextLong);
		for (Document document : thingsList) {
			Thing thing = new Thing(document);
			return thing;
		}

		return null;
	}

}
