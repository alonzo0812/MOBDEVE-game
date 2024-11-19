package com.example.mobdevemain;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private int stage = 1; // Current stage
    private int enemyIndex = 1; // Enemy index (1-9, or 10 for boss)
    private double enemyHealth = 100.0; // Current enemy health
    private double baseEnemyHealth = 100.0; // Base health scaling
    private double dps = 25.0; // Player's damage per second
    private double gold = 0.0; // Player's gold

    private TextView stageText, enemyHealthText, goldText; // UI elements
    private Button attackButton; // Button for manual attacks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStages = findViewById(R.id.button_stages);
        TextView enemyHealthText = findViewById(R.id.enemyHealthText);
        TextView goldText = findViewById(R.id.goldText);
        TextView dpsText = findViewById(R.id.dpsText);

        SQLiteHelper dbHelper = new SQLiteHelper(this);
        // Get the username from LoginActivity
        String username = getIntent().getStringExtra("username");

        // Load user progress
        Cursor cursor = dbHelper.getUserData(username);

        if (cursor.moveToFirst()) {
            dps = cursor.getDouble(cursor.getColumnIndexOrThrow("dps")); // User DPS
            stage = cursor.getInt(cursor.getColumnIndexOrThrow("stage"));  // Current Stage
            enemyIndex = cursor.getInt(cursor.getColumnIndexOrThrow("enemy")); // Current Enemy
            gold = cursor.getDouble(cursor.getColumnIndexOrThrow("gold")); // User Gold
            enemyHealth = cursor.getDouble(cursor.getColumnIndexOrThrow("enemy_health"));

            updateUI(buttonStages, enemyHealthText, goldText, dpsText);

        }
        cursor.close();


        // Initialize with default values


        // Start DPS damage loop
        startDpsTimer(buttonStages, enemyHealthText, goldText, dpsText);





        // Stages Button
        findViewById(R.id.button_stages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StagesActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.button_achievements).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AchievementActivity.class);
                startActivity(intent);
            }
        });
    }
    private void startDpsTimer(Button buttonStages, TextView enemyHealthText, TextView goldText, TextView dpsText) {
        Handler dpsHandler = new Handler();

        dpsHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Apply DPS damage to the enemy
                enemyHealth -= dps;

                if (enemyHealth <= 0) {
                    onEnemyDefeated(buttonStages, goldText, dpsText);
                }

                // Update the enemy's health text
                enemyHealthText.setText("Enemy Health: " + Math.max(0, enemyHealth));

                // Repeat every second
                dpsHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void onEnemyDefeated(Button buttonStages, TextView goldText,TextView dpsText) {
        gold += 50 * stage * enemyIndex *.55; // Reward gold
        enemyIndex++;

        if (enemyIndex > 4) { // After defeating all enemies, go to the next stage
            enemyIndex = 1;
            stage++;
            buttonStages.setText("Stage: " + stage + "-" + enemyIndex);// Update stage text

        }

        // Scale enemy health for the next stage
        enemyHealth = Math.floor(baseEnemyHealth * Math.pow(1.22, enemyIndex) * stage * 0.55);
        if(enemyIndex == 4){
            baseEnemyHealth = enemyHealth;
        }
        // Update gold display
        buttonStages.setText("Stage: " + stage + "-" + enemyIndex);
        goldText.setText("Gold: " + gold);
        dpsText.setText("DPS: " + dps);

        saveProgress();
        Toast.makeText(this, "Stage: " + stage + "-" + enemyIndex + " enemy defeated! Moving to the next one.", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(Button buttonStages, TextView enemyHealthText, TextView goldText,TextView dpsText) {
        buttonStages.setText("Stage: " + stage);
        enemyHealthText.setText("Enemy Health: " + enemyHealth);
        goldText.setText("Gold: " + gold);
        dpsText.setText("DPS: " + dps);
    }
    private void saveProgress() {
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        String username = getIntent().getStringExtra("username"); // Ensure username is passed

        dbHelper.updateUserProgress(username, dps, stage, enemyIndex, enemyHealth, gold);
        System.out.println("Progress saved: Stage " + stage + ", Enemy " + enemyIndex + ", Gold " + gold);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProgress();
    }
}
