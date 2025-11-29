package chess;

import board.Board; 
import board.Piece;
import board.Position;
import chess.pieces.Rook; 
import chess.pieces.King;
import chess.pieces.Queen;
import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import java.util.List;
import java.util.ArrayList;

public class ChessMatch {

    private Board board;
    private Color currentPlayer; 
    private int turn; 
    private boolean check; 
    private boolean checkMate; 
    private ChessPiece enPassantVulnerable;

    public ChessMatch() {
        board = new Board(8, 8);
        initialSetup(); 
        turn = 1;
        currentPlayer = Color.WHITE; 
        check = false;
        checkMate = false; 
    }
    
    public int getTurn() { return turn; }
    public Color getCurrentPlayer() { return currentPlayer; }
    public boolean getCheck() { return check; }
    public boolean getCheckMate() { return checkMate; }
    public ChessPiece getEnPassantVulnerable() { return enPassantVulnerable; }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i=0; i<board.getRows(); i++) {
            for (int j=0; j<board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j); 
            }
        }
        return mat;
    }
    
    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position); 
        return board.piece(position).possibleMoves();
    }
    
    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        
        validateSourcePosition(source);
        validateTargetPosition(source, target);

        Piece capturedPiece = makeMove(source, target); 
        
        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece); 
            throw new ChessException("Você não pode se colocar em xeque!");
        }
        
        ChessPiece movedPiece = (ChessPiece)board.piece(target); 
        
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
            enPassantVulnerable = movedPiece; 
        }
        else {
            enPassantVulnerable = null; 
        }
        
        check = (testCheck(opponent(currentPlayer))) ? true : false;
        
        if (testCheck(opponent(currentPlayer)) && testCheckMate(opponent(currentPlayer))) {
            checkMate = true;
        }

        nextTurn();
        
        return (ChessPiece)capturedPiece;
    }
    
    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece)board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        
        if (p instanceof Pawn && target.getColumn() != source.getColumn() && capturedPiece == null) {
            Position pawnCapturedPosition;
            if (p.getColor() == Color.WHITE) {
                pawnCapturedPosition = new Position(target.getRow() + 1, target.getColumn());
            }
            else {
                pawnCapturedPosition = new Position(target.getRow() - 1, target.getColumn());
            }
            capturedPiece = board.removePiece(pawnCapturedPosition); 
        }

        board.placePiece(p, target);
        
        if (p instanceof King) {

            if (target.getColumn() == source.getColumn() + 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() + 3); 
                Position targetR = new Position(source.getRow(), source.getColumn() + 1); 
                ChessPiece rook = (ChessPiece)board.removePiece(sourceR);
                board.placePiece(rook, targetR);
                rook.increaseMoveCount();
            }

            if (target.getColumn() == source.getColumn() - 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() - 4); 
                Position targetR = new Position(source.getRow(), source.getColumn() - 1); 
                ChessPiece rook = (ChessPiece)board.removePiece(sourceR);
                board.placePiece(rook, targetR);
                rook.increaseMoveCount();
            }
        }
        
        return capturedPiece;
    }
    
    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece)board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);

        if (capturedPiece != null) {

            if (p instanceof Pawn && target.getColumn() != source.getColumn() && board.piece(target) == null) {
                 Position pawnCapturedPosition;
                 if (p.getColor() == Color.WHITE) {
                     pawnCapturedPosition = new Position(3, target.getColumn());
                 }
                 else {
                     pawnCapturedPosition = new Position(4, target.getColumn());
                 }
                 board.placePiece(capturedPiece, pawnCapturedPosition);
            }
            else {
                board.placePiece(capturedPiece, target);
            }
        }
        
        if (p instanceof King) {

            if (target.getColumn() == source.getColumn() + 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() + 3); 
                Position targetR = new Position(source.getRow(), source.getColumn() + 1); 
                ChessPiece rook = (ChessPiece)board.removePiece(targetR);
                board.placePiece(rook, sourceR);
                rook.decreaseMoveCount();
            }

            if (target.getColumn() == source.getColumn() - 2) {
                Position sourceR = new Position(source.getRow(), source.getColumn() - 4); 
                Position targetR = new Position(source.getRow(), source.getColumn() - 1); 
                ChessPiece rook = (ChessPiece)board.removePiece(targetR);
                board.placePiece(rook, sourceR);
                rook.decreaseMoveCount();
            }
        }
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private List<Piece> piecesOnTheBoard(Color color) {
        List<Piece> list = new ArrayList<>();
        for (int i=0; i<board.getRows(); i++) {
            for (int j=0; j<board.getColumns(); j++) {
                Piece p = board.piece(i, j);
                if (p != null && ((ChessPiece)p).getColor() == color) {
                    list.add(p);
                }
            }
        }
        return list;
    }

    private ChessPiece King(Color color) {
        for (int i=0; i<board.getRows(); i++) {
            for (int j=0; j<board.getColumns(); j++) {
                if (board.piece(i, j) != null && board.piece(i, j) instanceof King && ((ChessPiece)board.piece(i, j)).getColor() == color) {
                    return (ChessPiece)board.piece(i, j);
                }
            }
        }
        throw new IllegalStateException("Não existe o Rei " + color + " no tabuleiro!");
    }

    protected boolean testCheck(Color color) {
        Position KingPosition = King(color).getChessPosition().toPosition();
        Color opponent = opponent(color);

        for (int i=0; i<board.getRows(); i++) {
            for (int j=0; j<board.getColumns(); j++) {
                Piece p = board.piece(i, j);
                if (p != null && ((ChessPiece)p).getColor() == opponent) {
                    boolean[][] mat = p.possibleMoves();
                    if (mat[KingPosition.getRow()][KingPosition.getColumn()]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    protected boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false; 
        }
        
        List<Piece> list = piecesOnTheBoard(color);
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves(); 
            
            for (int i=0; i<board.getRows(); i++) {
                for (int j=0; j<board.getColumns(); j++) {
                    if (mat[i][j]) { 
                        Position source = ((ChessPiece)p).getChessPosition().toPosition(); 
                        Position target = new Position(i, j);
                        Piece captured = makeMove(source, target); 
                        boolean testCheck = testCheck(color); 
                        undoMove(source, target, captured); 
                        
                        if (!testCheck) {
                            return false; 
                        }
                    }
                }
            }
        }
        return true; 
    }

    private void validateSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("Não há peça na posição de origem.");
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
            throw new ChessException("A peça escolhida não é sua.");
        }
        if (!((ChessPiece)board.piece(position)).isThereAnyPossibleMove()) {
            throw new ChessException("Não há movimentos possíveis para a peça selecionada.");
        }
    }
    
    private void validateTargetPosition(Position source, Position target) {

        ChessPiece sourcePiece = (ChessPiece)board.piece(source);
        if (!sourcePiece.possibleMove(target)) { 
            throw new ChessException("A peça não pode se mover para a posição de destino.");
        }
        if (board.thereIsAPiece(target) && sourcePiece.getColor() == ((ChessPiece)board.piece(target)).getColor()) {
            throw new ChessException("Você não pode capturar sua própria peça.");
        }
    }
    
    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        for (char c = 'a'; c <= 'h'; c++) {
            placeNewPiece(c, 2, new Pawn(board, Color.WHITE, this));
        }
        
        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        for (char c = 'a'; c <= 'h'; c++) {
            placeNewPiece(c, 7, new Pawn(board, Color.BLACK, this));
        }
    }
}