# roadtrip

A fun project using M-Tree algorithms developed during COMP215 at Rice to find
interesting locations in the game [Ingress](http://ingress.com) on my drive
home from Houston to Denver.

Useful for stopping in the middle of nowhere and figuring out interesting 
locations that I may have missed in the game, and a good field test of the
data structures I wrote in class.

![Screenshot](/png/screenshot.png?raw=true)

## Usage

1. Get "ShareGPS" app or similar. Enable live NMEA logging.
2. Forward a port over adb, e.g. `adb forward tcp:50000 tcp:50000`
3. Run roadtrip using a `portals.json` of your choice.

```sh
java -cp 'jar/json-simple-1.1.1.jar:bin' com.numinit.roadtrip.RoadTrip \
    --nmea-host localhost --nmea-port 50000 --file portals.json
```

## FAQ

* **Project name**: `roadtrip` (not to be confused with [Field 
  Trip](https://www.fieldtripper.com))
* **Project abstract**: Ingest NMEA data from my phone's GPS to 
  retrieve the bearing and distance of interesting locations in Ingress.
* **Project date**: 14 December 2014
* **Teamwork involved**: Solo.

