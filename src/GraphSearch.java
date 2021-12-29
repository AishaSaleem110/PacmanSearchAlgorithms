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
 * This file contains a skeleton of the search procedure. You need to implement
 * the concrete search functions.
 *
 * The printing is for outputting statistics about search, it is there to help you
 * see what you are running and how good your implementation is.
 *
 * Please only change the parts of the file you are asked to.  Look for the lines
 * that say
 *
 * //TODO: YOUR CODE HERE
 */

import java.util.*;

import static java.lang.System.out;

public class GraphSearch<S,A> {
    /**
     *
     * @param function string describing search strategy: dfs, bfs, greedy and astar
     * @param problem search problem instance, for instance, PacmanPositionSearchProblem, PacmanCornersProblem etc
     * @param heuristic can be null, not used for dfs and bfs
     * @param <S> states
     * @param <A> actions
     * @return
     */
    public static <S, A> Solution<S, A> search(String function, SearchProblem<S, A> problem, SearchHeuristic<S, A> heuristic) {
        Solution<S, A> solution;
        long start = System.currentTimeMillis();
        switch (function) {
            case "dfs":
                solution = depthFirstSearch(problem);
                break;
            case "bfs":
                solution = breadthFirstSearch(problem);
                break;
            case "greedy":
                solution = greedySearch(problem, heuristic);
                break;
            case "astar":
                solution = aStarSearch(problem, heuristic);
                break;
            case "gowest":
                if (problem instanceof PacmanPositionSearchProblem) {
                    solution = (Solution<S, A>) goWestSearch((PacmanPositionSearchProblem) problem);
                    break;
                }
            default:
                throw new RuntimeException("Unknown search strategy: " + function);
        }

        long end = System.currentTimeMillis();
        printSearchInfo(problem, solution, end - start);
        return solution;
    }

    private static Solution<PacmanPositionSearchState, PacmanAction> goWestSearch(PacmanPositionSearchProblem problem) {
        out.println("Hardcoded Go West strategy that works for testMaze");

        List<PacmanAction> actions = new ArrayList<>();
        PacmanPositionSearchState state = problem.getStartState();
        double cost = 0;

        while (!problem.isGoalState(state)) {
            problem.expand(state);
            cost += problem.getCost(state, PacmanAction.WEST);
            state = problem.getSuccessor(state, PacmanAction.WEST);
            actions.add(PacmanAction.WEST);
        }
        return new Solution<>(state, actions, cost);
    }

    private static <S, A> Solution<S, A> breadthFirstSearch(SearchProblem<S, A> problem) {
        out.println("Search using BFS algorithm");

        Util.Queue<Node> frontier =new Util.Queue<>();
        frontier.push(new Node(problem.getStartState(),new ArrayList(),0));
        Node<S,A> currentNode = null;
        while (!frontier.isEmpty()){
            currentNode =  frontier.pop();
            System.out.println(currentNode.state);
            if(problem.isGoalState(currentNode.state)){
                return new Solution<S,A>(currentNode.state,currentNode.actions,currentNode.pathCost);
            }
            if(!problem.visitedSet.contains(currentNode.state)){
                Collection<SuccessorInfo<S,A>> successorInfos= problem.expand(currentNode.state);

                for (SuccessorInfo<S,A> s:successorInfos) {
                    List<A> actions = new ArrayList<>(currentNode.actions);
                    actions.add(s.action);
                    frontier.push(new Node(s.nextState,actions, currentNode.pathCost + s.cost ));
                }
            }

        }
        return null;
    }

