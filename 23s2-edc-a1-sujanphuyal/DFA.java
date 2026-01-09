import java.util.ArrayList;

public class DFA {
    ArrayList<Transitions> transitions = new ArrayList<>();
    ArrayList<ArrayList<Integer>> entries = new ArrayList<>();
    ArrayList<Boolean> marked = new ArrayList<>();
    ArrayList<Integer> finalStates = new ArrayList<>();

    String join(ArrayList<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append(list.get(i));
        }
        return stringBuilder.toString();
    }

    int addEntry(ArrayList<Integer> entry) {
        entries.add(entry);
        marked.add(false);
        return entries.size() - 1;
    }

    int nextUnmarkedEntryIndex() {
        for (int i = 0; i < marked.size(); i++) {
            if (!marked.get(i)) {
                return i;
            }
        }
        return -1;
    }

    void markEntry(int index) {
        marked.set(index, true);
    }

    ArrayList<Integer> entryAt(int i) {
        return entries.get(i);
    }

    int findEntry(ArrayList<Integer> entry) {
        for (int i = 0; i < entries.size(); i++) {
            ArrayList<Integer> it = entries.get(i);
            if (it.equals(entry)) {
                return i;
            }
        }
        return -1;
    }

    void setFinalState(int nfaFinalState) {
        for (int i = 0; i < entries.size(); i++) {
            ArrayList<Integer> entry = entries.get(i);
            for (int node : entry) {
                if (node == nfaFinalState) {
                    finalStates.add(i);
                }
            }
        }
    }

    void setTransition(int source, int destination, char symbol) {
        Transitions newTransition = new Transitions(source, destination, symbol);
        transitions.add(newTransition);
    }

    void display() {
        Transitions newTransition;
        System.out.println("From\t\t\tInput\t\t\tGo to");
        for (Transitions transition : transitions) {
            newTransition = transition;
            System.out.println("q" + newTransition.source + " {" + join(entries.get(newTransition.source)) + "}\t\t" + newTransition.symbol + "\t\tq" + newTransition.destination + " {" + join(entries.get(newTransition.destination)) + "}");
        }
        System.out.println("The final state is q : " + join(finalStates));
    }

    boolean evaluate(String x) {
        int i, l = x.length(), j;
        int state = 0;
        for (i = 0; i < l; i++) {
            char ch = x.charAt(i);
            for (j = 0; j < transitions.size(); j++) {
                Transitions t = transitions.get(j);
                if (t.source == state && t.symbol == ch) {
                    state = t.destination;
                    break;
                }
            }
            if (j == transitions.size()) return false;
        }
        return finalStates.contains(state);
    }
}