package com.example.mobdevemain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    Button signInBtn;
    TextView registerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        signInBtn = findViewById(R.id.signInButton);
        registerText = findViewById(R.id.registerLink);
    }

    public void signInClicked(View v) {
        SQLiteHelper dbHelper = new SQLiteHelper(this);

        String username = ((TextView) findViewById(R.id.usernameInput)).getText().toString();
        String password = ((TextView) findViewById(R.id.passwordInput)).getText().toString();

        if (dbHelper.checkUser(username, password)) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("username", username); // Pass the username to MainActivity
            startActivity(i);
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }


    public void registerClicked(View v){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}