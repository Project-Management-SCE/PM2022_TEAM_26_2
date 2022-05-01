package com.example.ymdbanking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.se.omapi.Session;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    TextView disp_username,disp_email,disp_phone;
    HashMap<String,String> userDetails;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hooks
        disp_email = findViewById(R.id.profile_email);
        disp_phone = findViewById(R.id.profile_phone);
        disp_username = findViewById(R.id.profile_user_name);
        backBtn = findViewById(R.id.profile_backBtn);



        //User Session
        SessionManager sessionManager = new SessionManager(UserProfileActivity.this,SessionManager.USER_SESSION);
        userDetails = sessionManager.getUserDetailFromSession();

        disp_username.setText(userDetails.get(SessionManager.KEY_USERNAME));
        disp_email.setText(userDetails.get(SessionManager.KEY_EMAIL));
        disp_phone.setText(userDetails.get(SessionManager.KEY_PHONE));


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}