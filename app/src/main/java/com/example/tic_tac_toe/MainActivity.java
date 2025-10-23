package com.example.tic_tac_toe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int[][] board = new int[3][3];
    private final Button[][] buttons = new Button[3][3];
    private boolean isPlayerXTurn = true;
    private boolean isSinglePlayer = true;
    private boolean isHardMode = false;
    private TextView statusTextView;
    private VictoryLineView victoryLineView;
    private final Random random = new Random();
    private final HardAiActivity hardAi = new HardAiActivity();

    public static final String EXTRA_SINGLE_PLAYER = "com.example.tic_tac_toe.SINGLE_PLAYER";
    public static final String EXTRA_HARD_MODE = "com.example.tic_tac_toe.HARD_MODE";

    public static Intent createIntent(Context context, boolean singlePlayer, boolean hardMode) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_SINGLE_PLAYER, singlePlayer);
        intent.putExtra(EXTRA_HARD_MODE, hardMode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parseModeFromIntent();
        initBoard();
        statusTextView = findViewById(R.id.statusTextView);
        victoryLineView = findViewById(R.id.victoryLineView);
        Button resetButton = findViewById(R.id.resetButton);
        if (resetButton != null) {
            resetButton.setOnClickListener(v -> resetBoard());
        }
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

    private void parseModeFromIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        if (intent.hasExtra(EXTRA_SINGLE_PLAYER)) {
            isSinglePlayer = intent.getBooleanExtra(EXTRA_SINGLE_PLAYER, true);
        }
        if (intent.hasExtra(EXTRA_HARD_MODE)) {
            isHardMode = intent.getBooleanExtra(EXTRA_HARD_MODE, false);
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
        updateStatusText();
        maybePerformComputerMove();
    }

    private void resetBoard() {
        isPlayerXTurn = true;
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
        updateStatusText();
        if (victoryLineView != null) {
            victoryLineView.clear();
        }
    }

    private boolean checkWinner(int currentPlayer) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                announceWinner(currentPlayer, i, 0, i, 2);
                return true;
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                announceWinner(currentPlayer, 0, i, 2, i);
                return true;
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            announceWinner(currentPlayer, 0, 0, 2, 2);
            return true;
        }

        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            announceWinner(currentPlayer, 0, 2, 2, 0);
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
            if (statusTextView != null) {
                statusTextView.setText("平局");
            }
            disableAllButtons();
            return true;
        }

        return false;
    }

    private void announceWinner(int player, int startRow, int startCol, int endRow, int endCol) {
        String message = player == 1 ? "X 获胜" : "O 获胜";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (statusTextView != null) {
            statusTextView.setText(message);
        }
        disableAllButtons();
        if (victoryLineView != null) {
            victoryLineView.showLine(startRow, startCol, endRow, endCol);
        }
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

    private void updateStatusText() {
        if (statusTextView != null) {
            statusTextView.setText(isPlayerXTurn ? "轮到玩家 X" : "轮到玩家 O");
        }
    }

    private void maybePerformComputerMove() {
        if (!isSinglePlayer || isPlayerXTurn) {
            return;
        }
        int[] target;
        if (isHardMode) {
            int aiPlayer = isPlayerXTurn ? 1 : 2;
            HardAiActivity.Move move = hardAi.findBestMove(board, aiPlayer);
            if (move != null) {
                target = new int[]{move.row, move.col};
            } else {
                target = pickRandomEmptyCell();
            }
        } else {
            target = pickRandomEmptyCell();
        }

        if (target == null) {
            return;
        }

        int row = target[0];
        int col = target[1];
        Button button = buttons[row][col];
        if (button != null) {
            button.postDelayed(() -> handleMove(row, col), 250);
        } else {
            handleMove(row, col);
        }
    }

    private int[] pickRandomEmptyCell() {
        int[][] emptyCells = new int[9][2];
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    emptyCells[count][0] = i;
                    emptyCells[count][1] = j;
                    count++;
                }
            }
        }
        if (count == 0) {
            return null;
        }
        int choice = random.nextInt(count);
        return new int[]{emptyCells[choice][0], emptyCells[choice][1]};
    }

}
