import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

class GraphAction implements Action {

    String label;
    public GraphAction(String action) {
        label = action;
    }

    @Override
    public String toString() {
        return label;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GraphAction))
            return false;
        return label.equals(((GraphAction) o).label);
    }
}

class GraphParser {
    public static GraphSearchProblem parse(String graphEncoding) {
        String[] lines = graphEncoding.split("\n");
        String[] r = lines[0].split("start_state: ");

        GraphSearchState startState = new GraphSearchState(r[1]);

        r = lines[1].split("goal_states: ");

        Set<GraphSearchState> goalStates = Arrays.asList(r[1].split("")).stream().map(GraphSearchState::new).collect(Collectors.toSet());

        Set<GraphSearchState> allStates = new HashSet<>();
        allStates.addAll(goalStates);
        allStates.add(startState);

        Map<GraphSearchState, Collection<SuccessorInfo<GraphSearchState, GraphAction>>> successors = new HashMap<>();
        for (int i=2; i< lines.length; i++) {
            String line = lines[i];
            r = line.split(" ");

            String start;
            String action;
            String next_state;
            double cost;
            if (r.length == 3) {
                start = r[0];
                action = r[1];
                next_state = r[2];
                cost = 1;
            }
            else if (r.length == 4) {
                start = r[0];
                action = r[1];
                next_state = r[2];
                cost = Double.parseDouble(r[3]);
            } else {
                throw new RuntimeException("Parsing error while parsing line " + i + ".\n " +
                        "Read line " + line);
            }

            GraphSearchState fromNode = new GraphSearchState(start);
            GraphSearchState toNode = new GraphSearchState(next_state);
            allStates.add(fromNode);
            allStates.add(toNode);

            Collection<SuccessorInfo<GraphSearchState, GraphAction>> successorsList =
                successors.containsKey(fromNode) ? successors.get(fromNode) : new ArrayList<>();
            successorsList.add(new SuccessorInfo<>(toNode, new GraphAction(action), cost));
            successors.put(fromNode, successorsList);
        }

        //make sure that for every state there is an entry in the successors map
        for(GraphSearchState state: allStates) {
            Collection<SuccessorInfo<GraphSearchState, GraphAction>> successorsList =
                    successors.containsKey(state) ? successors.get(state) : new ArrayList<>();
            successors.put(state, successorsList);
        }

        return new GraphSearchProblem(startState, goalStates, successors);
    }

    public static List<List<GraphAction>> parseSolutions(List<String> solutionEncodings) {
        List<List<GraphAction>> solutions = new ArrayList<>();

        for(String solutionEncoding: solutionEncodings) {
            String[] r = solutionEncoding.split(" ");
            List<GraphAction> actions = Arrays.stream(r).map(GraphAction::new).collect(Collectors.toList());
            solutions.add(actions);
        }

        return solutions;
    }

    public static List<Set<GraphSearchState>> parseExpandedStates(List<String> expandedStates) {
        List<Set<GraphSearchState>> expandedSets = new ArrayList<>();

        for(String encoding: expandedStates) {
            String[] r = encoding.split(" ");
            Set<GraphSearchState> expanded = Arrays.stream(r).map(GraphSearchState::new).collect(Collectors.toSet());
            expandedSets.add(expanded);
        }

        return expandedSets;
    }

    public static GraphHeuristic<GraphSearchState,GraphAction> parseHeuristic(String heuristicEncoding) {
        String[] lines = heuristicEncoding.split("\n");

        String[] r;

        Map<GraphSearchState, Double> values = new HashMap<>();

        for (int i=0; i< lines.length; i++) {
            String line = lines[i];
            r = line.split(" ");

            if (r.length == 2) {
                values.put(new GraphSearchState(r[0]), Double.valueOf(r[1]));
            } else {
                throw new RuntimeException("Parsing error while parsing line " + i + " of the heuristic encoding.\n " +
                        "Read line " + line);
            }
        }

        return new GraphHeuristic<>(values);
    }
}

class GraphSearchState implements SearchState {
    String nodeName;
    public GraphSearchState(String name) {
        nodeName = name;
    }

    @Override
    public String toString() {
        return nodeName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GraphSearchState))
            return false;
        return nodeName.equals(((GraphSearchState) o).nodeName);
    }

    @Override
    public int hashCode() {
        return nodeName.hashCode();
    }
}

class GraphSearchProblem extends SearchProblem<GraphSearchState, GraphAction> {
    GraphSearchState startState;
    Set<GraphSearchState> goalStates;
    Map<GraphSearchState, Collection<SuccessorInfo<GraphSearchState, GraphAction>>> successors;

    public GraphSearchProblem(GraphSearchState startState,
                              Set<GraphSearchState> goalStates,
                              Map<GraphSearchState, Collection<SuccessorInfo<GraphSearchState, GraphAction>>> successors) {

        this.startState = startState;
        this.goalStates = goalStates;
        this.successors = successors;
    }

    @Override
    public GraphSearchState getStartState() {
        return startState;
    }

    @Override
    public boolean isGoalState(GraphSearchState state) {
        return goalStates.contains(state);
    }

