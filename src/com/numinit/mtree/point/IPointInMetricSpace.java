package com.numinit.mtree.point;
/**
 * This interface corresponds to a point in some metric space.
 * @author cmj4
 *
 * @param <PointInMetricSpace> The point type
 */
public interface IPointInMetricSpace<PointInMetricSpace> {
	/**
	 * Get the distance to another point. For this to work in an M-Tree,
	 * distances should be "metric" and obey the triangle inequality
	 * @param toMe
	 * @return
	 */
	double getDistance(PointInMetricSpace toMe);
}