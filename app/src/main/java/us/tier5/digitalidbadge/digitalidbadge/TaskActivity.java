package us.tier5.digitalidbadge.digitalidbadge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.RegisterUser;
import HelperClasses.UserConstants;

public class TaskActivity extends AppCompatActivity implements AsyncResponse.Response{

    //view variables
    TextView tvTaskText;
    Button btnYes;
    Button btnNo;
    Button btnAlreadyAnswered;

    //server variables
    RegisterUser registerUser = new RegisterUser("POST");
    HashMap<String, String> data = new HashMap<>();
    String route = "/api/v1/beacons/log";

    //page variable
    String beaconUUID;
    String beaconMajor;
    String beaconMinor;
    String Id;

    JSONObject currentBeacon;

    //loading variables
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        registerUser.delegate = this;

        tvTaskText = (TextView) findViewById(R.id.tvTaskText);
        btnYes = (Button) findViewById(R.id.btnYes);
        btnNo = (Button) findViewById(R.id.btnNo);
        btnAlreadyAnswered = (Button) findViewById(R.id.btnAlreadyAnswered);

        currentBeacon = UserConstants.lastBeaconInteractionDetail;

        SharedPreferences prefs = getSharedPreferences("Digital-Employee", Context.MODE_PRIVATE);

        Id = prefs.getString("ID","");

        if(Id.equals(""))
        {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }


        try
        {
            tvTaskText.setText(currentBeacon.getString("text"));
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","Error in getting becondetails in task page "+e.toString());
            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show();
        }



        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerAction("1");

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAction("0");

            }
        });

        btnAlreadyAnswered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAction("2");
            }
        });
    }

    public void registerAction(String choice)
    {
        try
        {
            data.put("action",choice);
            data.put("uuid",currentBeacon.getString("uuid"));
            data.put("major",currentBeacon.getString("major"));
            data.put("minor",currentBeacon.getString("minor"));
            data.put("employee_id",Id);


            loading = ProgressDialog.show(this, "","Please wait", true, false);
            registerUser.register(data,route);



        }
        catch(Exception e)
        {
            Toast.makeText(TaskActivity.this, "Something went wrong "+e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    /*private int backButtonCount = 0;
    @Override
    public void onBackPressed() {
        *//*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {*//*
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
        *//*}*//*
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UserConstants.braconCallIsGoingON=false;
    }

    @Override
    public void processFinish(String output) {
        loading.dismiss();
        //Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        try {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                UserConstants.braconCallIsGoingON=false;
                /*Intent intent = new Intent(TaskActivity.this,ShowBadge.class);
                startActivity(intent);*/
                super.onBackPressed();
            }
            else
            {
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.i("kingsukmajumder","Error in question answer response "+e.toString());
        }


    }
}
