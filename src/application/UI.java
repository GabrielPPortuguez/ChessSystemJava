package application;

import java.util.InputMismatchException; 
import java.util.Scanner; 
import chess.ChessPiece; 
import chess.ChessPosition; 
import chess.Color; 
import chess.ChessMatch; 

public class UI {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m"; 
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m"; 
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printMatch(ChessMatch chessMatch) {
        printBoard(chessMatch.getPieces());
        System.out.println();
        
        if (chessMatch.getCheckMate()) {
            System.out.println(ANSI_RED_BACKGROUND + ANSI_WHITE + "!!! XEQUEMATE !!!" + ANSI_RESET);
            Color winner = (chessMatch.getCurrentPlayer() == Color.WHITE) ? Color.BLACK : Color.WHITE;
            System.out.println("VENCEDOR: " + winner);
        }
        else {
            System.out.println("Turno: " + chessMatch.getTurn());
            System.out.println("Aguardando o jogador: " + chessMatch.getCurrentPlayer());
            if (chessMatch.getCheck()) {
                System.out.println(ANSI_RED_BACKGROUND + ANSI_WHITE + "!!! XEQUE !!!" + ANSI_RESET);
            }
        }
    }
    public static void printBoard(ChessPiece[][] pieces) {
        printBoard(pieces, new boolean[pieces.length][pieces.length]);
    }
    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves) {
        System.out.println("-------------------------");
        
        for (int i = 0; i < pieces.length; i++) {
            System.out.print((8 - i) + " "); 
            
            for (int j = 0; j < pieces.length; j++) {
                if (possibleMoves[i][j]) {
                    System.out.print(ANSI_GREEN_BACKGROUND); 
                }
                printPiece(pieces[i][j]); 
                System.out.print(ANSI_RESET); 
            }
            System.out.println(); 
        }
        System.out.println("  a b c d e f g h");
        System.out.println("-------------------------");
    }

    private static void printPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print("- "); 
        } else {
            if (piece.getColor() == Color.WHITE) {
                System.out.print(ANSI_YELLOW + piece + ANSI_RESET + " ");
            } else {
                System.out.print(ANSI_WHITE + piece + ANSI_RESET + " ");
            }
        }
    }
    
    public static ChessPosition readChessPosition(Scanner sc) {
        try {
            String s = sc.nextLine().toLowerCase(); 
            char column = s.charAt(0);
            int row = Integer.parseInt(s.substring(1));
            return new ChessPosition(column, row);
        }
        catch (RuntimeException e) {
            throw new InputMismatchException("Erro lendo ChessPosition. Valores válidos são de a1 a h8.");
        }
    }
}