    @Override
    public Collection<SuccessorInfo<GraphSearchState, GraphAction>> expand(GraphSearchState state) {
        doBookKeeping(state);

        return successors.get(state);
    }

    @Override
    public List<GraphAction> getActions(GraphSearchState state) {
        return successors.get(state).stream()
                .map(successor -> successor.action ).collect(Collectors.toList());
    }

    @Override
    public GraphSearchState getSuccessor(GraphSearchState state, GraphAction action) {
        List<GraphSearchState> nextStates = successors.get(state).stream()
                .filter(successor -> successor.action.equals(action))
                .map(successor -> successor.nextState)
                .collect(Collectors.toList());
        if (!nextStates.isEmpty())
            return nextStates.get(0);
        else
            throw new RuntimeException("Invalid action " + action + " at state " + state);
    }

    @Override
    public double getCost(GraphSearchState state, GraphAction action) {
        List<Double> costs = successors.get(state).stream()
                .filter(successor -> successor.action.equals(action))
                .map(successor -> successor.cost)
                .collect(Collectors.toList());
        if (!costs.isEmpty())
            return costs.get(0);
        else
            throw new RuntimeException("Invalid action " + action + " at state " + state);
    }
}

class QuestionSuite {
    String questionName;
    TestCase[] tests;

    public QuestionSuite(String questionName, TestCase[] tests) {
        this.questionName = questionName;
        this.tests = tests;
    }
}

class TestResult {
    boolean passed;
    String message;

    public TestResult(boolean passed, String message) {
        this.passed = passed;
        this.message = message;
    }
}

class ProblemSetup<S, A> {
    SearchProblem<S, A> problem;
    S state;
    SearchHeuristic<S, A> heuristic;

    public ProblemSetup(SearchProblem<S, A> problem,
            S state,
            SearchHeuristic<S, A> heuristic) {
        this.problem = problem;
        this.state = state;
        this.heuristic = heuristic;
    }
}

abstract class TestCase<S,A> {
    String testname;
    String problemEncoding;
    String searchProblemClassName;

    public TestCase(String testname, String problemEncoding, String searchProblemClassName) {
        this.testname = testname;
        this.problemEncoding = problemEncoding;
        this.searchProblemClassName = searchProblemClassName;
    }

    abstract public TestResult execute() throws Exception;

    protected boolean checkSolution(SearchProblem<S, A> problem, Solution<S, A> solution) {
        S state = problem.getStartState();
        for (A action: solution.actions) {
            state = problem.getSuccessor(state, action);
        }
        return problem.isGoalState(state);
    }

    protected double computeSolutionCost(SearchProblem<S, A> problem, Solution<S, A> solution) {
        double cost = 0;
        S state = problem.getStartState();
        for(A action: solution.actions) {
            cost += problem.getCost(state, action);
            state = problem.getSuccessor(state, action);
        }
        return cost;
    }
}

class GraphSearchTest<S,A> extends TestCase<S,A> {
    String strategy;
    String heuristicEncoding;
    List<String> solutionEncodings;
    List<String> expandedStates;

    public GraphSearchTest(String testname, String problemEncoding, String searchProblemClassName,
                           String strategy, List<String> solutionEncodings, List<String> expandedStates) {
        super(testname, problemEncoding, searchProblemClassName);
        this.strategy = strategy;
        this.solutionEncodings = solutionEncodings;
        this.expandedStates = expandedStates;
        this.heuristicEncoding = null;
    }

    public GraphSearchTest(String testname, String problemEncoding, String searchProblemClassName,
                           String strategy, List<String> solutionEncodings, List<String> expandedStates,
                           String heuristicEncoding) {
        super(testname, problemEncoding, searchProblemClassName);
        this.strategy = strategy;
        this.solutionEncodings = solutionEncodings;
        this.expandedStates = expandedStates;
        this.heuristicEncoding = heuristicEncoding;
    }

    @Override
    public TestResult execute() throws Exception {

        GraphSearchProblem problem = GraphParser.parse(this.problemEncoding);

        SearchHeuristic<GraphSearchState, GraphAction> heuristic = heuristicEncoding == null ?
                new NullHeuristic<GraphSearchState, GraphAction>() : GraphParser.parseHeuristic(heuristicEncoding);

        Solution<GraphSearchState, GraphAction> solution = GraphSearch.search(strategy, problem, heuristic);
        List<Set<GraphSearchState>> expandedSets = GraphParser.parseExpandedStates(expandedStates);
        List<List<GraphAction>> goldSolutions = GraphParser.parseSolutions(solutionEncodings);

        if (goldSolutions.contains(solution.actions) && expandedSets.contains(problem.visitedSet))
            return new TestResult(true, "\tsolution:\t\t" + solution.actions + "\n" +
                                        "\texpanded states:\t" + problem.visitedSet + "\n");

        String correctSolutionMessage =
            "\tcorrect solution:\t\t" + goldSolutions.get(0) + "\n" +
            "\tcorrect expanded states:\t" + expandedSets.get(0) + "\n";
        if(expandedSets.size() > 1 && goldSolutions.size() > 1) {
            correctSolutionMessage +=
                "\tcorrect reverse solution:\t\t" + goldSolutions.get(1) + "\n" +
                "\tcorrect reverse expanded states:\t" + expandedSets.get(1) + "\n";
        }
        return new TestResult(false, "\tstudent solution:\t\t" + solution.actions + "\n" +
                                    "\tstudent expanded states:\t" + problem.visitedSet + "\n" + correctSolutionMessage);
    }
}

