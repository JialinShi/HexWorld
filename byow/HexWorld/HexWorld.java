package byow.lab12;

import org.junit.Test;

import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 80;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with Nothing
     *
     * @param tiles
     */
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    /**
     * Draw a row of tiles to board, anchored at a given position
     */
    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int x = 0; x < length; x++) {
            tiles[p.x + x][p.y] = tile;
        }
    }


    public static void hexHelper(TETile[][] tiles, Position p, int b, int t, int n, TETile tile) {
        // Draw this row
        Position startOfRow = p.shift(b, 0);
        drawRow(tiles, startOfRow, tile, t);

        // Draw remaining rows recursively
        if (b > 0) {
            Position nextP = p.shift(0, -1);
            hexHelper(tiles, nextP, b - 1, t + 2, n - 1, tile);
        }

        // Draw this row again to be the reflection
        Position startOfReflectedRow = startOfRow.shift(0, -(2 * n - 1));
        drawRow(tiles, startOfReflectedRow, tile, t);
    }

    /**
     * draw Hexagon starting at position p, using given tile pattern.
     *
     * @param tiles
     * @param p
     * @param size
     * @param tile
     */
    public static void drawHex(TETile[][] tiles, Position p, int size, TETile tile) {
        if (size < 2) {
            return;
        }
        int b = size - 1;
        int t = size;
        hexHelper(tiles, p, b, t, size, tile);
    }

    /**
     * Adds a column of num hexagons, each of whose biomes are chosen randomly to the world at
     * position p. Each of the hexagons are of size SIZE.
     */
    public static void drawHexCol(TETile[][] tiles, Position p, int size, int num) {
        if (num < 1) {
            return;
        }
        //Draw this hexagon
        drawHex(tiles, p, size, randomTile());

        //Draw n-1 hexagon
        if (num > 1) {
            Position nextP = getBottomNeighbor(p, size);
            drawHexCol(tiles, nextP, size, num - 1);
        }
    }

    /**
     * Gets the position of the bottom neighbor of a hexagon at position p.
     */
    public static Position getBottomNeighbor(Position p, int n) {
        return p.shift(0, -2 * n);
    }

    /**
     * Gets the position of the top right neighbor of a hexagon at position p.
     */
    public static Position getTopRightNeighbor(Position p, int n) {
        return p.shift(2 * n - 1, n);
    }

    /**
     * Gets the position of the bottom right neighbor of a hexagon at position p.
     */
    public static Position getBottomRightNeighbor(Position p, int n) {
        return p.shift(2 * n - 1, -n);
    }

    //private helper class to deal with positions
    private static class Position {

        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int x, int y) {
            return new Position(this.x + x, this.y + y);
        }

    }

    /**
     * Picks a RANDOM tile with a 33% change of being a wall, 33% chance of being a flower, and 33%
     * chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0:
                return Tileset.SAND;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.GRASS;
            case 3:
                return Tileset.MOUNTAIN;
            case 4:
                return Tileset.TREE;
            default:
                return Tileset.NOTHING;
        }
    }

    public static void drawWorld(TETile[][] world, Position p, int hexSize, int tessSize) {
        //Draw the first column
        drawHexCol(world, p, hexSize, tessSize);

        // Expand up and to the right
        for (int i = 1; i < tessSize; i++) {
            p = getTopRightNeighbor(p, hexSize);
            drawHexCol(world, p, hexSize, tessSize + i);
        }
        // Expand down and to the right
        for (int i = tessSize - 2; i >= 0; i--) {
            p = getBottomRightNeighbor(p, hexSize);
            drawHexCol(world, p, hexSize, tessSize + i);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillWithNothing(world);
        Position p = new Position(5, 50);
        drawWorld(world, p, 4, 4);

        ter.renderFrame(world);
    }
}
