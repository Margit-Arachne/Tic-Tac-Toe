package com.example.tic_tac_toe;

public class HardAiActivity {

    public static final class Move {
        public final int row;
        public final int col;

        private Move(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private static final int BOARD_SIZE = 3;
    private static final int WIN_SCORE = 10;
    private static final int LOSS_SCORE = -10;
    private static final int DRAW_SCORE = 0;

    /**
     * 对外接口：基于当前 board 和 aiPlayer 返回最佳落子坐标
     * @param board int[3][3]  0=空，1=X，2=O
     * @param aiPlayer 1 或 2，表示 AI 使用的棋子
     * @return Move 最佳移动 (row, col). 如果没有合法落子，返回 null。
     */
    public Move findBestMove(int[][] board, int aiPlayer) {
        Move best = null;
        int bestScore = Integer.MIN_VALUE;
        int human = 3 - aiPlayer;

        // 遍历所有空格，尝试落子并调用 minimax
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == 0) {
                    board[r][c] = aiPlayer;
                    int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE, aiPlayer, human);
                    board[r][c] = 0;
                    if (score > bestScore) {
                        bestScore = score;
                        best = new Move(r, c);
                    }
                }
            }
        }
        return best;
    }

    /**
     * Minimax with Alpha-Beta pruning.
     * @param board 当前棋盘（会被递归内修改并回溯）
     * @param depth 当前深度（可用于对更浅获胜进行加权）
     * @param isMaximizing 是否为最大化玩家（AI）
     * @param alpha 当前 alpha
     * @param beta 当前 beta
     * @param aiPlayer AI 标识
     * @param human 对手标识
     * @return 该分支的评分值
     */
    private int minimax(int[][] board, int depth, boolean isMaximizing, int alpha, int beta, int aiPlayer, int human) {
        Integer winner = evaluateWinner(board);
        if (winner != null) {
            // 胜负或平局结束的基准分
            if (winner == aiPlayer) return WIN_SCORE - depth;     // 越早赢分数越高
            if (winner == human)    return LOSS_SCORE + depth;   // 越晚输损失越小
            return DRAW_SCORE;
        }

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (board[r][c] == 0) {
                        board[r][c] = aiPlayer;
                        int eval = minimax(board, depth + 1, false, alpha, beta, aiPlayer, human);
                        board[r][c] = 0;
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha) {
                            // Beta 剪枝
                            return maxEval;
                        }
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (board[r][c] == 0) {
                        board[r][c] = human;
                        int eval = minimax(board, depth + 1, true, alpha, beta, aiPlayer, human);
                        board[r][c] = 0;
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha) {
                            // Alpha 剪枝
                            return minEval;
                        }
                    }
                }
            }
            return minEval;
        }
    }

    /**
     * 判断当前棋盘是否达到终局（胜者或平局）
     * 返回：若有人获胜，返回该获胜方（1 或 2）；若平局返回 0；若未结束返回 null。
     */
    private Integer evaluateWinner(int[][] b) {
        // 检查行
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (b[r][0] != 0 && b[r][0] == b[r][1] && b[r][1] == b[r][2]) {
                return b[r][0];
            }
        }
        // 检查列
        for (int c = 0; c < BOARD_SIZE; c++) {
            if (b[0][c] != 0 && b[0][c] == b[1][c] && b[1][c] == b[2][c]) {
                return b[0][c];
            }
        }
        // 对角线
        if (b[0][0] != 0 && b[0][0] == b[1][1] && b[1][1] == b[2][2]) return b[0][0];
        if (b[0][2] != 0 && b[0][2] == b[1][1] && b[1][1] == b[2][0]) return b[0][2];

        // 检查是否还有空格
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (b[r][c] == 0) {
                    return null;
                }
            }
        }

        // 平局
        return 0;
    }
}
