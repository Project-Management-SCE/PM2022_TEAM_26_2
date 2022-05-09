package com.example.ymdbanking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ymdbanking.adapters.AccountAdapter;
import com.example.ymdbanking.adapters.ProfileAdapter;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowUsersActivity extends AppCompatActivity {

    private ListView usersList;
    private int selectedAccountIndex;
    private Admin admin;
    private ArrayList<Customer>customers;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        usersList = findViewById(R.id.lst_users);
        
        
        selectedAccountIndex = 0;
        
        sessionManager = new SessionManager(ShowUsersActivity.this,SessionManager.USER_SESSION);
        admin = sessionManager.getAdminObjFromSession();

        customers = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for(DataSnapshot ds : task.getResult().getChildren())
                    if(ds.child("typeID").getValue(int.class)== 3)
                        customers.add(ds.getValue(Customer.class));

                ProfileAdapter adapter = new ProfileAdapter(ShowUsersActivity.this, R.layout.lst_profile_row, customers);
                usersList.setAdapter(adapter);

                usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        selectedAccountIndex = i;
//                        viewUser();

                    }
                });
            }
        });


//
//        lstAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                selectedAccountIndex = i;
//                viewAccount();
//            }
//        });




    }

    private void viewUser() {

        sessionManager = new SessionManager(ShowUsersActivity.this,"AccountView");
        startActivity(new Intent(ShowUsersActivity.this,AccountsOverViewActivity.class));
    }
}