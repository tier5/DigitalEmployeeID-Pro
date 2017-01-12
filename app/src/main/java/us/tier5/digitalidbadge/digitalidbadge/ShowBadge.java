package us.tier5.digitalidbadge.digitalidbadge;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import HelperClasses.AsyncResponse;
import HelperClasses.AsyncResponse2;
import HelperClasses.AsyncResponse3;
import HelperClasses.RegisterUser;
import HelperClasses.RegisterUser2;
import HelperClasses.RegisterUser3;
import HelperClasses.UserConstants;

public class ShowBadge extends AppCompatActivity implements AsyncResponse.Response, AsyncResponse2.Response2, AsyncResponse3.Response3{

    //view variables
    ImageView id_image;
    TextView tvLoadingText;
    TextView textView;
    TextView textView1;

    //server variables
    RegisterUser registerUser = new RegisterUser("POST");
    HashMap<String, String> data = new HashMap<>();
    String route = "api/v1/employee/showidnew";

    //loading variables
    ProgressDialog loadingPicture;
    ProgressBar progressBar;
    ProgressDialog loading;

    //page variable
    String badgeUrl;
    String Id;
    Boolean appIsInForground = true;

    RegisterUser2 registerUser2 = new RegisterUser2("POST");
    HashMap<String,String> data2 = new HashMap<>();
    String routeBeaconValidation = "api/v1/beacons/get";

    RegisterUser3 registerUser3 = new RegisterUser3("POST");
    HashMap<String,String> data3 = new HashMap<>();
    String routeEnterUserLog = "/api/v1/beacons/log";

    HashMap<String,String> dataUserInteraction = new HashMap<>();

    //beacon variables
    private BeaconManager beaconManager;
    private Region region;

