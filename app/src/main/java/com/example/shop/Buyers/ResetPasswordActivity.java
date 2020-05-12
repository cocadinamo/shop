package com.example.shop.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shop.Prevalent.Prevalent;
import com.example.shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
    private String check = "";
    private TextView pageTitle, titleQusetions;
    private EditText phoneNumber, question1, question2;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_reset_password );

        check = getIntent ().getStringExtra ( "check" );

        pageTitle = findViewById ( R.id.page_title );
        titleQusetions = findViewById ( R.id.title_questions );
        phoneNumber = findViewById ( R.id.find_phone_number );
        question1 = findViewById ( R.id.question_1 );
        question2 = findViewById ( R.id.question_2 );
        verifyButton = findViewById ( R.id.verify_btn );
    }

    @Override
    protected void onStart() {
        super.onStart ();

        phoneNumber.setVisibility ( View.GONE );

        if (check.equals ( "settings" )) {
            pageTitle.setText ( "Odaberi pitanje" );
            titleQusetions.setText ( "Odaberi odgovor za sigurnosno pitanje" );
            verifyButton.setText ( "Odaberi" );

            displayPreviousAnsers ();

            verifyButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    setAnswers ();
                }
            } );

        } else if (check.equals ( "login" )) {
            phoneNumber.setVisibility ( View.VISIBLE );

            verifyButton.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    verifyUser ();
                }
            } );
        }
    }

    private void setAnswers() {
        String answer1 = question1.getText ().toString ().toLowerCase ();
        String answer2 = question2.getText ().toString ().toLowerCase ();

        if (question1.equals ( "" ) && question2.equals ( "" )) {
            Toast.makeText ( ResetPasswordActivity.this, "Odgovori na oba pitanja", Toast.LENGTH_SHORT ).show ();
        } else {
            DatabaseReference ref = FirebaseDatabase.getInstance ()
                    .getReference ()
                    .child ( "Users" )
                    .child ( Prevalent.currentOnlineUser.getPhone () );

            HashMap<String, Object> userdataMap = new HashMap<> ();
            userdataMap.put ( "answer1", answer1 );
            userdataMap.put ( "answer2", answer2 );

            ref.child ( "Security Questions" ).updateChildren ( userdataMap )
                    .addOnCompleteListener ( new OnCompleteListener<Void> () {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful ()) {
                                Toast.makeText ( ResetPasswordActivity.this, "Postavio si odgovor na sigurnosno pitanje", Toast.LENGTH_SHORT ).show ();

                                Intent intent = new Intent ( ResetPasswordActivity.this, HomeActivity.class );
                                startActivity ( intent );
                            }
                        }
                    } );

        }
    }

    private void displayPreviousAnsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance ()
                .getReference ()
                .child ( "Users" )
                .child ( Prevalent.currentOnlineUser.getPhone () );

        ref.child ( "Security Questions" ).addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists ()) {
                    String ans1 = dataSnapshot.child ( "answer1" ).getValue ().toString ();
                    String ans2 = dataSnapshot.child ( "answer2" ).getValue ().toString ();

                    question1.setText ( ans1 );
                    question2.setText ( ans2 );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    private void verifyUser() {
        final String phone = phoneNumber.getText ().toString ();
        final String answer1 = question1.getText ().toString ().toLowerCase ();
        final String answer2 = question2.getText ().toString ().toLowerCase ();

        if (!phone.equals ( "" ) && !answer1.equals ( "" ) && !answer2.equals ( "" )) {

            final DatabaseReference ref = FirebaseDatabase.getInstance ()
                    .getReference ()
                    .child ( "Users" )
                    .child ( phone );

            ref.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists ()) {
                        String mPhone = dataSnapshot.child ( "phone" ).getValue ().toString ();

                        if (dataSnapshot.hasChild ( "Security Questions" )) {
                            String ans1 = dataSnapshot.child ( "Security Questions" ).child ( "answer1" ).getValue ().toString ();
                            String ans2 = dataSnapshot.child ( "Security Questions" ).child ( "answer2" ).getValue ().toString ();

                            if (!ans1.equals ( answer1 )) {
                                Toast.makeText ( ResetPasswordActivity.this, "Tvoj prvi odgovor je pogrešan", Toast.LENGTH_SHORT ).show ();
                            } else if (!ans2.equals ( answer2 )) {
                                Toast.makeText ( ResetPasswordActivity.this, "Tvoj drugi odgovor je pogrešan", Toast.LENGTH_SHORT ).show ();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder ( ResetPasswordActivity.this );
                                builder.setTitle ( "Nova lozinka" );

                                final EditText newPassword = new EditText ( ResetPasswordActivity.this );
                                newPassword.setHint ( "Ovdje upiši novu lozinku" );
                                builder.setView ( newPassword );

                                builder.setPositiveButton ( "Promijeni", new DialogInterface.OnClickListener () {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        if (!newPassword.getText ().toString ().equals ( "" )) {
                                            ref.child ( "password" )
                                                    .setValue ( newPassword.getText ().toString () )
                                                    .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful ()) {
                                                                Toast.makeText ( ResetPasswordActivity.this, "Lozinka je promjenjena", Toast.LENGTH_SHORT ).show ();
                                                                Intent intent = new Intent ( ResetPasswordActivity.this, LoginActivity.class );
                                                                startActivity ( intent );
                                                            }
                                                        }
                                                    } );
                                        }
                                    }
                                } );
                                builder.setNegativeButton ( "Cancel", new DialogInterface.OnClickListener () {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel ();
                                    }
                                } );
                                builder.show ();
                            }
                        } else {
                            Toast.makeText ( ResetPasswordActivity.this, "Nije postavljeno sigurnosno pitanje", Toast.LENGTH_SHORT ).show ();
                        }
                    } else {
                        Toast.makeText ( ResetPasswordActivity.this, "Broj mobitela nije točan", Toast.LENGTH_SHORT ).show ();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            } );
        }
    }
}