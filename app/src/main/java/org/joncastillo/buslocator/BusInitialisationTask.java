package org.joncastillo.buslocator;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;


public class BusInitialisationTask extends AsyncTask<BusInitialisationTask.TaskParams, String, String>
{
    public class TaskParams
    {
        AssetManager am;
    }

    MainActivity context;

    public BusInitialisationTask(MainActivity context) {
        this.context = context;
    }



    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        ((TextView)context.findViewById(R.id.ID_StatusMessage1)).setText(values[0]);
        ((TextView)context.findViewById(R.id.ID_StatusMessage2)).setText(values[1]);
    }

    @Override
    protected String doInBackground(TaskParams... params) {
        try {

            AssetManager am = params[0].am;

            String[] statusMessage = {"",""};


            statusMessage[0]="Extracting from Assets...";

            InputStream isAgency = am.open("bus/"+"agency.txt");
            InputStream isCalendar = am.open("bus/"+"calendar.txt");
            InputStream isCalendarDates= am.open("bus/"+"calendar_dates.txt");
            InputStream isNotes = am.open("bus/"+"notes.txt");
            InputStream isShapes = am.open("bus/"+"shapes.txt");
            InputStream isRoutes = am.open("bus/"+"routes.txt");
            InputStream isStopTimes= am.open("bus/"+"stop_times.txt");
            InputStream isStops= am.open("bus/"+"stops.txt");
            InputStream isTrips= am.open("bus/"+"trips.txt");

            ContainerBusNetwork oContainerBusNetwork = ContainerBusNetwork.get_instance();
            //oContainerBusNetwork.initialize(isShapes,isNotes,isRoutes,isTrips,isStops,isAgency);

            statusMessage[1] = "Routes 1/5";
            publishProgress(statusMessage);
            oContainerBusNetwork.initializeRoutes(isRoutes);

            statusMessage[1] = "Trips 2/5";
            publishProgress(statusMessage);
            oContainerBusNetwork.initializeTrips(isTrips);

            statusMessage[1] = "Stops 3/5";
            publishProgress(statusMessage);
            oContainerBusNetwork.initializeStops(isStops);

            statusMessage[1] = "StopTimes 4/5";
            publishProgress(statusMessage);
            // too slow            oContainerBusNetwork.initializeStopTimes(isStopTimes);

            statusMessage[1] = "Polygons 5/5";
            publishProgress(statusMessage);
//            oContainerBusNetwork.initializeShapes(isShapes);

            statusMessage[0] = "Creating redundant memory mappings...";

            RedundantMapping oRedundantMapping;
            oRedundantMapping = RedundantMapping.get_instance();

            oRedundantMapping.routeIds_agencyIds.createRedundancy(oContainerBusNetwork.oContainerRoutes.oEntities);


            statusMessage[1] = "RouteIds -> AgencyIds";
            publishProgress(statusMessage);

            oRedundantMapping.tripIds_routeIds.createRedundancy(oContainerBusNetwork.oContainerTrips.oEntities);

            statusMessage[1] = "TripIds <--> RouteIds";
            publishProgress(statusMessage);

            oRedundantMapping.stopId_tripIds.createRedundancy(oContainerBusNetwork.oContainerStopTimes.oEntities);
            statusMessage[1] = "StopIds  --> TripIds";
            publishProgress(statusMessage);

        }
        catch (IOException e)
        {
            Log.e("MainActivity", "Exception: " + Log.getStackTraceString(e));
        }
        return "done";
    }

    protected void onPostExecute(String input)
    {
        Intent intent = new Intent(context, MapsActivity.class);
        context.startActivity(intent);
    }
}
