package org.joncastillo.buslocator;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnitRunner;
import android.support.test.runner.intercepting.SingleActivityFactory;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

import com.google.android.gms.maps.GoogleMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MapsActivityTest extends AndroidJUnitRunner{
    private GoogleMap oGoogleMapMock;
    private RealtimeVehicleDataPollingService oRealtimeVehicleDataPollingServiceMock;

    @Rule
    public final ActivityTestRule<MapsActivity> activityRule =
            new ActivityTestRule<>(new SingleActivityFactory<MapsActivity>(MapsActivity.class)
            {
                @Override
                protected MapsActivity create(Intent intent) {
                    MapsActivity testTarget = new MapsActivity();
                    oRealtimeVehicleDataPollingServiceMock = mock (RealtimeVehicleDataPollingService.class);
                    // I am able to mock the final class \O/
                    oGoogleMapMock = mock (GoogleMap.class);

                    testTarget.m_oRealtimeVehicleDataPollingService = oRealtimeVehicleDataPollingServiceMock;
                    testTarget.mMap = oGoogleMapMock;

                    return testTarget;
                }

            }, false, false);

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    // Calls to onMapReady initializes the default map location.
    @Test
    public void onMapReady_should_focus_sydney() {
        activityRule.launchActivity(null);
        activityRule.getActivity().onMapReady(oGoogleMapMock);
        verify(oGoogleMapMock, times(1)).animateCamera((com.google.android.gms.maps.CameraUpdate) any());
        activityRule.finishActivity();
    }

    @Test
    public void RealtimeVehicleDataPollingService_GetterSetter() {
        try {
            RealtimeVehicleDataPollingService oService = new RealtimeVehicleDataPollingService();

            oService.set_apikey("123");
            oService.set_url("http://www.google.com");
            oService.set_refreshRate(123);

            assertThat(oService.get_apikey(), is("123"));
            assertThat(oService.get_url(), is("http://www.google.com"));
            assertThat(oService.get_refreshRate(), is(123));

        } catch (Exception e) {

        }

    }

}