class GraphHeuristic<S,A> implements SearchHeuristic<S,A> {

    Map<S,Double> heuristicTable;

    public GraphHeuristic(Map<S,Double> heuristicTable) {
        this.heuristicTable = heuristicTable;
    }

    @Override
    public Double value(S state, SearchProblem<S, A> problem) {
        return heuristicTable.get(state);
    }
}


abstract class PacmanTestCase<S,A> extends TestCase<S,A> {
    public PacmanTestCase(String testname, String layoutText, String searchProblemClassName) {
        super(testname, layoutText, searchProblemClassName);
    }

    protected static Maze getMaze(String layoutText) throws Exception {
        String mazeFileName = "mazes/autograder.lay";

        PrintWriter out = new PrintWriter(mazeFileName);
        out.println(layoutText);
        out.close();

        return MazeParser.parseMaze(mazeFileName);
    }

    public ProblemSetup<S, A> setupProblem(String heuristicName) throws Exception {
        Maze maze = getMaze(problemEncoding);

        SearchProblem<S, A> searchProblem =
                (SearchProblem) Class.forName(this.searchProblemClassName).getConstructor(Maze.class).newInstance(maze);

        S state = searchProblem.getStartState();

        SearchHeuristic<S, A> heuristic = new NullHeuristic<>();
        if (heuristicName != null)
                heuristic = (SearchHeuristic) Class.forName(heuristicName).getConstructor().newInstance();

        return new ProblemSetup<S, A>(searchProblem, state, heuristic);
    }
}

class PacmanSearchTest<S,A> extends PacmanTestCase<S,A> {
    List<List<A>> goldSolutions;
    List<Integer> expandedCounts;
    String strategy;
    String heuristicName;

    public PacmanSearchTest(String testname, String layoutText, String searchProblemClassName,
                            String strategy,
                            List<List<A>> solutions, List<Integer> expandedCounts) {
        super(testname, layoutText, searchProblemClassName);
        this.strategy = strategy;
        this.goldSolutions = solutions;
        this.expandedCounts = expandedCounts;
        this.heuristicName = null;
    }

    public PacmanSearchTest(String testname, String layoutText, String searchProblemClassName,
                            String strategy,
                            List<List<A>> solutions, List<Integer> expandedCounts,
                            String heuristicName) {
        super(testname, layoutText, searchProblemClassName);
        this.strategy = strategy;
        this.goldSolutions = solutions;
        this.expandedCounts = expandedCounts;
        this.heuristicName = heuristicName;
    }

    @Override
    public TestResult execute() throws Exception {

        ProblemSetup<S, A> setup = setupProblem(heuristicName);
        Solution<S, A> solution = GraphSearch.search(strategy, setup.problem, setup.heuristic);

        if (!goldSolutions.contains(solution.actions)) {
            return new TestResult(false,
                    "Solution not correct.\n" +
                    "\tstudent solution length: " + solution.actions.size() + "\n" +
                    "\tstudent solution:\n" + solution.actions + "\n\n" +
                    "\tcorrect solution length: " + goldSolutions.get(0).size() + "\n" +
                    "\tcorrect solution:\n" + goldSolutions.get(0) + "\n" +
                    "\tcorrect (reversed) solution length: " + goldSolutions.get(1).size() + "\n" +
                    "\tcorrect (reversed) solution:\n" + goldSolutions.get(1).size() + "\n");
        }

        if (setup.problem.getExpandedCount() > Collections.max(expandedCounts)) {
            return new TestResult(false,
                    "Too many nodes expanded. Are you expanding nodes twice?\n" +
                    "\tstudent expanded count: " + setup.problem.getExpandedCount() +
                    "\tcorrect expanded count: " + expandedCounts + "\n");
        }

        return new TestResult(true,
                "\tsolution length: " + solution.actions.size() + "\n" +
                "\texpanded count:\t" + setup.problem.getExpandedCount() + "\n");

    }
}

class PacmanCornersTest extends PacmanTestCase<PacmanCornersSearchState,PacmanAction> {
    String strategy;
    int goldSolutionLength;

    public PacmanCornersTest(String testname, String layoutText, String searchProblemClassName,
                             String strategy, int solutionLength) {
        super(testname, layoutText, searchProblemClassName);
        this.strategy = strategy;
        this.goldSolutionLength = solutionLength;
    }

