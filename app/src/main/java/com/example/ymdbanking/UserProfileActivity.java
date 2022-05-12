package com.example.ymdbanking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.se.omapi.Session;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    TextView disp_username,disp_email,disp_phone;
    HashMap<String,String> userDetails;
    ImageView backBtn,changeProfileBtn;
    private CircleImageView profileImage;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private String myUri = "";


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
        profileImage = findViewById(R.id.profile_image);
        changeProfileBtn = findViewById(R.id.change_profile_btn);


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

        changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(openGallery);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageUri = data.getData();
        profileImage.setImageURI(imageUri);
    }
}