    private static <S, A> Solution<S, A> depthFirstSearch(SearchProblem<S, A> problem) {
        out.println("Search using DFS algorithm");

        Util.Stack<Node> frontier =new Util.Stack<>();
        frontier.push(new Node(problem.getStartState(),new ArrayList(),0));
        Node<S,A> currentNode = null;
        while (!frontier.isEmpty()){
            currentNode =  frontier.pop();
            if(problem.isGoalState(currentNode.state)){
                return new Solution<S,A>(currentNode.state,currentNode.actions,currentNode.pathCost);
            }
            if(!problem.visitedSet.contains(currentNode.state)){
                Collection<SuccessorInfo<S,A>> successorInfos= problem.expand(currentNode.state);

                for (SuccessorInfo<S,A> s:successorInfos) {
                    List<A> actions = new ArrayList<>(currentNode.actions);
                    actions.add(s.action);
                    frontier.push(new Node(s.nextState,actions, currentNode.pathCost + s.cost ));
                }
            }

        }
        return null;


    }

    private static <S, A> Solution<S, A> greedySearch(SearchProblem<S, A> problem, SearchHeuristic<S, A> heuristic) {
        out.println("Search using Greedy algorithm and heuristic " + heuristic);

        //TODO: YOUR CODE HERE
        throw new RuntimeException("Not Implemented");
    }

    private static <S, A> Solution<S, A> aStarSearch(SearchProblem<S, A> problem, SearchHeuristic<S, A> heuristic) {
        out.println("Search using A* algorithm and heuristic " + heuristic);
        Util.Frontier<Node<S,A>> frontier = new Util.PriorityQueue<Node<S,A>>(
                (node1, node2) -> Double.compare(heuristic.value(node1.state,problem)+node1.pathCost,heuristic.value(node2.state,problem)+node2.pathCost)
        );
        frontier.push(new Node(problem.getStartState(),new ArrayList(),0));
        Node<S,A> currentNode = null;
        while (!frontier.isEmpty()){
            currentNode =  frontier.pop();
            if(problem.isGoalState(currentNode.state)){
                return new Solution<S,A>(currentNode.state,currentNode.actions,currentNode.pathCost);
            }
            if(!problem.visitedSet.contains(currentNode.state)){
                Collection<SuccessorInfo<S,A>> successorInfos= problem.expand(currentNode.state);

                for (SuccessorInfo<S,A> s:successorInfos) {
                    List<A> actions = new ArrayList<>(currentNode.actions);
                    actions.add(s.action);
                    frontier.push(new Node(s.nextState,actions, currentNode.pathCost + s.cost ));
                }
            }

        }
        return null;

    }

    private static <S, A> Solution<S, A> graphSearch(SearchProblem<S, A> problem, Util.Frontier<Node<S,A>> frontier, SearchHeuristic<S, A> heuristic) {
        // You could also implement a single graphSearch function
        // that all other search algorithm call with appropriate parameters.
        // But it is not required to do so.
        throw new RuntimeException("Not Implemented");
    }

    public static <S, A> void printSearchInfo(SearchProblem<S, A> searchProblem,
                                Solution<S, A> solution,
                                long runtime) {
        out.println("Search Problem: " + searchProblem.getClass().getName());
        if (solution != null) {
            out.println("Solution found in " + (double) runtime / 1000 + "s");
            out.println("Solution cost " + solution.pathCost);
        } else {
            out.println("No solution found in " + (double) runtime / 1000 + "s");
        }
        out.println("Expanded count " + searchProblem.getExpandedCount());
        out.println("");
    }
}

/**
 * You might want to implement this data structure to represent nodes of a search tree.
 * You could store objects of this class in the frontier.
 */
class Node<S, A> implements Comparable<Node<S, A>> {


    public S state;
    public List<A> actions;
    double pathCost;

    public Node(S state, List<A> actions,double pathCost) {
        this.state = state;
        this.actions = actions;
        this.pathCost = pathCost;
    }

    @Override
    public int compareTo(Node<S, A> otherNode) {
        //TODO: YOUR CODE HERE
        throw new RuntimeException("Not Implemented");
    }

    /**
     * Useful for debugging
     */
    @Override
    public String toString() {
        return "state: "+state;
        //throw new RuntimeException("Not Implemented");
    }
}


