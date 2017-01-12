package HelperClasses;

import android.os.AsyncTask;

import java.util.HashMap;

/**
 * Created by root on 26/8/16.
 */
public class RegisterUser3 {
    String method;
    public AsyncResponse3.Response3 delegate = null;

    public RegisterUser3(String method)
    {
        this.method = method;
    }


    public void register(final HashMap<String, String> data, String route) {
        final String FEED_URL = UserConstants.BASE_URL+route;
        class RegisterUserData extends AsyncTask<String, Void, String> {

            ConnectToServer ruc = new ConnectToServer(method);


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                delegate.processFinish3(s);
            }

            @Override
            protected String doInBackground(String... params) {

                String result = ruc.sendPostRequest(FEED_URL,data);

                return  result;
            }
        }


        RegisterUserData ru = new RegisterUserData();

        ru.execute();

    }
}
