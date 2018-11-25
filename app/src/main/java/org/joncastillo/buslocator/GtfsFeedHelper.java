package org.joncastillo.buslocator;

import java.util.Arrays;
import java.util.List;

public final class GtfsFeedHelper {

    private static GtfsFeedHelper m_oGtfsFeedHelper = null;

    private GtfsFeedHelper() {
    }

    public static GtfsFeedHelper get_instance()
    {
        if (m_oGtfsFeedHelper == null)
            m_oGtfsFeedHelper = new GtfsFeedHelper();
        return m_oGtfsFeedHelper;
    }

    public enum EntityVehicleVehicleId_Fields_e
    {
        OPERATOR_ID,
        TO_DIS_TRIP_ID ,
        TO_DIS_CONTRACT_ID ,
        TO_DIS_ROUTE_ID,
        TRIP_INSTANCE_NUMBER,
    }

    class EntityVehicleVehicleId
    {
        public String getOperatorId() {
            return operatorId;
        }

        public String getToDisTripId() {
            return toDisTripId;
        }

        public String getToDisContractId() {
            return toDisContractId;
        }

        public String getToDisRouteId() {
            return toDisRouteId;
        }

        private String operatorId = null;
        private String toDisTripId = null;
        private String toDisContractId = null;
        private String toDisRouteId = null;
        private String tripInstanceNumber = null;


        public boolean parseEntityVehicleVehicleId(String entityId) {
            List<String> entities = Arrays.asList(entityId.split("_"));

            if (entities.size() == EntityVehicleVehicleId_Fields_e.values().length) {
                operatorId = entities.get(EntityVehicleVehicleId_Fields_e.OPERATOR_ID.ordinal());
                toDisTripId = entities.get(EntityVehicleVehicleId_Fields_e.TO_DIS_TRIP_ID.ordinal());
                toDisContractId = entities.get(EntityVehicleVehicleId_Fields_e.TO_DIS_CONTRACT_ID.ordinal());
                toDisRouteId = entities.get(EntityVehicleVehicleId_Fields_e.TO_DIS_ROUTE_ID.ordinal());
                tripInstanceNumber =  entities.get(EntityVehicleVehicleId_Fields_e.TRIP_INSTANCE_NUMBER.ordinal());
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            return "operatorId:         " + operatorId + "\n" +
                   "toDisTripId:        " + toDisTripId + "\n" +
                   "toDisContractId:    " + toDisContractId + "\n" +
                   "toDisRouteId:       " + toDisRouteId + "\n" +
                   "tripInstanceNumber: " + tripInstanceNumber + "\n";
        }
    }


    public enum EntityId_Fields_e
    {
        CONTRACT_ID,
        ROUTE_ID,
    }

    class EntityVehicleTripId
    {
        public String getContractId() {
            return contractId;
        }

        public void setContractId(String contractId) {
            this.contractId = contractId;
        }

        public String getRouteId() {
            return routeId;
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        private String contractId = null;
        private String routeId = null;

        public boolean parseEntityVehicleTripId(String entityId) {
            List<String> entities = Arrays.asList(entityId.split("_"));

            if (entities.size() == EntityId_Fields_e.values().length) {
                contractId = entities.get(EntityId_Fields_e.CONTRACT_ID.ordinal());
                routeId = entities.get(EntityId_Fields_e.ROUTE_ID.ordinal());
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            return  "contractId:         " + contractId + "\n" +
                    "routeId:            " + routeId;
        }
    }




}
