package com.asyprod.thesparksfoundation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePage extends AppCompatActivity {

    TextView username, email;
    CircleImageView userimage;
    Button logout;
    String platform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //To make notification bar transparent
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //To make and start the background gradient animation
        RelativeLayout rLayout = findViewById(R.id.rl1);
        rLayout.setBackgroundResource(R.drawable.gradient_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) rLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        username = findViewById(R.id.username);
        email = findViewById(R.id.useremail);
        userimage = findViewById(R.id.profile_pic);
        logout = findViewById(R.id.logout);

        platform = getIntent().getStringExtra("platform");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                if(platform=="Twitter"){
                    Toast.makeText(HomePage.this, "You Logged Out from Twitter!", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        String userName = getIntent().getStringExtra("userName");
        String userEmail = getIntent().getStringExtra("userEmail");
        String URL = getIntent().getStringExtra("imageURL");

        username.setText(userName);
        email.setText(userEmail);
        Glide.with(HomePage.this).load(URL).into(userimage);
    }
    //Disabling the back button
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Toast.makeText(this, "Logout to go back!", Toast.LENGTH_LONG).show();
    }
}