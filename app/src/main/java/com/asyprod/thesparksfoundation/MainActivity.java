package com.asyprod.thesparksfoundation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    LoginButton loginButton;
    TwitterLoginButton loginButtonTwitter;
    TextView privacyPolicy,terms;

    String twitterEmail;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);

        privacyPolicy = findViewById(R.id.privacyPolicy);
        terms = findViewById(R.id.terms);

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://login-3.flycricket.io/privacy.html"));
                startActivity(i);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://login-2.flycricket.io/terms.html"));
                startActivity(i);
            }
        });

        //To make notification bar transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //To make and start the background gradient animation
        RelativeLayout rLayout = findViewById(R.id.rl);
        rLayout.setBackgroundResource(R.drawable.gradient_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) rLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();


        loginButton = findViewById(R.id.login_button);
        loginButtonTwitter = (TwitterLoginButton) findViewById(R.id.login_button_twitter);

   //Creating callback for twitter login button
        loginButtonTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;

                twitterLoad(session);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(MainActivity.this, "Twitter Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

        /*===============================FACEBOOK===========================================*/
  //Creating & registering callback for facebook login button
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    //Using access token to check if user is either logged in or logged out
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                Toast.makeText(MainActivity.this, "You Logged Out from Facebook!", Toast.LENGTH_LONG).show();
            }
            else{
                fbLoadProfile(currentAccessToken);
            }
        }
    };

    //The main function which performs when user logs in by facebook
    public void fbLoadProfile(AccessToken newAT){
        GraphRequest request = GraphRequest.newMeRequest(newAT, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String userName = object.getString("name");
                    String userEmail;

                    if(object.has("email")){
                        userEmail = object.getString("email");
                    }
                    else{
                        userEmail = "No valid email available";
                    }
                    String URL = "https://graph.facebook.com/"+object.getString("id")+"/picture?type=normal";

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Intent intent = new Intent(MainActivity.this,HomePage.class);

                    intent.putExtra("userName",userName);
                    intent.putExtra("userEmail",userEmail);
                    intent.putExtra("imageURL",URL);
                    intent.putExtra("platform","Facebook");
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    /*================================TWITTER========================================================*/

    //The main function which performs when user logs in by twitter

    public void twitterLoad(TwitterSession session){

        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
                twitterEmail = result.data;
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                twitterEmail = "No valid email available";
            }
        });

        String userName = session.getUserName();

        String URL = "https://unavatar.now.sh/twitter/"+userName;

        Intent intent = new Intent(MainActivity.this,HomePage.class);

        intent.putExtra("userName",userName);
        intent.putExtra("userEmail",twitterEmail );
        intent.putExtra("imageURL",URL);
        intent.putExtra("platform","Twitter");
        startActivity(intent);
    }


    /*=============================================================================================*/

    //Overriding onActivityResult method for both facebook and twitter

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButtonTwitter.onActivityResult(requestCode,resultCode,data);
    }

}