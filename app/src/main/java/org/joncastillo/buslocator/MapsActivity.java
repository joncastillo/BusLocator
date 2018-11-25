package org.joncastillo.buslocator;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.protobuf.ByteString;
import com.google.transit.realtime.TfnswGtfsRealtimeProtoTxt.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener {
    private final static Integer refreshrate = 1000;

    private Intent realtimeVehicleDataPollingServiceIntent;
    private GtfsContainer oGtfsContainer = new GtfsContainer();
    private HashMap<Marker,GtfsContainer.GtfsEntity> disMarkerToGtfsEntity;
    private HashMap<String, ArrayList<Marker>> ServiceIdToMarkerId;
    private Boolean mBound;
    String lastservice;
    private Button filterButton;
    BitmapDescriptor oBitmap;
    BitmapDescriptor oBitmap2;

    @VisibleForTesting
    public GoogleMap mMap = null;

    @VisibleForTesting
    public RealtimeVehicleDataPollingService m_oRealtimeVehicleDataPollingService;



    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.ID_OverlayRight);
        layout.setVisibility(View.VISIBLE);

        TextView textViewRoute = (TextView)findViewById(R.id.ID_Route);
        TextView textViewService = (TextView)findViewById(R.id.ID_Service);
        TextView textViewTrip = (TextView)findViewById(R.id.ID_Trip);
        TextView textViewNextStop = (TextView)findViewById(R.id.ID_NextStop);
        TextView textViewOccupancy = (TextView)findViewById(R.id.ID_Occupancy);
        TextView textViewCongestion = (TextView)findViewById(R.id.ID_Congestion);
        TextView textViewBusModel = (TextView)findViewById(R.id.ID_BusModel);
        TextView textViewAirConditioned = (TextView)findViewById(R.id.ID_AirConditioned);
        TextView textViewHasWifi = (TextView)findViewById(R.id.ID_HasWifi);
        TextView textViewHasWheelChair = (TextView)findViewById(R.id.ID_HasWheelChair);

        GtfsContainer.GtfsEntity oEntity = disMarkerToGtfsEntity.get(marker);
        if (oEntity == null)
        {
            Log.e("ERROR!","No entity for this marker!");
            return false;
        }

        ContainerBusNetwork oNetwork = ContainerBusNetwork.get_instance();
        String routeId = oEntity.getRoute().getRoute_id();
        String tripId = oEntity.getPosition().getTripId();
        String stopId = oEntity.getPosition().getStopId();

        textViewRoute.setText(oEntity.getRoute().getService_number());
        textViewService.setText(oNetwork.oContainerRoutes.oEntities.get(routeId).getRouteLongName());
        textViewTrip.setText(oNetwork.oContainerTrips.oEntities.get(tripId).getHeadsign());


        //textViewNextStop.setText(oNetwork.oContainerStops.oEntities.get(stopId).getStopName());

        textViewOccupancy.setText(oEntity.getVehicleStatus().getOccupancy());
        textViewCongestion.setText(oEntity.getVehicleStatus().getCongestionLevel());

        if (oEntity.getVehicleStatus().getWheelchairAccessible())
            textViewHasWheelChair.setText("Yes");
        else
            textViewHasWheelChair.setText("No");

        if (oEntity.getVehicleStatus().getHasWifi())
            textViewHasWifi.setText("Yes");
        else
            textViewHasWifi.setText("No");

        if (oEntity.getVehicleStatus().getHasAirconditioning())
            textViewAirConditioned.setText("Yes");
        else
            textViewAirConditioned.setText("No");

        textViewBusModel.setText(oEntity.getVehicleStatus().getBusModel());

        return true;
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RealtimeVehicleDataPollingService.LocalBinder binder = (RealtimeVehicleDataPollingService.LocalBinder) service;
            Service aService = binder.getService();
            if (aService instanceof RealtimeVehicleDataPollingService) {
                m_oRealtimeVehicleDataPollingService = binder.getService();
                mBound = true;
                try {
                    String url = "https://api.transport.nsw.gov.au/v1/gtfs/vehiclepos/buses";
                    String apikey = "ugYfGEv2tnobnynQDOMSUm00rINngpYQzVnt";


                    URL oUrl = new URL(url);
                    m_oRealtimeVehicleDataPollingService.initialize(oUrl, url, apikey, refreshrate);
                } catch (MalformedURLException e) {
                    System.out.println("Malformed url");
                    System.exit(1);
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        disMarkerToGtfsEntity = new HashMap<Marker,GtfsContainer.GtfsEntity>();
        ServiceIdToMarkerId = new HashMap<String, ArrayList<Marker>>();
        mBound = false;

        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("arrow", "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 35, 35, false );
        oBitmap = BitmapDescriptorFactory.fromBitmap(resizedBitmap);

        Bitmap imageBitmap2 = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier("arrow_red", "drawable", getPackageName()));
        Bitmap resizedBitmap2 = Bitmap.createScaledBitmap(imageBitmap2, 45, 45, false );
        oBitmap2 = BitmapDescriptorFactory.fromBitmap(resizedBitmap2);

        filterButton = (Button) findViewById(R.id.ID_FilterButton);
        filterButton.setOnClickListener(this);

        EditText text = (EditText) findViewById(R.id.ID_EditableText);
        text.setFocusable(true);

    }

    @Override
    public void onClick(View v) {
        if (v == filterButton) {
            if (lastservice != null)
            {
                ArrayList<Marker> oldMarkers = ServiceIdToMarkerId.get(lastservice);
                if (oldMarkers!=null) {
                    for (Marker marker : oldMarkers) {
                        marker.setIcon(oBitmap);
                    }
                }
            }

            EditText text = (EditText) findViewById(R.id.ID_EditableText);
            lastservice = text.getText().toString();

            ArrayList<Marker> markers = ServiceIdToMarkerId.get(lastservice);
            if (markers != null) {
                for (Marker marker : markers) {
                    marker.setIcon(oBitmap2);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();




        // Bind to LocalService
        realtimeVehicleDataPollingServiceIntent = new Intent(this, RealtimeVehicleDataPollingService.class);
        bindService(realtimeVehicleDataPollingServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MainActivity", "Broadcast received!");
            FeedMessage oFeedMessage = m_oRealtimeVehicleDataPollingService.getFeedMessage();

            //if (mMap != null)
            //  mMap.clear();



            for (FeedEntity oFeedEntity : oFeedMessage.getEntityList()) {
                GtfsContainer.GtfsEntity oEntity = oGtfsContainer.new GtfsEntity();
                GtfsContainer.GtfsPosition oPosition = oGtfsContainer.new GtfsPosition();
                GtfsContainer.GtfsRoute oRoute = oGtfsContainer.new GtfsRoute();
                GtfsContainer.GtfsVehicleStatus oVehicleStatus = oGtfsContainer.new GtfsVehicleStatus();

                oEntity.setPosition(oPosition);
                oEntity.setVehicleStatus(oVehicleStatus);
                oEntity.setRoute(oRoute);

                // set location, bearing, speed
                oPosition.setLatitude(oFeedEntity.getVehicle().getPosition().getLatitude());
                oPosition.setLongitude(oFeedEntity.getVehicle().getPosition().getLongitude());
                oPosition.setBearing(oFeedEntity.getVehicle().getPosition().getBearing());
                oPosition.setTripId(oFeedEntity.getVehicle().getTrip().getTripId());

                ContainerBusNetwork oContainerBusNetwork = ContainerBusNetwork.get_instance();
                String tripId = oFeedEntity.getVehicle().getTrip().getTripId();

/*
                ContainerBusNetwork.EntityStopTimesPerTrip oEntityStopTimesForCurrentTrip = oContainerBusNetwork.oContainerStopTimes.oEntities.get(tripId);
                int size = oEntityStopTimesForCurrentTrip.oStoptimesOfTripSegment.size();
                if (size == 0)
                {

                }
                else if (size == 1)
                {
                    oPosition.setStopId ( oEntityStopTimesForCurrentTrip.oStoptimesOfTripSegment.get(0).getStopId());
                }
                else {

                    Time currentTime = new Time(new java.util.Date().getTime());
                    for (int i = 1; i < size; i++) {
                        Time stopTime = oEntityStopTimesForCurrentTrip.oStoptimesOfTripSegment.get(i).getArrivalTime();
                        if ( currentTime.after(stopTime))
                        {
                            oPosition.setStopId ( oEntityStopTimesForCurrentTrip.oStoptimesOfTripSegment.get(i).getStopId());
                            break;
                        }
                    }
                }
*/
                // too slow
                oPosition.setStopId ("0");


                GtfsFeedHelper oGtfsFeedHelper = GtfsFeedHelper.get_instance();
                GtfsFeedHelper.EntityVehicleVehicleId  oEntityVehicleId = oGtfsFeedHelper.new EntityVehicleVehicleId();
                GtfsFeedHelper.EntityVehicleTripId oEntityVehicleTripId = oGtfsFeedHelper.new EntityVehicleTripId();

                oEntityVehicleId.parseEntityVehicleVehicleId(oFeedEntity.getId());
                oEntityVehicleTripId.parseEntityVehicleTripId(oFeedEntity.getVehicle().getTrip().getRouteId());
                oRoute.setService_number(oEntityVehicleTripId.getRouteId());
                oRoute.setRoute_id(oFeedEntity.getVehicle().getTrip().getRouteId());


                ByteString myfield_for_debug = oFeedEntity.getVehicle().getVehicle().getUnknownFields().getField(1999).getLengthDelimitedList().get(0);

                Boolean isAirConditioned = false;
                Boolean isWheelChairAccessible = false;
                String busModel = "";
                Boolean isPerformingPriorTrip = false;
                Boolean isChristmasBus = false;
                Boolean hasWifi = false;

                for (int i = 0 ; i < myfield_for_debug.size() ;)
                {
                    switch (myfield_for_debug.byteAt(i))
                    {
                        case 8:
                            isAirConditioned = myfield_for_debug.byteAt(i+1) == 1;
                            i+=2;
                            break;
                        case 16:
                            isWheelChairAccessible = myfield_for_debug.byteAt(i+1) == 1;
                            i+=2;
                            break;
                        case 26:
                            int strlength = myfield_for_debug.byteAt(i+1);
                            ByteString busModelByte = myfield_for_debug.substring(i+2,i+2+strlength);
                            busModel = busModelByte.toStringUtf8();
                            i+= (2+strlength);
                            break;
                        case 32:
                            isPerformingPriorTrip = myfield_for_debug.byteAt(i+1) == 1;
                            i+=2;
                            break;
                        case 40:
                            int maskfield = myfield_for_debug.byteAt(i+1);
                            hasWifi = (maskfield & 0b01) != 0;
                            isChristmasBus = (maskfield & 0b10) != 0;
                            i+=2;
                            break;
                        default:
                            i+=1;
                            break;
                    }
                }

                oVehicleStatus.setBusModel(busModel);
                oVehicleStatus.setHasWifi(hasWifi);
                oVehicleStatus.setHasAirconditioning(isAirConditioned);
                oVehicleStatus.setWheelchairAccessible(isWheelChairAccessible);
                oVehicleStatus.setOccupancy(oFeedEntity.getVehicle().getOccupancyStatus().toString());
                oVehicleStatus.setCongestionLevel(oFeedEntity.getVehicle().getCongestionLevel().toString());

                String key = "";
                key = oFeedEntity.getVehicle().getTrip().getTripId();


                if (mMap == null)
                    continue;



                try {
                    GtfsContainer.GtfsEntity oOldEntity;
                    oOldEntity = oGtfsContainer.getEntity(oFeedEntity.getId());
                    Marker oMarker = oOldEntity.marker;
                    LatLng newpos = new LatLng(oPosition.getLatitude(), oPosition.getLongitude());
                    oMarker.setRotation(oPosition.getBearing());
                    oMarker.setPosition(newpos);
                }
                catch (NullPointerException e)
                {
                    String serviceNumber = oRoute.getService_number();
                    LatLng newpos = new LatLng(oPosition.getLatitude(), oPosition.getLongitude());
                    Marker oMarker = mMap.addMarker(new MarkerOptions().position(newpos).title(serviceNumber));
                    oMarker.setRotation(oPosition.getBearing());

                    oMarker.setIcon(oBitmap);

                    oEntity.marker = oMarker;
                    oGtfsContainer.addEntity(oFeedEntity.getId(), oEntity);

                    disMarkerToGtfsEntity.put(oMarker,oEntity);

                    if (ServiceIdToMarkerId.get(serviceNumber) == null)
                        ServiceIdToMarkerId.put(serviceNumber, new ArrayList<Marker>());

                    if (!ServiceIdToMarkerId.get(serviceNumber).contains(oMarker))
                        ServiceIdToMarkerId.get(serviceNumber).add(oMarker);
                }
            }

        }

    };

    @Override
    public void onResume() {
        super.onResume();
        startService(realtimeVehicleDataPollingServiceIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(RealtimeVehicleDataPollingService.BROADCAST_ACTION_NEWDATA));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(realtimeVehicleDataPollingServiceIntent);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LinearLayout layout = findViewById(R.id.ID_OverlayRight);
                layout.setVisibility(View.INVISIBLE);
            }
        });


        LatLng sydney = new LatLng(-34, 151);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
        mMap.setOnMarkerClickListener(this);

        //mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

    }
}

