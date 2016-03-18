package com.numinit.roadtrip.nmea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.numinit.roadtrip.index.Geo2D;
import com.numinit.utils.DoubleUtils;

public class NMEAParser {
	/**
	 * A buffered reader
	 */
	private BufferedReader _is;
	
	/**
	 * A pattern
	 */
	private Pattern _gpsFixPattern;
	
	/**
	 * Initializes this NMEAParser with the specified input stream
	 * @param is The stream
	 */
	public NMEAParser(InputStream is) {
		this._is = new BufferedReader(new InputStreamReader(is));
		this._gpsFixPattern = Pattern.compile("^\\$GPGGA,(?<time>\\d{6}),(?<lat>(?<latdeg>\\d{2})(?<latmin>\\d+\\.\\d+)),(?<latns>N|S),(?<lng>(?<lngdeg>\\d{3})(?<lngmin>\\d+\\.\\d+)),(?<lngew>E|W),(?<fix>\\d)?,(?<satellites>\\d{2})?,(?<hdop>\\d\\.\\d)?,+(?:(?<alt>\\-?\\d+\\.\\d+),M)?,+(?:(?<geoidheight>-?\\d+\\.\\d+),M)?\\*(?<checksum>\\d{2})?$");
	}
	
	/**
	 * Returns the next point from the NMEA stream
	 * @return The next point
	 */
	public Geo2D getNextPoint() throws IOException {
		Matcher m = null;
		
		// Advance to the right line
		while (m == null) {
			String line = this._is.readLine();
			if (line == null) {
				throw new IOException("eof");
			}
			Matcher potential = this._gpsFixPattern.matcher(line);
			if (potential.find()) {
				m = potential;
			}
		}
		
		// We have a new GPS position! Convert.
		// 4124.8963  => 41º 24.8963'
		// 08151.6838 => 081º 51.6838'
		double latDegrees = Double.parseDouble(m.group("latdeg")), latDecimal = Double.parseDouble(m.group("latmin")) / 60;
		double lngDegrees = Double.parseDouble(m.group("lngdeg")), lngDecimal = Double.parseDouble(m.group("lngmin")) / 60;
		
		// Sanity check
		if (DoubleUtils.compare(latDegrees, 90) >= 0 || latDecimal > 1.0) {
			throw new IllegalArgumentException("out-of-bounds latitude");
		} else if (DoubleUtils.compare(lngDegrees, 180) >= 0 || lngDecimal > 1.0) {
			throw new IllegalArgumentException("out-of-bounds longitude");
		} else {
			latDegrees += latDecimal;
			lngDegrees += lngDecimal;
		}

		return new Geo2D((m.group("latns").charAt(0) == 'N' ? 1 : -1) * latDegrees, (m.group("lngew").charAt(0) == 'E' ? 1 : -1) * lngDegrees);
	}
	
	/**
	 * Computes the NMEA checksum on str
	 * @param str The string
	 * @return The checksum
	 */
	private static boolean nmeaChecksumMatches(String str) {
		int idx = str.indexOf("$") + 1;
		
		if (idx >= str.length()) {
			throw new IllegalArgumentException("invalid NMEA string start index");
		}
		
		char ret = 0x00, cur = str.charAt(idx);
		while (cur != '*') {
			ret ^= cur;
			
			if (++idx >= str.length()) {
				throw new IllegalArgumentException("invalid NMEA string");
			} else {
				cur = str.charAt(idx);
			}
		}
		
		if (idx + 2 >= str.length()) {
			throw new IllegalArgumentException("invalid NMEA string end index");
		}
		
		return (char)Integer.parseUnsignedInt(str.substring(idx + 1, idx + 3), 16) == ret;
	}
}
