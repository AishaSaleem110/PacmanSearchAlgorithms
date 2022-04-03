# PACMAN Game Search Algorithms

Assignment reconstructs Optional Homework on Search (based on the Search project brom Berkeley http://ai.berkeley.edu/), with the difference it is in Java and comes without fancy graphical interface.

# Background

**Files I have worked on/ edited:**
GraphSearch.java	Where all of my search algorithms reside.
SearchProblem.java 	Where all formalisations of search problems and search heuristics reside.

**Files given:**
Pacman.java	The main file that runs search algorithms for Pacman search problems.
Maze.java	The class describing maze with some useful functionality and a dedicated maze parser.
Util.java	Useful data structures for implementing search algorithms.


### The Code

This code is written in Java

### Running The Code
Download the code, unzip it and change to the directory. You can compile the code from command line as follows:

javac @compile_list.txt
Once compiled, you should be able to run the code for a naive search strategy  that always tells Pacman to go west and that has been implemented for you. This strategy allows Pacman to successfully arrive at the goal state in testMaze.

java Pacman -l testMaze -f gowest -a
java Pacman -l testMaze -f gowest -s
You can see the list of all options and their default values via:

java Pacman --help


*The files are self-contained and all necessary libraries are imported.

### Search Algorithms Implemented
Question 1: Finding a Fixed Food Dot using Depth First Search
Question 2: Breadth First Search
Question 3: A* search
Question 4: Finding All the Corners
Question 5: Corners Problem: Heuristic
Question 6: Eating All The Dots


