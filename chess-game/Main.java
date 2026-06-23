enum Color {
    WHITE, 
    BLACK
};

enum GameStatus {
    ACTIVE,
    CHECK, 
    CHECKMATE,
    STALEMATE
};

abstract class Piece {
    protected Color color;
    protected boolean hasMoved;

    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved() {
        hasMoved = true;
    }

    public abstract boolean canMove(Board board, int sr, int sc, int er, int ec);
}

class King extends Piece {
    public King(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        return Math.abs(sr-er) <= 1 && Math.abs(sc-ec) <= 1;
    }
}

class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        return sr == er || sc == ec || Math.abs(sr-er) == Math.abs(sc-ec);
    }
}

class Rook extends Piece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        return sr == er || sc == ec;
    }
}

class Bishop extends Piece {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        return Math.abs(sr-er) == Math.abs(sc-ec);
    }
}

class Knight extends Piece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dx = Math.abs(sr-er);
        int dy = Math.abs(sc-ec);
        return (dx == 2 && dy == 1) || (dy == 2 && dx == 1);
    }
}

class Pawn extends Piece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, int sr, int sc, int er, int ec) {
        int dir = color == Color.WHITE ? -1 : 1;
        return er == sr + dir && sc == ec;
    }
}

class Cell {
    private Piece piece;

    public Cell() {
        piece = null;
    }

    public Piece getPiece() {
        return piece;
    }
    
    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}

class Board {
    private Cell[][] board;

    public Board() {
        board = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = new Cell();
            }
        }
        init();
    }

    private void init() {
        board[0][4].setPiece(new King(Color.BLACK));
        board[7][4].setPiece(new King(Color.WHITE));

        board[0][3].setPiece(new Queen(Color.BLACK));
        board[7][3].setPiece(new Queen(Color.WHITE));

        board[0][0].setPiece(new Rook(Color.BLACK));
        board[0][7].setPiece(new Rook(Color.BLACK));

        board[7][0].setPiece(new Rook(Color.WHITE));
        board[7][7].setPiece(new Rook(Color.WHITE));

        board[0][2].setPiece(new Bishop(Color.BLACK));
        board[0][5].setPiece(new Bishop(Color.BLACK));

        board[7][2].setPiece(new Bishop(Color.WHITE));
        board[7][5].setPiece(new Bishop(Color.WHITE));

        board[0][1].setPiece(new Knight(Color.BLACK));
        board[0][6].setPiece(new Knight(Color.BLACK));

        board[7][1].setPiece(new Knight(Color.WHITE));
        board[7][6].setPiece(new Knight(Color.WHITE));

        for (int i = 0; i < 8; i++) {
            board[1][i].setPiece(new Pawn(Color.BLACK));
            board[6][i].setPiece(new Pawn(Color.WHITE));
        }
    }

    public Cell getCell(int row, int col) {
        return board[row][col];
    }
}

class Player {
    private String name;
    private Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}

class Move {
    int sr;
    int sc;
    int er;
    int ec;

    public Move(int sr, int sc, int er, int ec) {
        this.sr = sr;
        this.sc = sc;
        this.er = er;
        this.ec = ec;
    }
}

class ChessGame {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Player currPlayer;

    private GameStatus status;

    public ChessGame(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        currPlayer = whitePlayer;
        status = GameStatus.ACTIVE;
        board = new Board();
    }

    private boolean castle(Move move) {
        int row = move.sr;
        King king = (King) board.getCell(row, 4).getPiece();
        if(king == null || king.hasMoved()) {
            return false;
        }
        if(move.ec == 6) {
            Piece rookPiece = board.getCell(row, 7).getPiece();
            if(!(rookPiece instanceof Rook)) {
                return false;
            }
            Rook rook = (Rook)rookPiece;
            if(rook.hasMoved()) {
                return false;
            }
            if(board.getCell(row, 5).getPiece() != null || board.getCell(row, 6) != null) {
                return false;
            }
            
            board.getCell(row, 6).setPiece(king);
            board.getCell(row, 4).setPiece(null);

            board.getCell(row, 5).setPiece(rook);
            board.getCell(row, 7).setPiece(null);

            king.setMoved();
            rook.setMoved();

            switchTurn();

            return true;
        }
        if(move.ec == 2) {
            Piece rookPiece = board.getCell(row, 0).getPiece();
            if(!(rookPiece instanceof Rook)) {
                return false;
            }
            Rook rook = (Rook)rookPiece;
            if(rook.hasMoved()) {
                return false;
            }
            if(board.getCell(row, 1).getPiece() != null || board.getCell(row, 2).getPiece() != null || board.getCell(row, 3).getPiece() != null) {
                return false;
            }
            board.getCell(row, 2).setPiece(king);
            board.getCell(row, 4).setPiece(null);

            board.getCell(row, 3).setPiece(rook);
            board.getCell(row, 0).setPiece(null);

            king.setMoved();
            rook.setMoved();

            switchTurn();

            return true;
        }

        return false;
    }

    public boolean makeMove(Move move) {
        Piece piece = board.getCell(move.sr, move.sc).getPiece();
        
        if(piece == null) {
            return false;
        }

        if(piece.getColor() != currPlayer.getColor()) {
            return false;
        }

        if(piece instanceof King && move.sr == move.er && Math.abs(move.ec-move.sc) == 2) {
            return castle(move);
        }

        if(!piece.canMove(board, move.sr, move.sc, move.er, move.ec)) {
            return false;
        }

        Piece destination = board.getCell(move.er, move.ec).getPiece();

        if(destination != null && destination.getColor() == piece.getColor()) {
            return false;
        }

        board.getCell(move.er, move.ec).setPiece(piece);
        board.getCell(move.sr, move.sc).setPiece(null);
        switchTurn();
        
        return true;
    }

    private void switchTurn() {
        if(currPlayer == whitePlayer) {
            currPlayer = blackPlayer;
        }
        else {
            currPlayer = whitePlayer;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Player p1 = new Player("Alice", Color.WHITE);
        Player p2 = new Player("Bob", Color.BLACK);
        ChessGame game = new ChessGame(p1, p2);
        Move move = new Move(6, 0, 5, 0);
        boolean success = game.makeMove(move);
        System.out.println(success);
    }
}