    @Override
    public TestResult execute() throws Exception {

        ProblemSetup<PacmanCornersSearchState, PacmanAction> setup = setupProblem("NullHeuristic");
        Solution<PacmanCornersSearchState, PacmanAction> solution = GraphSearch.search(strategy, setup.problem, setup.heuristic);

        Maze maze = getMaze(problemEncoding);
        Set<Coordinate> notVisitedCorners = new HashSet<>(Arrays.asList(
                maze.getBottomLeftCorner(),
                maze.getTopLeftCorner(),
                maze.getTopRightCorner(),
                maze.getBottomRightCorner()
        ));

        Coordinate coordinate = maze.getPacmanLocation();
        if (notVisitedCorners.contains(coordinate)) notVisitedCorners.remove(coordinate);

        for (PacmanAction action: solution.actions) {
            coordinate = coordinate.add(action.toVector());
            if (notVisitedCorners.contains(coordinate)) notVisitedCorners.remove(coordinate);
        }

        if (!notVisitedCorners.isEmpty()) {
            return new TestResult(false, "Corners missed: " + notVisitedCorners + "\n");
        }

        if (solution.actions.size() != goldSolutionLength) {
            return new TestResult(false,
                    "Optimal solution not found.\n" +
                            "\tstudent solution length: " + solution.actions.size() + "\n" +
                            "\tcorrect solution length: " + goldSolutionLength + "\n");
        }

        return new TestResult(true, "\tsolution length: " + solution.actions.size() + "\n");
    }
}

class HeuristicTest<S,A> extends PacmanTestCase<S,A> {
    String heuristicName;
    double solutionCost;
    List<A> solutionPath;

    public HeuristicTest(String testname, String layoutText, String searchProblemClassName,
                         String heuristicName, double solutionCost) {
        super(testname, layoutText, searchProblemClassName);
        this.heuristicName = heuristicName;
        this.solutionCost = solutionCost;
        this.solutionPath = null;
    }

    public HeuristicTest(String testname, String layoutText, String searchProblemClassName,
                         String heuristicName, double solutionCost, List<A> solutionPath) {
        super(testname, layoutText, searchProblemClassName);
        this.heuristicName = heuristicName;
        this.solutionCost = solutionCost;
        this.solutionPath = solutionPath;
    }

    public TestResult execute() throws Exception {
        ProblemSetup<S,A> setup = this.setupProblem(this.heuristicName);
        double h0 = setup.heuristic.value(setup.state, setup.problem);

        if (solutionCost == 0) {
            if (h0 == 0)
                return new TestResult(true, "");
            else
                return new TestResult(false, "FAIL: H(goal) != 0\n");
        }

        if (h0 < 0)
            return new TestResult(false, "FAIL: must use non-negative heuristic\n");
        if (h0 == 0)
            return new TestResult(false, "FAIL: must use non-trivial heuristic\n");
        if (h0 > solutionCost)
            return new TestResult(false, "FAIL: Inadmissible heuristic\n");

        for (SuccessorInfo<S, A> successor: setup.problem.expand(setup.state)) {
            double h1 = setup.heuristic.value(successor.nextState, setup.problem);
            if (h1 < 0)
                return new TestResult(false, "FAIL: must use non-negative heuristic\n");
            if (h0 - h1 > successor.cost)
                return new TestResult(false, "FAIL: inconsistent heuristic\n");
        }

        if (solutionPath != null) {
            S state = setup.problem.getStartState();
            for(A action: solutionPath) {
                S nextState = setup.problem.getSuccessor(state, action);
                double h1 = setup.heuristic.value(nextState, setup.problem);

                if (h0 - h1 > 1)
                    return new TestResult(false, "FAIL: inconsistent heuristic\n");

                if (h0 < 0 || h1 < 0)
                    return new TestResult(false, "FAIL: must use non-negative heuristic\n");

                h0 = h1;
                state = nextState;
            }

            if (h0 != 0)
                return new TestResult(false, "FAIL: heuristic non-zero at goal\n");
        }
        return new TestResult(true, "PASS: heuristic value less than true cost at start state\n");    }
}

class GradedHeuristicTest<S,A> extends PacmanTestCase<S,A> {
    String heuristicName;
    double solutionCost;
    List<Integer> gradingThresholds;

    public GradedHeuristicTest(String testname, String layoutText, String searchProblemClassName,
                               String heuristicName, double solutionCost,
                               List<Integer> thresholds) {
        super(testname, layoutText, searchProblemClassName);
        this.heuristicName = heuristicName;
        this.solutionCost = solutionCost;
        this.gradingThresholds = thresholds;
    }

    public TestResult execute() throws Exception {
        ProblemSetup<S, A> setup = this.setupProblem(this.heuristicName);

        double h0 = setup.heuristic.value(setup.state, setup.problem);
        if (h0 > solutionCost) {
            return new TestResult(false, "FAIL: Inadmissible heuristic\n");
        }

        Solution<S, A> solution = GraphSearch.search("astar", setup.problem, setup.heuristic);

        //System.out.println("Sequence of actions: " + solution.actions);
        //System.out.println("Number of actions:" + solution.actions.size());
        if (!checkSolution(setup.problem, solution)) {
            return new TestResult(false, "FAIL: Returned sequence of actions is not a solution\n");
        }

        double cost = computeSolutionCost(setup.problem, solution);
        if (cost != solution.pathCost)
            return new TestResult(false, "FAIL: Solution path cost is calculated incorrectly\n");
        if (cost > solutionCost)
            return new TestResult(false, "FAIL: Inconsistent heuristic\n");

        int points = 0;
        for (int threshold : gradingThresholds) {
            if (setup.problem.getExpandedCount() <= threshold)
                points += 1;
        }

        String pass = (points >= gradingThresholds.size()) ? "PASS" : "FAIL";
        return new TestResult(true, pass + ": Heuristic resulted in expansion of " + setup.problem.getExpandedCount() + " nodes\n");

    }
}

