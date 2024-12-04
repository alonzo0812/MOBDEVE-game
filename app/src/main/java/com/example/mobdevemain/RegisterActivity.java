package com.example.mobdevemain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    Button signUpBtn;
    ImageButton backBtn;
    TextView loginTextLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        signUpBtn = findViewById(R.id.signUpBtn);
        backBtn = findViewById(R.id.backBtn);
        loginTextLink = findViewById(R.id.loginTextLink);
    }
    public void backBtnClicked(View v){
        finish();
    }

    public void hereClicked(View v){
        finish();
    }

    public void signUpClicked(View v) {
        SQLiteHelper dbHelper = new SQLiteHelper(this);

        String username = ((TextView) findViewById(R.id.registerUsername)).getText().toString();
        String password = ((TextView) findViewById(R.id.registerPassword)).getText().toString();

        if (dbHelper.addUser(username, password)) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show();
        }
    }

}