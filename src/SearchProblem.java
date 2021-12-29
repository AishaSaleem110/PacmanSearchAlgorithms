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
 * This file contains the generic SearchProblem, SearchState and Heuristic interfaces and
 * their instantiations for pacman search problems.
 *
 * Please only change the parts of the file you are asked to.  Look for the lines
 * that say
 *
 * //TODO: YOUR CODE HERE
 */

import java.util.*;

/**
 * Abstraction of a generic search problem (does not have to be a search problem for Pacman).
 * Implemented for you.
 *
 * @param <S> class for states
 * @param <A> class for actions
 */
public abstract class SearchProblem<S,A> {
    long expandedCount;
    List<S> visitedList;
    Set<S> visitedSet;

    public SearchProblem() {
        expandedCount = 0;
        visitedList = new ArrayList<>();
        visitedSet = new HashSet<>();
    }

    public void doBookKeeping(S state) {
        expandedCount++;
        if (!visitedSet.contains(state)) {
            visitedSet.add(state);
            visitedList.add(state);
        }
    }

    /**
     * This method is useful for printing statistics once solution has been found.
     *
     * @return the number of expanded nodes
     */
    public long getExpandedCount() {
        return expandedCount;
    }

    /**
     * Not used in the current version. Will be useful when
     * there will be a GUI version.
     * @return
     */
    public List<S> getVisitedList() { return visitedList; }

    /**
     * @return the start state
     */
    public abstract S getStartState();

    /**
     * @param state
     * @return whether state is a goal state or not
     */
    public abstract boolean isGoalState(S state);

    /**
     * Expands a state and returns for each valid action a triple of:
     * (next state, action, cost)
     * @param state
     * @return
     */
    public abstract Collection<SuccessorInfo<S, A>> expand(S state);

    /**
     * @param state
     * @return Actions available in the state
     */
    public abstract List<A> getActions(S state);

    /**
     * @param state
     * @param action
     * @return successor of state via action
     */
    public abstract S getSuccessor(S state, A action);

    /**
     * @param state
     * @param action
     * @return the cost of action from state
     */
    public abstract double getCost(S state, A action);

}

/**
 * Formalisation of the position search problem for Pacman.
 * Implemented for you.
 * You do not need to modify this class.
 * You can study it to understand how to implement other search problems.
 */
class PacmanPositionSearchProblem extends SearchProblem<PacmanPositionSearchState, PacmanAction> {

    private final Maze maze;
    private final Coordinate goalLocation;
    private final Coordinate startLocation;


    public PacmanPositionSearchProblem(Maze maze) {
        this.maze = maze;

        goalLocation = new Coordinate(1,1);
        startLocation = maze.getPacmanLocation();
    }

    @Override
    public PacmanPositionSearchState getStartState() {
        return new PacmanPositionSearchState(startLocation);
    }

    @Override
    public boolean isGoalState(PacmanPositionSearchState state) {
        return state.pacmanLocation.equals(goalLocation);
    }

    @Override
    public Collection<SuccessorInfo<PacmanPositionSearchState, PacmanAction>> expand(PacmanPositionSearchState state) {

        Collection<SuccessorInfo<PacmanPositionSearchState, PacmanAction>> successors = new ArrayList<>();
        for (PacmanAction action : getActions(state)) {
            successors.add(new SuccessorInfo<>(getSuccessor(state, action), action, getCost(state, action)));
        }

        doBookKeeping(state); // do not remove

        return successors;
    }

    @Override
    public List<PacmanAction> getActions(PacmanPositionSearchState state) {
        return maze.getPacmanActions(state.pacmanLocation);
    }

    @Override
    public PacmanPositionSearchState getSuccessor(PacmanPositionSearchState state, PacmanAction action) {
        if (! getActions(state).contains(action)) {
            throw new RuntimeException("Invalid arguments. Action" + action + "is not valid from state" + state);
        }

        return new PacmanPositionSearchState(state.pacmanLocation.add( action.toVector() ));
    }

    @Override
    public double getCost(PacmanPositionSearchState state, PacmanAction action) {
        if (! getActions(state).contains(action)) {
            // action leads into the wall
            return 999999;
        }
        return 1;
    }

    public Coordinate getGoalLocation() {
        return goalLocation;
    }
}

