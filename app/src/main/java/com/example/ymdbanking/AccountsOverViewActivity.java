package com.example.ymdbanking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AccountsOverViewActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Dialog accountDialog;
    private EditText edtAccountName;
    private EditText edtInitAccountBalance;
    private Button btnCancel;
    private Button btnAddAccount;
    private String accountName,accountBalance;

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
                setAccountName(edtAccountName.getText().toString().trim());
                setAccountBalance(edtAccountName.getText().toString().trim());
                //addAccount();
            }
        }
    };

    private void addAccount() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_over_view);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fab = findViewById(R.id.floating_action_btn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAccountDialog();
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