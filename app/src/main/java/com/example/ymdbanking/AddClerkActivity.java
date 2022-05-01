package com.example.ymdbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddClerkActivity extends AppCompatActivity {

    private TextInputEditText inputUser,inputEmail,inputPassword,inputPhone;
    private Button addBtn;
    private FirebaseAuth mAuth;
    private String username;
    private String email;
    private String password;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clerk);

        //Hooks
        hook();


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createClerk();

            }
        });



    }

    private void createClerk() {

        setUsername(inputUser.getText().toString().trim());
        setEmail(inputEmail.getText().toString().trim());
        setPassword(inputPassword.getText().toString().trim());
        setPhone(inputPhone.getText().toString().trim());

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    storeNewClerkData();
                    Toast.makeText(AddClerkActivity.this,"Successfully Added Clerk",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(AddClerkActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void storeNewClerkData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Clerks");

        ClerkHelperClass addNewClerk = new ClerkHelperClass(username,email,password,phone);

        reference.child(username).setValue(addNewClerk).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private void hook() {

        inputUser = findViewById(R.id.clerk_username);
        inputEmail = findViewById(R.id.clerk_email);
        inputPassword = findViewById(R.id.clerk_password);
        inputPhone = findViewById(R.id.clerk_phone);

        addBtn = findViewById(R.id.add_btn);

        mAuth = FirebaseAuth.getInstance();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}