/**
 * Formalisation of Corners problem for Pacman.
 * You need to fill in the missing parts.
 */
class PacmanCornersProblem extends SearchProblem<PacmanCornersSearchState, PacmanAction> {

    private final Maze maze;
    private final Coordinate startLocation;

    private final List<Coordinate> cornersLocations;

    public PacmanCornersProblem(Maze maze) {
        this.maze = maze;
        this.startLocation = maze.getPacmanLocation();

        this.cornersLocations = Arrays.asList(
                maze.getBottomLeftCorner(),
                maze.getTopLeftCorner(),
                maze.getTopRightCorner(),
                maze.getBottomRightCorner()
        );

    }

    @Override
    public PacmanCornersSearchState getStartState() {

        return new PacmanCornersSearchState(startLocation, getNextCornerCoordinates(startLocation,this.cornersLocations));
    }

    @Override
    public boolean isGoalState(PacmanCornersSearchState state) {
        return state.cornerCoordinates.isEmpty();
    }

    @Override
    public Collection<SuccessorInfo<PacmanCornersSearchState, PacmanAction>> expand(PacmanCornersSearchState state) {

        Collection<SuccessorInfo<PacmanCornersSearchState, PacmanAction>> successors = new ArrayList<>();
        for (PacmanAction action : getActions(state)) {
            successors.add(new SuccessorInfo<>(getSuccessor(state, action), action, getCost(state, action)));
        }

        doBookKeeping(state); // do not remove

        return successors;
    }

    @Override
    public List<PacmanAction> getActions(PacmanCornersSearchState state) {
        return maze.getPacmanActions(state.pacmanLocation);
    }

    @Override
    public PacmanCornersSearchState getSuccessor(PacmanCornersSearchState state, PacmanAction action) {
        if (! getActions(state).contains(action)) {
            throw new RuntimeException("Invalid arguments. Action" + action + "is not valid from state" + state);
        }

        Coordinate nextLocation = state.pacmanLocation.add(action.toVector());
        return new PacmanCornersSearchState(nextLocation,this.getNextCornerCoordinates(nextLocation, state.cornerCoordinates));
    }

    @Override
    public double getCost(PacmanCornersSearchState state, PacmanAction action) {
        if (! getActions(state).contains(action)) {
            // action leads into the wall
            return 999999;
        }
        return 1;
    }

    /**
     Computes the updated food coordinates given a new position.
     */
    private List<Coordinate> getNextCornerCoordinates(Coordinate position, List<Coordinate> cornerCoordinates) {
        List<Coordinate> nextCornerCoordinates = cornerCoordinates;
        if (cornerCoordinates.contains(position)) {
            nextCornerCoordinates = new ArrayList<>(cornerCoordinates);
            nextCornerCoordinates.remove(position);
            //we need to remove it from corner coordinates as well
        }
        return nextCornerCoordinates;
    }
}

/**
 * Formalisation of the problem of Eating All Food for Pacman.
 * Implemented for you.
 * You should not need to modify this class.
 */
class PacmanFoodSearchProblem extends SearchProblem<PacmanFoodSearchState, PacmanAction> {

    private final Maze maze;
    private final Coordinate startLocation;

    private final List<Coordinate> foodCoordinates;

    public PacmanFoodSearchProblem(Maze maze) {
        this.maze = maze;
        this.startLocation = maze.getPacmanLocation();

        this.foodCoordinates = maze.getFoodCoordinates();
    }

    @Override
    public PacmanFoodSearchState getStartState() {
        return new PacmanFoodSearchState(startLocation, foodCoordinates);
    }

    @Override
    public boolean isGoalState(PacmanFoodSearchState state) {
        return state.foodCoordinates.isEmpty();
    }

    /**
     Computes the updated food coordinates given a new position.
     */
    private List<Coordinate> getNextFoodCoordinates(Coordinate position, List<Coordinate> foodCoordinates) {
        List<Coordinate> nextFoodCoordinates = foodCoordinates;
        if (foodCoordinates.contains(position)) {
            nextFoodCoordinates = new ArrayList<>(foodCoordinates);
            nextFoodCoordinates.remove(position);
        }
        return nextFoodCoordinates;
    }

