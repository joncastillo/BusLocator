//Aetos Dios

package org.joncastillo.buslocator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    @Override
    protected void onStart() {
        super.onStart();
        BusInitialisationTask oBusInitialisationTask = new BusInitialisationTask(this);
        BusInitialisationTask.TaskParams oTaskParams = oBusInitialisationTask.new TaskParams();
        oTaskParams.am = getAssets();
        oBusInitialisationTask.execute(oTaskParams);

    }

}
