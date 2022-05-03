package com.example.ymdbanking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ymdbanking.adapters.AccountAdapter;
import com.example.ymdbanking.db.ApplicationDB;
import com.example.ymdbanking.model.Account;
import com.example.ymdbanking.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountsOverViewActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Dialog accountDialog;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;
    private TextView txtTitleMessage;
    private TextView txtDetailMessage;
    private ListView lstAccounts;
    private Button btnCancel;
    private Button btnAddAccount;
    private String accountName,accountBalance;
    private Customer customer;
    private int selectedAccountIndex;

    private View.OnClickListener addAccountClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if (view.getId() == btnCancel.getId())
            {
                accountDialog.dismiss();
                Toast.makeText(AccountsOverViewActivity.this, "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == btnAddAccount.getId())
            {
//                setAccountName(edtAccountName.getText().toString().trim());
//                setAccountBalance(edtAccountName.getText().toString().trim());
                addAccount();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_over_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtTitleMessage = findViewById(R.id.txt_title_msg);
        txtDetailMessage = findViewById(R.id.txt_details_msg);
        lstAccounts = findViewById(R.id.lst_accounts);
        fab = findViewById(R.id.floating_action_btn);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                displayAccountDialog();
//            }
//        });
        setValues();
    }

    private void setValues()
    {
        selectedAccountIndex = 0;

        SessionManager sessionManager = new SessionManager(this,SessionManager.USER_SESSION);
        Gson gson = new Gson();
        customer = (Customer) sessionManager.getCustomerObjFromSession();
//        customer = gson.fromJson(json, Customer.class);
        ApplicationDB applicationDB = new ApplicationDB(this);
//        customer = applicationDB.getCustomerByID(sessionManager.userSession.getString(SessionManager.KEY_ID,""));
//        customer.setAccounts(applicationDB.getAccountsFromCurrentCustomer(customer.getId()));

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (customer.getAccounts().size() >= 10)
                {
                    Toast.makeText(getApplicationContext(), "You have reached the maximum amount of accounts (10)", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    displayAccountDialog();
                }
            }
        });
    }

    private void displayAccountDialog()
    {

        accountDialog = new Dialog(AccountsOverViewActivity.this);
        accountDialog.setContentView(R.layout.account_dialog);

        accountDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        accountDialog.setCanceledOnTouchOutside(true);
        accountDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(AccountsOverViewActivity.this, "Account Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtAccountName = accountDialog.findViewById(R.id.edt_payee_name);
        edtInitAccountBalance = accountDialog.findViewById(R.id.edt_init_bal);

        btnCancel = accountDialog.findViewById(R.id.btn_cancel_dialog);
        btnAddAccount = accountDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(addAccountClickListener);
        btnAddAccount.setOnClickListener(addAccountClickListener);

        accountDialog.show();

    }

    private void addAccount()
    {
        String balance = edtInitAccountBalance.getText().toString();
        boolean isNum = false;
        double initDepositAmount = 0;

        if (!(edtAccountName.getText().toString().equals("")))
        {
            try
            {
                initDepositAmount = Double.parseDouble(edtInitAccountBalance.getText().toString());
                isNum = true;
            }
            catch (Exception e)
            {
                if (!edtInitAccountBalance.getText().toString().equals(""))
                {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    edtInitAccountBalance.getText().clear();
                }
            }

            if (edtAccountName.getText().toString().length() > 10)
            {
                Toast.makeText(this, R.string.account_name_exceeds_char, Toast.LENGTH_SHORT).show();
                edtAccountName.getText().clear();

            }
            else if ((isNum) || balance.equals(""))
            {
                boolean match = false;

                for (int i = 0; i < customer.getAccounts().size(); i++)
                {
                    if (edtAccountName.getText().toString().equalsIgnoreCase(customer.getAccounts().get(i).getAccountName())) {
                        match = true;
                    }
                }

                if (!match)
                {
                    ApplicationDB applicationDb = new ApplicationDB(getApplicationContext());
                    customer.addAccount(edtAccountName.getText().toString(), 0);

                    if (!balance.equals(""))
                    {
                        if (isNum)
                        {
                            if (initDepositAmount >= 0.01)
                            {
                                customer.getAccounts().get(customer.getAccounts().size() - 1).addDepositTransaction(initDepositAmount);
                                applicationDb.saveNewAccount(customer, customer.getAccounts().get(customer.getAccounts().size() - 1));
                                applicationDb.saveNewTransaction(customer,customer.getAccounts().get(customer.getAccounts().size() - 1).getTransactions()
                                                                                   .get(customer.getAccounts().get(customer.getAccounts().size() - 1).getTransactions().size() - 1));
                            }
                        }
                    }

//                    applicationDb.saveNewAccount(customer, customer.getAccounts().get(
//                            customer.getAccounts().size() - 1));
                    Toast.makeText(this, R.string.acc_saved_successfully, Toast.LENGTH_SHORT).show();

                    if (customer.getAccounts().size() == 1)
                    {
                        //txtTitleMessage.setText("Select an Account to view Transactions");
                        txtDetailMessage.setVisibility(View.VISIBLE);
                        lstAccounts.setVisibility(View.VISIBLE);
                    }
//                    HashMap<String,Account> accounts = customer.getAccounts();
//                    HashMap<String,Object> listAccounts = new HashMap<>();
//                    listAccounts.put("accounts",accounts);
                    ArrayList<Account> arrayAccounts = (ArrayList<Account>) customer.getAccounts().values();
                    AccountAdapter adapter = new AccountAdapter(AccountsOverViewActivity.this, R.layout.lst_accounts, arrayAccounts);
                    lstAccounts.setAdapter(adapter);
                    SessionManager sessionManager = new SessionManager(this,SessionManager.USER_SESSION);
                    Gson gson = new Gson();
                    String json = gson.toJson(customer);
                    sessionManager.editor.putString(SessionManager.SESSION_OBJ,json);

                    accountDialog.dismiss();

                }
                else
                {
                    Toast.makeText(this, R.string.account_name_error, Toast.LENGTH_SHORT).show();
                    edtAccountName.getText().clear();
                }
            }
        }
        else
        {
            Toast.makeText(this, "Please enter an account name", Toast.LENGTH_SHORT).show();
        }
    }

    public String getAccountName() {
        return accountName;
    }
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    public String getAccountBalance() {
        return accountBalance;
    }
    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }
}