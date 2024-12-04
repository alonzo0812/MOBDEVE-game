package com.example.mobdevemain;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private int stage = 1;
    private int enemyIndex = 1;
    private double enemyHealth = 100.0;
    private double baseEnemyHealth = 100.0;
    private double dps = 25.0;
    private double gold = 0.0;
    private double upgradeCost;
    private TextView stageText, enemyHealthText, goldText;
    private Button attackButton;
    private ImageView enemyImage;
    private int isAchievementClaimed = 0;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStages = findViewById(R.id.button_stages);
        TextView enemyHealthText = findViewById(R.id.enemyHealthText);
        TextView goldText = findViewById(R.id.goldText);
        TextView dpsText = findViewById(R.id.dpsText);
        setupGestureDetector();
        SQLiteHelper dbHelper = new SQLiteHelper(this);

        username = getIntent().getStringExtra("username");

        Cursor cursor = dbHelper.getUserData(username);
        if (cursor.moveToFirst()) {
            dps = cursor.getDouble(cursor.getColumnIndexOrThrow("dps"));
            stage = cursor.getInt(cursor.getColumnIndexOrThrow("stage"));
            enemyIndex = cursor.getInt(cursor.getColumnIndexOrThrow("enemy"));
            gold = cursor.getDouble(cursor.getColumnIndexOrThrow("gold"));
            enemyHealth = cursor.getDouble(cursor.getColumnIndexOrThrow("enemy_health"));
            upgradeCost = cursor.getDouble(cursor.getColumnIndexOrThrow("upgrade_cost"));
            isAchievementClaimed = cursor.getInt(cursor.getColumnIndexOrThrow("achievement_claimed"));
            updateUI(buttonStages, enemyHealthText, goldText, dpsText);
        }
        cursor.close();

        startDpsTimer(buttonStages, enemyHealthText, goldText, dpsText);
        /* in case i add stages back
        findViewById(R.id.button_stages).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StagesActivity.class);
            startActivity(intent);
        });
        */
        findViewById(R.id.button_profile).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        findViewById(R.id.button_shop).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("gold", gold);
            intent.putExtra("upgradeCost", upgradeCost);
            intent.putExtra("dps", dps);
            startActivity(intent);
        });

        findViewById(R.id.button_achievements).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AchievementActivity.class);

            intent.putExtra("username", username);
            intent.putExtra("stage", stage);
            intent.putExtra("gold", gold);
            intent.putExtra("dps", dps);
            intent.putExtra("enemyIndex", enemyIndex);
            intent.putExtra("enemyHealth", enemyHealth);
            intent.putExtra("upgradeCost", upgradeCost);
            intent.putExtra("achievementClaimed", isAchievementClaimed);

            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            stage = data.getIntExtra("stage", stage);
            gold = data.getDoubleExtra("gold", gold);
            dps = data.getDoubleExtra("dps", dps);
            enemyIndex = data.getIntExtra("enemyIndex", enemyIndex);
            enemyHealth = data.getDoubleExtra("enemyHealth", enemyHealth);
            upgradeCost = data.getDoubleExtra("upgradeCost", upgradeCost);
            isAchievementClaimed = data.getIntExtra("achievementClaimed", isAchievementClaimed);

            Button buttonStages = findViewById(R.id.button_stages);
            TextView enemyHealthText = findViewById(R.id.enemyHealthText);
            TextView goldText = findViewById(R.id.goldText);
            TextView dpsText = findViewById(R.id.dpsText);

            updateUI(buttonStages, enemyHealthText, goldText, dpsText);
        }
    }

    /* 1st attempt at swipe
    private void setupGestureDetector() {
        enemyImage = findViewById(R.id.enemy_box);
        Button buttonStages = findViewById(R.id.button_stages);
        TextView goldText = findViewById(R.id.goldText);
        TextView dpsText = findViewById(R.id.dpsText);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                handleSwipe(buttonStages, goldText, dpsText);
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        enemyImage.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
    */

    private void setupGestureDetector() {
        View gestureOverlay = findViewById(R.id.gesture_overlay);
        Button buttonStages = findViewById(R.id.button_stages);
        TextView goldText = findViewById(R.id.goldText);
        TextView dpsText = findViewById(R.id.dpsText);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) { // Horizontal swipe
                    if (Math.abs(diffX) > 10 && Math.abs(velocityX) > 10) {
                        handleSwipe(buttonStages, goldText, dpsText);
                        return true;
                    }
                } else { // Vertical swipe
                    if (Math.abs(diffY) > 10 && Math.abs(velocityY) > 10) {
                        handleSwipe(buttonStages, goldText, dpsText);
                        return true;
                    }
                }
                return false;
            }
            //swipe bug fix
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        gestureOverlay.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }



    private void handleSwipe(Button buttonStages, TextView goldText, TextView dpsText) {
        enemyHealth -= dps;
        if (enemyHealth <= 0) {
            onEnemyDefeated(buttonStages, goldText, dpsText);
        }

        TextView enemyHealthText = findViewById(R.id.enemyHealthText);
        enemyHealthText.setText("Enemy Health: " + Math.max(0, enemyHealth));
    }

    private void startDpsTimer(Button buttonStages, TextView enemyHealthText, TextView goldText, TextView dpsText) {
        Handler dpsHandler = new Handler();
        dpsHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enemyHealth -= dps;
                if (enemyHealth <= 0) {
                    onEnemyDefeated(buttonStages, goldText, dpsText);
                }
                enemyHealthText.setText("Enemy Health: " + Math.max(0, enemyHealth));
                dpsHandler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void onEnemyDefeated(Button buttonStages, TextView goldText, TextView dpsText) {
        gold += 50 * stage * enemyIndex * 0.55;
        enemyIndex++;

        ImageView enemyImage = findViewById(R.id.enemy_box);
        int[][] enemyImages = {
                {R.drawable.hello_1_1, R.drawable.loops_1_2, R.drawable.array_1_3, R.drawable.flowchart_1_4, R.drawable.oop_1_5}, // Stage 1
                {R.drawable.graph_2_1, R.drawable.log_2_2, R.drawable.summation_2_3, R.drawable.sqrt_2_4, R.drawable.matrix_2_5}, // Stage 2
                {R.drawable.set_3_1, R.drawable.logic_3_2, R.drawable.permutation_3_3, R.drawable.combination_3_4, R.drawable.sequence_3_5}, // Stage 3
                {R.drawable.ruby_4_1, R.drawable.r_4_2, R.drawable.go_4_3, R.drawable.html_4_4, R.drawable.js_4_5}, // Stage 4
                {R.drawable.sql_5_1, R.drawable.database_5_2, R.drawable.searchdatabase_5_3, R.drawable.createdb_5_4, R.drawable.dbmanager_5_5}, // Stage 5
                {R.drawable.dfa_6_1, R.drawable.nfa_6_2, R.drawable.bubble_6_3, R.drawable.binary_6_4, R.drawable.turing_6_5}, // Stage 6
                {R.drawable.neural_7_1, R.drawable.ai_7_2, R.drawable.prolog_7_3, R.drawable.linear_7_4, R.drawable.machine_7_5}, // Stage 7
                {R.drawable.pandas_8_1, R.drawable.olap_8_2, R.drawable.queryopti_8_3, R.drawable.concurency_8_4, R.drawable.db_replica_8_5}, // Stage 8
                {R.drawable.agile_9_1, R.drawable.waterfall_9_2, R.drawable.scrum_9_3, R.drawable.cypress_9_4, R.drawable.qa_9_5}, // Stage 9
                {R.drawable.neural_7_1, R.drawable.matrix_2_5, R.drawable.turing_6_5, R.drawable.permutation_3_3, R.drawable.summation_2_3}, // Stage 10
        };

        if (stage >= 1 && stage <= 10 && enemyIndex >= 1 && enemyIndex <= 5) {
            enemyImage.setImageResource(enemyImages[stage - 1][enemyIndex - 1]);
        }

        if (enemyIndex > 4) {
            enemyIndex = 1;
            stage++;
        }
        if (stage > 10) {
            stage = 1;
        }

        enemyHealth = Math.floor(baseEnemyHealth * Math.pow(1.30, enemyIndex) * stage * 1.05);

        buttonStages.setText("Stage: " + stage + "-" + enemyIndex);
        goldText.setText("Gold: " + gold);
        dpsText.setText("DPS: " + dps);

        saveProgress();
    }

    private void updateUI(Button buttonStages, TextView enemyHealthText, TextView goldText, TextView dpsText) {
        buttonStages.setText("Stage: " + stage + "-" + enemyIndex);
        enemyHealthText.setText("Enemy Health: " + enemyHealth);
        goldText.setText("Gold: " + gold);
        dpsText.setText("DPS: " + dps);
    }

    private void saveProgress() {
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        dbHelper.updateUserProgress(username, dps, stage, enemyIndex, enemyHealth, gold, upgradeCost);

        if (isAchievementClaimed == 1) {
            dbHelper.markAchievementClaimed(username);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveProgress();
    }
}
