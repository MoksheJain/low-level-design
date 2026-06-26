package cricbuzz;

import java.util.ArrayList;
import java.util.List;

enum BallType {
    NORMAL,
    WIDE,
    NO_BALL,
    BYE,
    LEG_BYE
}

enum WicketType {
    NONE,
    BOWLED,
    CAUGHT,
    LBW,
    RUN_OUT,
    STUMPED
}

enum MatchStatus {
    NOT_STARTED,
    LIVE,
    FINISHED
}

class PlayerStats {
    private int runs;
    private int balls;
    private int fours;
    private int sixes;

    private int wickets;
    private int ballsBowled;
    private int runsGiven;

    public void addBattingRuns(int run) {
        runs += run;
        balls++;

        if (run == 4) {
            fours++;
        }
        if (run == 6) {
            sixes++;
        }
    }

    public void dotBall() {
        balls++;
    }

    public void addBowling(int run) {
        ballsBowled++;
        runsGiven += run;
    }

    public void takeWicket() {
        wickets++;
    }

    public int getRuns() {
        return runs;
    }

    public int getBalls() {
        return balls;
    }

    public int getRunsGiven() {
        return runsGiven;
    }

    public int getBallsBowled() {
        return ballsBowled;
    }

    public double getStrikeRate() {
        if (balls == 0) {
            return 0;
        }
        return runs * 100.00 / balls;
    }

    public double getEconomy() {
        if (ballsBowled == 0) {
            return 0;
        }
        return runsGiven * 6.0 / ballsBowled;
    }
}

class Player {
    private String name;
    private PlayerStats stats;

    public Player(String name) {
        this.name = name;
        this.stats = new PlayerStats();
    }

    public String getName() {
        return name;
    }

    public PlayerStats getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return name;
    }
}

class Team {
    private String name;
    private List<Player> players;

    public Team(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player player) {
        if (players.size() == 11) {
            throw new RuntimeException("Only 11 players are allowed");
        }
        players.add(player);
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }
}

class Ball {
    private Player batsman;
    private Player bowler;

    private int runs;

    private BallType ballType;

    private WicketType wicketType;

    public Ball(Player batsman, Player bowler, int runs, BallType ballType, WicketType wicketType) {
        this.batsman = batsman;
        this.bowler = bowler;
        this.runs = runs;
        this.ballType = ballType;
        this.wicketType = wicketType;
    }

    public Player getBatsman() {
        return batsman;
    }

    public Player getBowler() {
        return bowler;
    }

    public int getRuns() {
        return runs;
    }

    public BallType getBallType() {
        return ballType;
    }

    public WicketType getWicketType() {
        return wicketType;
    }

    public boolean isLegalBall() {
        return ballType != BallType.WIDE && ballType != BallType.NO_BALL;
    }

    public boolean isWicket() {
        return wicketType != WicketType.NONE;
    }
}

class Over {
    private Player bowler;
    private List<Ball> balls;

    public Over(Player bowler) {
        this.bowler = bowler;
        this.balls = new ArrayList<>();
    }

    public void addBall(Ball ball) {
        balls.add(ball);
    }

    public Player getBowler() {
        return bowler;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public boolean isCompleted() {
        int legalBalls = 0;
        for (Ball ball : balls) {
            if (ball.isLegalBall()) {
                legalBalls++;
            }
        }
        return legalBalls == 6;
    }
}

class Score {
    private int runs;
    private int wickets;
    private int legalBalls;
    private int extras;

    public void addRuns(int runs) {
        this.runs += runs;
    }

    public void addExtra(int runs) {
        extras += runs;
        this.runs += runs;
    }

    public void wicketFell() {
        wickets++;
    }

    public void legalBallCompleted() {
        legalBalls++;
    }

    public int getRuns() {
        return runs;
    }

    public int getWickets() {
        return wickets;
    }

    public int getLegalBalls() {
        return legalBalls;
    }

    public int getExtras() {
        return extras;
    }

    public String getOvers() {
        return (legalBalls / 6) + "." + (legalBalls % 6);
    }

