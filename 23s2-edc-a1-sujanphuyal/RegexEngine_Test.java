import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RegexEngine_Test {
    @Test
    public void testConcatenation() {
        RegexEngine regexEngine = new RegexEngine();
        NFA nfa = regexEngine.postfixToNFA("ab.");
        DFA dfa = regexEngine.nfaToDFA(nfa);

        assertTrue(dfa.evaluate("ab"));
        assertFalse(dfa.evaluate("a"));
        assertFalse(dfa.evaluate("b"));
        assertFalse(dfa.evaluate("abc"));
    }

    @Test
    public void testKleeneClosure() {
        RegexEngine regexEngine = new RegexEngine();
        NFA nfa = regexEngine.postfixToNFA("a*");
        DFA dfa = regexEngine.nfaToDFA(nfa);

        assertTrue(dfa.evaluate(""));
        assertTrue(dfa.evaluate("a"));
        assertTrue(dfa.evaluate("aaa"));
        assertFalse(dfa.evaluate("b"));
        assertFalse(dfa.evaluate("ab"));
    }

    @Test
    public void testPlusClosure() {
        RegexEngine regexEngine = new RegexEngine();
        NFA nfa = regexEngine.postfixToNFA("a+");
        DFA dfa = regexEngine.nfaToDFA(nfa);

        assertFalse(dfa.evaluate(""));
        assertTrue(dfa.evaluate("a"));
        assertTrue(dfa.evaluate("aaa"));
        assertFalse(dfa.evaluate("b"));
        assertFalse(dfa.evaluate("ab"));
    }

    @Test
    public void testInfixToPostfix() {
        RegexEngine regexEngine = new RegexEngine();
        String infixRegex = "a|b.";
        String postfixRegex = regexEngine.infixToPostfix(infixRegex);

        assertEquals("ab.|", postfixRegex);
    }

    @Test
    public void testSelection() {
        RegexEngine regexEngine = new RegexEngine();
        NFA nfa = regexEngine.postfixToNFA("ab|");
        DFA dfa = regexEngine.nfaToDFA(nfa);
        assertTrue(dfa.evaluate("a"));
        assertTrue(dfa.evaluate("b"));
        assertFalse(dfa.evaluate("ab"));
        assertFalse(dfa.evaluate("c"));
    }

    @Test
    public void testJoin() {
        DFA dfa = new DFA();

        // Test case 1: Join a list of integers
        ArrayList<Integer> list1 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list1.add(3);
        String result1 = dfa.join(list1);
        assertEquals("1,2,3", result1);

        // Test case 2: Join an empty list (should result in an empty string)
        ArrayList<Integer> list2 = new ArrayList<>();
        String result2 = dfa.join(list2);
        assertEquals("", result2);

        // Test case 3: Join a single integer
        ArrayList<Integer> list3 = new ArrayList<>();
        list3.add(42);
        String result3 = dfa.join(list3);
        assertEquals("42", result3);
    }
}
