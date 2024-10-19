package com.example.mobdevemain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class StagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stages);

        // Additional logic for MC03 unlocking stages or displaying completion status
        LinearLayout stageContainer = findViewById(R.id.stage_container);

        // Create buttons dynamically
        for (int i = 1; i <= 20; i++) {
            Button stageButton = new Button(this);
            stageButton.setText("Stage " + i);
            stageButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));


            stageContainer.addView(stageButton);
        }

        Button backToMainButton = findViewById(R.id.button_back_to_main);
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StagesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
