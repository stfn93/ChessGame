import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGame {
    private static String[][] board = {
        {"R", "N", "B", "Q", "K", "B", "N", "R"}, // Black major pieces
        {"P", "P", "P", "P", "P", "P", "P", "P"}, // Black pawns
        {"", "", "", "", "", "", "", ""},         // Empty rows
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"p", "p", "p", "p", "p", "p", "p", "p"}, // White pawns
        {"r", "n", "b", "q", "k", "b", "n", "r"}  // White major pieces
    };
    
    private static JButton[][] buttons = new JButton[8][8];
    private static int selectedRow = -1;
    private static int selectedCol = -1;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess Game");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(8, 8));

        boolean isWhite = true;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton(getPieceIcon(board[row][col]));
                button.setFont(new Font("Serif", Font.PLAIN, 60)); // Font size for Unicode chess pieces
                button.setPreferredSize(new Dimension(100, 100)); // Ensure button size is visible
                button.setBackground(isWhite ? Color.WHITE : Color.GRAY);
                button.addActionListener(new ChessButtonListener(row, col));
                buttons[row][col] = button;
                frame.add(button);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }

        frame.pack(); // Pack the frame to fit the content
        frame.setVisible(true);
    }

    // Get icon or text representation of the piece
    private static String getPieceIcon(String piece) {
        switch (piece) {
            case "P": return "\u265F"; // Black pawn
            case "R": return "\u265C"; // Black rook
            case "N": return "\u265E"; // Black knight
            case "B": return "\u265D"; // Black bishop
            case "Q": return "\u265B"; // Black queen
            case "K": return "\u265A"; // Black king
            case "p": return "\u2659"; // White pawn
            case "r": return "\u2656"; // White rook
            case "n": return "\u2658"; // White knight
            case "b": return "\u2657"; // White bishop
            case "q": return "\u2655"; // White queen
            case "k": return "\u2654"; // White king
            default: return "";
        }
    }

    // Update the board UI after a move
    private static void updateBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                buttons[row][col].setText(getPieceIcon(board[row][col]));
            }
        }
    }

    // Event listener class for buttons
    private static class ChessButtonListener implements ActionListener {
        private int row, col;

        public ChessButtonListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedRow == -1 && selectedCol == -1) { // First click: select a piece
                if (!board[row][col].isEmpty()) { // Only select if there's a piece
                    selectedRow = row;
                    selectedCol = col;
                    buttons[row][col].setBackground(Color.YELLOW); // Highlight the selected piece
                }
            } else { // Second click: move the piece
                if (isValidMove(selectedRow, selectedCol, row, col)) {
                    board[row][col] = board[selectedRow][selectedCol]; // Move the piece
                    board[selectedRow][selectedCol] = ""; // Clear the original spot
                    updateBoard();
                }
                // Reset selection
                buttons[selectedRow][selectedCol].setBackground((selectedRow + selectedCol) % 2 == 0 ? Color.WHITE : Color.GRAY);
                selectedRow = -1;
                selectedCol = -1;
            }
        }
    }

    // Validate the move based on piece type and chess rules
    private static boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        String piece = board[fromRow][fromCol];
        String targetPiece = board[toRow][toCol];

        if (piece.isEmpty() || (targetPiece.isEmpty() && toRow == fromRow && toCol == fromCol)) {
            return false; // No piece to move or move to same spot
        }

        if (!targetPiece.isEmpty() && Character.isUpperCase(piece.charAt(0)) == Character.isUpperCase(targetPiece.charAt(0))) {
            return false; // Can't capture own piece
        }

        switch (piece.toLowerCase()) {
            case "p": return isValidPawnMove(fromRow, fromCol, toRow, toCol, piece);
            case "r": return isValidRookMove(fromRow, fromCol, toRow, toCol);
            case "n": return isValidKnightMove(fromRow, fromCol, toRow, toCol);
            case "b": return isValidBishopMove(fromRow, fromCol, toRow, toCol);
            case "q": return isValidQueenMove(fromRow, fromCol, toRow, toCol);
            case "k": return isValidKingMove(fromRow, fromCol, toRow, toCol);
            default: return false;
        }
    }

    // Validate pawn movement
    private static boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, String piece) {
        int direction = piece.equals("P") ? 1 : -1; // Black pawns move down, white pawns move up
        if (fromCol == toCol && board[toRow][toCol].isEmpty()) {
            if (toRow == fromRow + direction && fromRow != 1 && fromRow != 6) return true; // Single square move
            if (toRow == fromRow + 2 * direction && fromRow == (piece.equals("P") ? 1 : 6) && board[toRow][toCol].isEmpty()) return true; // Double square move
        }
        if (Math.abs(fromCol - toCol) == 1 && toRow == fromRow + direction && !board[toRow][toCol].isEmpty()) {
            return true; // Capture move
        }
        return false;
    }

    // Validate rook movement
    private static boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow != toRow && fromCol != toCol) return false; // Rooks move in straight lines
        if (isPathClear(fromRow, fromCol, toRow, toCol)) return true;
        return false;
    }

    // Validate knight movement
    private static boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) return true;
        return false;
    }

    // Validate bishop movement
    private static boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) return false; // Bishops move diagonally
        if (isPathClear(fromRow, fromCol, toRow, toCol)) return true;
        return false;
    }

    // Validate queen movement
    private static boolean isValidQueenMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == toRow || fromCol == toCol) return isValidRookMove(fromRow, fromCol, toRow, toCol);
        if (Math.abs(fromRow - toRow) == Math.abs(fromCol - toCol)) return isValidBishopMove(fromRow, fromCol, toRow, toCol);
        return false;
    }

    // Validate king movement
    private static boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        if ((rowDiff <= 1 && colDiff <= 1) && (rowDiff + colDiff > 0)) return true;
        return false;
    }

    // Check if path is clear for straight-line or diagonal movement
    private static boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);
        int row = fromRow + rowStep;
        int col = fromCol + colStep;
        while (row != toRow || col != toCol) {
            if (!board[row][col].isEmpty()) return false; // Blocked by another piece
            row += rowStep;
            col += colStep;
        }
        return true;
    }
}

