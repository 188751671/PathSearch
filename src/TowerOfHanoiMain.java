/*
 * This project was developed for the Introduction to Artificial Intelligence
 * module COMP5280/8250 at University of Kent.
 *
 * The java code was created by Elena Botoeva (e.botoeva@kent.ac.uk).
 */
import java.sql.Array;
import java.util.*;

import static java.lang.System.out;
import static java.lang.System.setOut;

public class TowerOfHanoiMain {
    static void usage() {
        out.println("usage: TowerofHanoiMain [<option>...]");
        out.println("options:");
        out.println("  -f <strategy> : Search strategy, one of dfs, bfs, ucs, greedy or astar");
        out.println("  -h <heuristic> : Search heuristic to use (name of the class)");
        out.println("  --help : Print this message and exit");
        System.exit(1);
    }

    public static void main(String [] args) throws Exception {
        // TODO 1. Chenage here when changing plates number
        Integer[][] disks = {{3, 2, 1},{},{}};
        String function = "ucs";
        String heuristicName = "NullHeuristic";

        for (int i = 0 ; i < args.length ; ++i) {
            String s = args[i];
            switch (s) {
                case "-f":
                    function = args[++i];
                    break;
                case "-h":
                    heuristicName = args[++i];
                    break;
                case "--help":
                    usage();
                default:
                    usage();
            }
        }

        /*
         Instantiate the search problem.
         */
        TowerOfHanoiSearchProblem problem = new TowerOfHanoiSearchProblem(disks);

        /*
         Instantiate the heuristic. By default it is the trivial heuristic (NullHeuristic), that always returns 0.
         */
        SearchHeuristic<TowerOfHanoiSearchState, TowerOfHanoiAction> heuristic =
                (SearchHeuristic) Class.forName(heuristicName).getConstructor().newInstance();

        Solution<TowerOfHanoiSearchState, TowerOfHanoiAction> solution = GraphSearch.search(function, problem, heuristic, true);


        /*
         Print the solution
         */
        TowerOfHanoiSearchState startState = problem.getStartState();
        out.println(startState);

        TowerOfHanoiSearchState currState = startState;
        for (TowerOfHanoiAction action: solution.actions) {
            TowerOfHanoiSearchState succState = problem.getSuccessor(currState, action);
            out.println(succState);
            currState = succState;
        }
    }
}

class TowerOfHanoiSearchProblem extends SearchProblem<TowerOfHanoiSearchState, TowerOfHanoiAction> {
    private final Integer[][] startCoordinate;
    // TODO 2. Chenage here when changing plates number
    private final Integer[][] goalCoordinate = {{0,0,0},{0,0,0},{3,2,1}};
    public final int NumberOfPlates = goalCoordinate[2].length;

    public TowerOfHanoiSearchProblem(Integer[][] disks) {

        if (NumberOfPlates != disks[0].length){
            throw new RuntimeException("StartCoordinate doesn't fit GoalCoordinate");
        }

        // making a StartCoordinate based on disks, filling blanks with 0
        startCoordinate = new Integer[3][disks[0].length];

        for (int i=0;i<3;i++){
            for (int k=0; k< disks[0].length; k++){
                if (i==0)   startCoordinate[0][k] =  disks[0][k];
                else        startCoordinate[i][k] = 0;
            }
        }
    };

    @Override
    public TowerOfHanoiSearchState getStartState() {
        return new TowerOfHanoiSearchState(startCoordinate);
    }

    @Override
    public boolean isGoalState(TowerOfHanoiSearchState state) {
        if (state==null)   return false;

        Boolean trigger = true;
        if (goalCoordinate[0].length == state.coordinate[0].length){
            for (int i=0;i<3;i++){
                for (int k=0;k<state.coordinate[0].length;k++){
                    if (goalCoordinate[i][k] != state.coordinate[i][k]){
                        trigger = false;
                    }
                }
            }
        }else new RuntimeException("length unfit");
        return trigger;
    }

