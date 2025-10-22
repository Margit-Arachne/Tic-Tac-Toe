package com.example.tic_tac_toe;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tic_tac_toe.R;

public class ModeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);

        Button playerVsPlayerButton = findViewById(R.id.btnPlayerVsPlayer);
        Button playerVsComputerEasyButton = findViewById(R.id.btnPlayerVsComputerEasy);
        Button playerVsComputerHardButton = findViewById(R.id.btnPlayerVsComputerHard);

        if (playerVsPlayerButton != null) {
            playerVsPlayerButton.setOnClickListener(v -> launchGame(false, false));
        }

        if (playerVsComputerEasyButton != null) {
            playerVsComputerEasyButton.setOnClickListener(v -> launchGame(true, false));
        }

        if (playerVsComputerHardButton != null) {
            playerVsComputerHardButton.setOnClickListener(v -> launchGame(true, true));
        }
    }

    private void launchGame(boolean singlePlayer, boolean hardMode) {
        startActivity(MainActivity.createIntent(this, singlePlayer, hardMode));
        finish();
    }
}