class ProblemEncodings {
    static final String GRAPH_BACKTRACK =
            "start_state: A\n" +
            "goal_states: G\n" +
            "A 0:A->B B 1.0\n" +
            "A 1:A->C C 2.0\n" +
            "A 2:A->D D 4.0\n" +
            "C 0:C->G G 8.0";

    static final String GRAPH_BFS_VS_DFS =
            "start_state: A\n" +
            "goal_states: G\n" +
            "A 0:A->B B 1.0\n" +
            "A 1:A->G G 2.0\n" +
            "A 2:A->D D 4.0\n" +
            "B 0:B->D D 8.0\n" +
            "D 0:D->G G 16.0";

    static final String GRAPH_INFINITE =
            "start_state: A\n" +
            "goal_states: G\n" +
            "A 0:A->B B 1.0\n" +
            "B 0:B->A A 2.0\n" +
            "B 1:B->C C 4.0\n" +
            "C 0:C->A A 8.0\n" +
            "C 1:C->G G 16.0\n" +
            "C 2:C->B B 32.0";

    static final String GRAPH_MANY_PATHS =
            "start_state: A\n" +
            "goal_states: G\n" +
            "A 0:A->B1 B1 1.0\n" +
            "A 1:A->C C 2.0\n" +
            "A 2:A->B2 B2 4.0\n" +
            "B1 0:B1->C C 8.0\n" +
            "B2 0:B2->C C 16.0\n" +
            "C 0:C->D D 32.0\n" +
            "D 0:D->E1 E1 64.0\n" +
            "D 1:D->F F 128.0\n" +
            "D 2:D->E2 E2 256.0\n" +
            "E1 0:E1->F F 512.0\n" +
            "E2 0:E2->F F 1024.0\n" +
            "F 0:F->G G 2048.0";

    static final String GRAPH_A_STAR_0 =
            "start_state: A\n" +
            "goal_states: H F\n" +
            "A Right B 2.0\n" +
            "B Right H 4.0\n" +
            "B Down D 1.0\n" +
            "B Up C 2.0\n" +
            "B Left A 2.0\n" +
            "C Down B 2.0\n" +
            "D Right E 2.5\n" +
            "D Down F 2.0\n" +
            "D Left G 1.5";

    static final String GRAPH_HEURISTIC =
            "start_state: S\n" +
            "goal_states: G\n" +
            "S 0 A 2.0\n" +
            "S 1 B 3.0\n" +
            "S 2 D 5.0\n" +
            "A 0 C 3.0\n" +
            "A 1 S 2.0\n" +
            "B 0 D 4.0\n" +
            "B 1 S 3.0\n" +
            "C 0 A 3.0\n" +
            "C 1 D 1.0\n" +
            "C 2 G 2.0\n" +
            "D 0 B 4.0\n" +
            "D 1 C 1.0\n" +
            "D 2 G 5.0\n" +
            "D 3 S 5.0";

    static final String GRAPH_GOAL_AT_DEQUEUE =
            "start_state: A\n" +
            "goal_states: G\n" +
            "A 0:A->G G 10.0\n" +
            "A 1:A->B B 1.0\n" +
            "B 0:B->C C 1.0\n" +
            "C 0:C->G G 1.0";

    static final String PACMAN_TINY_CORNERS =
            "%%%%%%%%\n" +
            "%.    .%\n" +
            "%   P  %\n" +
            "% %%%% %\n" +
            "% %    %\n" +
            "% % %%%%\n" +
            "%.%   .%\n" +
            "%%%%%%%%";

    static final String PACMAN_TINY_CORNERS2 =
            "%%%%%%%%\n" +
            "%P    .%\n" +
            "%      %\n" +
            "% %%%% %\n" +
            "% %    %\n" +
            "% % %%%%\n" +
            "%.%   .%\n" +
            "%%%%%%%%";

    static final String PACMAN_TINY_CORNERS3 =
            "%%%%%%%%\n" +
            "%.    .%\n" +
            "%      %\n" +
            "% %%%% %\n" +
            "% %    %\n" +
            "% % %%%%\n" +
            "%P%   .%\n" +
            "%%%%%%%%";

