package com.numinit.roadtrip.index;

import com.numinit.utils.DoubleUtils;

public class Cardinal {
	/**
	 * String directions
	 */
	private static final String[] DIRECTIONS = {"N", "NW", "W", "SW", "S", "SE", "E", "NE"};
	
	/**
	 * String emojis
	 */
	private static final String[] EMOJIS = {"⬆️️️️️️️️", "↖️️️️️️️️", "⬅️️️️️️️️", "↙️️️️️️️️", "⬇️️️️️️️️", "↘️️️️️️️️", "➡️️️️️️️️", "↗️️️️️️️️"};
	
	/**
	 * Degree intervals
	 */
	private static final double[][] INTERVALS = {
		{60, 90, 120},
		{120, 135, 150},
		{150, 180, 210},
		{210, 225, 240},
		{240, 270, 300},
		{300, 315, 330},
		{330, 0, 30},
		{30, 45, 60}
	};

	/**
	 * The bearing
	 */
	private double _bearing;
	
	/**
	 * The direction
	 */
	private int _direction;
	
	/**
	 * Initializes this Cardinal
	 * @param bearing The bearing
	 */
	public Cardinal(double bearing) {
		if (DoubleUtils.compare(bearing, 0) < 0 || DoubleUtils.compare(bearing, 360) >= 0) {
			throw new IllegalArgumentException(String.format("invalid bearing: %f", bearing));
		}
		this._bearing = bearing;
		
		// Assign the direction
		this._direction = -1;
		for (int i = 0; i < INTERVALS.length; i++) {
			final double[] interval = INTERVALS[i];
			if ((DoubleUtils.compare(this.getBearing(), interval[0]) >= 0 && DoubleUtils.compare(this.getBearing(), DoubleUtils.compare(interval[1], 0) == 0 ? 360 : interval[1]) < 0) ||
			    (DoubleUtils.compare(this.getBearing(), interval[1]) >= 0 && DoubleUtils.compare(this.getBearing(), DoubleUtils.compare(interval[2], 0) == 0 ? 360 : interval[2]) < 0)) {
				this._direction = i;
				break;
			}
		}
	}
	
	/**
	 * Returns the bearing
	 * @return The bearing
	 */
	public double getBearing() {
		return this._bearing;
	}
	
	/**
	 * Returns the direction
	 * @return The direction
	 */
	public int getDirection() {
		return this._direction;
	}
	
	/**
	 * Returns a direction string
	 * @return The string
	 */
	public final String getDirectionString() {
		return DIRECTIONS[this.getDirection()];
	}
	
	/**
	 * Returns an emoji
	 * @return The emoji string
	 */
	public final String getEmoji() {
		return EMOJIS[this.getDirection()];
	}
}
