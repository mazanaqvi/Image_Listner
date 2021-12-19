package com.example.fyp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;

public class MainActivity extends AppCompatActivity {
     EditText txt_user;
     EditText txt_pass;
     Button btn_login;
     Spinner _spinner;
      DataBaseHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adapter_view_layout1);
        txt_user = (EditText) findViewById(R.id.txt_username);
        txt_pass = (EditText) findViewById(R.id.txt_password);
        btn_login = (Button) findViewById(R.id.button6);
        _spinner = (Spinner) findViewById(R.id.spinners);
        mydb = new DataBaseHelper(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.usertype,R.layout.support_simple_spinner_dropdown_item);
        _spinner.setAdapter(adapter);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_user.getText().toString().equals("") && !txt_pass.getText().toString().equals("")) {
                    String item = _spinner.getSelectedItem().toString();
                    if (item.equals("Admin")) {
                        Intent intent = new Intent(MainActivity.this, admin.class);
                        startActivity(intent);
                    }

                    else if (item.equals("User")) {

                        String u = txt_user.getText().toString();
                        String p = txt_pass.getText().toString();
                        boolean var =  mydb.insert_hard_coded_data();
                         var = mydb.CheckUser(u,p);
                        if(var)
                        {
                            Toast.makeText(MainActivity.this, "Login Successfully by regular user", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, take_photo.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
                        }

                    } else if (item.equals("Manager")) {
                        Intent intent = new Intent(MainActivity.this, manager.class);
                        startActivity(intent);
                    }
                }
            }
            /*
            private void login_regular_user()
            {
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean var = mydb.CheckUser(txt_user.getText().toString(),txt_pass.getText().toString());
                        if(var)
                        {
                            Toast.makeText(MainActivity.this,"Login Successfully by regular user",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            */
        });
    }
}