    @Override
    public Collection<SuccessorInfo<PacmanFoodSearchState, PacmanAction>> expand(PacmanFoodSearchState state) {
        Collection<SuccessorInfo<PacmanFoodSearchState, PacmanAction>> successors = new ArrayList<>();
        for (PacmanAction action : getActions(state)) {
            successors.add(new SuccessorInfo<>(getSuccessor(state, action), action, getCost(state, action)));
        }

        doBookKeeping(state); // do not remove

        return successors;
    }

    @Override
    public List<PacmanAction> getActions(PacmanFoodSearchState state) {
        return maze.getPacmanActions(state.pacmanLocation);
    }

    @Override
    public PacmanFoodSearchState getSuccessor(PacmanFoodSearchState state, PacmanAction action) {
        if (! getActions(state).contains(action)) {
            throw new RuntimeException("Invalid arguments. Action" + action + "is not valid from state" + state);
        }

        Coordinate nextLocation = state.pacmanLocation.add(action.toVector());
        return new PacmanFoodSearchState(nextLocation,
                                         this.getNextFoodCoordinates(nextLocation, state.foodCoordinates));
    }

    @Override
    public double getCost(PacmanFoodSearchState state, PacmanAction action) {
        if (! getActions(state).contains(action)) {
            // action leads into the wall
            return 999999;
        }
        return 1;
    }
}

/**************************************************************
 ************            Search states            *************
 **************************************************************/

interface SearchState {
}

/**
 * Formalisation of search state for PacmanPositionSearchProblem.
 * Implemented for you.
 * You should not need to modify this class.
 */
class PacmanPositionSearchState implements SearchState {
    Coordinate pacmanLocation;

    public PacmanPositionSearchState(Coordinate pacmanLocation) {
        this.pacmanLocation = pacmanLocation;
    }

    @Override
    public String toString() {
        return pacmanLocation.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PacmanPositionSearchState))
            return false;

        return pacmanLocation.equals(((PacmanPositionSearchState) o).pacmanLocation);
    }

    @Override
    public int hashCode() {
        return pacmanLocation.hashCode();
    }
}

/**
 * Formalisation of search state for PacmanCornersProblem.
 * You need to implement this class.
 */
class PacmanCornersSearchState implements SearchState {
    Coordinate pacmanLocation;
    List<Coordinate> cornerCoordinates;

    public PacmanCornersSearchState(Coordinate pacmanLocation, List<Coordinate> cornerCoordinates) {
        this.pacmanLocation = pacmanLocation;
        this.cornerCoordinates = cornerCoordinates;
    }

    /**
     * This method is useful for debugging
     */
    @Override
    public String toString() {
        return pacmanLocation.toString() + ", " + cornerCoordinates.toString();
    }

    /**
     * You need to implement this method properly for correct functioning
     * of sets.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PacmanCornersSearchState))
            return false;

        return pacmanLocation.equals(((PacmanCornersSearchState) o).pacmanLocation) &&
                cornerCoordinates.equals(((PacmanCornersSearchState) o).cornerCoordinates);
    }

    /**
     * You need to implement this method properly for correct functioning
     * of sets.
     */
    @Override
    public int hashCode() {
        return 31 * pacmanLocation.hashCode() + cornerCoordinates.hashCode();
    }
}

/**
 * Formalisation of search state for PacmanFoodSearchProblem.
 * Implemented for you.
 * You should not need to modify this class.
 */
class PacmanFoodSearchState implements SearchState {
    Coordinate pacmanLocation;
    List<Coordinate> foodCoordinates;

    public PacmanFoodSearchState(Coordinate pacmanLocation, List<Coordinate> foodCoordinates) {
        this.pacmanLocation = pacmanLocation;
        this.foodCoordinates = foodCoordinates;
    }

    @Override
    public String toString() {
        return pacmanLocation.toString() + ", " + foodCoordinates.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PacmanFoodSearchState))
            return false;

        return pacmanLocation.equals(((PacmanFoodSearchState) o).pacmanLocation) &&
                foodCoordinates.equals(((PacmanFoodSearchState) o).foodCoordinates);
    }

    @Override
    public int hashCode() {
        return 31 * pacmanLocation.hashCode() + foodCoordinates.hashCode();
    }
}

/**************************************************************
 ************           Search actions            *************
 **************************************************************/

interface Action {
}

