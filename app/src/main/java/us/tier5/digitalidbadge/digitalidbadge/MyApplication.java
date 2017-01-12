package us.tier5.digitalidbadge.digitalidbadge;

/**
 * Created by Maya on 12/22/2016.
 */

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

public class MyApplication extends Application /*implements AsyncResponse.Response*/{

    /*//beacon variables
    private BeaconManager beaconManager;
    private Region region;

    //server variables
    RegisterUser registerUser = new RegisterUser("POST");
    HashMap<String, String> data = new HashMap<>();
    String route = "api/v1/beacons/get";*/

    @Override
    public void onCreate() {
        super.onCreate();
        EstimoteSDK.initialize(getApplicationContext(), "tier5-s-digital-employee-i-f55", "9552bc9060c81203e9a28f5f18723778");
        /*registerUser.delegate = this;

        // this is were we left off:
        beaconManager = new BeaconManager(getApplicationContext());
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {

                    if(!UserConstants.braconCallIsGoingON)
                    {
                        UserConstants.braconCallIsGoingON = true;
                        Beacon nearestBeacon = list.get(0);
                        Double beaconDistance = Utils.computeAccuracy(nearestBeacon);
                        Log.i("kingsukmajumder",nearestBeacon.toString());
                        data.put("uuid",nearestBeacon.getProximityUUID().toString());
                        data.put("major",String.valueOf(nearestBeacon.getMajor()));
                        data.put("minor",String.valueOf(nearestBeacon.getMinor()));
                        data.put("range",beaconDistance.toString());

                        Log.i("kingsukmajumder",data.toString());

                        registerUser.register(data,route);
                        Toast.makeText(MyApplication.this, data.toString(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                    }



                }
            }
        });*/

    }


    /*@Override
    public void processFinish(String output) {
        UserConstants.braconCallIsGoingON=false;
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
    }*/
}