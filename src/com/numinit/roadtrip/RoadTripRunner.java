package com.numinit.roadtrip;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.numinit.interactive.CommandLine;
import com.numinit.mtree.IMTree;
import com.numinit.mtree.MTree;
import com.numinit.mtree.utils.DataWrapper;
import com.numinit.roadtrip.index.Cardinal;
import com.numinit.roadtrip.index.Geo2D;
import com.numinit.roadtrip.index.Portal;
import com.numinit.roadtrip.index.Team;
import com.numinit.roadtrip.nmea.NMEAParser;
import com.numinit.utils.DoubleUtils;

public class RoadTripRunner {
	/**
	 * M-Tree constants
	 */
	private static final int MT_INTERNAL_SIZE = 4, MT_LEAF_SIZE = 8;

	/**
	 * The command line
	 */
	private CommandLine _cmdline;
	
	/**
	 * The input stream
	 */
	@SuppressWarnings("unused")
	private InputStream _in;
	
	/**
	 * The output stream
	 */
	private PrintStream _out;
	
	/**
	 * A M-Tree for spatial indexing
	 */
	private IMTree<Geo2D, Portal> _m;
	
	/**
	 * Whether we're running
	 */
	private boolean _run;

	/**
	 * Initializes this RoadTripRunner
	 * @param cmdline the command line
	 * @param in the input stream
	 * @param out the output stream
	 */
	public RoadTripRunner(CommandLine cmdline, InputStream in, PrintStream out) {
		this._cmdline = cmdline;
		this._in = in;
		this._out = out;
		this._run = true;
	}
	
	/**
	 * Starts this Runner's main loop
	 */
	public void go() {
		// Load the M-Tree
		this._m = this.loadMTreeFrom(this._cmdline.get(String.class, "file"));
		
		// Connect to the socket
		try (final Socket client = new Socket(this._cmdline.get(String.class, "nmea_host"), this._cmdline.get(Integer.class, "nmea_port"))) {
			final NMEAParser parser = new NMEAParser(client.getInputStream());
			client.setTcpNoDelay(true);
			client.setReuseAddress(true);
			
			while (this._run) {
				final double distance = this._cmdline.get(Double.class, "query_distance") * 1000;
				final int size = this._cmdline.get(Integer.class, "query_size"), distanceSize = this._cmdline.get(Integer.class, "query_distance_size");
				
				final Geo2D currentLocation;
				try {
					currentLocation = parser.getNextPoint();
				} catch (IOException e) {
					e.printStackTrace(this._out);
					client.close();
					this.stop();
					continue;
				}
				
				// Run a find and a top-K query
				List<DataWrapper<Geo2D, Portal>> nearbyList = this._m.find(currentLocation, distance);
				List<DataWrapper<Geo2D, Portal>> closestList = this._m.findKClosest(currentLocation, size);
				nearbyList = nearbyList.subList(0, Math.min(distanceSize, nearbyList.size()));
				nearbyList.sort(new Comparator<DataWrapper<Geo2D, Portal>>() {
					@Override
					public int compare(DataWrapper<Geo2D, Portal> o1, DataWrapper<Geo2D, Portal> o2) {
						return DoubleUtils.compare(o1.getKey().getDistance(currentLocation), o2.getKey().getDistance(currentLocation));
					}
				});
				
				// Search the lists for interesting portals
				List<String> closest = describe(currentLocation, closestList);	
				List<String> nearby  = describe(currentLocation, nearbyList);
				
				// Print everything
				this._out.print("\033[H\033[2J");
				this._out.flush();
				this._out.format("=== Portals within %.2fkm\n", distance / 1000);
				for (String line : nearby) {
					this._out.println(line);
				}
				
				this._out.format("\n=== %d closest %s\n", closest.size(), closest.size() == 1 ? "portal" : "portals");
				for (String line : closest) {
					this._out.println(line);
				}
				
				List<String> loneWolf = describe(currentLocation, this.loneWolf(closestList, 3, 5000));
				this._out.format("\n== %d potential lone %s\n", loneWolf.size(), loneWolf.size() == 1 ? "wolf" : "wolves");
				for (String line : loneWolf) {
					this._out.println(line);
				}
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Stops this Runner's main loop
	 */
	public void stop() {
		this._run = false;
	}
	
	/**
	 * Loads the M-Tree from a file
	 * @param file The file
	 * @return this
	 */
	private IMTree<Geo2D, Portal> loadMTreeFrom(String file) {
		// Warm up the M-Tree
		final IMTree<Geo2D, Portal> ret = new MTree<Geo2D, Portal>(MT_INTERNAL_SIZE, MT_LEAF_SIZE);
		JSONParser jsonParser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject)jsonParser.parse(new FileReader(file));
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
		
		// Insert the portals
		for (Object key : obj.keySet()) {
			Portal p = Portal.fromJSON((JSONArray)obj.get(key));
			ret.insert(p.getPoint(), p);
		}
		
		return ret;
	}
	
	private List<DataWrapper<Geo2D, Portal>> loneWolf(List<DataWrapper<Geo2D, Portal>> portals, int loneWolfThreshold, double distanceThreshold) {
		return portals.stream().filter((wrapper) -> {
			Portal portal = wrapper.getData();
			return this._m.find(portal.getPoint(), distanceThreshold).size() - 1 <= loneWolfThreshold;
		}).collect(Collectors.toList());
	}
	
	/**
	 * Describes a list of portals based on a current location
	 * @param currentLocation The current location
	 * @param portals The portals
	 * @return A list of string descriptions
	 */
	private static List<String> describe(final Geo2D currentLocation, List<DataWrapper<Geo2D, Portal>> portals) {
		final AtomicInteger a = new AtomicInteger(1);
		return portals.stream().map((portal) -> {
			double distance = currentLocation.getDistance(portal.getKey()), bearing = currentLocation.getBearing(portal.getKey());
			Cardinal direction = new Cardinal(bearing);
			return String.format("[%d] %s: %.2fkm, %s %.0fº %s: %s", a.getAndIncrement(), portal.getData().toConsole(), distance / 1000.0d, direction.getEmoji(), bearing, direction.getDirectionString(), String.format("http://www.ingress.com/intel?ll=%1$f,%2$f&pll=%1$f,%2$f", portal.getKey().getLat(), portal.getKey().getLng()));
		}).collect(Collectors.toList());
	}
}
