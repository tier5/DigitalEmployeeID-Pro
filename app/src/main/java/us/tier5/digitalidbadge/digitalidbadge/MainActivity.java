package us.tier5.digitalidbadge.digitalidbadge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import HelperClasses.AsyncResponse;
import HelperClasses.RegisterUser;
import HelperClasses.UserConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse.Response{

    //view variables
    EditText id;
    Button showBadge;


    //server variables
    RegisterUser registerUser = new RegisterUser("POST");
    HashMap<String, String> data = new HashMap<>();
    String route = "api/v1/employee/showidnew";

    //loading variables
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerUser.delegate=this;

        id = (EditText) findViewById(R.id.id);



        showBadge = (Button) findViewById(R.id.showBadge);
        showBadge.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(!id.getText().toString().equals(""))
        {
            loading = ProgressDialog.show(this, "","Please wait", true, false);
            data.put("id",id.getText().toString());
            registerUser.register(data,route);


        }
        else
        {
            Toast.makeText(this, "Please Enter an ID First.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processFinish(String output) {
        loading.dismiss();
        Log.i("kingsukmajumder",output);

        try {
            JSONObject jsonObject = new JSONObject(output);
            if(jsonObject.getBoolean("status"))
            {
                String imagename = jsonObject.getString("response");
                String imageUrl = UserConstants.BASE_URL+UserConstants.IMAGE_FOLDER_MID+imagename;
                Log.i("kingsukmajumder","image url "+imageUrl);
                UserConstants.ImageUrl = imageUrl;
                Toast.makeText(this, "Showing Badge", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("Digital-Employee", MODE_PRIVATE).edit();
                editor.putString("ID",id.getText().toString());
                editor.putString("BadgeUrl", imageUrl);

                if(editor.commit())
                {
                    Intent intent = new Intent(MainActivity.this,ShowBadge.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, "Some error occurred!", Toast.LENGTH_SHORT).show();
                }


            }
            else
            {
                Toast.makeText(this, jsonObject.getString("response"), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.i("kingsukmajumder","error in id response "+e.toString());
        }
    }
}
