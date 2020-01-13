package com.example.sisswork;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.database.sqlite.SQLiteDatabase;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity<view> extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 7117;
    List<AuthUI.IdpConfig> providers;
    Button btn_sign_out;

    EditText editName, editRoll, editMark;
    Button addbtn, deletebtn, modifybtn, viewbtn, viewallbtn, showbtn;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_sign_out = (Button) findViewById(R.id.btn_sign_out);

        editName = findViewById(R.id.editName);
        editRoll = findViewById(R.id.editRoll);
        editMark = findViewById(R.id.editMark);

        addbtn = findViewById(R.id.addbtn);

        deletebtn = findViewById(R.id.deletebtn);

        modifybtn = findViewById(R.id.modifybtn);

        viewbtn = findViewById(R.id.viewbtn);

        viewallbtn = findViewById(R.id.viewallbtn);

        showbtn = findViewById(R.id.showbtn);


        db = openOrCreateDatabase("Student_Database_App", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS students(name VARCHAR,rollno INTEGER,marks VARCHAR);");


        // add button
        addbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                        if (editName.getText().toString().trim().length() == 0 ||
                                editRoll.getText().toString().trim().length() == 0 ||
                                editRoll.getText().toString().trim().length() == 0) {
                            showMessage("Error", "Provide all valuse");
                            return;
                        }
                        db.execSQL("INSERT INTO students VALUES(' " + editName.getText() + "','" + editRoll.getText() + "','" + editMark.getText() + "')");
                        showMessage("Success", "Record entered Successfully");
                        ClearText();
                    }
        });

        //delete button
        deletebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (editRoll.getText().toString().trim().length() == 0) {
                        showMessage("Error", "Invalid Roll No");
                        return;

                    }
                    Cursor c=db.rawQuery("SELECT * FROM students WHERE rollno='"+editRoll.getText()+ "'",null);
                    if (c.moveToFirst()) {

                        db.execSQL("DELETE FROM sudents WHERE rollno='"+editRoll.getText()+"'");
                        showMessage("Success", "Record Deleted");
                    }
                    else
                        {
                        showMessage("Error", "Invalid Roll No.");
                    }
                    ClearText();

                }
        });

        //modify button
        modifybtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (editRoll.getText().toString().trim().length() == 0) {
                        showMessage("Error", "Invalid Roll no.");
                        return;

                    }

                    Cursor c = db.rawQuery("SELECT * FROM students WHERE rollno='" + editRoll.getText() + "'", null);
                    if (c.moveToFirst()) {
                        db.execSQL("UPDATE students SET name='" + editName.getText() + "',marks='" + editMark.getText() + "' WHERE rollno='" + editRoll.getText() + "'");
                        showMessage("Success", "Record Modified");

                    } else {
                        showMessage("Error", "Invalid Roll no.");
                    }
                    ClearText();
                }

        });
//view individual
        viewbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (editRoll.getText().toString().trim().length() == 0) {
                        showMessage("Error", "Please Provide Roll no.");
                        return;

                    }
                    Cursor c = db.rawQuery("SELECT * FROM students WHERE rollno='" + editRoll.getText() + "'", null);
                    if (c.moveToFirst()) {
                        editName.setText(c.getString(0));
                        editMark.setText(c.getString(2));
                    } else {
                        showMessage("Error", "Invalid Roll No.");
                        ClearText();
                    }
                }
        });

//viewall
        viewallbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    Cursor c = db.rawQuery("SELECT * FROM students", null);
                    if (c.getCount() == 0) {
                        showMessage("Error", "No Records Found.");
                    }
                    StringBuffer buffer = new StringBuffer();
                    while (c.moveToNext()) {
                        buffer.append("Roll No:" + c.getString(1) + "\n");
                        buffer.append("Name: " + c.getString(0) + "\n");
                        buffer.append("Address: " + c.getString(2) + "\n\n");

                    }
                    showMessage("Students Detail:", buffer.toString());
                }


        });
        //about us
        showbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                    showMessage("Students Database Management App", "Developed by Mohd Danish");

            }
        });


        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logout
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_sign_out.setEnabled(false);
                                showSignInOptions();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //Init Providers
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), //EmailBuilder
                new AuthUI.IdpConfig.PhoneBuilder().build(),  //PhoneBuilder
                new AuthUI.IdpConfig.FacebookBuilder().build(), //FacebookBuilder
                new AuthUI.IdpConfig.GoogleBuilder().build()  //GoogleBuilder

        );
        showSignInOptions();
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.MyTheme)
                        .build(), MY_REQUEST_CODE
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //GetUser

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //ShowEmailOnToast

                Toast.makeText(this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();

                //setButtonSignOut
                btn_sign_out.setEnabled(true);

            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }


        private void ClearText () {
            editName.setText("");
            editRoll.setText("");
            editMark.setText("");
            editRoll.requestFocus();
        }

        private void showMessage (String title, String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }/*
*/

    }
