import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelManager {
    int currLevel;
    int currLevelWidth;
    public List<Tile> tiles;
    public TileFactory tf;
    private static final String path = "C:\\Users\\yonat\\IdeaProjects\\Dungeons and Dragons\\levels_dir"; //TODO: CHANGE THIS PATH TO URS BERFORE RUNNING
    public Player selected;
    public List<Enemy> enemies;

    public boolean won = false;

    public LevelManager(TileFactory tileFactory) {
        currLevel = 1;
        this.tf = tileFactory;
        enemies = new ArrayList<>();
        selected = tileFactory.selected;
        loadLevel(currLevel);

    }

    private void loadLevel(int currLevel) {
        List<String> readLines = readAllLines(path + "\\level" + currLevel + ".txt");
        this.currLevelWidth = readLines.get(0).length();
        this.tiles = convertReadLinesToTiles(readLines);
    }

    public void advanceLevel() {
        if(currLevel < 4) {
            currLevel++;
            loadLevel(currLevel );
        }
        else
            won = true;
    }

    private List<String> readAllLines(String path) {
        List<String> lines = Collections.emptyList();
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            System.out.println(e.getMessage() + "\n" +
                    e.getStackTrace());
        }
        return lines;
    }

    private List<Tile> convertReadLinesToTiles(List<String> readLines) {
        List<Tile> tiles = new ArrayList<>();
        int x = 0; // x position
        int y = 0; // y position
        for(String row : readLines) {
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                Position pos = new Position(x,y);
                Tile tile;
                switch (c) {
                    case '.': tile = tf.produceEmpty(pos); break;
                    case '#': tile = tf.produceWall(pos); break;

                    case '@':{
                        Player player = tf.producePlayer(pos);
                        tile = selected;
                        selected.initialize(pos); break; //TODO: CHANGE IDX
                    }

                    default:{
                        Enemy enemy = tf.produceEnemy(c,pos);
                        tile = enemy;
                        enemies.add(enemy);
                    }

                }
                tiles.add(tile);
                if(x == currLevelWidth - 1) {
                    x = 0;
                } else {
                    x++;
                }
            }
            y++;
        }
        this.tiles = tiles;
        return tiles;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
