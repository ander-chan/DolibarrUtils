package co.medicamecanica.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import co.medicamecanica.rest.login.*;

public class RestClient {

    public static final String LOGIN = "SERVER_LOGIN";
    public static final String PASSWORD = "SERVER_PASSWORD";
    public static final String TOKEN = "SERVER_TOKEN";

    static SharedPreferences preferences;
    public static String URL = "SERVER_URL";
    //###################################
    //String text = app_preferences.getString("name", "default");
    public static ClientResource BuildClientResource(String uri) {
        store(URL, uri);
        ClientResource cr = new ClientResource(uri + "/api/index.php");
        cr.setRequestEntityBuffering(true);
        cr.accept(MediaType.APPLICATION_JSON);
        return cr;
    }

    public static String fomratDate(Date dt) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(dt);
    }

    public static void store(String name, String string) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, string);
        editor.commit();
    }

    public static void conf(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Engine.getInstance().getRegisteredConverters()
                .add(new JacksonConverter());
    }

    public static String getURL() {
        return preferences.getString(URL, "");
    }

    public static void prepareLogin(ClientResource cr, String login, String password) {

        //  String login = preferences.getString(LOGIN, "");
        //String pass = preferences.getString(PASSWORD, "");
        //ClientResource cr = BuildClientResource(mUrl)
        store(LOGIN, login);
        store(PASSWORD, password);
        cr.addSegment("login");
        cr.addQueryParameter("login", login);
        cr.addQueryParameter("password", password);


    }

    public static String getSucces() {
        return preferences.getString(URL, "");
    }

    public static void storeToken(String token) {
        store(TOKEN, token);
    }

    public static boolean setToken() {
        return preferences.getString(TOKEN, null) != null;
    }

    public static Series<Header> getHeader(ClientResource clientr) {
        ConcurrentMap<String, Object> attrs = clientr.getRequest().getAttributes();
        Series<Header> headers = (Series<Header>) attrs.get(HeaderConstants.ATTRIBUTE_HEADERS);
        if (headers == null) {
            headers = new Series<>(Header.class);
            Series<Header> prev = (Series<Header>) attrs.putIfAbsent(HeaderConstants.ATTRIBUTE_HEADERS, headers);
            if (prev != null) {
                headers = prev;
            }
        }
        return headers;
    }

    public static String getToken() {
        return preferences.getString(TOKEN, null);
    }

    public static String getLogin() {
        return preferences.getString(LOGIN, null);
    }

    public static String getStored(String key) {
        return preferences.getString(key, null);
    }

    public static void addToken(ClientResource cr, String token) {
        getHeader(cr).add("DOLAPIKEY", token);
    }

    public static String getPassword() {
        return preferences.getString(PASSWORD, null);
    }

    public static class ConsumeWSTask<Resource> extends AsyncTask<Resource, Void, Integer> {


        private ConsumeListener<Resource> mListener;
        private ClientResource cr;
        private Resource resource;
        Class<Resource> classResource;

        public ConsumeWSTask(ConsumeListener<Resource> mListener) {


            this.mListener = mListener;
            cr = RestClient.BuildClientResource(RestClient.getURL());

            RestClient.addToken(cr, RestClient.getToken());
        }

        @Override
        protected Integer doInBackground(Resource... r) {
            if (RestClient.getToken() == null)
                return 0;

            try {if(classResource==null){
                throw new RuntimeException("Wrap not defined");
            }
                return mListener.doInBackground(cr.wrap(classResource));

            } catch (ResourceException e) {

                e.printStackTrace();
                Log.d("get", e.getResource().toString());
                return e.getResponse().getStatus().getCode();
            }

        }

        public ConsumeWSTask addQueryParameter(String name, String value) {
            cr.addQueryParameter(name, value);
            return this;
        }

        public ConsumeWSTask addSegment(String segment) {
            cr.addSegment(segment);
            return this;
        }

        @Override
        protected void onPostExecute(Integer code) {
            mListener.onPostExecute(code);
        }

        public void wrap(Class<Resource> classResource) {
            this.classResource=classResource;
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public static class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        private final String mUrl;
        private final String mLogin;
        private final String mPassword;
        LoginListener loginListener;

        public UserLoginTask(String url, String login, String password, LoginListener loginListener) {
            this.mUrl = url;
            this.mLogin = login;
            this.mPassword = password;
            this.loginListener = loginListener;
        }


        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                // Representation r = cr.get(MediaType.APPLICATION_JSON);
                // Log.i("login",r.getText());
                if (mUrl == null || mLogin == null || mPassword == null)
                    return 0;
                ClientResource cr = RestClient.BuildClientResource(mUrl);

                RestClient.prepareLogin(cr, mLogin, mPassword);
                LoginResource resource = cr.wrap(LoginResource.class);

                Login login = resource.login();
                Log.i("success", login.toString());
                System.out.println(login);
                RestClient.storeToken(login.getSuccess().getToken());
                /// Log.i("success", login.toString());
                return 200;
            } catch (ResourceException e) {
                Log.e("err", e.getResource().toString());
                String resp = e.getResponse().getEntityAsText();
                int code;
                try {
                    ResponseError responseError = new Gson().fromJson(resp, ResponseError.class);
                    code = responseError.getError().getCode();
                    Log.e("login", responseError.toString());
                } catch (JsonSyntaxException ex) {
                    code = 500;
                } catch (NullPointerException ex) {
                    code = 500;
                }
                RestClient.storeToken(null);
                e.printStackTrace();

                return code;
            }
            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(final Integer code) {
            loginListener.onPostExecute(code);


        }

        @Override
        protected void onCancelled() {
            loginListener.onCancelled();

        }
    }

    public static interface LoginListener {
        public void onPostExecute(Integer code);

        void onCancelled();
    }
}
