package com.example.ymdbanking;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ymdbanking.adapters.TransactionAdapter;
import com.example.ymdbanking.model.Clerk;
import com.example.ymdbanking.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PendingLoansActivity extends AppCompatActivity
{
	private ListView lstPendingLoans;
	private TextView txtTitle;
	private SessionManager sessionManager;
	private Clerk clerk;
	private int selectedLoanIndex;
	private ArrayList<Transaction> pendingLoans;
	private ArrayAdapter<Transaction> pendingLoansAdapter;
	private Dialog pendingLoanDlg;
	private TextView txtAmount;
	private TextView txtDestAccount;
	private TextView txtLoanTime;
	private TextView txtStatus;
	private Button btnApproveLoan;
	private Button btnDenyLoan;

	private View.OnClickListener PendingLoanClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v.getId() == R.id.btn_approve_pending_loan_dialog)
			{
				approveLoan();
			}
			else if(v.getId() == R.id.btn_deny_pending_loan_dialog)
			{
				denyLoan();
			}
		}
	};

	private void denyLoan()
	{
		Transaction loan = pendingLoans.get(selectedLoanIndex);
		//Setting status field in DB to DENIED
		FirebaseDatabase.getInstance().getReference("Loans").child(clerk.getId())
			.child(loan.getDestinationCustomerId()).child(loan.getTransactionID()).child("status").setValue(Transaction.STATUS.DENIED.toString());

		Toast.makeText(PendingLoansActivity.this,"Loan denied on account " + loan.getDestinationAccount(),Toast.LENGTH_SHORT).show();
		pendingLoanDlg.dismiss();
		setValues();
	}

	private void approveLoan()
	{
		Transaction loan = pendingLoans.get(selectedLoanIndex);

		//Adding loan amount to customer's account
		FirebaseDatabase.getInstance().getReference("Accounts").child(loan.getDestinationCustomerId())
			.child(loan.getDestinationAccount()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				//Getting original account's balance from DB
				DataSnapshot ds = task.getResult();
				double newbalance = ds.child("accountBalance").getValue(double.class);
				//Adding loan amount to account current balance
				newbalance += loan.getAmount();

				//Setting account balance in DB to new balance
				FirebaseDatabase.getInstance().getReference("Accounts").child(loan.getDestinationCustomerId())
					.child(loan.getDestinationAccount()).child("accountBalance").setValue(newbalance).addOnCompleteListener(new OnCompleteListener<Void>()
				{
					@Override
					public void onComplete(@NonNull Task<Void> task)
					{
						//Setting status field in DB to APPROVED
						FirebaseDatabase.getInstance().getReference("Loans").child(clerk.getId())
								.child(loan.getDestinationCustomerId()).child(loan.getTransactionID()).child("status").setValue(Transaction.STATUS.APPROVED.toString());

						Toast.makeText(PendingLoansActivity.this,"Loan applied on account " + loan.getDestinationAccount(),Toast.LENGTH_SHORT).show();
						pendingLoanDlg.dismiss();
						setValues();
					}
				})
					.addOnFailureListener(new OnFailureListener()
				{
					@Override
					public void onFailure(@NonNull Exception e)
					{
						Toast.makeText(PendingLoansActivity.this,"Loan applied on account " + loan.getDestinationAccount(),Toast.LENGTH_SHORT).show();
						Log.d("SET BALANCE ERROR",e.toString());
					}
				});
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(PendingLoansActivity.this,"Could not apply loan on account " + loan.getDestinationAccount(),Toast.LENGTH_SHORT).show();
				Log.d("FETCH ACCOUNT ERROR",e.toString());
			}
		});
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pending_loans);

		txtTitle = findViewById(R.id.txt_title_pending_loans);
		lstPendingLoans = findViewById(R.id.lst_pending_loans);

		sessionManager = new SessionManager(PendingLoansActivity.this,SessionManager.USER_SESSION);
		clerk = sessionManager.getClerkObjFromSession();
		pendingLoans = new ArrayList<>();

		setValues();
	}

	private void setValues()
	{
		selectedLoanIndex = 0;
		pendingLoans = new ArrayList<>();

		//Setting click listener for list view
		lstPendingLoans.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int i, long id)
			{
				selectedLoanIndex = i;
				if(pendingLoans.get(selectedLoanIndex).getStatus() == Transaction.STATUS.DENIED)
					Toast.makeText(PendingLoansActivity.this,"You've already denied this loan",Toast.LENGTH_SHORT).show();
				else
					viewLoanDetail();
			}
		});
		getPendingLoans();
	}

	private void viewLoanDetail()
	{
		pendingLoanDlg = new Dialog(PendingLoansActivity.this);
		pendingLoanDlg.setContentView(R.layout.pending_loan_dialog);
		pendingLoanDlg.setCanceledOnTouchOutside(true);
		pendingLoanDlg.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				Toast.makeText(PendingLoansActivity.this,"Loan view cancelled",Toast.LENGTH_SHORT).show();
			}
		});

		txtAmount = pendingLoanDlg.findViewById(R.id.txt_amount_pending_loan_dialog);
		txtDestAccount = pendingLoanDlg.findViewById(R.id.txt_dst_acc_pending_loan_dialog);
		txtLoanTime = pendingLoanDlg.findViewById(R.id.txt_time_pending_loan_dialog);
		txtStatus = pendingLoanDlg.findViewById(R.id.txt_status_loan_dialog);
		btnApproveLoan = pendingLoanDlg.findViewById(R.id.btn_approve_pending_loan_dialog);
		btnDenyLoan = pendingLoanDlg.findViewById(R.id.btn_deny_pending_loan_dialog);

		Transaction loan = pendingLoans.get(selectedLoanIndex);
		txtAmount.setText(String.valueOf((int) loan.getAmount()));
		txtDestAccount.setText(loan.getDestinationAccount());
		txtLoanTime.setText(loan.getTimestamp());
		txtStatus.setText(loan.getStatus().toString());

		btnApproveLoan.setOnClickListener(PendingLoanClickListener);
		btnDenyLoan.setOnClickListener(PendingLoanClickListener);

		pendingLoanDlg.show();
	}

	private void getPendingLoans()
	{
		FirebaseDatabase.getInstance().getReference("Loans").child(clerk.getId()).get()
			.addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
		{
			@Override
			public void onComplete(@NonNull Task<DataSnapshot> task)
			{
				for(DataSnapshot ds : task.getResult().getChildren())
				{
					for(DataSnapshot dsa : ds.getChildren())
					{
						if(dsa.getValue(Transaction.class).getStatus() == Transaction.STATUS.PENDING)
							pendingLoans.add(dsa.getValue(Transaction.class));
					}
				}

				pendingLoansAdapter = new TransactionAdapter(PendingLoansActivity.this, R.layout.lst_transactions,pendingLoans);
				lstPendingLoans.setAdapter(pendingLoansAdapter);
			}
		})
			.addOnFailureListener(new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				Toast.makeText(PendingLoansActivity.this,"Can't get pending loans from DB",Toast.LENGTH_SHORT).show();
				Log.d("DB_GET_LOANS_ERROR",e.toString());
			}
		});
	}
}
