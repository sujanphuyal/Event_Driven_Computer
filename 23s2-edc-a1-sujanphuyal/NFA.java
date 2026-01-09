import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NFA {
    ArrayList<Integer> node = new ArrayList<>();
    ArrayList<Transitions> transitions = new ArrayList<>();
    int finalState;

    int getNodeCount() {
        return node.size();
    }

    void setNodes(int total_node) {
        for (int i = 0; i < total_node; i++) {
            node.add(i);
        }
    }

    void setTransition(int source, int destination, char symbol) {
        Transitions transition = new Transitions(source, destination, symbol);
        transitions.add(transition);
    }

    void setFinalState(int fs) {
        finalState = fs;
    }

    int getFinalState() {
        return finalState;
    }

    void display() {
        System.out.println("From\tInput\tGo to");
        for (Transitions temp : transitions) {
            System.out.println("q" + temp.source + "\t" + temp.symbol + "\tq" + temp.destination);
        }
        System.out.println("The final state is q" + getFinalState());
    }

    ArrayList<Character> findPossibleInputSymbols(ArrayList<Integer> node) {
        ArrayList<Character> result = new ArrayList<>();
        for (int node_source : node) {
            for (Transitions it : transitions) {
                if (it.source == node_source && it.symbol != '^') {
                    result.add(it.symbol);
                }
            }
        }
        return result;
    }

    ArrayList<Integer> unique(ArrayList<Integer> list) {
        return IntStream.range(0, list.size()).filter(i -> ((i < list.size() - 1 && !list.get(i).equals(list.get(i + 1))) || i == list.size() - 1)).mapToObj(list::get).collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<Integer> eclosure(ArrayList<Integer> node) {
        ArrayList<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[getNodeCount()];
        for (Integer integer : node) {
            eclosure(integer, result, visited);
        }
        Collections.sort(result);
        return unique(result);
    }

    void eclosure(int currentState, ArrayList<Integer> result, boolean[] alreadyVisited) {
        result.add(currentState);
        for (Transitions transition : transitions) {
            if (transition.source == currentState && transition.symbol == '^') {
                int nextState = transition.destination;
                if (!alreadyVisited[nextState]) {
                    alreadyVisited[nextState] = true;
                    eclosure(nextState, result, alreadyVisited);
                }
            }
        }
    }

    ArrayList<Integer> move(ArrayList<Integer> currentStateSet, char symbol) {
        ArrayList<Integer> nextStateSet = new ArrayList<>();
        for (int state : currentStateSet) {
            for (Transitions transition : transitions) {
                if (transition.source == state && transition.symbol == symbol) {
                    nextStateSet.add(transition.destination);
                }
            }
        }
        Collections.sort(nextStateSet);
        int originalSize = nextStateSet.size();
        unique(nextStateSet);
        int newSize = nextStateSet.size();
        if (newSize < originalSize) {
            System.out.println("move(currentStateSet, a) returns non-unique ArrayList");
            System.exit(1);
        }
        return nextStateSet;
    }
}