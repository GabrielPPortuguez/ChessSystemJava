package chess.pieces;

import board.Board;
import board.Position;
import chess.ChessPiece;
import chess.Color;
import board.Piece;

public class King extends ChessPiece {

    public King(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "K";
    }

    private boolean canMove(Position position) {
        ChessPiece p = (ChessPiece)getBoard().piece(position);
        return p == null || isThereOpponentPiece(position);
    }
    
    private boolean testRookCastling(Position position) {
        Piece p = getBoard().piece(position);
        return p != null && p instanceof Rook && ((ChessPiece)p).getColor() == getColor() && ((ChessPiece)p).isFirstMove();
    }
    
    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
        
        Position p = new Position(0, 0);

        p.setValues(position.getRow() - 1, position.getColumn());
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow() + 1, position.getColumn());
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow(), position.getColumn() - 1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow(), position.getColumn() + 1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        p.setValues(position.getRow() + 1, position.getColumn() + 1);
        if (getBoard().positionExists(p) && canMove(p)) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        if (isFirstMove()) {

            Position posR1 = new Position(position.getRow(), position.getColumn() + 3);
            if (testRookCastling(posR1)) {
                Position p1 = new Position(position.getRow(), position.getColumn() + 1); 
                Position p2 = new Position(position.getRow(), position.getColumn() + 2); 
                
                if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
                    mat[position.getRow()][position.getColumn() + 2] = true; 
                }
            }
            
            Position posR2 = new Position(position.getRow(), position.getColumn() - 4);
            if (testRookCastling(posR2)) {
                Position p1 = new Position(position.getRow(), position.getColumn() - 1); 
                Position p2 = new Position(position.getRow(), position.getColumn() - 2); 
                Position p3 = new Position(position.getRow(), position.getColumn() - 3); 
                
                if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
                    mat[position.getRow()][position.getColumn() - 2] = true; 
                }
            }
        }
        
        return mat;
    }
}