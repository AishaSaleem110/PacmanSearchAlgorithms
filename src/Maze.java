/*
 * This project was developed for the Introduction to Artificial Intelligence/Intelligent Systems
 * module COMP5280/8250 at University of Kent.
 *
 * The java code was created by Elena Botoeva (e.botoeva@kent.ac.uk) and
 * follows the structure and the design of the Pacman AI projects
 * (the core part of the project on search)
 * developed at UC Berkeley http://ai.berkeley.edu.
 */

/**
 * File implementing a maze parser and a maze structure with useful functionality.
 *
 * You should not need to modify this file.
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Maze
{
    /**
     * A structure describing a pacman maze, with walls, food locations and pacman location.
     * Offers maze related functionality.
     *
     *
     * The first coordinate in walls and food is y, the second is x
     *
     * walls[y][x] stores whether there is wall at position (x,y)
     * food[y][x] stores whether there is a dot at position (x,y)
     *
     * x increases from left to right
     * y increases from bottom to top
     */
    private final boolean[][] walls;
    private final boolean[][] food;
    private Coordinate pacmanLocation;
    private final int width;
    private final int height;

    private final String TEXT_RESET = "\u001B[0m";
    private final String TEXT_YELLOW = "\u001B[33m";
    private final String TEXT_BLUE = "\u001B[34m";

    private final String SQUARE_SYMBOL = "\u2588";
    private final String CIRCLE_SYMBOL = "\u25CF";
    private final String PACMAN_SYMBOL = "\u263B";
    private final String UP_SYMBOL = "\u23F6";
    private final String DOWN_SYMBOL = "\u23F7";
    private final String LEFT_SYMBOL = "\u23F4";
    private final String RIGHT_SYMBOL = "\u23F5";


    public Maze(boolean[][] walls, boolean[][] food, int width, int height, Coordinate pacmanLocation)
    {
        this.walls = walls;
        this.food = food;
        this.width = width;
        this.height = height;
        this.pacmanLocation = pacmanLocation;
    }

    /**
     * Creates a deep copy of maze, which could then be used, e.g., for animation
     * by using applyAction, which mutates the object.
     */
    public Maze copy() {
        int width = this.width;
        int height = this.height;
        boolean[][] walls = new boolean[height][width];
        boolean[][] food = new boolean[height][width];
        Coordinate pacmanLocation = new Coordinate(this.pacmanLocation.x, this.pacmanLocation.y);

        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                walls[i][j] = this.walls[i][j];
                food[i][j] = this.food[i][j];
            }
        }

        return new Maze(walls, food, width, height, pacmanLocation);
    }

    /**
     * The coordinates of corners of the maze
     */
    public Coordinate getBottomLeftCorner() {
        return new Coordinate(1,1);
    }

    public Coordinate getTopLeftCorner() {
        return new Coordinate(1,height - 2);
    }

    public Coordinate getBottomRightCorner() {
        return new Coordinate(width - 2,1);
    }

    public Coordinate getTopRightCorner() {
        return new Coordinate(width - 2,height - 2);
    }

    public Coordinate getPacmanLocation() {
        return pacmanLocation;
    }

    public List<Coordinate> getFoodCoordinates() {
        List<Coordinate> list = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isFood(x, y))
                    list.add(new Coordinate(x, y));
            }
        }
        return list;
    }

    /**
     * String representation of the maze, with the walls,
     * food locations and pacman location.
     *
     * @return
     */
    public String toString() {

        StringBuilder output = new StringBuilder();

        /*
         * Revert the order of the lines back for printing
         */
        for (int i = height - 1; i >= 0; i--) {
            String[] printableLine = new String[width];

            for (int j = 0; j < width; j++) {
                if (walls[i][j])
                    printableLine[j] = TEXT_BLUE + SQUARE_SYMBOL + TEXT_RESET;
                else if (food[i][j])
                    printableLine[j] = CIRCLE_SYMBOL;
                else
                    printableLine[j] = " ";
            }
            if (i == pacmanLocation.y)
                printableLine[pacmanLocation.x] = TEXT_YELLOW + PACMAN_SYMBOL + TEXT_RESET;

            for (int j = 0; j < width; j++) {
                output.append(printableLine[j]);
            }
            output.append('\n');
        }

        return output.toString();
    }

    /**
     * For visualising an action sequence in the maze.
     *
     * @param actions
     * @return String representation of the maze and the given plan.
     */
    public String toString(List<PacmanAction> actions) {
        String[][] printable = new String[height][];
        /*
         * Revert the order of the lines back for printing
         */
        for (int i = 0; i < height; i++) {
            printable[i] = new String[width];

            for (int j = 0; j < width; j++) {
                if (walls[i][j])
                    printable[i][j] = TEXT_BLUE + SQUARE_SYMBOL + TEXT_RESET;
                else if (food[i][j])
                    printable[i][j] = CIRCLE_SYMBOL;
                else
                    printable[i][j] = " ";
            }
            if (i == pacmanLocation.y)
                printable[i][pacmanLocation.x] = TEXT_YELLOW + PACMAN_SYMBOL + TEXT_RESET;
        }

        /*
         * Visualise the solution path of pacman
         */
        Coordinate currentLocation = pacmanLocation;
        Map<PacmanAction, String> actionToText = Map.of(
                PacmanAction.NORTH, UP_SYMBOL,
                PacmanAction.SOUTH, DOWN_SYMBOL,
                PacmanAction.EAST, RIGHT_SYMBOL,
                PacmanAction.WEST, LEFT_SYMBOL);
        for(PacmanAction action: actions) {
            Coordinate nextLocation = currentLocation.add(action.toVector());

            if (isWall(nextLocation))
                throw new RuntimeException("Invalid actions resulting in moving into wall. Aborting...");

            printable[nextLocation.y][nextLocation.x] = actionToText.get(action);

            currentLocation = nextLocation;
        }

        /*
         * Build the corresponding string
         */
        StringBuilder output = new StringBuilder();
        for (int i = height - 1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                output.append(printable[i][j]);
            }
            output.append('\n');
        }
        return output.toString();
    }


    public boolean isWall(Coordinate c) {
        return walls[c.y][c.x];
    }

    public boolean isFood(int x, int y) {
        return food[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void eatFood(Coordinate c) {
        food[c.y][c.x] = false;
    }

    /**
     * Applies the action to the maze.
     *
     * Mutates the object. In particular, the pacman location and the food matrix.
     *
     * @param action to move pacman
     */
    public void applyAction(PacmanAction action) {
        /**
         * m
         */
        Coordinate newPacmanLocation = pacmanLocation.add(action.toVector());

        if (isWall(newPacmanLocation))
            throw new RuntimeException("Received illegal action resulting in moving into the wall.\n" +
                    "Current pacman location " + pacmanLocation+".\n" +
                    "Supplied action " + action);

        eatFood(newPacmanLocation);

        pacmanLocation = newPacmanLocation;
    }

    /**
     * Return list of available Pacman actions from a given position in the maze.
     *
     * @param pacmanLocation
     * @return actions that do not result in moving into wall
     */
    public List<PacmanAction> getPacmanActions(Coordinate pacmanLocation) {
        PacmanAction[] possibleActions = new PacmanAction[]{PacmanAction.NORTH, PacmanAction.SOUTH,
                PacmanAction.EAST, PacmanAction.WEST};

        List<PacmanAction> validActions = new ArrayList<>();
        for (PacmanAction action : possibleActions) {
            Coordinate actionVector = action.toVector();
            Coordinate nextLocation = pacmanLocation.add( actionVector );

            if (!this.isWall(nextLocation)) {
                validActions.add(action);
            }
        }

        return validActions;
    }
}

class MazeParser {
    /**
     * Parses maze file and returns a Maze object
     *
     * @param mazeFilename (relative) path to the file containing maze encoding
     * @return parsed Maze object
     * @throws Exception
     */
    public static Maze parseMaze(String mazeFilename) throws Exception {
        File file = new File(mazeFilename);

        boolean[][] walls;
        boolean[][] food;
        int height = -1, width = -1;
        int pacmanX = -1, pacmanY = -1;

        BufferedReader br = new BufferedReader(new FileReader(file));
        List<boolean[]> wallLinesList = new ArrayList<boolean[]>();
        List<boolean[]> foodLinesList = new ArrayList<boolean[]>();

        String line;
        int lineCounter = 0;

        while ((line = br.readLine()) != null) {
            if (width == -1) {
                width = line.length();
            } else if (width != line.length()) {
                throw new Exception("Invalid maze file. Got lines of different length. Line "+(lineCounter+1));
            }

            boolean[] wallsLine = new boolean[line.length()];
            boolean[] foodLine = new boolean[line.length()];

            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == '.') {
                    wallsLine[i] = false;
                    foodLine[i] = true;
                } else if (line.charAt(i) == 'P') {
                    pacmanX = i;
                    pacmanY = lineCounter;
                    wallsLine[i] = false;
                    foodLine[i] = false;
                } else if (line.charAt(i) == '%') {
                    wallsLine[i] = true;
                    foodLine[i] = false;
                } else {
                    //line.charAt(i) == ' '
                    wallsLine[i] = false;
                    foodLine[i] = false;
                }
            }
            wallLinesList.add(wallsLine);
            foodLinesList.add(foodLine);
            lineCounter++;
        }

        height = lineCounter;

        /**
         * Revert the order of the lines so as to start line count from the bottom.
         *
         * That is, position (1,1) is always the bottom left corner of the maze.
         */
        walls = new boolean[height][];
        food = new boolean[height][];
        for (int i = 0; i < height; i++) {
            walls[i] = wallLinesList.get(height - i - 1);
            food[i] = foodLinesList.get(height - i - 1);
        }

        pacmanY = height - pacmanY - 1;

        return new Maze(walls, food, width, height, new Coordinate(pacmanX, pacmanY));
    }
}