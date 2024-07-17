import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Queue;

public class Sudoku extends JFrame {

    private static final int BOARD_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int EMPTY_CELL = 0;

    private JTextField[][] textFields;
    private int[][] board;
    private JButton solveButton;
    private JButton newGameButton;
    private JButton manualEntryButton;

    public Sudoku() {
        setTitle("Sudoku Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeBoard();
        add(createBoardPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initializeBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        textFields = new JTextField[BOARD_SIZE][BOARD_SIZE];
        // Initialize all cells to 0 (empty)
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        // Create text fields for each cell in the Sudoku board
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                textFields[i][j] = new JTextField(2);
                textFields[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                textFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                textFields[i][j].setBorder(createCellBorder(i, j));

                // Add key listener for input validation
                textFields[i][j].addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!((c >= '1') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                            getToolkit().beep();
                            e.consume();
                        }
                    }
                });

                boardPanel.add(textFields[i][j]);
            }
        }
        return boardPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        solveButton = new JButton("Solve");
        newGameButton = new JButton("New Game");
        manualEntryButton = new JButton("Manual Entry");

        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateNewGame();
            }
        });

        manualEntryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                manualEntry();
            }
        });

        buttonPanel.add(solveButton);
        buttonPanel.add(newGameButton);
        buttonPanel.add(manualEntryButton);

        return buttonPanel;
    }

    private Border createCellBorder(int row, int col) {
        int top = 1, left = 1, bottom = 1, right = 1;
        if (row % SUBGRID_SIZE == 0) {
            top = 2;
        }
        if (col % SUBGRID_SIZE == 0) {
            left = 2;
        }
        if ((row + 1) % SUBGRID_SIZE == 0) {
            bottom = 2;
        }
        if ((col + 1) % SUBGRID_SIZE == 0) {
            right = 2;
        }
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }

    private void solveSudoku() {
        int[][] boardCopy = copyBoard(board);
        if (solveUsingBFSWithBacktracking(boardCopy)) {
            board = boardCopy;
            updateBoard();
        } else {
            JOptionPane.showMessageDialog(this, "No solution exists for the given Sudoku.");
        }
    }

    private void generateNewGame() {
        System.out.println("Generating new game...");
        // Add your logic to generate a new game here
        clearBoard();
    }

    private void manualEntry() {
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                textFields[i][j].setText("");
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    private void updateBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != EMPTY_CELL) {
                    textFields[i][j].setText(String.valueOf(board[i][j]));
                } else {
                    textFields[i][j].setText("");
                }
            }
        }
    }

    private int[][] copyBoard(int[][] originalBoard) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newBoard[i][j] = originalBoard[i][j];
            }
        }
        return newBoard;
    }

    private boolean solveUsingBFSWithBacktracking(int[][] board) {
        class BoardState {
            int[][] board;
            int row;
            int col;

            BoardState(int[][] board, int row, int col) {
                this.board = board;
                this.row = row;
                this.col = col;
            }
        }

        Queue<BoardState> queue = new LinkedList<>();
        queue.add(new BoardState(copyBoard(board), 0, 0));

        while (!queue.isEmpty()) {
            BoardState currentState = queue.poll();
            int[][] currentBoard = currentState.board;
            int row = currentState.row;
            int col = currentState.col;

            // Find the next empty cell
            while (row < BOARD_SIZE && currentBoard[row][col] != EMPTY_CELL) {
                col++;
                if (col == BOARD_SIZE) {
                    col = 0;
                    row++;
                }
            }

            // If there are no empty cells, the board is solved
            if (row == BOARD_SIZE) {
                for (int i = 0; i < BOARD_SIZE; i++) {
                    for (int j = 0; j < BOARD_SIZE; j++) {
                        board[i][j] = currentBoard[i][j];
                    }
                }
                return true;
            }

            // Try all possible values for the current cell
            for (int num = 1; num <= BOARD_SIZE; num++) {
                if (isValid(currentBoard, row, col, num)) {
                    int[][] newBoard = copyBoard(currentBoard);
                    newBoard[row][col] = num;
                    queue.add(new BoardState(newBoard, row, col));
                }
            }
        }

        return false;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check the row
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }

        // Check the column
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }

        // Check the 3x3 subgrid
        int startRow = row / SUBGRID_SIZE * SUBGRID_SIZE;
        int startCol = col / SUBGRID_SIZE * SUBGRID_SIZE;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (board[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Sudoku();
            }
        });
    }
}
