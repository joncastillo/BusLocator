package org.joncastillo.buslocator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.transit.realtime.TfnswGtfsRealtimeProtoTxt.*;
import com.google.transit.realtime.TfnswGtfsRealtimeProtoTxt;

public class RealtimeVehicleDataPollingService extends Service {

    public static final String BROADCAST_ACTION_NEWDATA = "org.joncastillo.littleowl.RealtimeVehicleDataPollingService.broadcast.newData";
    private final Intent m_intent_broadcast_newdata = new Intent(BROADCAST_ACTION_NEWDATA);
    private final Handler handler = new Handler();
    private final IBinder mBinder = (IBinder) new LocalBinder();

    private String m_url = null;
    private String m_apikey = null;
    private Integer m_refreshRate = null;
    private FeedMessage m_feed = null;

    /* injectable objects for unit testing */
    private URL m_oUrl;

    /************************************************************/

    public RealtimeVehicleDataPollingService() {
    }

    public void initialize(URL oUrl, String url, String apikey, Integer refreshRate) {
        m_oUrl = oUrl;

        m_url = url;
        m_apikey = apikey;
        m_refreshRate = refreshRate;
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            HttpURLConnection oHttpURLConnection = null;

            if (m_url == null || m_apikey == null || m_refreshRate == null || m_oUrl == null) {
                // do nothing if uninitialized.
                Log.d("RealtimeVehicleDataPollingService", "uninitialized service.");
                handler.postDelayed(this, 1000);
            } else {
                try {
                    oHttpURLConnection = (HttpURLConnection) m_oUrl.openConnection();

                    oHttpURLConnection.setRequestMethod("GET");
                    oHttpURLConnection.setRequestProperty("Authorization", "apikey " + m_apikey);
                    oHttpURLConnection.setDoOutput(false);

                    oHttpURLConnection.setConnectTimeout(5000);
                    oHttpURLConnection.setReadTimeout(5000);
                    oHttpURLConnection.connect();

                    InputStream inputStream;
                    int status = oHttpURLConnection.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK)
                        inputStream = oHttpURLConnection.getErrorStream();
                    else {
                        Log.d("RealtimeVehicleDataPollingService", "transport.nsw.gov.au server Response: " + Integer.toString(status));
                        m_feed = FeedMessage.parseFrom(oHttpURLConnection.getInputStream());
                        sendBroadcast(m_intent_broadcast_newdata);
                    }
                    oHttpURLConnection.disconnect();
                } catch (IOException e) {
                    Log.e("RealtimeVehicleDataPollingService", Log.getStackTraceString(e));
                    Log.d("RealtimeVehicleDataPollingService", "Excepted");
                } finally {
                    if (oHttpURLConnection != null) {
                        oHttpURLConnection.disconnect();
                    }
                }
                handler.postDelayed(this, m_refreshRate);
            }

        }
    };

    /****************************** Interface Overrides ******************************/

    public class LocalBinder extends Binder {
        RealtimeVehicleDataPollingService getService() {
            return RealtimeVehicleDataPollingService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //m_url = "https://api.transport.nsw.gov.au/v1/gtfs/vehiclepos/buses";
        //m_apikey = "ugYfGEv2tnobnynQDOMSUm00rINngpYQzVnt";
        //m_refreshRate = 3000; // 3 seconds
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 0);
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }

    /****************************** Getters and Setters ******************************/

    /**
     * Getter for Realtime Vehicle Data's url
     *
     * @return url for Realtime Vehicle Data's restful api.
     */
    public String get_url() {
        return m_url;
    }

    /**
     * Setter for Realtime Vehicle Data's url
     *
     * @param m_url url  url for Realtime Vehicle Data's restful api.
     */
    public void set_url(String m_url) {
        this.m_url = m_url;
    }

    /**
     * Getter for apikey
     *
     * @return the api key from opendata.transport.nsw.gov.au
     */
    public String get_apikey() {
        return m_apikey;
    }

    /**
     * Setter for apikey
     *
     * @param m_apikey apikey from opendata.transport.nsw.gov.au
     */
    public void set_apikey(String m_apikey) {
        this.m_apikey = m_apikey;
    }

    /**
     * Getter for refreshRate
     *
     * @return the refresh rate
     */
    public Integer get_refreshRate() {
        return m_refreshRate;
    }

    /**
     * Setter for refreshRate
     *
     * @param m_refreshRate refresh rate in milli seconds.
     */
    public void set_refreshRate(Integer m_refreshRate) {
        this.m_refreshRate = m_refreshRate;
    }

    /**
     * Getter for the latest realtime vehicle data feed in google protobuf format
     *
     * @return the feed
     */
    public TfnswGtfsRealtimeProtoTxt.FeedMessage getFeedMessage() {
        return m_feed;
    }
}
