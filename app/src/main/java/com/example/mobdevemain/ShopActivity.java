package com.example.mobdevemain;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {
    private double gold;
    private double dps;
    private double upgradeCost = 100;
    private SQLiteHelper dbHelper;
    private String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        dbHelper = new SQLiteHelper(this);

        username = getIntent().getStringExtra("username");
        gold = getIntent().getDoubleExtra("gold", 0);
        dps = getIntent().getDoubleExtra("dps", 1);
        upgradeCost = getIntent().getDoubleExtra("upgradeCost", 100);
        TextView skillStats = findViewById(R.id.skillStats);
        skillStats.setText("Damage: " + dps + "\nUpgrade Cost: " + upgradeCost);

        setupUpgradeButton();

        findViewById(R.id.backBtn).setOnClickListener(v -> {
            saveProgressToDatabase();
            Intent intent = new Intent(ShopActivity.this, MainActivity.class);
            intent.putExtra("gold", gold);
            intent.putExtra("dps", dps);
            intent.putExtra("upgradeCost", upgradeCost);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });
    }
    private void saveProgressToDatabase() {
        if (username != null) {
            SQLiteHelper dbHelper = new SQLiteHelper(this);

            Cursor cursor = dbHelper.getUserData(username);
            int currentStage = 1;
            int currentEnemyIndex = 1;
            double currentEnemyHealth = 100;


            if (cursor.moveToFirst()) {
                currentStage = cursor.getInt(cursor.getColumnIndexOrThrow("stage"));
                currentEnemyIndex = cursor.getInt(cursor.getColumnIndexOrThrow("enemy"));
                currentEnemyHealth = cursor.getDouble(cursor.getColumnIndexOrThrow("enemy_health"));
            }
            cursor.close();

            dbHelper.updateUserProgress(username, dps, currentStage, currentEnemyIndex, currentEnemyHealth, gold, upgradeCost);
            //System.out.println("Progress saved: Stage = " + currentStage + ", Enemy = " + currentEnemyIndex + ", Gold = " + gold);
        }
    }

    private void setupUpgradeButton() {
        Button upgradeButton = findViewById(R.id.upgradeBtn);
        TextView skillStats = findViewById(R.id.skillStats);

        updateSkillStatsUI(skillStats);

        upgradeButton.setOnClickListener(v -> {
            if (gold >= upgradeCost) {
                gold -= upgradeCost;
                dps *= 2;
                upgradeCost *= 1.7;

                saveProgressToDatabase();

                updateSkillStatsUI(skillStats);
                Toast.makeText(this, "Upgraded! DPS: " + dps + ", Gold: " + gold, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Not enough gold to upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateSkillStatsUI(TextView skillStats) {
        skillStats.setText("Damage: " + dps + "\nUpgrade Cost: " + upgradeCost);
    }


}
