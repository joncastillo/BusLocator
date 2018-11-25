package org.joncastillo.buslocator;

import com.google.android.gms.maps.model.Marker;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.Date;
import java.sql.Time;

public class GtfsContainer implements Iterable<GtfsContainer.GtfsEntity>, Iterator<GtfsContainer.GtfsEntity> {

    public final NavigableMap<String, GtfsEntity> oGtfsContainer = new TreeMap<String, GtfsEntity>();
    private int count = 0;


    public enum GtfsScheduleRelationship {
        SCHEDULED,
        ADDED,
        UNSCHEDULED
    }


    public class GtfsPosition {
        public Float getLongitude() {
            return longitude;
        }

        public void setLongitude(Float longitude) {
            this.longitude = longitude;
        }

        public Float getLatitude() {
            return latitude;
        }

        public void setLatitude(Float latitude) {
            this.latitude = latitude;
        }

        public Float getBearing() {
            return bearing;
        }

        public void setBearing(Float bearing) {
            this.bearing = bearing;
        }

        public Float getSpeed() {
            return speed;
        }

        public void setSpeed(Float speed) {
            this.speed = speed;
        }

        public String getTripId() {
            return tripId;
        }

        public void setTripId(String tripId) {
            this.tripId = tripId;
        }

        public String getStopId() {
            return stopId;
        }

        public void setStopId(String stopId) {
            this.stopId = stopId;
        }

        String stopId;
        String tripId;
        Float longitude;
        Float latitude;
        Float bearing;
        Float speed;
    }

    public class GtfsVehicleStatus {
        public GtfsScheduleRelationship getScheduleRelationship() {
            return scheduleRelationship;
        }

        public void setScheduleRelationship(GtfsScheduleRelationship scheduleRelationship) {
            this.scheduleRelationship = scheduleRelationship;
        }

        public String getCongestionLevel() {
            return congestionLevel;
        }

        public void setCongestionLevel(String congestionLevel) {
            this.congestionLevel = congestionLevel;
        }

        public String getOccupancy() {
            return occupancy;
        }

        public void setOccupancy(String occupancy) {
            this.occupancy = occupancy;
        }

        public Boolean getWheelchairAccessible() {
            return isWheelchairAccessible;
        }

        public void setWheelchairAccessible(Boolean wheelchairAccessible) {
            isWheelchairAccessible = wheelchairAccessible;
        }

        public Boolean getHasWifi() {
            return hasWifi;
        }

        public void setHasWifi(Boolean hasWifi) {
            this.hasWifi = hasWifi;
        }

        public String getBusModel() {
            return busModel;
        }

        public void setBusModel(String busModel) {
            this.busModel = busModel;
        }

        public Boolean getHasAirconditioning() {
            return hasAirconditioning;
        }

        public void setHasAirconditioning(Boolean hasAirconditioning) {
            this.hasAirconditioning = hasAirconditioning;
        }

        private Boolean hasAirconditioning;
        private Boolean isWheelchairAccessible;
        private Boolean hasWifi;
        private String busModel;

        private GtfsScheduleRelationship scheduleRelationship;
        private String congestionLevel;
        private String occupancy;
    }

    public class GtfsRoute {
        public String getRoute_id() {
            return route_id;
        }

        public void setRoute_id(String route_id) {
            this.route_id = route_id;
        }

        public String getService_number() {
            return service_number;
        }

        public void setService_number(String service_number) {
            this.service_number = service_number;
        }

        String route_id;
        String service_number;
    }

    public class GtfsEntity {

        Marker marker;
        Time startTime;
        Date startDate;
        GtfsVehicleStatus vehicleStatus;
        GtfsPosition position;

        public GtfsRoute getRoute() {
            return route;
        }

        public void setRoute(GtfsRoute route) {
            this.route = route;
        }

        GtfsRoute route;

        public GtfsEntity()
        {}
        public Time getStartTime() {
            return startTime;
        }

        public void setStartTime(Time startTime) {
            this.startTime = startTime;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public GtfsVehicleStatus getVehicleStatus() {
            return vehicleStatus;
        }

        public void setVehicleStatus(GtfsVehicleStatus vehicleStatus) {
            this.vehicleStatus = vehicleStatus;
        }

        public GtfsPosition getPosition() {
            return position;
        }

        public void setPosition(GtfsPosition position) {
            this.position = position;
        }
    }



    @Override
    public Iterator iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        if (count < oGtfsContainer.size())
            return true;

        count = 0;
        return false;
    }

    @Override
    public GtfsEntity next() {
        if (count == oGtfsContainer.size())
            throw new NoSuchElementException();

        count++;
        Map.Entry<Integer, GtfsEntity> oEntry = (Map.Entry<Integer, GtfsEntity>) oGtfsContainer.entrySet().toArray()[count - 1];
        return oEntry.getValue();
    }

    public GtfsContainer() {
    }

    public void addEntity(String key, GtfsEntity entity) {
        oGtfsContainer.put(key, entity);
    }

    public GtfsEntity getEntity(String key) {
        return oGtfsContainer.get(key);
    }
}

