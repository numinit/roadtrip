package com.numinit.roadtrip.index;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Portal {
	/**
	 * The portal's UUID
	 */
	private String _uuid;
	
	/**
	 * The timestamp
	 */
	private long _timestamp;
	
	/**
	 * The name
	 */
	private String _name;
	
	/**
	 * The team
	 */
	private Team _faction;
	
	/**
	 * The point
	 */
	private Geo2D _point;
	
	/**
	 * Portal metrics
	 */
	private int _level, _health, _resonators;
	

	/**
	 * Initializes this portal
	 * @param uuid The UUID
	 * @param timestamp The timestamp
	 * @param name The name
	 * @param faction The faction
	 * @param point The location
	 * @param level The level
	 * @param health The health
	 * @param resonators The resonator count
	 */
	public Portal(String uuid, long timestamp, String name, Team faction, Geo2D point, int level, int health, int resonators) {
		this._uuid = uuid;
		this._timestamp = timestamp;
		this._name = name;
		this._faction = faction;
		this._point = point;
		this._level = level;
		this._health = health;
		this._resonators = resonators;
	}
	
	@Override
	public String toString() {
		if (this.getLevel() > 0) {
			return String.format("%s [%s]: L%d (%d%%, %dR)", this.getName(), this.getFaction().toString(), this.getLevel(), this.getHealth(), this.getResonators());
		} else {
			return String.format("%s [%s]: UNCAPTURED", this.getName(), this.getFaction().toString());
		}
	}
	
	/**
	 * Converts to a console string
	 * @return the string
	 */
	public String toConsole() {
		if (this.getLevel() > 0) {
			return String.format("%s [%s]: L%d (%d%%, %dR)", this.getName(), this.getFaction().toConsole(), this.getLevel(), this.getHealth(), this.getResonators());
		} else {
			return String.format("%s [%s]: UNCAPTURED", this.getName(), this.getFaction().toString());
		}
	}
	
	/**
	 * @return the uuid
	 */
	public String getUUID() {
		return this._uuid;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return this._timestamp;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this._name;
	}
	
	/**
	 * @return the faction
	 */
	public Team getFaction() {
		return this._faction;
	}

	/**
	 * @return the point
	 */
	public Geo2D getPoint() {
		return this._point;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return this._level;
	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return this._health;
	}

	/**
	 * @return the resonators
	 */
	public int getResonators() {
		return this._resonators;
	}
	
	/**
	 * Creates a portal from JSON
	 * @param entity The entity
	 * @return The portal
	 */
	public static Portal fromJSON(JSONArray entity) {
		if (entity.size() != 3) {
			throw new IllegalArgumentException("entity did not have size 3");
		}
		String uuid = (String)entity.get(0);
		long timestamp = (Long)entity.get(1);
		JSONObject fields = (JSONObject)entity.get(2);
		
		if (!((String)fields.get("type")).equals("portal")) {
			throw new IllegalArgumentException("object is not a portal");
		}
		
		String name = ((String)fields.get("title")).trim();
		Team team = Team.getTeamFor((String)fields.get("team"));
		long latE6 = (Long)fields.get("latE6"), lngE6 = (Long)fields.get("lngE6");
		long level = (Long)fields.get("level"), health = (Long)fields.get("health"), resCount = (Long)fields.get("resCount");
		return new Portal(uuid, timestamp, name, team, Geo2D.fromE6(latE6, lngE6), (int)level, (int)health, (int)resCount);
	}
}
