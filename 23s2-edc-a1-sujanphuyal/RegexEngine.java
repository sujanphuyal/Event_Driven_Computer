import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class RegexEngine {
    NFA concatenate(NFA a, NFA b) {
        NFA result = new NFA();
        result.setNodes(a.getNodeCount() + b.getNodeCount());
        int i;
        Transitions new_trans;
        for (i = 0; i < a.transitions.size(); i++) {
            new_trans = a.transitions.get(i);
            result.setTransition(new_trans.source, new_trans.destination, new_trans.symbol);
        }
        result.setTransition(a.getFinalState(), a.getNodeCount(), '^');
        for (i = 0; i < b.transitions.size(); i++) {
            new_trans = b.transitions.get(i);
            result.setTransition(new_trans.source + a.getNodeCount(), new_trans.destination + a.getNodeCount(), new_trans.symbol);
        }
        result.setFinalState(a.getNodeCount() + b.getNodeCount() - 1);
        return result;
    }
    NFA kleeneClosure(NFA a) {
        NFA result = new NFA();
        int i;
        Transitions newTransition;
        result.setNodes(a.getNodeCount() + 2);
        result.setTransition(0, 1, '^');
        for (i = 0; i < a.transitions.size(); i++) {
            newTransition = a.transitions.get(i);
            result.setTransition(newTransition.source + 1, newTransition.destination + 1, newTransition.symbol);
        }
        result.setTransition(a.getNodeCount(), a.getNodeCount() + 1, '^');
        result.setTransition(a.getNodeCount(), 1, '^');
        result.setTransition(0, a.getNodeCount() + 1, '^');
        result.setFinalState(a.getNodeCount() + 1);
        return result;
    }
    NFA plusClosure(NFA a) {
        NFA result = new NFA();
        int i;
        Transitions newTransition;

        // Create a new start state and connect it to the old start state
        result.setNodes(a.getNodeCount() + 2);
        result.setTransition(0, 1, '^');

        for (i = 0; i < a.transitions.size(); i++) {
            newTransition = a.transitions.get(i);
            result.setTransition(newTransition.source + 1, newTransition.destination + 1, newTransition.symbol);
        }

        // Create a new final state and connect it to the old final state and the new start state
        result.setTransition(a.getNodeCount(), a.getNodeCount() + 1, '^');
        result.setTransition(a.getNodeCount() + 1, 0, '^'); // Connect the new final state to the new start state

        result.setFinalState(a.getNodeCount() + 1);
        return result;
    }
    NFA orSelection(ArrayList<NFA> selections, int selectionCount) {
        NFA result = new NFA();
        int nodeCount = 2;
        int i, j;
        NFA currentSelectionNFA;
        Transitions newTransition;
        for (i = 0; i < selectionCount; i++) {
            nodeCount += selections.get(i).getNodeCount();
        }
        result.setNodes(nodeCount);
        int additionTracker = 1;
        for (i = 0; i < selectionCount; i++) {
            result.setTransition(0, additionTracker, '^');
            currentSelectionNFA = selections.get(i);
            for (j = 0; j < currentSelectionNFA.transitions.size(); j++) {
                newTransition = currentSelectionNFA.transitions.get(j);
                result.setTransition(newTransition.source + additionTracker, newTransition.destination + additionTracker, newTransition.symbol);//Copy all transitions in first NFA
            }
            additionTracker += currentSelectionNFA.getNodeCount();
            result.setTransition(additionTracker - 1, nodeCount - 1, '^');
        }
        result.setFinalState(nodeCount - 1);
        return result;
    }
    DFA nfaToDFA(NFA nfa) {
        DFA dfa = new DFA();
        ArrayList<Integer> initialNode = new ArrayList<>();
        initialNode.add(0);
        ArrayList<Integer> initialState = nfa.eclosure(initialNode);
        int currentNodeIndex = dfa.addEntry(initialState);
        while (currentNodeIndex != -1) {
            ArrayList<Integer> currentState = dfa.entryAt(currentNodeIndex);
            dfa.markEntry(currentNodeIndex);
            ArrayList<Character> inputSymbols = nfa.findPossibleInputSymbols(currentState);
            for (char inputSymbol : inputSymbols) {
                ArrayList<Integer> nextState = nfa.eclosure(nfa.move(currentState, inputSymbol));
                int nextNodeIndex = dfa.findEntry(nextState);
                if (nextNodeIndex == -1) {
                    nextNodeIndex = dfa.addEntry(nextState);
                }
                dfa.setTransition(currentNodeIndex, nextNodeIndex, inputSymbol);
            }
            currentNodeIndex = dfa.nextUnmarkedEntryIndex();
        }
        dfa.setFinalState(nfa.getFinalState());
        return dfa;
    }
    public String addConcatenator(String reg) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < reg.length(); i++) {
            output.append(reg.charAt(i));
            if (reg.charAt(i) == '(' || reg.charAt(i) == '|') {
                continue;
            }
            if (i != reg.length() - 1) {
                if (reg.charAt(i + 1) == ')' ||
                        reg.charAt(i + 1) == '+' || reg.charAt(i + 1) == '*' ||
                        reg.charAt(i + 1) == '|') {
                    continue;
                }
                output.append(".");
            }
        }
        return output.toString();
    }
    // Method to convert infix regex to postfix
    public String infixToPostfix(String infixRegex) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();

        for (char c : infixRegex.toCharArray()) {
            if (Character.isLetterOrDigit(c)|| Character.isWhitespace(c)) {
                postfix.append(c);
            } else if (c == '(') {
                operatorStack.push(c);
            } else if (c == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    postfix.append(operatorStack.pop());
                }
                operatorStack.pop(); // Pop '('
            } else if (!Character.isWhitespace(c)) {
                // Operator (+, *, ., |) encountered
                while (!operatorStack.isEmpty() && precedence(c) <= precedence(operatorStack.peek())) {
                    postfix.append(operatorStack.pop());
                }
                operatorStack.push(c);
            }
        }

        while (!operatorStack.isEmpty()) {
            postfix.append(operatorStack.pop());
        }

        return postfix.toString();
    }
    public int precedence(char operator) {
        switch (operator) {
            case '|':
                return 1;
            case '.':
                return 2;
            case '*':
                return 3;
            case '+':
                return 4;
        }
        return -1;
    }
    // Method to construct NFA from postfix regex
    public NFA postfixToNFA(String postfixRegex) {
        Stack<NFA> nfaStack = new Stack<>();

        for (char c : postfixRegex.toCharArray()) {
            if (Character.isLetterOrDigit(c)|| Character.isWhitespace(c)) {
                NFA nfa = new NFA();
                nfa.setNodes(2);
                nfa.setTransition(0, 1, c);
                nfa.setFinalState(1);
                nfaStack.push(nfa);
            } else {
                if (c == '*') {
                    NFA nfa = nfaStack.pop();
                    nfaStack.push(kleeneClosure(nfa));
                } else if (c == '+') {
                    NFA nfa = nfaStack.pop();
                    nfaStack.push(plusClosure(nfa));
                } else if (c == '.') {
                    NFA nfa2 = nfaStack.pop();
                    NFA nfa1 = nfaStack.pop();
                    nfaStack.push(concatenate(nfa1, nfa2));
                } else if (c == '|') {
                    NFA nfa2 = nfaStack.pop();
                    NFA nfa1 = nfaStack.pop();
                    ArrayList<NFA> selections = new ArrayList<>();
                    selections.add(nfa1);
                    selections.add(nfa2);
                    nfaStack.push(orSelection(selections, 2));
                }
            }
        }

        return nfaStack.pop();
    }
    public static void main(String[] args) {
        RegexEngine regexEngine = new RegexEngine();

        boolean isVerboseMode = args.length > 0 && args[0].equals("-v");

//        System.out.println("Enter the regular expression:");
        Scanner scanner = new Scanner(System.in);
        String infixRegex = scanner.nextLine();
        System.out.println("Ready");

        String concatenated = regexEngine.addConcatenator(infixRegex);
        String postfixRegex = regexEngine.infixToPostfix(concatenated);

//        System.out.println("Postfix notation: " + postfixRegex);
        NFA nfa = regexEngine.postfixToNFA(postfixRegex);
        DFA dfa = regexEngine.nfaToDFA(nfa);

        if (isVerboseMode) {
//            System.out.println("\nNFA Transition Table:");
            nfa.display();
//            System.out.println("\nDFA Transition Table:");
//            dfa.display();
        }

        while (true) {
            try {
//                System.out.println("Enter input string (or hit Ctrl + c to quit):");
                String input = scanner.nextLine();

                boolean result = dfa.evaluate(input);
                if (isVerboseMode) {
                    System.out.println(result);
                } else {
                    System.out.println(result);
                }
            } catch (NoSuchElementException e) {
//                System.out.println("\nProgram terminated by user.");
                System.exit(0);
            }
        }
    }
}