    static final String PACMAN_MEDIUM_MAZE =
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
            "%                                 P%\n" +
            "% %%%%%%%%%%%%%%%%%%%%%%% %%%%%%%% %\n" +
            "% %%   %   %      %%%%%%%   %%     %\n" +
            "% %% % % % % %%%% %%%%%%%%% %% %%%%%\n" +
            "% %% % % % %             %% %%     %\n" +
            "% %% % % % % % %%%%  %%%    %%%%%% %\n" +
            "% %  % % %   %    %% %%%%%%%%      %\n" +
            "% %% % % %%%%%%%% %%        %% %%%%%\n" +
            "% %% %   %%       %%%%%%%%% %%     %\n" +
            "%    %%%%%% %%%%%%%      %% %%%%%% %\n" +
            "%%%%%%      %       %%%% %% %      %\n" +
            "%      %%%%%% %%%%% %    %% %% %%%%%\n" +
            "% %%%%%%      %       %%%%% %%     %\n" +
            "%        %%%%%% %%%%%%%%%%% %%  %% %\n" +
            "%%%%%%%%%%                  %%%%%% %\n" +
            "%.         %%%%%%%%%%%%%%%%        %\n" +
            "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%";

    static final List<PacmanAction> MEDIUM_MAZE_SOLUTION_DFS1 =
            List.of(PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST);
    static final List<PacmanAction> MEDIUM_MAZE_SOLUTION_DFS2 =
            List.of(PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.NORTH,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.NORTH,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST);
    static final List<PacmanAction> MEDIUM_MAZE_SOLUTION_BFS =
            List.of(PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.NORTH,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                    PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH,
                    PacmanAction.SOUTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.SOUTH, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                    PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST);
}

public class Autograder {