    //dialog popup view
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_badge);

        registerUser.delegate=this;
        registerUser2.delegate = this;
        registerUser3.delegate = this;

        id_image = (ImageView) findViewById(R.id.id_image);
        tvLoadingText = (TextView) findViewById(R.id.tvLoadingText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);



        SharedPreferences prefs = getSharedPreferences("Digital-Employee", Context.MODE_PRIVATE);
        badgeUrl = prefs.getString("BadgeUrl", "");
        Id = prefs.getString("ID","");

        if(Id.equals(""))
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            downloadImage(badgeUrl);


            beaconImplement();
            data.put("id",Id);
            registerUser.register(data,route);
            //beaconImplement();
        }


        myDialog = new Dialog(ShowBadge.this);
        myDialog.getWindow();
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);



    }



    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        appIsInForground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        appIsInForground = false;
    }

    private int backButtonCount = 0;
    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {*/
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
        /*}*/
    }

    public void downloadImage(final String imageUrl)
    {
        //Toast.makeText(this, ""+imageUrl, Toast.LENGTH_SHORT).show();
        Log.i("kingsukmajumder",""+imageUrl);
        Glide.with(getApplicationContext()).load(imageUrl).into(id_image);
        /*AsyncTask asyncTask = new AsyncTask<Void, Void, Void>() {
            Bitmap bmp;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    InputStream in = new URL(imageUrl).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    //Toast.makeText(getContext(),"Some error occoured while loading images!",Toast.LENGTH_LONG).show();
                    Log.i("kingsukmajumder","error in loading images "+e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //loading.dismiss();
                if (bmp != null)

                    id_image.setImageBitmap(bmp);
                tvLoadingText.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();*/
    }

    @Override
    public void processFinish(String output) {

        Log.i("kingsukmajumder",output);
        //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();

        try {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                String imagename = jsonObject.getString("response");
                String imageUrl = UserConstants.BASE_URL+UserConstants.IMAGE_FOLDER+imagename;
                UserConstants.ImageUrl = imageUrl;

                //Glide.with(getApplicationContext()).load(imageUrl).into(id_image);

                SharedPreferences.Editor editor = getSharedPreferences("Digital-Employee", MODE_PRIVATE).edit();
                editor.putString("ID",Id);
                editor.putString("BadgeUrl", imageUrl);

                if(editor.commit())
                {
                    /*Intent intent = new Intent(MainActivity.this,ShowBadge.class);
                    startActivity(intent);*/
                    downloadImage(imageUrl);
                    //beaconImplement();
                    //Toast.makeText(this, "Showing Badge again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }


            }
            else
            {
                Toast.makeText(this, jsonObject.getString("response"), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("Digital-Employee", MODE_PRIVATE).edit();
                editor.putString("ID","");
                editor.putString("BadgeUrl", "");

                if(editor.commit())
                {
                    Intent intent = new Intent(this,MainActivity.class);
                    startActivity(intent);
                }

            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in id response "+e.toString());
        }

    }

    public void beaconImplement()
    {
        //beacon implementation
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
                        //Log.i("kingsukmajumder",nearestBeacon.toString());
                        data2.put("uuid",nearestBeacon.getProximityUUID().toString());
                        data2.put("major",String.valueOf(nearestBeacon.getMajor()));
                        data2.put("minor",String.valueOf(nearestBeacon.getMinor()));
                        data2.put("range",beaconDistance.toString());
                        data2.put("card_id",Id);

                        Log.i("kingsukmajumder",data2.toString());
                        textView1.setText(data2.toString());

                        registerUser2.register(data2,routeBeaconValidation);
                        //Toast.makeText(ShowBadge.this, data2.toString(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //Toast.makeText(ShowBadge.this, "One call is going on", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });


    }

    @Override
    public void processFinish2(String output) {

        //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        Log.i("kingsukmajumder","beacon finding response "+output);
        textView.setText(output);

        try
        {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                JSONObject beacon = new JSONObject(jsonObject.getString("beacon"));

                if(beacon.getInt("type")==1)
                {

                    String taskText = beacon.getString("text");
                    Log.i("kingsukmajumder","Task Activity needs to open");
                    UserConstants.lastBeaconInteractionDetail = beacon;
                    showNotification("Digital Employee ID","Please response to the task notification");
                    if(appIsInForground)
                    {
                        popupActivity(beacon);
                    }
                    else
                    {
                        Intent intent = new Intent(ShowBadge.this,TaskActivity.class);
                        startActivity(intent);
                    }
                    /*Intent intent = new Intent(ShowBadge.this,TaskActivity.class);
                    startActivity(intent);*/


                }
                else
                {
                    Log.i("kingsukmajumder","Log needs to be inserted");
                    data3.put("uuid",beacon.getString("uuid"));
                    data3.put("major",beacon.getString("major"));
                    data3.put("minor",beacon.getString("minor"));
                    data3.put("employee_id",Id);

                    Toast.makeText(this, "Posting log for log beacon", Toast.LENGTH_SHORT).show();
                    registerUser3.register(data3,routeEnterUserLog);
                }
            }
            else
            {
                Log.i("kingsukmajumder","Response is: "+output);
                //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
                UserConstants.braconCallIsGoingON=false;
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","Error in beacon response "+e.toString());
            UserConstants.braconCallIsGoingON=false;
        }
    }

    @Override
    public void processFinish3(String output) {

        Log.i("kingsukmajumder","Output after log insertion "+output);
        //textView.setText(output);
        //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        UserConstants.braconCallIsGoingON=false;
        try {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if(myDialog.isShowing())
                {

                    loading.dismiss();
                    myDialog.dismiss();
                }

            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                if(myDialog.isShowing())
                {
                    loading.dismiss();
                    myDialog.dismiss();
                }
            }
        } catch (JSONException e) {
            Log.i("kingsukmajumder","Error in log insertion response "+e.toString());
        }
    }

    public void popupActivity(final JSONObject beacon)
    {




        View inflatedLayout= getLayoutInflater().inflate(R.layout.activity_task, null, false);

        TextView tvTaskText = (TextView) inflatedLayout.findViewById(R.id.tvTaskText);
        try
        {
            tvTaskText.setText(beacon.getString("text"));
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","Error in getting becondetails in task page "+e.toString());
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show();
        }

        Button btnYes = (Button) inflatedLayout.findViewById(R.id.btnYes);
        Button btnNo = (Button) inflatedLayout.findViewById(R.id.btnNo);
        Button btnAlreadyAnswered = (Button) inflatedLayout.findViewById(R.id.btnAlreadyAnswered);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerAction("1",beacon);

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAction("0",beacon);

            }
        });

        btnAlreadyAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAction("2",beacon);
            }
        });

        myDialog.setContentView(inflatedLayout);

        myDialog.setCancelable(false);
        super.onResume();
        myDialog.show();
    }


    public void registerAction(String choice,JSONObject currentBeacon)
    {
        try
        {
            dataUserInteraction.put("action",choice);
            dataUserInteraction.put("uuid",currentBeacon.getString("uuid"));
            dataUserInteraction.put("major",currentBeacon.getString("major"));
            dataUserInteraction.put("minor",currentBeacon.getString("minor"));
            dataUserInteraction.put("employee_id",Id);


            loading = ProgressDialog.show(this, "","Please wait", true, false);
            registerUser3.register(dataUserInteraction,routeEnterUserLog);



        }
        catch(Exception e)
        {
            Toast.makeText(ShowBadge.this, "Something went wrong please relaunch the app and try again.", Toast.LENGTH_LONG).show();
            Log.i("kingsukmajumder","error in making interaction vairable from currentBeacon json object "+e.toString());
        }
    }

    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, ShowBadge.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

}