    @Override
    public List<TowerOfHanoiAction> getActions(TowerOfHanoiSearchState state) {

        List<TowerOfHanoiAction> availableActs = new ArrayList<>();

        TopElementAndIndex StackA = this.getTopElementAndIndex(state.coordinate[0]);
        TopElementAndIndex StackB = this.getTopElementAndIndex(state.coordinate[1]);
        TopElementAndIndex StackC = this.getTopElementAndIndex(state.coordinate[2]);

/**
        //                                                          A B==0 is the goal state
        if (StackA.TopElement==0 && StackB.TopElement==0){
            availableActs.add(TowerOfHanoiAction.StackCtoA);
            availableActs.add(TowerOfHanoiAction.StackCtoB);
        }
        else**/ if (StackB.TopElement==0 && StackC.TopElement==0) {            // State State    B C = 0
            availableActs.add(TowerOfHanoiAction.StackAtoB);
            availableActs.add(TowerOfHanoiAction.StackAtoC);
        } else if (StackA.TopElement==0 && StackC.TopElement==0) {
            availableActs.add(TowerOfHanoiAction.StackBtoA);
            availableActs.add(TowerOfHanoiAction.StackBtoC);
        }else{
            if (StackC.TopElement == 0){
                // only C empty, compare A B
                if (StackA.TopElement > StackB.TopElement){
                    // A > B
                    availableActs.add(TowerOfHanoiAction.StackAtoC);
                    availableActs.add(TowerOfHanoiAction.StackBtoC);
                    availableActs.add(TowerOfHanoiAction.StackBtoA);
                }else {
                    // A < B
                    availableActs.add(TowerOfHanoiAction.StackAtoC);
                    availableActs.add(TowerOfHanoiAction.StackBtoC);
                    availableActs.add(TowerOfHanoiAction.StackAtoB);
                }
            } else if (StackB.TopElement == 0) {
                // only B empty, compare A C
                if (StackA.TopElement > StackC.TopElement){
                    // A > C
                    availableActs.add(TowerOfHanoiAction.StackAtoB);
                    availableActs.add(TowerOfHanoiAction.StackCtoB);
                    availableActs.add(TowerOfHanoiAction.StackCtoA);
                }else {
                    // A < C
                    availableActs.add(TowerOfHanoiAction.StackAtoB);
                    availableActs.add(TowerOfHanoiAction.StackCtoB);
                    availableActs.add(TowerOfHanoiAction.StackAtoC);
                }
            } else if (StackA.TopElement == 0) {
                // only A empty, compare B C
                if (StackB.TopElement > StackC.TopElement){
                    // B > C
                    availableActs.add(TowerOfHanoiAction.StackBtoA);
                    availableActs.add(TowerOfHanoiAction.StackCtoA);
                    availableActs.add(TowerOfHanoiAction.StackCtoB);
                }else {
                    // B < C
                    availableActs.add(TowerOfHanoiAction.StackBtoA);
                    availableActs.add(TowerOfHanoiAction.StackCtoA);
                    availableActs.add(TowerOfHanoiAction.StackBtoC);
                }
            }else {
                // no one is empty, compare A B C
                if (StackA.TopElement>StackB.TopElement){
                    // A>B
                    if (StackA.TopElement>StackC.TopElement){
                        // A>B  A>C
                        if (StackB.TopElement>StackC.TopElement){
                            // A > B > C
                            availableActs.add(TowerOfHanoiAction.StackCtoB);
                            availableActs.add(TowerOfHanoiAction.StackCtoA);
                            availableActs.add(TowerOfHanoiAction.StackBtoA);
                        }else {
                            // A > C > B
                            availableActs.add(TowerOfHanoiAction.StackBtoC);
                            availableActs.add(TowerOfHanoiAction.StackBtoA);
                            availableActs.add(TowerOfHanoiAction.StackCtoA);
                        }
                    }else{
                        // A>B  A<C     C > A > B
                        availableActs.add(TowerOfHanoiAction.StackBtoA);
                        availableActs.add(TowerOfHanoiAction.StackBtoC);
                        availableActs.add(TowerOfHanoiAction.StackAtoC);
                    }
                }else{
                    // A<B
                    if (StackA.TopElement<StackC.TopElement){
                        // A<B  A<C
                        if (StackB.TopElement<StackC.TopElement){
                            // C > B > A
                            availableActs.add(TowerOfHanoiAction.StackAtoB);
                            availableActs.add(TowerOfHanoiAction.StackAtoC);
                            availableActs.add(TowerOfHanoiAction.StackBtoC);
                        }else{
                            // B > C > A
                            availableActs.add(TowerOfHanoiAction.StackAtoC);
                            availableActs.add(TowerOfHanoiAction.StackAtoB);
                            availableActs.add(TowerOfHanoiAction.StackCtoB);
                        }
                    }else{
                        // A<B  A>C     B > A > C
                        availableActs.add(TowerOfHanoiAction.StackCtoA);
                        availableActs.add(TowerOfHanoiAction.StackCtoB);
                        availableActs.add(TowerOfHanoiAction.StackAtoB);
                    }
                }
            }
        }

        return availableActs;
    }
    private TopElementAndIndex getTopElementAndIndex(Integer[] array){
        // find the top element that is non-zero
        int index=-1;
        for(int i=0; i<array.length; i++){
            if (array[i]!=0){
                index = i;
            }else break;
        }
        if (index==-1)  return new TopElementAndIndex(0,-1);        // topElement== 0 & index== -1   meaning  empty stack
        else            return new TopElementAndIndex(array[index],index);
    }

