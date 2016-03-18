package com.numinit.roadtrip;

import com.numinit.interactive.CommandLine;

public class RoadTrip {

	public static void main(String[] args) {		
		CommandLine cmdline = new CommandLine("RoadTrip", args)
		.add(new CommandLine.Option<String>(String.class, "nmea-host", "h", null, "The NMEA host", "host"))
		.add(new CommandLine.Option<Integer>(Integer.class, "nmea-port", "p", 50000, "The NMEA port", "port"))
		.add(new CommandLine.Option<String>(String.class, "file", "f", null, "The location file", "file"))
		.add(new CommandLine.Option<Integer>(Integer.class, "query-size", "k", 10, "How many neighbors we should retrieve", "number"))
		.add(new CommandLine.Option<Double>(Double.class, "query-distance", "d", 10.0d, "The max distance (in kilometers) a 'nearby' location should be from us", "number"))
		.add(new CommandLine.Option<Integer>(Integer.class, "query-distance-size", "dk", 20, "The maximum number of items to return from the distance query", "number")).parse();

		final RoadTripRunner runner = new RoadTripRunner(cmdline, System.in, System.out);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				runner.stop();
			}
		});
		
		runner.go();
		System.exit(0);
	}
}
