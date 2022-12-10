/*
 * @author: Hannah Cooper
 * @author: Elena Botoeva
 *
 * This code was originally developed by Hannah Cooper as part of her Master Thesis,
 * and later adapted for teaching the Introduction to Artificial Intelligence
 * module COMP5280/8250 at University of Kent by Elena Botoeva.
 */
import java.util.*;

public abstract class GhostAgent {
    Game game;

    /*
     * The ghost managed by the agent
     */
    Ghost ghost;

    int delay = 2;
    int scaredCoeff = 2;
    int ticksAfterLastAction = 0;

    public GhostAgent(Game game, Ghost ghost) {
        this.game = game;
        this.ghost = ghost;
    }

    public void doTick()
    {
        if (ghost.getIsDead())
            return;

        ghost.tick();

        if ( ticksAfterLastAction == delay * (ghost.getIsScared() ? scaredCoeff : 1) ) {

            ticksAfterLastAction = 0;

            game.applyAction(ghost, getNextMove());
        }

        ticksAfterLastAction += 1;
    }

    /***
     * Returns a next valid move
     */
    protected abstract PacmanAction getNextMove();
}

class RandomGhostAgent extends GhostAgent {
    Random nextMoveGenerator;

    public RandomGhostAgent(Game game, Ghost ghost) {
        super(game, ghost);

        nextMoveGenerator = new Random();
    }


    public PacmanAction getNextMove() {
        /***
         * Returns a valid move chosen randomly from the list of available ones
         */
        List<PacmanAction> availableActions = game.maze.getPacmanActions(ghost.getLocation());
        int directionIndex = nextMoveGenerator.nextInt(availableActions.size());
        return availableActions.get(directionIndex);
    }
}

class BlinkyGhostAgent<S,A> extends RandomGhostAgent {
    Pacman pacman;

    public BlinkyGhostAgent(Game game, Ghost ghost, Pacman pacman) {
        super(game, ghost);

        this.pacman = pacman;
    }


    @Override
    public PacmanAction getNextMove() {
        //Util.Frontier<Node<S,A>> frontier = new Util.PriorityQueue<>(
        //        Comparator.comparingDouble(node -> node.pathCost)
        //);

        Coordinate startLocation = ghost.location;
        Coordinate goalLocation = pacman.location;
        SearchProblem<S,A> problem = (SearchProblem<S, A>) new PacmanPositionSearchProblem(game.maze, goalLocation, startLocation);

        SearchHeuristic heuristic = new NullHeuristic();
        return (PacmanAction) GraphSearch.search("ucs",problem, heuristic, true).actions.get(0);
    }

/**                        // I accidently implemented the ghost using A* which isn't what the task asks for
    @Override
    public PacmanAction getNextMove() {

        Coordinate startLocation = ghost.location;
        Coordinate goalLocation = pacman.location;

        SearchProblem<S, A> problem = (SearchProblem<S, A>) new PacmanPositionSearchProblem(game.maze, goalLocation, startLocation);

        Util.Frontier<Node2<S,A>> frontier = new Util.PriorityQueue<>();

        // Get ManhattanDistance of the start node
        Double h = startLocation.manhattanDistance(goalLocation);

        // Create the starting node
        Node2<S, A> startNode = new Node2<S, A>((S) new PacmanPositionSearchState(startLocation),new ArrayList<>(), 0,h+0);
        // Add it to the frontier
        frontier.push(startNode);

        // We will keep track of the states that have been already expanded
        Set<S> expanded = new HashSet<>();


        while(!frontier.isEmpty()) {

            Node2<S,A> currentNode = frontier.pop();

            // If the state in the current node is a goal state, then we are finished!
            if(goalLocation.toString().equals(currentNode.state.toString())) {
                // Return the solution
                return (PacmanAction) currentNode.actions.get(0);
            }

            if (!expanded.contains(currentNode.state)) {
                expanded.add((S) currentNode.state);

                // Expand the current state      new PacmanPositionSearchState((Coordinate) currentNode.state)
                Collection<SuccessorInfo<S,A>> successors = problem.expand((S) currentNode.state);

                for(SuccessorInfo<S,A> successor : successors) {
                    S childState =  successor.nextState;     // nextState is the state of the successor

                    List<A> childActions = new ArrayList<>();
                    childActions.addAll(currentNode.actions);
                    childActions.add(successor.action);

                    // Calculate the cost of the above sequence of actions
                    double childPathCost = currentNode.pathCost + successor.cost;

                    // Calculate the F value = pathCost + heuristic
                    Double f = childPathCost + ((PacmanPositionSearchState)childState).getPacmanLocation().manhattanDistance(goalLocation);

                    // Create a new node
                    Node2<S,A> child = new Node2<S,A>(childState, childActions, childPathCost, f);
                    // And add it to the frontier
                    frontier.push(child);
                }
            }
        }

        // No solution has been found. Return null.
        return null;
    }
 **/
}
