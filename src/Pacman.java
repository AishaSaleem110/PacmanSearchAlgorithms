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
 * This file contains the code to run the search algorithms for Pacman problems.
 *
 * You should not need to modify this file.
 *
 * To test the code, run 'java Pacman -l testMaze -f gowest -a'.
 *
 * To test your implementation run, e.g.,
 *
 *      'java Pacman -l mediumMaze -f bfs'
 * or
 *      'java Pacman -l bigMaze -f astar -h ManhattanDistanceHeuristic'
 *
 * Once you have implemented depth-first search,
 * you will be able to run the code with default parameters
 *
 *      'java Pacman'
 *
 * that will run depth-first search for 'PacmanPositionSearchProblem' on 'mediumMaze'.
 *
 * The option '-f' defines the search strategy to use and should be one of:
 * dfs (depth-first search), bfs (breadth-first search), greedy (greedy search) and astar (A* search).
 *
 * The option -a does not require a value, it enables animation of the obtained sequence of pacman moves.
 * The animation does not work well in IDE, but it does in a Unix terminal.
 *
 * The option -s visualises the solution sequence on the maze.
 */

import java.util.concurrent.TimeUnit;

import static java.lang.System.out;


public class Pacman {
    static void usage() {
        out.println("usage: Pacman [<option>...]");
        out.println("options:");
        out.println("  -l <mazeName> : Name of the layout, see 'mazes' folder");
        out.println("  -p <searchProblem> : Name of the search problem class");
        out.println("  -f <strategy> : Search strategy, one of dfs, bfs, greedy and astar");
        out.println("  -h <heuristic> : Search heuristic to use (name of the class)");
        out.println("  -a : Show textual animation of pacman moves");
        out.println("  -s : Show solution, sequence of pacman moves");
        out.println("  --help : Print this message and exit");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {

        String problem = "PacmanPositionSearchProblem";
        String mazeFile = "mediumMaze";
        String function = "dfs";
        String heuristicName = "NullHeuristic";
        boolean visualise = false;
        boolean animate = false;

        for (int i = 0 ; i < args.length ; ++i) {
            String s = args[i];
            switch (s) {
                case "-l":
                    mazeFile = args[++i];
                    break;
                case "-p":
                    problem = args[++i];
                    break;
                case "-f":
                    function = args[++i];
                    break;
                case "-h":
                    heuristicName = args[++i];
                    break;
                case "-s":
                    visualise = true;
                    break;
                case "-a":
                    animate = true;
                    break;
                case "--help":
                    usage();
                default:
                    usage();
            }
        }

        if (animate)
            visualise = false;


        Maze maze = MazeParser.parseMaze("mazes/" + mazeFile + ".lay");
        out.println("Maze: "+mazeFile);
        out.println(maze);


        /*
         Instantiate the search problem.
         For instance, PositionSearchProblem or CornersProblem or FoodSearchProblem
         */
        SearchProblem<SearchState, PacmanAction> searchProblem =
                (SearchProblem) Class.forName(problem).getConstructor(Maze.class).newInstance(maze);

        /*
         Instantiate the heuristic. By default it is the trivial heuristic (NullHeuristic), that always returns 0.
         */
        SearchHeuristic<SearchState, PacmanAction> heuristic =
                (SearchHeuristic) Class.forName(heuristicName).getConstructor().newInstance();

        /*
         Run the search algorithm, where the strategy is determined by function.
         Can be one of
            - dfs for depth first search
            - bfs for breadth first search
            - greedy for greedy search
            - astar for A* search
         */
        Solution<SearchState, PacmanAction> solution = GraphSearch.search(function, searchProblem, heuristic);

        /*
         Textual animation of Pacman moves.
         */
        if (solution != null) {
            if (animate) {
                animatePacmanActions(maze, solution);
            }
            if (visualise) {
                out.println("Solution:");
                out.println(maze.toString(solution.actions));
            }
        }
    }

    private static void animatePacmanActions(Maze maze,
                                             Solution<? extends SearchState, PacmanAction> solution)
            throws InterruptedException {

        for (PacmanAction action : solution.actions) {
            maze.applyAction(action);

            TimeUnit.MILLISECONDS.sleep(250);
            System.out.print("\033[H\033[2J");
            System.out.flush();
            out.println(maze);
        }
    }

}

 
