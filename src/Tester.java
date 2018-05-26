import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Екатерина on 09.05.2018.
 */
public class Tester {
    private final static int playerCount = 2;


    public static void main(String args[]) throws Exception {

        int gamesCount = Integer.valueOf(args[2]);
        HashMap<GameState, Integer> results = new HashMap<>();
        double placesSum = 0;
        int playedGames = 0;

        for (int i = 0; i < gamesCount; i++) {
            playedGames ++;
            System.out.print("Game " + playedGames + ": ");
            GameState state = playGame(args, playedGames % 2);
            results.put(state, results.getOrDefault(state, 0) + 1);
            placesSum += GameState.DRAW.equals(state) ? 1.5 : state.getId();
            System.out.print("; avg " + String.format("%.2f", (placesSum * 1.0 / playedGames)) + "; wins "
                    + String.format("%.2f", results.getOrDefault(GameState.WIN, 0) * 100.0 / playedGames) + "%\n");
        }

        System.out.println("Results for bot " + args[0]);
        System.out.println("Wins: " + results.getOrDefault(GameState.WIN, 0)
                + "; Loses: " + results.getOrDefault(GameState.LOSE, 0)
                + "; Draws: " + results.getOrDefault(GameState.DRAW, 0));
        System.out.println("Average place " + placesSum * 1.0 / playedGames);
        System.out.println("Wins " + results.getOrDefault(GameState.WIN, 0) * 100.0 / playedGames + " %");
    }

    private static GameState playGame(String[] args, int testedBot) throws Exception {
        Game game = new Game();
        List<BotProcess> players = getPlayerProcess(args[0], args[1], testedBot);

        GameState state = GameState.UNFINISHED;
        int frame = 0;
        GameState testedBotState = GameState.UNFINISHED;
        while (GameState.UNFINISHED.equals(state)) {
            for (int i = 0; i < playerCount; i++) {
                frame ++;
                printToPlayer(players.get(i), game.output());
                players.get(i).flushOut();
                players.get(i).clearErrorStream();
                try {
                    String input = readFromPlayer(players.get(i));
                    state = game.readInput(input);
                } catch (InvalidInputException e) {
                    state = GameState.LOSE;
                    System.out.println((i == testedBot ? "newPlayer" : "oldPlayer") +  " wrong input");
                } catch (Exception e) {
                    state = GameState.LOSE;
                    System.out.println((i == testedBot ? "newPlayer" : "oldPlayer") +  " does not provide input");
                    try {
                        System.out.println(players.get(i).getError());
                    } catch (IOException ioe) {
                    }
                }
                if (!GameState.UNFINISHED.equals(state)) {
                    if (GameState.DRAW.equals(state)) {
                        testedBotState = GameState.DRAW;
                    } else if (i == testedBot && GameState.WIN.equals(state)
                            || i != testedBot && GameState.LOSE.equals(state)) {
                        testedBotState = GameState.WIN;
                    } else {
                        testedBotState = GameState.LOSE;
                    }
                    break;
                }
            }
        }

        System.out.print("frame " + frame + "; " + testedBotState.name());
        destroyAll(players);
        return testedBotState;
    }

    private static List<BotProcess> getPlayerProcess(String newPlayer, String oldPlayer, int testedBot) throws Exception{
        ArrayList<ProcessBuilder> playersBuilders = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            if (i == testedBot) {
                playersBuilders.add(new ProcessBuilder(newPlayer.split(" ")));
            } else {
                playersBuilders.add(new ProcessBuilder(oldPlayer.split(" ")));
            }
        }
        List<BotProcess> players = new ArrayList<>();
        for (ProcessBuilder builder : playersBuilders) {
            players.add(new BotProcess(builder.start()));
        }
        return players;
    }

    private static void printToPlayer(BotProcess process, String string) {
        process.print(string);
    }

    private static String readFromPlayer(BotProcess process) {
        return process.readLine();
    }

    private static void destroyAll(List<BotProcess> processes) throws Exception {
        for (BotProcess process : processes) {
            process.destroy();
        }
    }
}
