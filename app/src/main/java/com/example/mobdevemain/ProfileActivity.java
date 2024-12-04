package com.example.mobdevemain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    ImageButton backBtn;
    Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        backBtn = findViewById(R.id.backbtn);
        logoutBtn = findViewById(R.id.logoutBtn);
    }

    public void profileBackBtn(View v){
        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void deleteAccountClicked(View v) {
        String username = getIntent().getStringExtra("username");

        SQLiteHelper dbHelper = new SQLiteHelper(this);
        boolean isDeleted = dbHelper.deleteUser(username);

        if (isDeleted) {
            Toast.makeText(this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete account. Try again.", Toast.LENGTH_SHORT).show();
        }
    }


    public void logoutClicked(View v){
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}