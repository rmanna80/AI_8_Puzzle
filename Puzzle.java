import java.awt.Taskbar.State;
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
        System.out.println("Example:\n 2 3 4\n1 5 6\n 8 7 0");

        // read the user input to generate the start state
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                currentState[i][j] = scanner.nextInt();

            }
        }
    }

    // A* search to solve the puzzle
    public void solve() {

        // create the start state with g = 0 and heuristic value (h(n))
        State startState = new State(currentState, 0, calculateSumOfError(currentState));
        openList.add(startState); // add start state to the openList

        // loop until a solution is found or no more states are available to explore
        while (!openList.isEmpty()) {

            State current = openList.poll(); // get the current state that has the lowest cost (g + h)
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
        System.out.println("Sorry but there was no solution found");

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

            if (!closedList.contains(neighbors)) {
                openList.add(neighbor);
            }

        }

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
            for (int j = 0; i < 3; j++) {
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

        }

        return moves;

    }

}