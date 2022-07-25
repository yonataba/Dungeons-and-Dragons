import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameFlow {
    private Player selected;
    private GameBoard gameBoard;
    private TileFactory tileFactory;
    private LevelManager levelManager;
    GameFlow() {
    }

    public void startGame() {
        this.tileFactory = new TileFactory();
        int player_index = selectPlayer();
        tileFactory.selected = tileFactory.listPlayers().get(player_index);
//        this.selected = tileFactory.listPlayers().get(player_index);
        this.levelManager= new LevelManager(tileFactory);
        this.selected = levelManager.selected;
        this.gameBoard = new GameBoard(levelManager);
        tileFactory.setGameBoard(gameBoard);
        this.gameBoard.tiles = this.gameBoard.tiles.stream().sorted().collect(Collectors.toList());
        gameTick();
    }

    private void gameTick() {
        System.out.println("You have selected:");
        System.out.println(selected.getName());
        System.out.println(gameBoard);
        char c;
        do {
            System.out.println(selected.describe());
            c = validInput();
            Position newPosition = new Position(selected.getPosition().getX(), selected.getPosition().getY());
            switch (c) {
                case 'w' -> {
                    newPosition.moveUp();
                    playerTick(newPosition);
                }
                case 'a' -> {
                    newPosition.moveLeft();
                    playerTick(newPosition);
                }
                case 's' -> {
                    newPosition.moveDown();
                    playerTick(newPosition);
                }
                case 'd' -> {
                    newPosition.moveRight();
                    playerTick(newPosition);
                }
                case 'q' -> {
                    selected.gameTick();
                }
                case 'e' -> {
                    selected.castAbility(levelManager.getEnemies());
                }
            }
                gameBoard.sortTiles();
                for (Enemy enemy : levelManager.getEnemies()){
                    enemy.gameTick(selected);
                    Position newEnemyPosition = enemy.move(selected);
                    Tile newT = tileInPosition(newEnemyPosition);
                    enemy.interact(newT);
                }
                if (levelManager.getEnemies().size() == 0)
                    gameBoard.advanceLevel();

                System.out.println(gameBoard);
        } while(!(selected.getHealthAmount() == 0 || levelManager.won) );
            if (levelManager.won)
                System.out.println("You WON!!!");
    }

    private Tile tileInPosition(Position position){
        int calc = position.getY() * gameBoard.boardWidth + position.getX();
        return gameBoard.tiles.get(calc);
    }

    private void playerTick(Position newPosition){
        Tile newT = tileInPosition(newPosition);
        int enemiesSize = levelManager.getEnemies().size();
        selected.interact(newT);
        gameBoard.sortTiles();
        if(levelManager.getEnemies().size() < enemiesSize)
            selected.interact(tileInPosition(newPosition));
        selected.gameTick();
    }

    private int selectPlayer() {
        int chosen;
        while (true){
            int iter = 1;
            for (Player player : tileFactory.listPlayers()){
                System.out.println(iter+".  " + player.describe());
                iter++;
            }
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()){
                chosen = scanner.nextInt();
                if (chosen >= 1 && chosen <=tileFactory.listPlayers().size() )
                    return chosen -1;
            }
        }
    }

    private char validInput(){
        List<Character> validChars = Arrays.asList('a', 's', 'd', 'w', 'e', 'q') ;
        while (true){
            Scanner scanner = new Scanner(System.in);
            String string = scanner.next();
            if (string.length() == 1){
                char c = string.charAt(0);
                if(validChars.contains(c))
                    return c;
            }
        }
    }
}
