package co.medicamecanica.rest.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.restlet.data.MediaType;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import co.medicamecanica.rest.R;
import co.medicamecanica.rest.RestClient;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RestClient.UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUrlView;
    private AutoCompleteTextView mLoginlView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private LoginResource resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();
        // Set up the login form.
        mUrlView = (AutoCompleteTextView) findViewById(R.id.url);
        mLoginlView = (AutoCompleteTextView) findViewById(R.id.login);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        //rest client conf

        RestClient.conf(getApplicationContext());
        // Initialize the resource proxy.

        String uri = "";

        mUrlView.setText(RestClient.getURL());
        mLoginlView.setText(RestClient.getLogin());
        //mPasswordView.setText();


        // Workaround for GAE servers to prevent chunk encoding


    }

    public ClientResource BuildClientResource(String uri) {
        ClientResource cr = new ClientResource(uri);
        cr.setRequestEntityBuffering(true);
        cr.accept(MediaType.APPLICATION_JSON);


        return cr;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            //if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  populateAutoComplete();
            // }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mLoginlView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String url = mUrlView.getText().toString();
        String login = mLoginlView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(url)) {
            mUrlView.setError(getString(R.string.error_field_required));
            focusView = mUrlView;
            cancel = true;
        }
        if (!URLUtil.isValidUrl(url)) {
            mUrlView.setError(getString(R.string.error_invalid_url));
            focusView = mUrlView;
            cancel = true;
        }
        if (TextUtils.isEmpty(login)) {
            mLoginlView.setError(getString(R.string.error_field_required));
            focusView = mLoginlView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            RestClient.store(RestClient.URL,url);
            RestClient.store(RestClient.LOGIN,login);
            // RestClient.store(RestClient.PASSWORD,url);
            mAuthTask = new RestClient.UserLoginTask(url, login, password,new RestClient.LoginListener(){

                @Override
                public void onPostExecute(Integer code) {
                    mAuthTask = null;
                    showProgress(false);
                    if (code == 200) {

                        finish();
                    }else  if (code == 403) {
                        mLoginlView.setError(getString(R.string.error_incorrect_login));
                        mLoginlView.requestFocus();
                        mPasswordView.setText("");

                    }else if (code==500){
                        mUrlView.setError(getString(R.string.error_incorrect_url));
                        mUrlView.requestFocus();

                    }
                }

                @Override
                public void onCancelled() {
                    mAuthTask = null;
                    showProgress(false);
                }
            });
            mAuthTask.execute();
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        private final String mUrl;
        private final String mLogin;
        private final String mPassword;

        UserLoginTask(String url, String login, String password) {
            mUrl = url;
            mLogin = login;
            mPassword = password;
        }


        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                // Representation r = cr.get(MediaType.APPLICATION_JSON);
                // Log.i("login",r.getText());
                ClientResource cr = RestClient.BuildClientResource(mUrl);

                RestClient.prepareLogin(cr,mLogin,mPassword);
                resource = cr.wrap(LoginResource.class);

                Login login = resource.login();
                RestClient.storeToken(login.getSuccess().getToken());
                Log.i("success", login.toString());
                return 200;
            } catch (ResourceException e) {
                Log.e("err", e.getResource().toString());
                RestClient.storeToken(null);
                String resp = e.getResponse().getEntityAsText();
                int code;
                try {
                    ResponseError responseError = new Gson().fromJson(resp, ResponseError.class);
                    code = responseError.getError().getCode();
                    Log.e("login", responseError.toString());
                } catch (JsonSyntaxException   ex) {
                    code = 500;
                }catch (NullPointerException ex){
                    code = 500;
                }
                e.printStackTrace();

                return code;
            }
            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(final Integer code) {
            mAuthTask = null;
            showProgress(false);
            if (code == 200) {

                finish();
            }else  if (code == 403) {
                mLoginlView.setError(getString(R.string.error_incorrect_login));
                mLoginlView.requestFocus();
                mPasswordView.setText("");

            }else if (code==500){
                mUrlView.setError(getString(R.string.error_incorrect_url));
                mUrlView.requestFocus();

            }


        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

