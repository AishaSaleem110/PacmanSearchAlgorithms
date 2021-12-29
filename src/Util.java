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
 * This file contains useful structures, including data structures for implementing
 * frontier.
 *
 *
 *
 * You should not need to modify this file.
 */

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Util {

    /**
     * A uniform data-structure for frontier.
     */
    public static interface Frontier<N> {
        /**
         * add item to the frontier
         */
        public void push(N item);
        /**
         * select a node to expand from the frontier
         */
        public N pop();

        /**
         * check if frontier is empty
         */
        public boolean isEmpty();
    }

    public static class Stack<N> implements Frontier<N> {
        /**
         * A container with a last-in-first-out (LIFO) queuing policy.
         */

        LinkedList<N> elements = null;

        Stack() {
            elements = new LinkedList<N>();
        }

        /**
         * Push 'item' onto the stack
         */
        public void push(N item) {
            elements.add(item);
        }

        /**
         * Pop the most recently pushed item from the stack
         */
        public N pop() {
            return elements.pollLast();
        }

        /**
         * Returns true if the stack is empty
         */
        public boolean isEmpty() {
            return elements.isEmpty();
        }
    };

    public static class Queue<N> implements Frontier<N> {
        /**
         * A container with a first-in-first-out (FIFO) queuing policy."
         */

        LinkedList<N> elements = null;

        Queue() {
            elements = new LinkedList<N>();
        }

        /**
         * Add 'item' to the queue
         */
        public void push(N item) {
            elements.add(item);
        }

        /**
         * Dequeue the earliest enqueued item still in the queue. This
         * operation removes the item from the queue.
         */
        public N pop() {
            return elements.poll();
        }

        /**
         * Returns true if the queue is empty
         */
        public boolean isEmpty() {
            return elements.isEmpty();
        }
    }

    public static class PriorityQueue<N> implements Frontier<N> {
        /**
         * A container for a priority queue. N is supposed to implement Comparable<N> interface.
         * The elements of the priority queue are ordered according to their ordering, where
         * the head of the queue is the least element."
         */

        java.util.PriorityQueue<N> elements = null;

        PriorityQueue() {
            elements = new java.util.PriorityQueue<>();
        }

        PriorityQueue(Comparator<? super N> comparator) {
            elements = new java.util.PriorityQueue<>(comparator);
        }
        /**
         * Add 'item' to the queue
         */
        public void push(N item) {
            elements.add(item);
        }

        /**
         * Dequeue the head of the queue. This operation removes the item from the queue.
         */
        public N pop() {
            return elements.poll();
        }

        /**
         * Returns true if the queue is empty
         */
        public boolean isEmpty() {
            return elements.isEmpty();
        }
    }
}

/**
 * This class contains an implementation of a two-dimensional integer coordinate.
 * Used for describing locations in a gridworld.
 *
 * Implements useful functionality, for instance, addition of two coordinates,
 * manhattan distance between coordinates.
 */

class Coordinate {
    public int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate add(Coordinate c) {
        return new Coordinate(x + c.x, y + c.y);
    }

    public double manhattanDistance(Coordinate other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * The following two methods are important for correct functioning of
     * Sets (for instance, HashSet) with objects of this class.
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coordinate))
            return false;

        Coordinate c = (Coordinate) o;
        return c.x == this.x && c.y == this.y;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 7;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }
}

class Solution<S, A> {
    public S goalState;
    public List<A> actions;
    public double pathCost;

    public Solution(S goalState, List<A> actions, double pathCost) {
        this.goalState = goalState;
        this.actions = actions;
        this.pathCost = pathCost;
    }

    public String toString() {
        return goalState.toString() + ", " + actions.toString() + ", " + pathCost;
    }
}

class SuccessorInfo<S,A> {
    /**
     * This is a container class for describing successors of a state when doing search.
     * In particular, when we expand a node, we get a collection of such triples:
     * the next state, the action to get to that state, and the cost of that action.
     */
    S nextState;
    A action;
    double cost;

    public SuccessorInfo(S nextState, A action, double cost) {
        this.nextState = nextState;
        this.action = action;
        this.cost = cost;
    }

    public String toString() {
        return nextState.toString() + ", " + action.toString() + ", " + cost;
    }
}


