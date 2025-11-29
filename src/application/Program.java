package application;

import java.util.InputMismatchException;
import java.util.Scanner; 
import chess.ChessMatch; 
import chess.ChessPosition; 
import chess.ChessException; 

public class Program {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();

        while (!chessMatch.getCheckMate()) {

            try {

                UI.clearScreen(); 
                
                UI.printMatch(chessMatch); 
                System.out.println();
                System.out.print("Origem: ");
                ChessPosition source = UI.readChessPosition(sc); 
                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), possibleMoves); 
                System.out.println();
                System.out.print("Destino: ");
                ChessPosition target = UI.readChessPosition(sc);
                chessMatch.performChessMove(source, target);
            }

            catch (ChessException e) {
                System.out.println("Erro do Jogo: " + e.getMessage());
                sc.nextLine(); 
            }

            catch (InputMismatchException e) {
                System.out.println("Erro de Entrada: " + e.getMessage());
                sc.nextLine(); 
            }

            catch (Exception e) {
                System.out.println("Erro Inesperado: " + e.getMessage());
                sc.nextLine(); 
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch);
        sc.close();
    }
}