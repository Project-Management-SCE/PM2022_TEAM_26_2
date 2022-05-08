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
import com.example.ymdbanking.model.Account;
import com.example.ymdbanking.model.Admin;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Customer;
import com.example.ymdbanking.model.Transaction;
import com.example.ymdbanking.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
    private Spinner spnAccounts;
    private String accountName,depositAmount;
    private Customer customer;
    private Clerk clerk;
    private Admin admin;
    private ArrayAdapter<Account> accountAdapter;
    private SessionManager sessionManager;
    private String TAG = "DashboardActivity";
    private Dialog loanDialog;
    private Spinner topSpinner;
    private Spinner bottomSpinner;
    private ArrayAdapter<Clerk> clerkAdapter;
    private EditText edtLoanAmount;
    private ArrayList<Clerk> clerks;
    private String sessionId;

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
                makeDeposit();
            }
        }
    };

    private View.OnClickListener loanClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == btnCancel.getId())
            {
                depositDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Loan Cancelled", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == btnSuccess.getId())
            {
                makeLoan();
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
        sessionManager = new SessionManager(this,SessionManager.USER_SESSION);
        sessionId = sessionManager.userSession.getString(SessionManager.KEY_SESSION_ID,null);
        if(sessionId.equals("1"))
            admin = sessionManager.getAdminObjFromSession();
        else if(sessionId.equals("2"))
            clerk = sessionManager.getClerkObjFromSession();
        else if(sessionId.equals("3"))
            customer = sessionManager.getCustomerObjFromSession();
        setValues();
        navigationDrawer();
    }

    private void setValues()
    {
        Customer tempCustomer = new Customer();
        tempCustomer.setAccounts(new ArrayList<>(0));
        FirebaseDatabase.getInstance().getReference("Accounts").child(customer.getId()).get()
            .addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task)
                {
                    for (DataSnapshot ds : task.getResult().getChildren())
                    {
                        tempCustomer.getAccounts().add(new Account(
                                ds.child("accountName").getValue(String.class),
                                ds.child("accountNo").getValue(String.class),
                                ds.child("accountBalance").getValue(Double.class)
                        ));
                        tempCustomer.getAccounts().get(
                                tempCustomer.getAccounts().size() - 1).getTransactions()
                                .addAll(getTransactionsForAccount(tempCustomer.getAccounts().get(
                                        tempCustomer.getAccounts().size() - 1)));
                    }
                    //Checking if there's any mismatch on customer's accounts between phone's memory and DB
                    if(customer.getAccounts().size() != tempCustomer.getAccounts().size())
                        //If there's a mismatch than we'll take the data from DB
                        customer.setAccounts(tempCustomer.getAccounts());
                    sessionManager.saveCustomerObjForSession(customer);
                }
            })
            .addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getApplicationContext(), "ERROR - Can't get customer's accounts from DB", Toast.LENGTH_SHORT).show();
                    Log.d("DB_ERROR", e.toString());
                }
            });

        //Getting all clerks from DB
        clerks = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Clerks").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                for(DataSnapshot ds : task.getResult().getChildren())
                    clerks.add(ds.getValue(Clerk.class));

                sessionManager.saveClerksForSession(clerks);
            }
        })
        .addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "ERROR - Can't get all clerks from DB", Toast.LENGTH_SHORT).show();
                Log.d("DB_ERROR",e.toString());
            }
        });
    }

    /**
     * Function to get every account's transactions from DB
     * @param account - account object to retrieve it's transactions
     * @return - account's transactions
     */
    public ArrayList<Transaction> getTransactionsForAccount(Account account)
    {
        ArrayList<Transaction> transactions = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Accounts").child(customer.getId()).child(account.getAccountNo())
            .child("transactions").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                for(DataSnapshot ds : task.getResult().getChildren())
                    transactions.add(ds.getValue(Transaction.class));
            }
        })
        .addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "ERROR - Can't get transactions from DB for this account", Toast.LENGTH_SHORT).show();
                Log.d("DB_ERROR",e.toString());
            }
        });
        return transactions;
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
            public void onDrawerStateChanged(int newState)
            {
//                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                drawerLayout.closeDrawers();
            }
        });


        disp_phone = findViewById(R.id.display_phone);

        //User Session Details
        SessionManager sessionManager = new SessionManager(DashboardActivity.this,SessionManager.USER_SESSION);
        userDetails = sessionManager.getUserDetailFromSession();
        String phone = userDetails.get(SessionManager.KEY_PHONE);

        //If user is customer
        if(sessionId.equals("3"))
        {
            //User's navigation drawer
            View headerView = navigationView.getHeaderView(0);
            disp_username = headerView.findViewById(R.id.menu_userName);
            disp_username.setText(userDetails.get(SessionManager.KEY_USERNAME));
            navigationView.getMenu().findItem(R.id.nav_add_clerks).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_users).setVisible(false);
            sessionManager.saveCustomerObjForSession(customer);
        }
        //If user is clerk
        else if(sessionId.equals("2"))
        {
            //Todo: Hide all nav bars that the other type of users shouldn't see
        }
        //If user is admin
        else if(sessionId.equals("1"))
        {
            //Todo: Hide all nav bars that the other type of users shouldn't see
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
        else if(id == R.id.nav_transfer)
        {
            setValuesForTransfer();
            startActivity(new Intent(getApplicationContext(), TransferActivity.class));
        }
        else if(id == R.id.nav_deposit)
            displayDepositDialog();
        else if(id == R.id.nav_loan)
            displayLoanDialog();
        else if(id == R.id.nav_logout)
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        else
            startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
        return true;
    }

    private void setValuesForTransfer()
    {
        ApplicationDB applicationDB = new ApplicationDB(getApplicationContext());
        ArrayList<Customer> customersForTransfer = applicationDB.getAllCustomersForTransfer(customer.getId());
        ArrayList<Account> customersAccountsForTransfer = applicationDB.getAllAccountsForTransfer(customer.getId());
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

        spnAccounts = depositDialog.findViewById(R.id.dep_spn_accounts);

        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customer.getAccounts());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnAccounts.setAdapter(accountAdapter);
        spnAccounts.setSelection(0);

        edtDepositAmount = depositDialog.findViewById(R.id.edt_deposit_amount);

        btnCancel = depositDialog.findViewById(R.id.btn_cancel_deposit);
        btnSuccess = depositDialog.findViewById(R.id.btn_deposit);

        btnCancel.setOnClickListener(depositClickListener);
        btnSuccess.setOnClickListener(depositClickListener);

        depositDialog.show();
    }

    private void makeDeposit()
    {
        int selectedAccountIndex = spnAccounts.getSelectedItemPosition();
        double depositAmount = 0;
        boolean isNum = false;

        try
        {
            depositAmount = Double.parseDouble(edtDepositAmount.getText().toString());
            isNum = true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (depositAmount < 0.01 && !isNum)
        {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
        else
        {
            customer.getAccounts().get(selectedAccountIndex).addDepositTransaction(depositAmount);
            sessionManager.saveCustomerObjForSession(customer);

            ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
            applicationDb.overwriteAccount(customer, customer.getAccounts().get(selectedAccountIndex));
//            applicationDb.saveNewTransaction(userCustomer, account.getAccountNo(),
//                    account.getTransactions().get(account.getTransactions().size() - 1));

            Toast.makeText(this,
                    "Deposit of $" + String.format(Locale.getDefault(), "%.2f", depositAmount) +
                    " " + "made successfully", Toast.LENGTH_SHORT).show();

            accountAdapter = new ArrayAdapter<Account>(this, android.R.layout.simple_spinner_item, customer.getAccounts());
            accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAccounts.setAdapter(accountAdapter);

            //TODO: Add checkbox if the user wants to make more than one deposit

            depositDialog.dismiss();
            drawerLayout.closeDrawers();
            //manualNavigation(manualNavID.ACCOUNTS_ID, null);
        }
    }

    private void displayLoanDialog()
    {
        loanDialog = new Dialog(this);
        loanDialog.setContentView(R.layout.loan_dialog);
        loanDialog.setCanceledOnTouchOutside(true);
        loanDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                Toast.makeText(getApplicationContext(),"Deposit Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        clerks = sessionManager.getClerksFromSession();
        topSpinner = loanDialog.findViewById(R.id.spn_clerk_list_Loan_dialog);
        clerkAdapter = new ArrayAdapter<Clerk>(this, android.R.layout.simple_spinner_item,clerks);
        clerkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topSpinner.setAdapter(clerkAdapter);
        topSpinner.setSelection(0);

        bottomSpinner = loanDialog.findViewById(R.id.spn_account_dialog);
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customer.getAccounts());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bottomSpinner.setAdapter(accountAdapter);
        bottomSpinner.setSelection(0);
        edtLoanAmount = loanDialog.findViewById(R.id.edt_loan_amount);

        btnCancel = loanDialog.findViewById(R.id.btn_cancel_loan_dialog);
        btnSuccess = loanDialog.findViewById(R.id.btn_success_loan_dialog);

        btnCancel.setOnClickListener(loanClickListener);
        btnSuccess.setOnClickListener(loanClickListener);
        loanDialog.show();
    }

    private void makeLoan()
    {
        double loanAmount = 0;
        boolean isNum = false;
        int clerkSelectedIndex = topSpinner.getSelectedItemPosition();
        int accountSelectedIndex = bottomSpinner.getSelectedItemPosition();

        try
        {
            loanAmount = Double.parseDouble(edtLoanAmount.getText().toString());
            isNum = true;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if (loanAmount < 0.01 && !isNum)
        {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
        else
        {
            sessionManager.saveCustomerObjForSession(customer);
            Clerk clerk = clerks.get(clerkSelectedIndex);
            Account account = customer.getAccounts().get(accountSelectedIndex);
            clerk.addLoanTransaction(account, loanAmount);
            Toast.makeText(this,
                    "Loan of $" + String.format(Locale.getDefault(), "%.2f", loanAmount) + " " +
                    "is pending", Toast.LENGTH_SHORT).show();
            loanDialog.dismiss();
            drawerLayout.closeDrawers();
//            manualNavigation(manualNavID.ACCOUNTS_ID, null);
        }
    }
}