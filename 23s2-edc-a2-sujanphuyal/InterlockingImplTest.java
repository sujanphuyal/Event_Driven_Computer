import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class InterlockingImplTest {

    private InterlockingImpl interlocking;

    @Before
    public void setUp() {
        interlocking = new InterlockingImpl();
    }

    @Test
    public void testAddTrain_ValidTrain_AddsSuccessfully() {
        interlocking.addTrain("Train1", 1, 4); // Assuming 1 -> 4 is a valid entry/exit for southbound
        assertEquals("Train1 should be at section 1", 1, interlocking.getTrain("Train1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddTrain_DuplicateTrainName_ThrowsException() {
        interlocking.addTrain("Train1", 1, 4);
        interlocking.addTrain("Train1", 3, 8); // Duplicate train name
    }

    @Test(expected = IllegalStateException.class)
    public void testAddTrain_SectionOccupied_ThrowsException() {
        interlocking.addTrain("Train1", 1, 4);
        interlocking.addTrain("Train2", 1, 4); // Same entry section as Train1
    }

    @Test
    public void testMoveTrains_ValidMove_MovesTrain() {
        interlocking.addTrain("Train1", 1, 4);
        String[] trainNames = {"Train1"};
        interlocking.moveTrains(trainNames);
        assertEquals("Train1 should be at section 2", 2, interlocking.getTrain("Train1"));
    }

    @Test
    public void testGetSection_OccupiedSection_ReturnsTrainName() {
        interlocking.addTrain("Train1", 1, 4);
        assertEquals("Section 1 should be occupied by Train1", "Train1", interlocking.getSection(1));
    }

    @Test
    public void testGetSection_UnoccupiedSection_ReturnsNull() {
        assertNull("Section 1 should be unoccupied", interlocking.getSection(1));
    }

    @Test
    public void testGetTrain_NonexistentTrain_ReturnsMinusOne() {
        assertEquals("Nonexistent train should return -1", -1, interlocking.getTrain("Nonexistent"));
    }
}
