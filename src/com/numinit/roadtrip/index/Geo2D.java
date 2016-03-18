package com.numinit.roadtrip.index;

import com.numinit.mtree.point.IPointInMetricSpace;
import com.numinit.utils.DoubleUtils;

public class Geo2D implements IPointInMetricSpace<Geo2D>, Comparable<Geo2D> {
	private static double EARTH_RADIUS = 6371000.0d;
	
	/**
	 * Lat/lng coords of this portal
	 */
	private double _lat, _lng;

	/**
	 * Initializes this Geo2D
	 * @param lat the latitude
	 * @param lng the longitude
	 */
	public Geo2D(double lat, double lng) {
		this._lat = lat;
		this._lng = lng;
	}
	
	@Override
	public String toString() {
		return String.format("#<%s@%#08x:%.8f,%.8f>", this.getClass().getSimpleName(), System.identityHashCode(this), this.getLat(), this.getLng());
	}
	
	/**
	 * Returns the latitude
	 * @return the latitude
	 */
	public double getLat() {
		return this._lat;
	}
	
	/**
	 * Returns the longitude
	 * @return the longitude
	 */
	public double getLng() {
		return this._lng;
	}
	
	@Override
	public double getDistance(Geo2D other) {
		double lat = toRadians(this.getLat()), lng = toRadians(this.getLng());
		double dLat = toRadians(other.getLat() - this.getLat()), dLng = toRadians(other.getLng() - this.getLng());
		double a = Math.pow(Math.sin(dLat / 2.0d), 2.0d) + Math.cos(lat) * Math.cos(lng) * Math.pow(Math.sin(dLng / 2.0d), 2.0d);
		return EARTH_RADIUS * 2.0d * Math.atan2(Math.sqrt(a), Math.sqrt(1.0d - a));
	}
	
	/**
	 * Gets the bearing from this point to another
	 * @param other The other point
	 * @return The bearing
	 */
	public double getBearing(Geo2D other) {
		double curLat = toRadians(this.getLat()), otherLat = toRadians(other.getLat()), k = Math.cos(otherLat);
		double dLng = toRadians(other.getLng() - this.getLng());
		double theta = Math.atan2(Math.sin(dLng) * k, Math.cos(curLat) * Math.sin(otherLat) - Math.sin(curLat) * k * Math.cos(dLng));
		return ((360 - (toDegrees(theta) + 360.0d) % 360.0d) + 90) % 360;
	}

	@Override
	public int compareTo(Geo2D other) {
		if (DoubleUtils.compare(this.getLat(), other.getLat()) == 0 || DoubleUtils.compare(this.getLng(), other.getLng()) == 0) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * Converts degrees to radians
	 * @param degrees The degrees
	 * @return The radians
	 */
	public static double toRadians(double degrees) {
		return (degrees * Math.PI) / 180.0d;
	}
	
	/**
	 * Converts radians to degrees
	 * @param radians The radians
	 * @return The degrees
	 */
	public static double toDegrees(double radians) {
		return (radians * 180) / Math.PI;
	}
	
	/**
	 * Returns a new Geo2D from E6 coords
	 * @param latE6 the latitude
	 * @param lngE6 the longitude
	 * @return The Geo2D	
	 */
	public static Geo2D fromE6(long latE6, long lngE6) {
		return new Geo2D(latE6 / 1.0e6, lngE6 / 1.0e6);
	}
}