    private class TopElementAndIndex{
        int TopElement;
        int Index;
        private TopElementAndIndex(int topElement, int index){
            this.TopElement = topElement;
            this.Index = index;
        }
    }

    @Override
    public TowerOfHanoiSearchState getSuccessor(TowerOfHanoiSearchState state, TowerOfHanoiAction action) {

        TopElementAndIndex StackA = this.getTopElementAndIndex(state.coordinate[0]);
        TopElementAndIndex StackB = this.getTopElementAndIndex(state.coordinate[1]);
        TopElementAndIndex StackC = this.getTopElementAndIndex(state.coordinate[2]);

        Integer[][] newCoordinate = new Integer[3][NumberOfPlates];
        for (int i=0;i<3;i++){
            for (int k=0; k< NumberOfPlates; k++){
                newCoordinate[i][k] = state.coordinate[i][k];
            }
        }
        TowerOfHanoiSearchState newState = new TowerOfHanoiSearchState(newCoordinate);

        int index;
        switch (action) {
            case StackAtoB:
                newState.coordinate[1][StackB.Index+1] = newState.coordinate[0][StackA.Index];
                newState.coordinate[0][StackA.Index] = 0;
                break;
            case StackAtoC:
                newState.coordinate[2][StackC.Index+1] = newState.coordinate[0][StackA.Index];
                newState.coordinate[0][StackA.Index] = 0;
                break;
            case StackBtoA:
                newState.coordinate[0][StackA.Index+1] = newState.coordinate[1][StackB.Index];
                newState.coordinate[1][StackB.Index] = 0;
                break;
            case StackBtoC:
                newState.coordinate[2][StackC.Index+1] = newState.coordinate[1][StackB.Index];
                newState.coordinate[1][StackB.Index] = 0;
                break;
            case StackCtoA:
                newState.coordinate[0][StackA.Index+1] = newState.coordinate[2][StackC.Index];
                newState.coordinate[2][StackC.Index] = 0;
                break;
            case StackCtoB:
                newState.coordinate[1][StackB.Index+1] = newState.coordinate[2][StackC.Index];
                newState.coordinate[2][StackC.Index] = 0;
                break;
            default:
                throw new RuntimeException("Invalid Action");
        }
        return newState;
    }

    @Override
    public double getCost(TowerOfHanoiSearchState state, TowerOfHanoiAction action) {
        return 1.0;
    }

}

class TowerOfHanoiSearchState implements SearchState {          // "new TowerOfHanoiSearchState(coor)" to make State
    protected final Integer[][] coordinate;

    public TowerOfHanoiSearchState(Integer[][] coordinate){
        this.coordinate = coordinate;
    }

    @Override
    public String toString() {
        //return coordinate.toString();    //  System.out.println(succState); in the end, print out all coordinates of States
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < coordinate[0].length; col++) {
                builder.append(coordinate[row][col]);
            }
            builder.append("  ");
        }
        //builder.append("\n");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TowerOfHanoiSearchState))
            return false;

        return toString().equals(o.toString());

        /**
        Integer[][] objCoordinate = ((TowerOfHanoiSearchState) o).coordinate;

        if (coordinate==null || o==null)   return false;

        Boolean trigger = true;
        if (coordinate[0].length == objCoordinate[0].length){
            for (int i=0;i<3;i++){
                for (int k=0;k<coordinate[0].length;k++){
                    if (coordinate[i][k] != objCoordinate[i][k]){
                        trigger = false;
                    }
                }
            }
        }
        return trigger;
         **/
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}


enum TowerOfHanoiAction implements Action {
    /**
     * Action to move the top disk from stack i to stack j
     */
    StackAtoB{
    },
    StackAtoC{
    },
    StackBtoA{
    },
    StackBtoC{
    },
    StackCtoA{
    },
    StackCtoB{
    }
}

class TowerOfHanoiHeuristic implements SearchHeuristic<TowerOfHanoiSearchState,TowerOfHanoiAction> {
    public TowerOfHanoiHeuristic() {}