    @Override
    public String toString() {
        return runs + "/" + wickets + " (" + getOvers() + ")";
    }
}

class Innings {
    private Team battingTeam;
    private Team bowlingTeam;
    private Score score;
    private List<Over> overs;
    private Player striker;
    private Player nonStriker;
    private Player currentBowler;
    private int nextBatsmanIndex = 2;

    public Innings(Team battingTeam, Team bowlingTeam, Player striker, Player nonStriker, Player bowler) {
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.striker = striker;
        this.nonStriker = nonStriker;
        this.currentBowler = bowler;

        score = new Score();
        overs = new ArrayList<>();
        overs.add(new Over(bowler));
    }

    public Team getBattingTeam() {
        return battingTeam;
    }

    public Team getBowlingTeam() {
        return bowlingTeam;
    }

    public Score getScore() {
        return score;
    }

    public List<Over> getOvers() {
        return overs;
    }

    public Over getCurrentOver() {
        return overs.get(overs.size() - 1);
    }

    public void startNewOver(Player bowler) {
        currentBowler = bowler;
        overs.add(new Over(bowler));
        rotateStrike();
    }

    public Player getCurrentBowler() {
        return currentBowler;
    }

    public Player getStriker() {
        return striker;
    }

    public Player getNonStriker() {
        return nonStriker;
    }

    public void rotateStrike() {
        Player temp = striker;
        striker = nonStriker;
        nonStriker = temp;
    }

    public void nextBatsman() {
        striker = battingTeam.getPlayer(nextBatsmanIndex++);
    }
}

interface MatchFormat {
    int getOvers();

    int getPlayersPerTeam();
}

class T20Format implements MatchFormat {
    @Override
    public int getOvers() {
        return 20;
    }

    @Override
    public int getPlayersPerTeam() {
        return 11;
    }
}

class Match {
    private Team t1;
    private Team t2;

    private MatchFormat format;
    private MatchStatus status;
    private Innings innings;

    public Match(Team t1, Team t2, MatchFormat format) {
        this.t1 = t1;
        this.t2 = t2;
        this.format = format;
        this.status = MatchStatus.NOT_STARTED;
    }

    public Team getTeam1() {
        return t1;
    }

    public Team getTeam2() {
        return t2;
    }

    public MatchFormat getFormat() {
        return format;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Innings getInnings() {
        return innings;
    }

    public void setInnings(Innings innings) {
        this.innings = innings;
    }
}

class BallService {
    private MatchSubject ms;

    public BallService(MatchSubject subject) {
        this.ms = subject;
    }

    public void bowlBall(Innings innings, Ball ball) {
        innings.getCurrentOver().addBall(ball);

        Player batsman = ball.getBatsman();
        Player bowler = ball.getBowler();

        PlayerStats batting = batsman.getStats();
        PlayerStats bowling = bowler.getStats();

        switch (ball.getBallType()) {
            case NORMAL:
                innings.getScore().legalBallCompleted();
                innings.getScore().addRuns(ball.getRuns());
                bowling.addBowling(ball.getRuns());

                if (ball.getRuns() == 0) {
                    batting.dotBall();
                } else {
                    batting.addBattingRuns(ball.getRuns());
                }
                break;
            case WIDE:
                innings.getScore().addExtra(1);
                break;
            case NO_BALL:
                innings.getScore().addExtra(1);
                break;
            case BYE:
                innings.getScore().legalBallCompleted();
                innings.getScore().addExtra(ball.getRuns());
                break;
            case LEG_BYE:
                innings.getScore().legalBallCompleted();
                innings.getScore().addExtra(ball.getRuns());
                break;
        }
        if (ball.isWicket()) {
            innings.getScore().wicketFell();
            bowling.takeWicket();
            innings.nextBatsman();
        }
        if ((ball.getBallType() == BallType.NORMAL && ball.getRuns() % 2 == 1)
                || innings.getCurrentOver().isCompleted()) {
            innings.rotateStrike();
        }
    }
}

class MatchService {
    public void startMatch(Match match, Player striker, Player nonStriker, Player bowler) {
        match.setStatus(MatchStatus.LIVE);
        Innings innings = new Innings(match.getTeam1(), match.getTeam2(), striker, nonStriker, bowler);
        match.setInnings(innings);
    }

