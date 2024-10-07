
import java.util.Collections;
import java.util.*;

public class Puzzle {

    // Goal state for the 8-puzzle
    private final int[][] goalState = {
            { 1, 2, 3, },
            { 4, 5, 6, },
            { 7, 8, 0 }
    };

    private int[][] currentState; // stores the current puzzle state
    private PriorityQueue<State> openList; // List of satets to be explored ( the PQ for A*)
    private Set<State> closedList; // set of already explored states
    private int nodesExpanded = 0;
    private int nodesGenerated = 0;
    private boolean useManhattan = true;

    // constructor to initialize the PQ and the HashSet
    public Puzzle() {

        openList = new PriorityQueue<>(Comparator.comparingInt(State::getCost));
        closedList = new HashSet<>();

    }

    // method sot get the user input for the start state of the puzzle
    public void userInput() {

        Scanner scanner = new Scanner(System.in);
        currentState = new int[3][3]; // initalize the 3x3 board

        System.out.println("Enter the start state in a (3x3 grid, seperated with space from numbers 0-8)");
        System.out.println("Example:\n2 3 4\n1 5 6\n8 7 0");
        System.out.println();

        // read the user input to generate the start state
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                currentState[i][j] = scanner.nextInt();

            }
        }

        System.out.println("Enter '1' to use the Manhattan Distance or '2' to use Misplaced tiles heuristic ");
        int choice = scanner.nextInt();
        useManhattan = (choice == 1); // if the input is '1' then use manhattan

    }

    // A* search to solve the puzzle
    public void solve() {

        // create the start state with g = 0 and heuristic value (h(n))
        State startState = new State(currentState, 0, calculateSumOfError(currentState));
        openList.add(startState); // add start state to the openList
        nodesGenerated++;

        // loop until a solution is found or no more states are available to explore
        while (!openList.isEmpty()) {

            State current = openList.poll(); // get the current state that has the lowest cost (g + h)
            nodesExpanded++;
            closedList.add(current); // mark the state as explored

            // want to check if the current state is the actual goal state
            if (Arrays.deepEquals(current.board, goalState)) {

                printSolution(current); // print the solution path
                return;

            }

            // find and explore the neighboring states
            makeAMove(current);

        }

        // at the end if no solution was found print
        System.out.println("No Solution found");

    }

    // get all possible moves from the current state
    private void makeAMove(State current) {

        List<State> neighbors = new ArrayList<>();

        // this is for trying to move in all four of the directions and find the
        // neighboting states
        neighbors.addAll(moveLeft(current));
        neighbors.addAll(moveRight(current));
        neighbors.addAll(moveUp(current));
        neighbors.addAll(moveDown(current));

        // add the neighbors that are valid to the open list if they havent been
        // explored yet
        for (State neighbor : neighbors) {

            if (!closedList.contains(neighbor)) {
                openList.add(neighbor);
                nodesGenerated++;
            }

        }

        // after getting the neighbors, add them to the closed List
        // closedList.add(current);

    }

    // Methods to generate specific movements
    private List<State> moveLeft(State current) {
        return move(current, 0, -1); // Move left (decrease column index)
    }

    private List<State> moveRight(State current) {
        return move(current, 0, 1); // Move right (increase column index)
    }

    private List<State> moveUp(State current) {
        return move(current, -1, 0); // Move up (decrease row index)
    }

    private List<State> moveDown(State current) {
        return move(current, 1, 0); // Move down (increase row index)
    }

    // method to perform the mvovement in a specific direction
    private List<State> move(State current, int x, int y) { // x = row y = column

        List<State> moves = new ArrayList<>();
        int[][] board = current.board;
        int zeroX = 0, zeroY = 0;

        // find all the postions of the empty cell -> empty cell has a value of 0
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {

                    zeroX = i;
                    zeroY = j;

                }
            }
        }

        // find the new postion after the move is made
        int newX = zeroX + x;
        int newY = zeroY + y;

        // see if the new positions are within the bounds of the baord/ valid
        if (newX >= 0 && newX < 3 && newY >= 0 && newY < 3) {

            int[][] newBoard = deepCopy(board);

            // swap the empty cell with the target cell
            newBoard[zeroX][zeroY] = newBoard[newX][newY];
            newBoard[newX][newY] = 0;

            // with teh updated board a new state has to be created, with the incremented
            // cost and heurisitic
            moves.add(new State(newBoard, current.g + 1, calculateSumOfError(newBoard), current));
            nodesGenerated++;

        }

        return moves;

    }

    // this method will calculate the heuristic (Manhattan distance) for the A*

    private int calculateSumOfError(int[][] board) {
        
          int heurisitic = 0;
          
          // calculate the Manhattan Distance for each tile
          for (int i = 0; i < 3; i++) {
          for (int j = 0; j < 3; j++) {
          int value = board[i][j];
          
          if (value != 0) {
          
          int targetX = (value - 1) / 3; // Target is the row for this value
          int targetY = (value - 1) % 3; // Target is the column for this value
          heurisitic += Math.abs(i - targetX) + Math.abs(j - targetY); // this is the
          //sum of the verticle and
          // horizontial values
          
          }
          }
         }
          
          
         

        // int misplaced = 0;

        // // count tiles that arent in their target postition
        // for (int i = 0; i < 3; i++) {
        //     for (int j = 0; j < 3; j++) {
        //         if (board[i][j] != 0 && board[i][j] != goalState[i][j]) {
        //             misplaced++;
        //         }
        //     }
        // }
        return heurisitic;

    }

    // print the solution path that is from the start state to the goal state
    private void printSolution(State goalState) {

        List<int[][]> path = new ArrayList<>();
        State current = goalState;

        // backtrack from the goal state to the start state
        while (current != null) {

            path.add(current.board);
            current = current.parent;

        }

        // reverse the path from the start to the goal
        Collections.reverse(path);
        System.out.println("Solution Path: ");
        for (int[][] board : path) {

            printBoard(board);
            System.out.println();

        }

        System.out.println("Number of takin steps: " + (path.size() - 1)); // this shows the numbers of steps required
        System.out.println("Nodes generated: " + nodesGenerated);
        System.out.println("Nodes expanded: " + nodesExpanded);

    }

    // healper method to print the board
    private void printBoard(int[][] board) {

        for (int[] row : board) {
            for (int value : row) {
                System.out.print(value + " ");
            }

            System.out.println();
        }

    }

    // create a copy of the 2D array
    private int[][] deepCopy(int[][] original) {

        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {

            copy[i] = original[i].clone();

        }

        return copy;

    }

}

// this class represents each state of the puzzle
class State {

    int[][] board; // represents the puzzle board
    int g; // cost to reach this state from the start
    int h; // heuristic cost
    State parent; // this is a refrence to the parent state to track the solution path

    // constructor
    State(int[][] baord, int g, int h) {

        this.board = baord;
        this.g = g;
        this.h = h;

    }

    // have a construictor for the parent refrences
    State(int[][] baord, int g, int h, State parent) {

        this.board = baord;
        this.g = g;
        this.h = h;
        this.parent = parent;

    }

    // get the total cost ( g + h) for the A*
    int getCost() {

        return g + h;

    }

    // override the equals and hashcode to use the State
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        State state = (State) obj;
        return Arrays.deepEquals(board, state.board);

    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

}
