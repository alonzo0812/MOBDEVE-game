package com.example.mobdevemain;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AchievementActivity extends AppCompatActivity {
    private Button claimBtn;
    private String username;
    private int stage;
    private int isAchievementClaimed;
    private double gold;
    private double dps;
    private int enemyIndex;
    private double enemyHealth;
    private double upgradeCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        claimBtn = findViewById(R.id.claimBtn);

        username = getIntent().getStringExtra("username");
        stage = getIntent().getIntExtra("stage", 1);
        gold = getIntent().getDoubleExtra("gold", 0);
        dps = getIntent().getDoubleExtra("dps", 0);
        enemyIndex = getIntent().getIntExtra("enemyIndex", 1);
        enemyHealth = getIntent().getDoubleExtra("enemyHealth", 100);
        upgradeCost = getIntent().getDoubleExtra("upgradeCost", 100);
        isAchievementClaimed = getIntent().getIntExtra("achievementClaimed", 0);

        updateButtonState();

        claimBtn.setOnClickListener(v -> {
            if (stage >= 4 && isAchievementClaimed == 0) {
                claimAchievement();
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(v -> {
            passDataBackToMain();
        });
    }

    private void updateButtonState() {
        if (stage >= 4 && isAchievementClaimed == 0) {
            claimBtn.setEnabled(true);
            claimBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_purple));
        } else {
            claimBtn.setEnabled(false);
            claimBtn.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));
        }
    }

    private void claimAchievement() {
        gold += 2000;

        isAchievementClaimed = 1;

        claimBtn.setEnabled(false);
        claimBtn.setBackgroundTintList(getColorStateList(android.R.color.darker_gray));

        Toast.makeText(this, "Achievement claimed! +2000 Gold", Toast.LENGTH_SHORT).show();

        SQLiteHelper dbHelper = new SQLiteHelper(this);
        dbHelper.markAchievementClaimed(username);
    }

    private void passDataBackToMain() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("stage", stage);
        resultIntent.putExtra("gold", gold); //+2000
        resultIntent.putExtra("dps", dps);
        resultIntent.putExtra("enemyIndex", enemyIndex);
        resultIntent.putExtra("enemyHealth", enemyHealth);
        resultIntent.putExtra("upgradeCost", upgradeCost);
        resultIntent.putExtra("achievementClaimed", isAchievementClaimed);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