    static final QuestionSuite Q1 = new QuestionSuite("Q1", new TestCase[]{
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphBacktrack", ProblemEncodings.GRAPH_BACKTRACK,
                    "GraphSearchProblem",
                    "dfs",
                    List.of("1:A->C 0:C->G", "1:A->C 0:C->G"), List.of("A D C", "A B C")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphBFSvsDFS", ProblemEncodings.GRAPH_BFS_VS_DFS,
                    "GraphSearchProblem", "dfs",
                    List.of("2:A->D 0:D->G", "0:A->B 0:B->D 0:D->G"), List.of("A D", "A B D")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphInfinite", ProblemEncodings.GRAPH_INFINITE,
                    "GraphSearchProblem", "dfs",
                    List.of("0:A->B 1:B->C 1:C->G"), List.of("A B C")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphManyPaths", ProblemEncodings.GRAPH_MANY_PATHS,
                    "GraphSearchProblem", "dfs",
                    List.of("2:A->B2 0:B2->C 0:C->D 2:D->E2 0:E2->F 0:F->G", "0:A->B1 0:B1->C 0:C->D 0:D->E1 0:E1->F 0:F->G"),
                    List.of("A B2 C D E2 F", "A B1 C D E1 F")
            ),
            new PacmanSearchTest<PacmanPositionSearchState, PacmanAction>(
                    "Pacman 1 test", ProblemEncodings.PACMAN_MEDIUM_MAZE,
                    "PacmanPositionSearchProblem",  "dfs",
                    List.of(ProblemEncodings.MEDIUM_MAZE_SOLUTION_DFS1, ProblemEncodings.MEDIUM_MAZE_SOLUTION_DFS2),
                    List.of(146, 269)
            ),
    });

    static final QuestionSuite Q2 = new QuestionSuite("Q2", new TestCase[]{
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphBacktrack", ProblemEncodings.GRAPH_BACKTRACK,
                    "GraphSearchProblem", "bfs",
                    List.of("1:A->C 0:C->G", "1:A->C 0:C->G"), List.of("A B C D", "A D C B")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphBFSvsDFS", ProblemEncodings.GRAPH_BFS_VS_DFS,
                    "GraphSearchProblem", "bfs",
                    List.of("1:A->G", "1:A->G"), List.of("A B", "A D")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphInfinite", ProblemEncodings.GRAPH_INFINITE,
                    "GraphSearchProblem", "bfs",
                    List.of("0:A->B 1:B->C 1:C->G"), List.of("A B C")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphManyPaths", ProblemEncodings.GRAPH_MANY_PATHS,
                    "GraphSearchProblem", "bfs",
                    List.of("1:A->C 0:C->D 1:D->F 0:F->G", "1:A->C 0:C->D 1:D->F 0:F->G"),
                    List.of("A B1 C B2 D E1 F E2", "A B2 C B1 D E2 F E1")
            ),
            new PacmanSearchTest<PacmanPositionSearchState, PacmanAction>(
                    "Pacman 1 test", ProblemEncodings.PACMAN_MEDIUM_MAZE,
                    "PacmanPositionSearchProblem",  "bfs",
                    List.of(ProblemEncodings.MEDIUM_MAZE_SOLUTION_BFS),
                    List.of(269)
            ),
    });

    static final QuestionSuite Q3 = new QuestionSuite("Q3", new TestCase[]{
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "AStar 0", ProblemEncodings.GRAPH_A_STAR_0,
                    "GraphSearchProblem", "astar",
                    List.of("Right Down Down"), List.of("A B D C G")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphHeuristic", ProblemEncodings.GRAPH_HEURISTIC,
                    "GraphSearchProblem", "astar",
                    List.of("0 0 2"), List.of("S A D C"),
                    "S 6.0\n" +
                    "A 2.5\n" +
                    "B 5.25\n" +
                    "C 1.125\n" +
                    "D 1.0625\n" +
                    "G 0"
            ),
            new PacmanSearchTest<PacmanPositionSearchState, PacmanAction>(
                    "AStar Manhattan", ProblemEncodings.PACMAN_MEDIUM_MAZE,
                    "PacmanPositionSearchProblem", "astar",
                    List.of(ProblemEncodings.MEDIUM_MAZE_SOLUTION_BFS),
                    List.of(221), "ManhattanDistanceHeuristic"
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphGoalAtDequeue", ProblemEncodings.GRAPH_GOAL_AT_DEQUEUE,
                    "GraphSearchProblem", "astar",
                    List.of("1:A->B 0:B->C 0:C->G"), List.of("A B C")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphBacktrack", ProblemEncodings.GRAPH_BACKTRACK,
                    "GraphSearchProblem", "astar",
                    List.of("1:A->C 0:C->G"), List.of("A B C D")
            ),
            new GraphSearchTest<GraphSearchState, GraphAction>(
                    "GraphManyPaths", ProblemEncodings.GRAPH_MANY_PATHS,
                    "GraphSearchProblem", "astar",
                    List.of("1:A->C 0:C->D 1:D->F 0:F->G"),
                    List.of("A B1 C B2 D E1 F E2")
            ),
    });

    static final QuestionSuite Q4 = new QuestionSuite("Q4", new TestCase[]{
            new PacmanCornersTest(
                    "Corners Tiny", ProblemEncodings.PACMAN_TINY_CORNERS,
                    "PacmanCornersProblem", "bfs",
                    28
            ),
            new PacmanCornersTest(
                    "Corners Tiny 2", ProblemEncodings.PACMAN_TINY_CORNERS2,
                    "PacmanCornersProblem", "bfs",
                    26
            ),
            new PacmanCornersTest(
                    "Corners Tiny 3", ProblemEncodings.PACMAN_TINY_CORNERS3,
                    "PacmanCornersProblem", "bfs",
                    21
            ),
    });

    static final QuestionSuite Q5 = new QuestionSuite("Q5", new TestCase[]{
            new HeuristicTest<PacmanCornersSearchState, PacmanAction>(
                    "Test 1",
                    "%%%%%%\n" +
                    "%.  .%\n" +
                    "%P   %\n" +
                    "%.  .%\n" +
                    "%%%%%%",
                    "PacmanCornersProblem", "CornersHeuristic", 8,
                    List.of(PacmanAction.NORTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.EAST,
                            PacmanAction.EAST, PacmanAction.EAST, PacmanAction.NORTH, PacmanAction.NORTH)
            ),
            new HeuristicTest<PacmanCornersSearchState, PacmanAction>(
                    "Test 2",
                    "%%%%%%\n" +
                    "%.  .%\n" +
                    "% %% %\n" +
                    "%.P%.%\n" +
                    "%%%%%%",
                    "PacmanCornersProblem", "CornersHeuristic", 8,
                    List.of(PacmanAction.WEST, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.EAST,
                            PacmanAction.EAST, PacmanAction.EAST, PacmanAction.SOUTH, PacmanAction.SOUTH)
            ),
            new HeuristicTest<PacmanCornersSearchState, PacmanAction>(
                    "Test 3",
                    "%%%%%%%%\n" +
                    "%.%   .%\n" +
                    "% % %  %\n" +
                    "% % %P %\n" +
                    "%   %  %\n" +
                    "%%%%%  %\n" +
                    "%.    .%\n" +
                    "%%%%%%%%",
                    "PacmanCornersProblem", "CornersHeuristic", 26,
                    List.of(PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST,
                            PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.EAST,
                            PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST, PacmanAction.EAST,
                            PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH,
                            PacmanAction.NORTH, PacmanAction.WEST, PacmanAction.WEST, PacmanAction.WEST,
                            PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.SOUTH, PacmanAction.WEST,
                            PacmanAction.WEST, PacmanAction.NORTH, PacmanAction.NORTH, PacmanAction.NORTH)
            ),
            new GradedHeuristicTest<PacmanCornersSearchState, PacmanAction>(
                    "MediumCorners",
                    "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n" +
                    "%.      % % %              %.%\n" +
                    "%       % % %%%%%% %%%%%%% % %\n" +
                    "%       %        %     % %   %\n" +
                    "%%%%% %%%%% %%% %% %%%%% % %%%\n" +
                    "%   % % % %   %    %     %   %\n" +
                    "% %%% % % % %%%%%%%% %%% %%% %\n" +
                    "%       %     %%     % % %   %\n" +
                    "%%% % %%%%%%% %%%% %%% % % % %\n" +
                    "% %           %%     %     % %\n" +
                    "% % %%%%% % %%%% % %%% %%% % %\n" +
                    "%   %     %      % %   % %%% %\n" +
                    "%.  %P%%%%%      % %%% %    .%\n" +
                    "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%",
                    "PacmanCornersProblem", "CornersHeuristic", 106,
                    List.of(2000, 1600, 1200))
    });

    static final QuestionSuite Q6 = new QuestionSuite("Q6", new TestCase[] {
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 1",
                    "%%%%%%\n" +
                    "%    %\n" +
                    "%    %\n" +
                    "%P   %\n" +
                    "%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 0),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 2",
                    "%%%\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "%P%\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 0),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 3",
                    "%%%%\n" +
                    "%  %\n" +
                    "%  %\n" +
                    "%P %\n" +
                    "%  %\n" +
                    "%  %\n" +
                    "%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 0),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 4",
                    "%%%%%%%%\n" +
                    "% %    %\n" +
                    "% % %% %\n" +
                    "% %P%% %\n" +
                    "%      %\n" +
                    "%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 0),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 5",
                    "%%%%%%\n" +
                    "%....%\n" +
                    "%....%\n" +
                    "%P...%\n" +
                    "%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 11),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 6",
                    "%%%%%%\n" +
                    "%   .%\n" +
                    "%.P..%\n" +
                    "%    %\n" +
                    "%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 5),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 7",
                    "%%%%%%%\n" +
                    "%    .%\n" +
                    "%. P..%\n" +
                    "%     %\n" +
                    "%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 7),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 8",
                    "%%%%%%\n" +
                    "%   .%\n" +
                    "%   .%\n" +
                    "%P  .%\n" +
                    "%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 5),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 9",
                    "%%%%%%\n" +
                    "% %. %\n" +
                    "% %%.%\n" +
                    "%P. .%\n" +
                    "%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 6),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 10",
                    "%%%%%%%%\n" +
                    "%      %\n" +
                    "%.  P .%\n" +
                    "%      %\n" +
                    "%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 7),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 11",
                    "%%%%%%%%\n" +
                    "%      %\n" +
                    "%   P  %\n" +
                    "%.  . .%\n" +
                    "%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 8),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 12",
                    "%%%%%%%%\n" +
                    "%      %\n" +
                    "%    P.%\n" +
                    "%      %\n" +
                    "%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 1),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 13",
                    "%%%%%%%%\n" +
                    "%      %\n" +
                    "%P.   .%\n" +
                    "%      %\n" +
                    "%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 5),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 14",
                    "%%%%%%%%%%\n" +
                    "%        %\n" +
                    "% ...%...%\n" +
                    "% .%.%.%.%\n" +
                    "% .%.%.%.%\n" +
                    "% .%.%.%.%\n" +
                    "% .%.%.%.%\n" +
                    "% .%.%.%.%\n" +
                    "%P.%...%.%\n" +
                    "%        %\n" +
                    "%%%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 31),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 15",
                    "%%%\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "%.%\n" +
                    "%.%\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "%.%\n" +
                    "% %\n" +
                    "%P%\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "% %\n" +
                    "%.%\n" +
                    "%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 21),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 16",
                    "%%%%\n" +
                    "% .%\n" +
                    "%  %\n" +
                    "%P %\n" +
                    "%  %\n" +
                    "% .%\n" +
                    "%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 7),
            new HeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "Test 17",
                    "%%%%%%%%\n" +
                    "%.%....%\n" +
                    "%.% %%.%\n" +
                    "%.%P%%.%\n" +
                    "%...  .%\n" +
                    "%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 16),
            new GradedHeuristicTest<PacmanFoodSearchState, PacmanAction>(
                    "trickySearch",
                    "%%%%%%%%%%%%%%%%%%%%\n" +
                    "%.           ..%   %\n" +
                    "%.%%.%%.%%.%%.%% % %\n" +
                    "%        P       % %\n" +
                    "%%%%%%%%%%%%%%%%%% %\n" +
                    "%.....             %\n" +
                    "%%%%%%%%%%%%%%%%%%%%",
                    "PacmanFoodSearchProblem", "FoodHeuristic", 60,
                    List.of(15000, 12000, 9000)),
            });

    public static void main(String[] args) throws Exception {

        QuestionSuite[] questionSuites = new QuestionSuite[]{
                Autograder.Q1,
                Autograder.Q2,
                Autograder.Q3,
                Autograder.Q4,
                Autograder.Q5,
                Autograder.Q6
        };

        StringBuilder messageTrace = new StringBuilder();
        for (QuestionSuite questionSuite: questionSuites) {
            messageTrace.append("Question " + questionSuite.questionName + "\n============").append("\n");
            boolean questionPassed = true;

            try {
                for (TestCase testCase : questionSuite.tests) {
                    TestResult result = testCase.execute();
                    if (result.passed) {
                        messageTrace.append("PASS: " + testCase.testname).append("\n");
                        messageTrace.append(result.message);
                    } else {
                        questionPassed = false;
                        messageTrace.append("FAIL: " + testCase.testname).append("\n");
                        messageTrace.append(result.message);
                        break;
                    }
                }
            } catch (Exception e) {
                questionPassed = false;
            }

            if(questionPassed) {
                messageTrace.append("Question " + questionSuite.questionName + " passed").append("\n\n");
            } else {
                messageTrace.append("Question " + questionSuite.questionName + " failed").append("\n\n");
            }
        }

        System.err.println(messageTrace);
    }
}
