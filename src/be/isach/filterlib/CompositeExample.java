package be.isach.filterlib;

import be.isach.filterlib.filters.AdditionFilter;
import be.isach.filterlib.filters.CompositeFilter;
import be.isach.filterlib.filters.DelayFilter;
import be.isach.filterlib.filters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

public class CompositeExample {
    public static void main(String[] args) {
        try {
            // Creates the CompositeFilter object, with one input and one output
            CompositeFilter audioFilter = new CompositeFilter(1, 1);

            // Creates the basic blocks
            Filter mult1 = new GainFilter(0.3);
            Filter mult2 = new DelayFilter(10 * 10 * 10 * 10 * 10);
            Filter add = new AdditionFilter();

            // Adds them to the CompositeFilter
            audioFilter.addBlock(mult1);
            audioFilter.addBlock(mult2);
            audioFilter.addBlock(add);

            // Connects the blocks together
            audioFilter.connectInputToBlock(0, mult1, 0);
            audioFilter.connectInputToBlock(0, mult2, 0);
            audioFilter.connectBlockToBlock(mult1, 0, add, 0);
            audioFilter.connectBlockToBlock(mult2, 0, add, 1);
            audioFilter.connectBlockToOutput(add, 0, 0);

            // Applies the filter
            TestAudioFilter.applyFilter(audioFilter, "queen.wav", "test.wav");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}