package com.example.ymdbanking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ymdbanking.db.ApplicationDB;
import com.example.ymdbanking.model.Customer;
import com.example.ymdbanking.model.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;

    TextView creatNewAccount;
    TextView alreadyHaveAccount;
    TextInputEditText inputEmail, inputPass,inputId;
    Button btnLogin, signupBtn,btnForgot;
    CheckBox rememberMe;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+]";
    ProgressDialog progressDialog;
//
//    FirebaseAuth mAuth;
//    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hooks
        signupBtn = findViewById(R.id.login_signinbtn);
        btnLogin = findViewById(R.id.login_btn);
        inputEmail = findViewById(R.id.login_email);
        inputPass = findViewById(R.id.login_pass);
        inputId = findViewById(R.id.login_id);
        btnForgot = findViewById(R.id.forgot_btn);
        rememberMe = findViewById(R.id.login_remember);

        //Variables
        progressDialog = new ProgressDialog(this);

        //check if id,email,password is already saved in Shared Preferences or not
        SessionManager sessionManager = new SessionManager(LoginActivity.this,SessionManager.REMEMBER_ME_SESSION);
        if(sessionManager.checkRememberMeLogin()) {

            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            inputEmail.setText(rememberMeDetails.get(SessionManager.KEY_SESSION_EMAIL));
            inputId.setText(rememberMeDetails.get(SessionManager.KEY_SESSION_ID));
            inputPass.setText(rememberMeDetails.get(SessionManager.KEY_SESSION_PASSWORD));

        }


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = inputEmail.getText().toString().trim();
                String pass = inputPass.getText().toString().trim();
                String id_login = inputId.getText().toString().trim();

                if(rememberMe.isChecked()) {
                    SessionManager sessionManager = new SessionManager(LoginActivity.this,SessionManager.REMEMBER_ME_SESSION);
                    sessionManager.createRememberMeSession(mail,id_login,pass);
                }
                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("id").equalTo(id_login);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child(id_login).exists())
                        {
                            String systemPassword = snapshot.child(id_login).child("password").getValue(String.class);
                            if(systemPassword.equals(pass))
                            {
                                inputPass.setError(null);

                                //Get users data from firebase DB
                                String fullName = snapshot.child(id_login).child("fullName").getValue(String.class);
                                String id = snapshot.child(id_login).child("id").getValue(String.class);
                                String username = snapshot.child(id_login).child("username").getValue(String.class);
                                String email = snapshot.child(id_login).child("email").getValue(String.class);
                                String password = snapshot.child(id_login).child("password").getValue(String.class);
                                String phone = snapshot.child(id_login).child("phone").getValue(String.class);

                                //Create a User Session
                                SessionManager sessionManager = new SessionManager(LoginActivity.this,SessionManager.USER_SESSION);
                                sessionManager.createLoginSession(fullName,id,username,email,password,phone);

//                                Account account = snapshot.child(id_login).child("accounts").getValue(Account.class);
                                Customer customer = new Customer(email,fullName,id,password,phone,username);
//                                ApplicationDB applicationDB = new ApplicationDB(getApplicationContext());
//                                customer.setAccounts(applicationDB.getAccountsFromCurrentCustomer(customer.getId()));
//                                customer.getAccounts(applicationDB.getTransactionsFromCurrentAccount());
                                sessionManager.saveCustomerObjForSession(customer);

                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            }
                            else
                            {
                                inputPass.setError("Password does not match!");
                                Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                            Toast.makeText(LoginActivity.this, "No such users exist!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(),ForgotPassActivity.class));
            }
        });


    }

}