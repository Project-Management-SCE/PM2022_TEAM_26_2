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
				Toast.makeText(PendingLoansActivity.this,"Exited from pending loan window",Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void approveLoan()
	{

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

		//Setting click listener for list view
		lstPendingLoans.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int i, long id)
			{
				selectedLoanIndex = i;
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
		btnApproveLoan = pendingLoanDlg.findViewById(R.id.btn_approve_pending_loan_dialog);
		btnDenyLoan = pendingLoanDlg.findViewById(R.id.btn_deny_pending_loan_dialog);

		Transaction loan = pendingLoans.get(selectedLoanIndex);
		txtAmount.setText((int) loan.getAmount());
		txtDestAccount.setText(loan.getDestinationAccount());
		txtLoanTime.setText(loan.getTimestamp());

		btnApproveLoan.setOnClickListener(PendingLoanClickListener);
		btnDenyLoan.setOnClickListener(PendingLoanClickListener);
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
						pendingLoans.add(dsa.getValue(Transaction.class));
					}
				}

				pendingLoansAdapter = new ArrayAdapter<Transaction>(PendingLoansActivity.this, R.layout.lst_transactions,pendingLoans);
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
