package org.joncastillo.buslocator;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.opencsv.CSVReader;

public class ContainerBusNetwork {

    static ContainerBusNetwork oContainerBusNetwork;

    public ContainerShapes oContainerShapes;
    public ContainerNotes oContainerNotes;
    public ContainerRoutes oContainerRoutes;
    public ContainerTrips oContainerTrips;
    public ContainerStops oContainerStops;
    public ContainerAgencies oContainerAgencies;
    public ContainerStopTimes oContainerStopTimes;
    public Boolean isInitialized;

    private ContainerBusNetwork(){
        oContainerShapes = new ContainerShapes();
        oContainerNotes = new ContainerNotes();
        oContainerRoutes = new ContainerRoutes();
        oContainerTrips = new ContainerTrips();
        oContainerStops = new ContainerStops();
        oContainerAgencies = new ContainerAgencies();
        oContainerStopTimes = new ContainerStopTimes();
        isInitialized = false;
    }


    public boolean initializeShapes(InputStream isShapes)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isShapes);
        oContainerShapes.parseCsv(isShapes);
        return true;
    }

    public boolean initializeNotes(InputStream isNotes)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isNotes);
        oContainerNotes.parseCsv(isNotes);
        return true;
    }

    public boolean initializeRoutes(InputStream isRoutes)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isRoutes);
        oContainerRoutes.parseCsv(isRoutes);
        return true;
    }

    public boolean initializeTrips(InputStream isTrips)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isTrips);
        oContainerTrips.parseCsv(isTrips);
        return true;
    }

    public boolean initializeStops(InputStream isStops)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isStops);
        oContainerStops.parseCsv(isStops);
        return true;
    }

    public boolean initializeStopTimes(InputStream isStopTimes)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isStopTimes);
        oContainerStopTimes.parseCsv(isStopTimes);
        return true;
    }

    public boolean initializeAgency(InputStream isAgency)
    {
        Log.d("ContainerBusNetwork", "Parsing: " + isAgency);
        oContainerAgencies.parseCsv(isAgency);
        return true;
    }

    public boolean initialize(InputStream isShapes,
                              InputStream isNotes,
                              InputStream isRoutes,
                              InputStream isTrips,
                              InputStream isStops,
                              InputStream isAgency )
    {

        initializeShapes(isShapes);
        initializeNotes(isNotes);
        initializeRoutes(isRoutes);
        initializeTrips(isTrips);
        initializeStops(isStops);
        initializeAgency(isAgency);

        isInitialized = true;
        return true;
    }
    static public ContainerBusNetwork get_instance()
    {
        if (oContainerBusNetwork == null) {
            oContainerBusNetwork = new ContainerBusNetwork();
        }
        return oContainerBusNetwork;
    }

    interface csvParseable {
        public Boolean parseCsv(InputStream is);
    }

    abstract class Entity {

    }

    abstract class Container<T> implements csvParseable {
        public HashMap<String, T> oEntities;

        public Container ()
        {
            oEntities = new HashMap<String, T>();
        }

        @Override
        public String toString() {
            for (HashMap.Entry<String,T> entry : oEntities.entrySet())
            {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            return super.toString();
        }
    }

    public class ContainerShapes extends Container<EntityShape> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {

                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    String shapeId = nextLine[0];

                    EntityShape oEntity = new EntityShape();
                    oEntity.shapePointLatitude = Float.parseFloat(nextLine[1]);
                    oEntity.shapePointLongitude = Float.parseFloat(nextLine[2]);
                    oEntity.shapePointSequence = Integer.parseInt(nextLine[3]);
                    oEntity.shapeDistanceTravelled = Integer.parseInt(nextLine[4]);

                    oEntities.put(shapeId, oEntity);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class ContainerNotes extends Container<EntityNote> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);

                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    String noteId = nextLine[0];
                    EntityNote oEntity = new EntityNote();
                    oEntity.note_text = nextLine[1];

                    oEntities.put(noteId, oEntity);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class ContainerRoutes extends Container<EntityRoute> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    String routeId = nextLine[0];

                    EntityRoute oEntity = new EntityRoute();
                    oEntity.agencyId = nextLine[1];
                    oEntity.routeShortName = nextLine[2];
                    oEntity.routeLongName = nextLine[3];
                    oEntity.routeDescription = nextLine[4];
                    oEntity.routeType = Integer.parseInt(nextLine[5]);
                    oEntity.routeColor = nextLine[6];
                    oEntity.textColor = nextLine[7];

                    oEntities.put(routeId, oEntity);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class ContainerTrips extends Container<EntityTrip> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    String tripId = nextLine[2];

                    EntityTrip oEntity = new EntityTrip();

                    oEntity.routeId = nextLine[0];
                    oEntity.serviceId = Integer.parseInt(nextLine[1]);
                    oEntity.shapeId = Integer.parseInt(nextLine[3]);
                    oEntity.headsign = nextLine[4];
                    oEntity.directionId = Integer.parseInt(nextLine[5]);
                    oEntity.routeDirection = nextLine[7];


                    oEntity.noteId = Integer.parseInt(nextLine[5]);


                    oEntities.put(tripId, oEntity);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class ContainerStops extends Container<EntityStop> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    String stopId = nextLine[0];

                    EntityStop oEntity = new EntityStop();

                    oEntity.stopName = nextLine[1];
                    oEntity.latitude = Float.parseFloat(nextLine[2]);
                    oEntity.longitude = Float.parseFloat(nextLine[3]);

                    oEntities.put(stopId, oEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class ContainerAgencies extends Container<EntityAgency> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {
                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    String agencyId = nextLine[0];

                    EntityAgency oEntity = new EntityAgency();

                    oEntity.agencyName = nextLine[1];
                    oEntity.agencyUrl = nextLine[2];
                    oEntity.agencyTimezone = nextLine[3];
                    oEntity.agencyLanguage = nextLine[4];
                    oEntity.agencyPhone = nextLine[5];

                    oEntities.put(agencyId, oEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class ContainerStopTimes extends Container<EntityStopTimesPerTrip> {
        @Override
        public Boolean parseCsv(InputStream is) {
            try {

                CSVReader reader = new CSVReader(new InputStreamReader(is), ',','"','\\',1, false);
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {

                    String tripId = nextLine[0];
                    String arrivalTime = nextLine[1];
                    //String departureTime = nextLine[2];
                    String stopId = nextLine[3];
                    Integer stopSequence = Integer.parseInt(nextLine[4]);
                    String stopHeadsign = nextLine[5];
                    //Integer pickupType = Integer.parseInt(nextLine[6]);
                    //Integer dropOffType = Integer.parseInt(nextLine[7]);
                    //Integer shapeDistanceTravelled = Integer.parseInt(nextLine[8]);
                    //Integer timepoint = Integer.parseInt(nextLine[9]);
                    //String stopNoteId = nextLine[10];

                    EntityStopTimesPerTrip oEntityStopTimesPerTrip = oEntities.get(tripId);
                    if (oEntityStopTimesPerTrip == null)
                    {
                        oEntityStopTimesPerTrip = new EntityStopTimesPerTrip();
                        oEntities.put(tripId,oEntityStopTimesPerTrip);
                    }

                    EntityStopTime oEntityStopTime = new EntityStopTime();

                    Time arrivalTimeInMs;
                    Time departureTimeInMs;

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                        long ms = sdf.parse(arrivalTime).getTime();
                        arrivalTimeInMs = new Time(ms);
                        //ms = sdf.parse(departureTime).getTime();
                        //departureTimeInMs = new Time(ms);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                        arrivalTimeInMs = new Time(0);
                        //departureTimeInMs = new Time(0);
                    }

                    oEntityStopTime.setArrivalTime(arrivalTimeInMs);
                    //oEntityStopTime.setDepartureTime(departureTimeInMs);
                    oEntityStopTime.setStopId(stopId);
                    oEntityStopTime.setStop_sequence(stopSequence);
                    oEntityStopTime.setStopHeadsign(stopHeadsign);
                    //oEntityStopTime.setPickupType(pickupType);
                    //oEntityStopTime.setDropOffType(dropOffType);
                    //oEntityStopTime.setShapeDistanceTravelled(shapeDistanceTravelled);
                    //oEntityStopTime.setTimepoint(timepoint);
                    //oEntityStopTime.setStopNoteId(stopNoteId);

                    oEntityStopTimesPerTrip.oStoptimesOfTripSegment.put(stopSequence, oEntityStopTime);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public class EntityShape extends Entity
    {
        private Float shapePointLatitude;
        private Float shapePointLongitude;
        private Integer shapePointSequence;
        private Integer shapeDistanceTravelled;

        public Float getShapePointLatitude() {
            return shapePointLatitude;
        }

        public void setShapePointLatitude(Float shapePointLatitude) {
            this.shapePointLatitude = shapePointLatitude;
        }

        public Float getShapePointLongitude() {
            return shapePointLongitude;
        }

        public void setShapePointLongitude(Float shapePointLongitude) {
            this.shapePointLongitude = shapePointLongitude;
        }

        public Integer getShapePointSequence() {
            return shapePointSequence;
        }

        public void setShapePointSequence(Integer shapePointSequence) {
            this.shapePointSequence = shapePointSequence;
        }

        public Integer getShapeDistanceTravelled() {
            return shapeDistanceTravelled;
        }

        public void setShapeDistanceTravelled(Integer shapeDistanceTravelled) {
            this.shapeDistanceTravelled = shapeDistanceTravelled;
        }



        @Override
        public String toString() {
            return "EntityShape{" +
                    ", shapePointLatitude=" + shapePointLatitude +
                    ", shapePointLongitude=" + shapePointLongitude +
                    ", shapePointSequence=" + shapePointSequence +
                    ", shapeDistanceTravelled=" + shapeDistanceTravelled +
                    '}';
        }
    }

    public class EntityNote extends Entity
    {
        private int noteId;
        private String note_text;

        public int getNoteId() {
            return noteId;
        }

        public void setNoteId(int noteId) {
            this.noteId = noteId;
        }

        public String getNote_text() {
            return note_text;
        }

        public void setNote_text(String note_text) {
            this.note_text = note_text;
        }

        @Override
        public String toString() {
            return "EntityNotes{" +
                    "noteId=" + noteId +
                    ", note_text='" + note_text + '\'' +
                    '}';
        }
    }

    public class EntityRoute extends Entity
    {
        private String agencyId;
        private String routeShortName;
        private String routeLongName;
        private String routeDescription;
        private Integer routeType;
        private String routeColor;
        private String textColor;

        public String getRouteShortName() {
            return routeShortName;
        }

        public void setRouteShortName(String routeShortName) {
            this.routeShortName = routeShortName;
        }

        public String getRouteLongName() {
            return routeLongName;
        }

        public void setRouteLongName(String routeLongName) {
            this.routeLongName = routeLongName;
        }

        public String getAgencyId() {
            return agencyId;
        }

        public void setAgencyId(String agencyId) {
            this.agencyId = agencyId;
        }

        public String getRouteDescription() {
            return routeDescription;
        }

        public void setRouteDescription(String routeDescription) {
            this.routeDescription = routeDescription;
        }

        public Integer getRouteType() {
            return routeType;
        }

        public void setRouteType(Integer routeType) {
            this.routeType = routeType;
        }

        public String getRouteColor() {
            return routeColor;
        }

        public void setRouteColor(String routeColor) {
            this.routeColor = routeColor;
        }

        public String getTextColor() {
            return textColor;
        }

        public void setTextColor(String textColor) {
            this.textColor = textColor;
        }

        @Override
        public String toString() {
            return "EntityRoute{" +
                    "agencyId='" + agencyId + '\'' +
                    ", routeShortName='" + routeShortName + '\'' +
                    ", routeLongName='" + routeLongName + '\'' +
                    ", routeDescription='" + routeDescription + '\'' +
                    ", routeType=" + routeType +
                    ", routeColor='" + routeColor + '\'' +
                    ", textColor='" + textColor + '\'' +
                    '}';
        }
    }

    public class EntityTrip extends Entity
    {
        private String routeId;
        private Integer serviceId;
        private String tripId;
        private Integer shapeId;
        private String headsign;
        private Integer noteId;
        private Integer directionId;
        private String routeDirection;

        public String getRouteId() {
            return routeId;
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public Integer getServiceId() {
            return serviceId;
        }

        public void setServiceId(Integer serviceId) {
            this.serviceId = serviceId;
        }

        public String getTripId() {
            return tripId;
        }

        public void setTripId(String tripId) {
            this.tripId = tripId;
        }

        public Integer getShapeId() {
            return shapeId;
        }

        public void setShapeId(Integer shapeId) {
            this.shapeId = shapeId;
        }

        public String getHeadsign() {
            return headsign;
        }

        public void setHeadsign(String headsign) {
            this.headsign = headsign;
        }

        public Integer getNoteId() {
            return noteId;
        }

        public void setNoteId(Integer noteId) {
            this.noteId = noteId;
        }

        public Integer getDirectionId() {
            return directionId;
        }

        public void setDirectionId(Integer directionId) {
            this.directionId = directionId;
        }

        public String getRouteDirection() {
            return routeDirection;
        }

        public void setRouteDirection(String routeDirection) {
            this.routeDirection = routeDirection;
        }

        @Override
        public String toString() {
            return "EntityTrips{" +
                    "routeId='" + routeId + '\'' +
                    ", serviceId=" + serviceId +
                    ", tripId=" + tripId +
                    ", shapeId=" + shapeId +
                    ", headsign='" + headsign + '\'' +
                    ", noteId=" + noteId +
                    ", directionId=" + directionId +
                    ", routeDirection='" + routeDirection + '\'' +
                    '}';
        }
    }

    public class EntityStopTime extends Entity
    {
        private Time arrivalTime;
        private Time departureTime;
        private String stopId;
        private int stopSequence;
        private String stopHeadsign;
        private int pickupType;
        private int dropOffType;
        private int shapeDistanceTravelled;
        private int timepoint;
        private String stopNoteId;

        public Time getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(Time arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public Time getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(Time departureTime) {
            this.departureTime = departureTime;
        }

        public String getStopId() {
            return stopId;
        }

        public void setStopId(String stopId) {
            this.stopId = stopId;
        }

        public int getStop_sequence() {
            return stopSequence;
        }

        public void setStop_sequence(int stop_sequence) {
            this.stopSequence = stop_sequence;
        }

        public String getStopHeadsign() {
            return stopHeadsign;
        }

        public void setStopHeadsign(String stopHeadsign) {
            this.stopHeadsign = stopHeadsign;
        }

        public int getPickupType() {
            return pickupType;
        }

        public void setPickupType(int pickupType) {
            this.pickupType = pickupType;
        }

        public int getDropOffType() {
            return dropOffType;
        }

        public void setDropOffType(int dropOffType) {
            this.dropOffType = dropOffType;
        }

        public int getShapeDistanceTravelled() {
            return shapeDistanceTravelled;
        }

        public void setShapeDistanceTravelled(int shapeDistanceTravelled) {
            this.shapeDistanceTravelled = shapeDistanceTravelled;
        }

        public Integer getTimepoint() {
            return timepoint;
        }

        public void setTimepoint(Integer timepoint) {
            this.timepoint = timepoint;
        }

        public String getStopNoteId() {
            return stopNoteId;
        }

        public void setStopNoteId(String stopNoteId) {
            this.stopNoteId = stopNoteId;
        }

        @Override
        public String toString() {
            return "EntityStopTime{" +
                    ", arrivalTime='" + arrivalTime + '\'' +
                    ", departureTime='" + departureTime + '\'' +
                    ", stopId='" + stopId + '\'' +
                    ", stop_sequence=" + stopSequence +
                    ", stopHeadsign='" + stopHeadsign + '\'' +
                    ", pickupType=" + pickupType +
                    ", dropOffType=" + dropOffType +
                    ", shapeDistanceTravelled=" + shapeDistanceTravelled +
                    ", timepoint='" + timepoint + '\'' +
                    ", stopNoteId='" + stopNoteId + '\'' +
                    '}';
        }
    }
    // [StopTimes Of Entire Trip] [StopTime of Trip Segment]
    // [(Trip) -> (ArrivalTime) ] [(ArrivalTime - StopId)  ]
    public class EntityStopTimesPerTrip extends Entity
    {
        EntityStopTimesPerTrip()
        {
            oStoptimesOfTripSegment = new HashMap<Integer, EntityStopTime>();
        }
        HashMap<Integer, EntityStopTime> oStoptimesOfTripSegment;
    }

    public class EntityStop extends Entity
    {
        private String stopName;
        private float latitude;
        private float longitude;
        // location type
        // parent station
        // wheelchair boarding

        public String getStopName() {
            return stopName;
        }

        public void setStopName(String name) {
            this.stopName = name;
        }

        public float getLatitude() {
            return latitude;
        }

        public void setLatitude(float latitude) {
            this.latitude = latitude;
        }

        public float getLongitude() {
            return longitude;
        }

        public void setLongitude(float longitude) {
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "EntityStops{" +
                    ", name='" + stopName + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    public class EntityAgency extends Entity
    {
        private Integer agencyId;
        private String agencyName;
        private String agencyUrl;
        private String agencyTimezone;
        private String agencyLanguage;
        private String agencyPhone;

        public Integer getAgencyId() {
            return agencyId;
        }

        public void setAgencyId(Integer agencyId) {
            this.agencyId = agencyId;
        }

        public String getAgencyName() {
            return agencyName;
        }

        public void setAgencyName(String agencyName) {
            this.agencyName = agencyName;
        }

        public String getAgencyUrl() {
            return agencyUrl;
        }

        public void setAgencyUrl(String agencyUrl) {
            this.agencyUrl = agencyUrl;
        }

        public String getAgencyTimezone() {
            return agencyTimezone;
        }

        public void setAgencyTimezone(String agencyTimezone) {
            this.agencyTimezone = agencyTimezone;
        }

        public String getAgencyLanguage() {
            return agencyLanguage;
        }

        public void setAgencyLanguage(String agencyLanguage) {
            this.agencyLanguage = agencyLanguage;
        }

        public String getAgencyPhone() {
            return agencyPhone;
        }

        public void setAgencyPhone(String agencyPhone) {
            this.agencyPhone = agencyPhone;
        }

        @Override
        public String toString() {
            return "EntityTrips{" +
                    "agencyId=" + agencyId +
                    ", agencyName='" + agencyName + '\'' +
                    ", agencyUrl='" + agencyUrl + '\'' +
                    ", agencyTimezone='" + agencyTimezone + '\'' +
                    ", agencyLanguage='" + agencyLanguage + '\'' +
                    ", agencyPhone='" + agencyPhone + '\'' +
                    '}';
        }
    }
}

