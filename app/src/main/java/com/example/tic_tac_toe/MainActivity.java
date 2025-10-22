package com.example.tic_tac_toe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final int[][] board = new int[3][3];
    private final Button[][] buttons = new Button[3][3];
    private boolean isPlayerXTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBoard();
        resetBoard();
    }

    private void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resId = getResources().getIdentifier("button" + i + j, "id", getPackageName());
                Button button = findViewById(resId);
                buttons[i][j] = button;
                final int row = i;
                final int col = j;
                if (button != null) {
                    button.setOnClickListener(v -> handleMove(row, col));
                }
            }
        }
    }

    private void handleMove(int row, int col) {
        if (board[row][col] != 0) {
            return;
        }
        int currentPlayer = isPlayerXTurn ? 1 : 2;
        board[row][col] = currentPlayer;
        Button button = buttons[row][col];
        if (button != null) {
            button.setText(currentPlayer == 1 ? "X" : "O");
            button.setEnabled(false);
        }
        if (checkWinner(currentPlayer)) {
            return;
        }
        isPlayerXTurn = !isPlayerXTurn;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0;
                Button button = buttons[i][j];
                if (button != null) {
                    button.setText("");
                    button.setEnabled(true);
                }
            }
        }
        isPlayerXTurn = true;
    }

    private boolean checkWinner(int currentPlayer) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                announceWinner(currentPlayer);
                return true;
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                announceWinner(currentPlayer);
                return true;
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            announceWinner(currentPlayer);
            return true;
        }

        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            announceWinner(currentPlayer);
            return true;
        }

        boolean isBoardFull = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    isBoardFull = false;
                    break;
                }
            }
            if (!isBoardFull) {
                break;
            }
        }

        if (isBoardFull) {
            Toast.makeText(this, "平局", Toast.LENGTH_SHORT).show();
            disableAllButtons();
            return true;
        }

        return false;
    }

    private void announceWinner(int player) {
        String message = player == 1 ? "X 获胜" : "O 获胜";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        disableAllButtons();
    }

    private void disableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = buttons[i][j];
                if (button != null) {
                    button.setEnabled(false);
                }
            }
        }
    }
}