/**
 * Enumeration of possible Pacman actions.
 * Implemented for you.
 * You should not need to modify this class.
 */
enum PacmanAction implements Action {
    NORTH {
        public PacmanAction reverse() {
            return PacmanAction.SOUTH;
        }
        public Coordinate toVector() {
            return new Coordinate(0,1);
        }
        public String toString() { return "North"; }
    },
    EAST {
        public PacmanAction reverse() {
            return PacmanAction.WEST;
        }
        public Coordinate toVector() {
            return new Coordinate(1,0);
        }
        public String toString() { return "East"; }
    },
    SOUTH {
        public PacmanAction reverse() {
            return PacmanAction.NORTH;
        }
        public Coordinate toVector() {
            return new Coordinate(0,-1);
        }
        public String toString() { return "South"; }
    },
    WEST {
        public PacmanAction reverse() {
            return PacmanAction.EAST;
        }
        public Coordinate toVector() {
            return new Coordinate(-1,0);
        }
        public String toString() { return "West"; }
    },
    STOP {
        public PacmanAction reverse() {
            return PacmanAction.STOP;
        }
        public Coordinate toVector() {
            return new Coordinate(0,0);
        }
        public String toString() { return "Stop"; }
    };

    public abstract PacmanAction reverse();
    public abstract Coordinate toVector();
}

/**************************************************************
 ************          Search heuristics          *************
 **************************************************************/

/**
 * Interface for search heuristics.
 * @param <S>
 * @param <A>
 */
interface SearchHeuristic<S,A> {
    Double value(S state, SearchProblem<S,A> problem);
}

/**
 * Null heuristic, i.e., the one that always returns 0.
 * A* with null heuristic is equivalent to uniform-cost search.
 * Implemented for you.
 * You should not need to modify this class.
 *
 * @param <S>
 * @param <A>
 */
class NullHeuristic<S,A> implements SearchHeuristic<S,A> {
    public NullHeuristic() {}

    @Override
    public Double value(S state, SearchProblem<S, A> problem) {
        return 0.0;
    }

    public String toString() { return this.getClass().getName(); }
}

/**
 * Manhattan Distance heuristic.
 * Currently works only for PacmanPositionSearchProblem.
 *
 * @param <S>
 * @param <A>
 */
class ManhattanDistanceHeuristic<S,A> implements SearchHeuristic<S,A> {
    public ManhattanDistanceHeuristic() {}

    @Override
    public Double value(S state, SearchProblem<S, A> problem) {

        if (problem instanceof PacmanPositionSearchProblem && state instanceof PacmanPositionSearchState) {
            return ((PacmanPositionSearchState) state).pacmanLocation.manhattanDistance(((PacmanPositionSearchProblem) problem).getGoalLocation());
        }
        return 0.0;
    }

    public String toString() { return this.getClass().getName(); }
}

/**
 * Heuristic for PacmanCornersProblem.
 * You need to implement it.
 * @param <S>
 * @param <A>
 */
class CornersHeuristic<S,A> implements SearchHeuristic<S,A> {
    public CornersHeuristic() {}

    @Override
    public Double value(S state, SearchProblem<S, A> problem) {

        if (problem instanceof PacmanCornersProblem && state instanceof PacmanCornersSearchState) {

            return  ((PacmanCornersSearchState) state).cornerCoordinates.stream().mapToDouble(e->e.manhattanDistance(((PacmanCornersSearchState) state).pacmanLocation)).max().orElse(0.0);

        }

        return 0.0;
    }

    public String toString() { return this.getClass().getName(); }
}

/**
 * Heuristic for PacmanFoodSearchProblem.
 * You need to implement it.
 * @param <S>
 * @param <A>
 */
class FoodHeuristic<S,A> implements SearchHeuristic<S,A> {
    public FoodHeuristic() {}

    @Override
    public Double value(S state, SearchProblem<S, A> problem) {

        if (problem instanceof PacmanFoodSearchProblem && state instanceof PacmanFoodSearchState) {

            return  ((PacmanFoodSearchState) state).foodCoordinates.stream().mapToDouble(e->e.manhattanDistance(((PacmanFoodSearchState) state).pacmanLocation)).max().orElse(0.0);

        }

        return 0.0;
    }

    public String toString() { return this.getClass().getName(); }
}

