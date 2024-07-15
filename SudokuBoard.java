
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SudokuBoard extends JFrame {

    private static final int BOARD_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int EMPTY_CELL = 0;

    private JTextField[][] textFields;
    private int[][] board;
    private JButton solveButton;
    private JButton newGameButton;
    private JButton manualEntryButton;

    public SudokuBoard() {
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
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || c == '0') {
                            e.consume(); // Ignore the input
                        }
                    }
                });

                boardPanel.add(textFields[i][j]);
            }
        }
        return boardPanel;
    }

    private Border createCellBorder(int row, int col) {
        int top = (row % SUBGRID_SIZE == 0) ? 2 : 1;
        int left = (col % SUBGRID_SIZE == 0) ? 2 : 1;
        int bottom = ((row + 1) % SUBGRID_SIZE == 0) ? 2 : 1;
        int right = ((col + 1) % SUBGRID_SIZE == 0) ? 2 : 1;

        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        solveButton = new JButton("Solve");
        newGameButton = new JButton("New Game");
        manualEntryButton = new JButton("Manual Entry");

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateNewGame();
            }
        });

        manualEntryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manualEntry();
            }
        });

        buttonPanel.add(solveButton);
        buttonPanel.add(newGameButton);
        buttonPanel.add(manualEntryButton);
        return buttonPanel;
    }

    private void solveSudoku() {
        readBoardFromTextFields(); // Read the current state of the board
        long startTime = System.nanoTime();

        if (solveUsingBruteForce()) {
            long endTime = System.nanoTime();
            double timeTaken = (endTime - startTime) / 1.0_000_000;
            updateBoardGUI();
            JOptionPane.showMessageDialog(this, "Sudoku solved successfully in " + timeTaken + " milliseconds!");
        } else {
            JOptionPane.showMessageDialog(this, "No solution exists for the given Sudoku board.");
        }
    }

    private boolean solveUsingBruteForce() {
        return solveBruteForce(board);
    }

    private boolean solveBruteForce(int[][] board) {
        int row = -1, col = -1;
        boolean isEmpty = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_CELL) {
                    row = i;
                    col = j;
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                break;
            }
        }

        // No empty cell left, puzzle is solved
        if (isEmpty) {
            return true;
        }

        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;
                if (solveBruteForce(board)) {
                    return true;
                }
                board[row][col] = EMPTY_CELL;
            }
        }
        return false; // Trigger backtracking
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

    private boolean isSolved() {
        // Check if all cells are filled
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY_CELL) {
                    return false;
                }
            }
        }
        return true;
    }

    private void generateNewGame() {
        clearBoard();
        solveUsingBruteForce(); // Solve a new Sudoku board using brute force
        removeNumbersFromBoard();
        updateBoardGUI();
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    private void removeNumbersFromBoard() {
        // Adjust difficulty by changing the number of cells to remove
        int cellsToRemove = 50; // Adjust difficulty by changing the number of cells to remove

        while (cellsToRemove > 0) {
            int row = (int) (Math.random() * BOARD_SIZE);
            int col = (int) (Math.random() * BOARD_SIZE);
            if (board[row][col] != EMPTY_CELL) {
                board[row][col] = EMPTY_CELL;
                cellsToRemove--;
            }
        }
    }

    private void updateBoardGUI() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != EMPTY_CELL) {
                    textFields[i][j].setText(String.valueOf(board[i][j]));
                    textFields[i][j].setEditable(false);
                } else {
                    textFields[i][j].setText("");
                    textFields[i][j].setEditable(true);
                }
            }
        }
    }

    private void manualEntry() {
        clearBoard();
        updateBoardGUI();
    }

    private void readBoardFromTextFields() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                String text = textFields[i][j].getText();
                if (text.isEmpty()) {
                    board[i][j] = EMPTY_CELL;
                } else {
                    board[i][j] = Integer.parseInt(text);
                }
            }
        }
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check if num exists in current row
        for (int c = 0; c < BOARD_SIZE; c++) {
            if (board[row][c] == num) {
                return false;
            }
        }
        // Check if num exists in current column
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (board[r][col] == num) {
                return false;
            }
        }
        // Check if num exists in current 3x3 sub-grid
        int startRow = row - row % SUBGRID_SIZE;
        int startCol = col - col % SUBGRID_SIZE;
        for (int r = startRow; r < startRow + SUBGRID_SIZE; r++) {
            for (int c = startCol; c < startCol + SUBGRID_SIZE; c++) {
                if (board[r][c] == num) {
                    return false;
                }
            }
        }
        return true; // Valid placement
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SudokuBoard();
            }
        });
    }
}
