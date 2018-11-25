package org.joncastillo.buslocator;

import java.util.ArrayList;
import java.util.HashMap;


// Created for O(1) Access time.
public class RedundantMapping {

    private RedundantMapping() {}
    static RedundantMapping oRedundantMapping;
    static public RedundantMapping get_instance()
    {
        if (oRedundantMapping == null) {
            oRedundantMapping = new RedundantMapping();
        }
        return oRedundantMapping;
    }

    public abstract class Redundancy<T>
    {
        public HashMap<String,ArrayList<String>> oRelationships;

        Redundancy()
        {
            oRelationships = new HashMap<>();
        }

        abstract public Boolean createRedundancy(HashMap<String,T> oContainer);
    }

    // uses routes.txt
    Redundancy routeIds_agencyIds = new Redundancy<ContainerBusNetwork.EntityRoute>() {
        public Boolean createRedundancy(HashMap<String,ContainerBusNetwork.EntityRoute> oContainer)
        {
            for ( HashMap.Entry<String,ContainerBusNetwork.EntityRoute> oEntry : oContainer.entrySet() )
            {
                String idto = oEntry.getKey();
                String idfrom = oEntry.getValue().getAgencyId();

                if (oRelationships.get(idfrom) == null)
                    oRelationships.put(idfrom, new ArrayList<String>());

                oRelationships.get(idfrom).add(idto);
            }
            return true;
        }
    };

    // uses trips.txt
    Redundancy tripIds_routeIds = new Redundancy<ContainerBusNetwork.EntityTrip>() {
        @Override
        public Boolean createRedundancy(HashMap<String,ContainerBusNetwork.EntityTrip> oContainer)
        {
            for ( HashMap.Entry<String,ContainerBusNetwork.EntityTrip> oEntry : oContainer.entrySet() )
            {
                String idto = oEntry.getKey();
                String idfrom = oEntry.getValue().getTripId();

                if (oRelationships.get(idfrom) == null)
                    oRelationships.put(idfrom, new ArrayList<String>());

                oRelationships.get(idfrom).add(idto);
            }
            return true;
        }
    };

    // uses stop_times.txt
    Redundancy stopId_tripIds = new Redundancy<ContainerBusNetwork.EntityStopTime>() {
        @Override
        public Boolean createRedundancy(HashMap<String,ContainerBusNetwork.EntityStopTime> oContainer)
        {
            for ( HashMap.Entry<String,ContainerBusNetwork.EntityStopTime> oEntry : oContainer.entrySet() )
            {
                String idto = oEntry.getKey();
                String idfrom = oEntry.getValue().getStopId();

                if (oRelationships.get(idfrom) == null)
                    oRelationships.put(idfrom, new ArrayList<String>());

                oRelationships.get(idfrom).add(idto);
            }
            return true;
        }
    };

}
