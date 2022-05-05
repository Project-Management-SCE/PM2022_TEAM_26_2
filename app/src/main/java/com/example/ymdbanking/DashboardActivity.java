package com.example.ymdbanking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ymdbanking.db.ApplicationDB;
import com.example.ymdbanking.model.Customer;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final float END_SCALE = 0.7f;

    //Drawer menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    TextView disp_username,disp_phone;
    Button addB;

    HashMap<String,String> userDetails;

    //Dialogs
    private Dialog depositDialog;
    private EditText edtDepositAmount;
    private Button btnCancel;
    private Button btnSuccess;
    private Spinner accounts;
    private String accountName,depositAmount;
    private Customer customer;



    private String TAG = "DashboardActivity";

    private View.OnClickListener depositClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == btnCancel.getId())
            {
                depositDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == btnSuccess.getId())
            {
//                makeDeposit();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hooks
        menuIcon = findViewById(R.id.menu_icon);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.content);
        SessionManager sessionManager = new SessionManager(this,SessionManager.USER_SESSION);
        Gson gson = new Gson();
        customer = sessionManager.getCustomerObjFromSession();
//        ApplicationDB applicationDB = new ApplicationDB(this);
//        customer.setAccounts(applicationDB.getAccountsFromCurrentCustomer(customer.getId()));



        navigationDrawer();



    }

    //Navigation Drawer Functions
    private void navigationDrawer() {
        //Navigation Drawer

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                Log.i(TAG, "onDrawerOpened");

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

                Log.i(TAG, "onDrawerClosed");
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        disp_phone = findViewById(R.id.display_phone);

        //User Session Details
        SessionManager sessionManager = new SessionManager(DashboardActivity.this,SessionManager.USER_SESSION);
        userDetails = sessionManager.getUserDetailFromSession();
        String phone = userDetails.get(SessionManager.KEY_PHONE);

        if(!userDetails.get(SessionManager.KEY_ID).equals("1"))
        {
            //User's navigation drawer
            View headerView = navigationView.getHeaderView(0);
            disp_username = headerView.findViewById(R.id.menu_userName);
            disp_username.setText(userDetails.get(SessionManager.KEY_USERNAME));
            navigationView.getMenu().findItem(R.id.nav_add_clerks).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_users).setVisible(false);
            sessionManager.saveCustomerObjForSession(customer);
        }
        disp_phone.setText(phone);



        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1-END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });



    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.nav_add_clerks)
            startActivity(new Intent(getApplicationContext(),AddClerkActivity.class));
        else if(id == R.id.nav_profile)
            startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
        else if(id == R.id.nav_accounts)
            startActivity(new Intent(getApplicationContext(),AccountsOverViewActivity.class));
        else if(id == R.id.nav_deposit)
           displayDepositDialog();
        return true;
    }

    private void displayDepositDialog()
    {
        depositDialog = new Dialog(this);
        depositDialog.setContentView(R.layout.deposit_dialog);

        depositDialog.setCanceledOnTouchOutside(true);
        depositDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(DashboardActivity.this, "Deposit Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        accounts = depositDialog.findViewById(R.id.dep_spn_accounts);
//        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userProfile.getAccounts());
//        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        topSpinner.setAdapter(accountAdapter);
//        topSpinner.setSelection(0);

        edtDepositAmount = depositDialog.findViewById(R.id.edt_deposit_amount);

        btnCancel = depositDialog.findViewById(R.id.btn_cancel_deposit);
        btnSuccess = depositDialog.findViewById(R.id.btn_deposit);

        btnCancel.setOnClickListener(depositClickListener);
        btnSuccess.setOnClickListener(depositClickListener);

        depositDialog.show();

    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(String depositAmount) {
        this.depositAmount = depositAmount;
    }
}