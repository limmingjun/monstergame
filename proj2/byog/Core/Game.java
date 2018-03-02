package byog.Core;

import byog.TileEngine.*;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Color;
import java.awt.Font;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;
    boolean gameOver = false;
    int midWidth = WIDTH/2;
    int midHeight = HEIGHT/2;
    int seed;

    public void initializeFrame() {
        StdDraw.setCanvasSize(this.WIDTH * 16, this.HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawStartFrame() {
        initializeFrame();
        /* Clears the frame and draws a new one using the stdDraw library tools */
        Font bigFont = new Font("Monaco", Font.BOLD, 50);
        Font smallFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, midHeight*(1.25), "GAME TITLE HERE");
        StdDraw.setFont(smallFont);
        StdDraw.text(midWidth, midHeight, "New Game (N)");
        StdDraw.text(midWidth, midHeight-2.5, "Load Game (L)");
        StdDraw.text(midWidth, midHeight-5, "Quit (Q)");
        StdDraw.show();
    }

    public void drawGameOverFrame() {
        StdDraw.clear(Color.BLACK);
        Font bigFont = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.red);
        StdDraw.text(midWidth, midHeight*(1.25), "GAME OVER");
        StdDraw.text(midWidth, midHeight, "YOU LOSE");
        StdDraw.show();
    }

    public void startScreen() {
        drawStartFrame();
        char userInput = ' ';
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            userInput = StdDraw.nextKeyTyped();
            if (userInput == 'N' || userInput == 'n') {
                this.seed = requestSeed();
                StdDraw.pause(300);
                startGame(seed);
                drawGameOverFrame(); // For test purposes
                return;
            }
            if (userInput == 'L' || userInput == 'l') {
                /* loadGame(); */
                drawGameOverFrame(); // For test purposes
                return;
            }
            if (userInput == 'Q' || userInput == 'q') {
                drawGameOverFrame(); // For test purposes
                /* quit(); */
            }
            else {continue;}
        }

    }

    public void startGame(int seed) {
        World myWorld = new World(WIDTH, seed);
        myWorld.generateWorld(70, 20);
        TETile[][] worldFrame = myWorld.world;
        ter.initialize(WIDTH, HEIGHT, 1, 1);
        Player p = new Player();
        p.insertPlayer(myWorld);
        ter.renderFrame(worldFrame);
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                System.out.print(readMouse4Tile(myWorld));
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            controller(key, myWorld, p);
            ter.renderFrame(worldFrame);
            continue;
        }
    }

    //Test

    void controller(char key, World world, Player player) {
        if (key == 'W' || key == 'w') {
            moveCharacter(0, 1, world, player);
        }
        else if (key == 'S' || key == 's') {
            moveCharacter(0, -1, world, player);
        }
        else if (key == 'D' || key == 'd') {
            moveCharacter(1, 0, world, player);
        }
        else if (key == 'A' || key == 'a') {
            moveCharacter(-1, 0, world, player);
        }
    }

    void moveCharacter(int horz, int vert, World world, Player player) {
        int projectedX = player.xPos + horz;
        int projectedY = player.yPos + vert;
        if (world.world[projectedX][projectedY] != Tileset.WALL && world.world[projectedX][projectedY] != Tileset.NOTHING) {
            world.world[player.xPos][player.yPos] = Tileset.FLOOR; //* MAYBE Need to figure out how to make tile revert to its previous type
            player.xPos += horz;
            player.yPos += vert;
            world.world[projectedX][projectedY] = player.type;
        }
    }

    public int requestSeed() {
        StdDraw.clear(Color.BLACK);
        String dispString = "Seed: ";
        Font bigFont = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, midHeight, dispString);
        StdDraw.text(midWidth, midHeight-3, "Enter a seed# and press s to start");
        StdDraw.show();
        char key = ' ';
        String userNumber = "";
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            key = StdDraw.nextKeyTyped();
            if (Character.isDigit(key)) {
                userNumber += key;
                dispString += key;
                StdDraw.clear(Color.BLACK);
                StdDraw.text(midWidth, midHeight, dispString);
                StdDraw.text(midWidth, midHeight-3, "Enter a seed# and press s to start");
                StdDraw.show();
            } else if (key == 's' || key == 'S') {
                return Integer.parseInt(userNumber);
            }
        }
    }

    /************************ HUD **********************/

    public static String readMouse4Tile(World myWorld) {
        int x = (int) StdDraw.mouseX(); //Casting rounds down
        int y = (int) StdDraw.mouseY();
        String type =  myWorld.world[x][y].description;
        return type;
    }

    /***************************************************/


    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {

    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // Run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        long seed = Long.parseLong(Character.toString(input.charAt(1)));
        int i = 2;
        while (input.charAt(i) != 's') {
            seed = 10 * seed + Long.parseLong(Character.toString(input.charAt(i)));
            i++;
        }
        World myWorld = new World(WIDTH, seed);
        myWorld.generateWorld(70, 20);
        TETile[][] finalWorldFrame = myWorld.world;
        return finalWorldFrame;
    }



    public static void main(String[] args) {

    }
}
