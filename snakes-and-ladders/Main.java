import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
class Jump {
    private int start;
    private int end;
    public Jump(int start, int end) {
        this.start = start;
        this.end = end;
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
}
class Board {
    private int size;
    private Map<Integer, Jump> jumps;
    public Board(int size) {
        this.size = size;
        this.jumps = new HashMap<>();
    }
    public int getSize() {
        return size;
    }
    public void addJump(int start, int end) {
        jumps.put(start, new Jump(start, end));
    }
    public int getNextPos(int pos) {
        if(jumps.containsKey(pos)) {
            Jump jump = jumps.get(pos);
            if(jump.getStart() < jump.getEnd()) {
                System.out.println("Ladder: climb up from: " + jump.getStart() + " to " + jump.getEnd());
            }
            else {
                System.out.println("Snake: climb down from: " + jump.getStart() + " to " + jump.getEnd());
            }
            return jump.getEnd();
        }
        return pos;
    }
}
class Dice {
    private int noOfDice;
    private Random random;
    public Dice(int noOfDice) {
        this.noOfDice = noOfDice;
        this.random = new Random();
    }   
    public int roll() {
        int total = 0;
        for(int i = 0; i < noOfDice; i++) {
            total += random.nextInt(6) + 1;
        }
        return total;
    }
}
class Player {
    private String name;
    private int pos;
    public Player(String name) {
        this.name = name;
        this.pos = 0;
    }
    public String getName() {
        return name;
    }
    public int getPos() {
        return pos;
    }
    public void setPos(int pos) {
        this.pos = pos; 
    }
}
class Game {
    private Queue<Player> players;
    private Board board;
    private Dice dice;
    private boolean isGameOver;
    public Game(List<Player> playersList, Board board, Dice dice) {
        this.players = new LinkedList<>(playersList);
        this.board = board;
        this.dice = dice;
        this.isGameOver = false;
    }
    private void playTurn(Player curr) {
        System.out.println("\n" + curr.getName() + "'s turn");
        int diceRoll = dice.roll();
        System.out.println("Dice Rolled: " + diceRoll);
        int currPos = curr.getPos();
        int newPos = currPos + diceRoll;
        if(newPos > board.getSize()) {
            System.out.println("Move exceeds board limit. Staying at: " + currPos);
            return;
        }
        newPos = board.getNextPos(newPos);
        curr.setPos(newPos);
        System.out.println(curr.getName() + " moved to position " + newPos);
        if(newPos == board.getSize()) {
            System.out.println("\n" + curr.getName() + " wins the game. Congratulations!!!");
            isGameOver = true;
        }
    }
    public void startGame() {
        while(!isGameOver) {
            Player curr = players.poll();
            playTurn(curr);
            if(!isGameOver) {
                players.offer(curr);
            }
        }
    }
}
public class Main {
    public static void main(String[] args) {
        Board board = new Board(100);
        // Snakes
        board.addJump(99, 2);
        board.addJump(70, 55);
        board.addJump(52, 42);
        board.addJump(25, 19);
        // Ladders
        board.addJump(6, 45);
        board.addJump(11, 50);
        board.addJump(60, 85);
        board.addJump(46, 90);
        
        Dice dice = new Dice(1);

        Player p1 = new Player("Alice");
        Player p2 = new Player("Bob");
        
        Game game = new Game(Arrays.asList(p1, p2), board, dice);
        game.startGame();
    }
}