    static int phase = 100; // default return value when AI hasn't expanded to a phase below. Smaller value means to be prioritized in the Queue
    @Override
    public Double value(TowerOfHanoiSearchState state, SearchProblem<TowerOfHanoiSearchState, TowerOfHanoiAction> problem) {
        if (problem instanceof TowerOfHanoiSearchProblem) {

            if (state.coordinate[2][0] == 3){
                return 50.0;
            }else if (state.coordinate[2][0] == 3 && state.coordinate[2][1] == 2){
                return 10.0;
            }
            return Double.valueOf(phase);
        }

        return 100.00;
    }

    /**
    @Override
    public Double value(TowerOfHanoiSearchState state, SearchProblem<TowerOfHanoiSearchState, TowerOfHanoiAction> problem) {

        if (problem instanceof TowerOfHanoiSearchProblem) {
            // Given   state  problem
            int numberOfPlates = problem.getStartState().coordinate[0].length;
            if (numberOfPlates <= 3)    return 0.0;

            if (numberOfPlates % 2 == 1){
                // platesNumber == 3 5 7 9 etc
                if (state.coordinate[2][0] == 1){
                    return Double.valueOf(numberOfPlates-1);
                } else if (state.coordinate[1][0] == 2 && state.coordinate[1][1] == 1) {
                    return Double.valueOf(numberOfPlates-2);
                } else if (numberOfPlates > 3 && state.coordinate[2][0] == 3 && state.coordinate[2][1] == 2 && state.coordinate[2][2] == 1) {
                    return Double.valueOf(numberOfPlates-3);
                } else if (numberOfPlates > 4 && state.coordinate[1][0] == 4 && state.coordinate[1][1] == 3 && state.coordinate[1][2] == 2 && state.coordinate[1][3] == 1) {
                    return Double.valueOf(numberOfPlates-4);
                } else if (numberOfPlates > 5 && state.coordinate[2][0] == 5 && state.coordinate[2][1] == 4 && state.coordinate[2][2] == 3 && state.coordinate[2][3] == 2 && state.coordinate[2][4] == 1) {
                    return Double.valueOf(numberOfPlates-5);
                } else if (numberOfPlates > 6 && state.coordinate[1][0] == 6 && state.coordinate[1][1] == 5 && state.coordinate[1][2] == 4 && state.coordinate[1][3] == 3 && state.coordinate[1][4] == 2 && state.coordinate[1][5] == 1) {
                    return Double.valueOf(numberOfPlates-6);
                } else if (numberOfPlates > 7 && state.coordinate[2][0] == 7 && state.coordinate[2][1] == 6 && state.coordinate[2][2] == 5 && state.coordinate[2][3] == 4 && state.coordinate[2][4] == 3 && state.coordinate[2][5] == 2 && state.coordinate[2][6] == 1) {
                    return Double.valueOf(numberOfPlates-7);
                }else return Double.valueOf(numberOfPlates);
            }else {
                // numberOfPlates is Odd
                if (state.coordinate[1][0] == 1){
                    return Double.valueOf(numberOfPlates-1);
                } else if (state.coordinate[2][0] == 2 && state.coordinate[2][1] == 1) {
                    return Double.valueOf(numberOfPlates-2);
                } else if (numberOfPlates > 3 && state.coordinate[1][0] == 3 && state.coordinate[1][1] == 2 && state.coordinate[1][2] == 1) {
                    return Double.valueOf(numberOfPlates-3);
                } else if (numberOfPlates > 4 && state.coordinate[2][0] == 4 && state.coordinate[2][1] == 3 && state.coordinate[2][2] == 2 && state.coordinate[2][3] == 1) {
                    return Double.valueOf(numberOfPlates-4);
                } else if (numberOfPlates > 5 && state.coordinate[1][0] == 5 && state.coordinate[1][1] == 4 && state.coordinate[1][2] == 3 && state.coordinate[1][3] == 2 && state.coordinate[1][4] == 1) {
                    return Double.valueOf(numberOfPlates-5);
                } else if (numberOfPlates > 6 && state.coordinate[2][0] == 6 && state.coordinate[2][1] == 5 && state.coordinate[2][2] == 4 && state.coordinate[2][3] == 3 && state.coordinate[2][4] == 2 && state.coordinate[2][5] == 1) {
                    return Double.valueOf(numberOfPlates-6);
                } else if (numberOfPlates > 7 && state.coordinate[1][0] == 7 && state.coordinate[1][1] == 6 && state.coordinate[1][2] == 5 && state.coordinate[1][3] == 4 && state.coordinate[1][4] == 3 && state.coordinate[1][5] == 2 && state.coordinate[1][6] == 1) {
                    return Double.valueOf(numberOfPlates-7);
                }else return Double.valueOf(numberOfPlates);
            }
        }

        return 0.0;
    }**/

}
