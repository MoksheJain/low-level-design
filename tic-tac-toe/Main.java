import java.util.Stack;

enum PlayerMark {
    X, 
    O, 
    EMPTY
}

class Cell {
    private PlayerMark mark;
    
    public Cell() {
        this.mark = PlayerMark.EMPTY;
    }

    public PlayerMark getMark() {
        return  mark;
    }

    public void setMark(PlayerMark mark) {
        this.mark = mark;
    }

    public boolean isEmpty() {
        return mark == PlayerMark.EMPTY;
    }
}

class Board {
    private final int size;
    private final Cell[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public boolean placeMark(int row, int col, PlayerMark mark) {
        if(row < 0 || row >= size || col < 0 || col >= size || !grid[row][col].isEmpty()) {
            return false;
        }

        grid[row][col].setMark(mark);
        return true;
    }

    public void clearCell(int row, int col) {
        grid[row][col].setMark(PlayerMark.EMPTY);
    }

    public PlayerMark getMark(int row, int col) {
        return grid[row][col].getMark();
    }

    public int getSize() {
        return size;
    }

    public boolean isFull() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(grid[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void display() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                System.out.print(grid[i][j].getMark() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

class Player {
    private final String name;
    private final PlayerMark mark;

    public Player(String name, PlayerMark mark) {
        this.name = name;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public PlayerMark getMark() {
        return mark;
    }
}

interface Command {
    void execute();
    void undo();
}

class MoveCommand implements Command {
    private final Board board;
    private final int row;
    private final int col;
    private final PlayerMark mark;

    public MoveCommand(Board board, int row, int col, PlayerMark mark) {
        this.board = board;
        this.row = row;
        this.col = col;
        this.mark = mark;
    }

    @Override
    public void execute() {
        board.placeMark(row, col, mark);
    }

    @Override
    public void undo() {
        board.clearCell(row, col);
    }
}

enum GameStatus {
    IN_PROGRESS,
    DRAW,
    WINNER
}

class Game {
    private final Board board;
    private final Player[] players;
    private int currentPlayerIndex;
    private Stack<Command> history;
    private GameStatus status;
    
    public Game(Player p1, Player p2, int size) {
        this.board = new Board(size);
        this.players = new Player[]{p1, p2};
        this.currentPlayerIndex = 0;
        this.history = new Stack<>();
        this.status = GameStatus.IN_PROGRESS;
    }

    public boolean makeMove(int row, int col) {
        Player currentPlayer = players[currentPlayerIndex];
        if(!board.placeMark(row, col, currentPlayer.getMark())) {
            return false;
        }

        Command move = new MoveCommand(board, row, col, currentPlayer.getMark());
        history.push(move);
        checkMoveStatus(row, col);
        if(status == GameStatus.IN_PROGRESS) {
            currentPlayerIndex = 1 - currentPlayerIndex;
        }

        return true;
    }

    public void undoMove() {
        if(history.isEmpty()) {
            System.out.println("No move to undo");
            return;
        }

        Command lastMove = history.pop();
        lastMove.undo();
        currentPlayerIndex = 1 - currentPlayerIndex;
        status = GameStatus.IN_PROGRESS;
    }

    private void checkMoveStatus(int row, int col) {
        PlayerMark mark = board.getMark(row, col);
        int n = board.getSize();
        boolean rowWin = true;
        boolean colWin = true;
        boolean diagWin = true;
        boolean diagWin2 = true;

        for(int i = 0; i < n; i++) {
            if(board.getMark(row, i) != mark) {
                rowWin = false;
            }
            if(board.getMark(i, col) != mark) {
                colWin = false;
            }
            if(board.getMark(i, i) != mark) {
                diagWin = false;
            }
            if(board.getMark(i, n-i-1) != mark) {
                diagWin2 = false;
            } 
        }

        if(rowWin || colWin || diagWin || diagWin2) {
            status = GameStatus.WINNER;
            return;
        }

        if(board.isFull()) {
            status = GameStatus.DRAW;
        }
    }

    public void displayBoard() {
        board.display();
    }

    public GameStatus getStatus() {
        return status;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }
}

public class Main {
    public static void main(String[] args) {
        Player p1 = new Player("Mokshe", PlayerMark.X);
        Player p2 = new Player("Computer", PlayerMark.O);

        Game game = new Game(p1, p2, 3);

        game.makeMove(0, 0);
        game.makeMove(1, 1);
        game.makeMove(1, 0);

        game.displayBoard();
        game.undoMove();
        
        System.out.println("After undo:");

        game.displayBoard();
    }
}