    public void finishMatch(Match match) {
        match.setStatus(MatchStatus.FINISHED);
    }
}

interface MatchObserver {
    void update(Innings innings, Ball ball);
}

class MatchSubject {
    private List<MatchObserver> observers = new ArrayList<>();

    public void subscribe(MatchObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(MatchObserver observer) {
        observers.remove(observer);
    }

    public void notifyObserver(Innings innings, Ball ball) {
        for (MatchObserver observer : observers) {
            observer.update(innings, ball);
        }
    }
}

class Scoreboard {
    private static final Scoreboard INSTANCE = new Scoreboard();

    private Scoreboard() {
    }

    public static Scoreboard getInstance() {
        return INSTANCE;
    }

    public void display(Score score) {
        System.out.println();
        System.out.println("========== SCORE ==========");
        System.out.println(score);
        System.out.println("===========================");
        System.out.println();
    }
}

class ScoreboardObserver implements MatchObserver {
    @Override
    public void update(Innings innings, Ball ball) {
        Scoreboard.getInstance().display(innings.getScore());
    }
}

class CommentaryObserver implements MatchObserver {
    @Override
    public void update(Innings innings, Ball ball) {
        String bowler = ball.getBowler().getName();
        String batsman = ball.getBatsman().getName();

        System.out.println(innings.getScore().getOvers() + " " + bowler + " to " + batsman + " : ");

        if (ball.getBallType() == BallType.WIDE) {
            System.out.println("Wide");
            return;
        }

        if (ball.isWicket()) {
            System.out.println("OUT! " + ball.getWicketType());
            return;
        }

        switch (ball.getRuns()) {
            case 0:
                System.out.println("Dot Ball");
                break;

            case 1:
                System.out.println("Single");
                break;

            case 2:
                System.out.println("Two Runs");
                break;

            case 3:
                System.out.println("Three Runs");
                break;

            case 4:
                System.out.println("FOUR!");
                break;

            case 6:
                System.out.println("SIX!");
                break;

            default:
                System.out.println(ball.getRuns() + " Runs");
        }
    }
}

public class Main {

    public static void main(String[] args) {

        Team india = new Team("India");
        Team australia = new Team("Australia");

        india.addPlayer(new Player("Rohit"));
        india.addPlayer(new Player("Gill"));
        india.addPlayer(new Player("Virat"));

        australia.addPlayer(new Player("Starc"));
        australia.addPlayer(new Player("Cummins"));

        Match match = new Match(india,
                australia,
                new T20Format());

        MatchService matchService = new MatchService();

        matchService.startMatch(
                match,
                india.getPlayer(0),
                india.getPlayer(1),
                australia.getPlayer(0));

        MatchSubject subject = new MatchSubject();

        subject.subscribe(
                new CommentaryObserver());

        subject.subscribe(
                new ScoreboardObserver());

        BallService ballService = new BallService(subject);

        Innings innings = match.getInnings();

        ballService.bowlBall(
                innings,
                new Ball(
                        innings.getStriker(),
                        innings.getCurrentBowler(),
                        1,
                        BallType.NORMAL,
                        WicketType.NONE));

        ballService.bowlBall(
                innings,
                new Ball(
                        innings.getStriker(),
                        innings.getCurrentBowler(),
                        4,
                        BallType.NORMAL,
                        WicketType.NONE));

        ballService.bowlBall(
                innings,
                new Ball(
                        innings.getStriker(),
                        innings.getCurrentBowler(),
                        0,
                        BallType.NORMAL,
                        WicketType.BOWLED));

        ballService.bowlBall(innings,
                new Ball(innings.getStriker(), innings.getCurrentBowler(), 6, BallType.NORMAL, WicketType.NONE));

    }

}