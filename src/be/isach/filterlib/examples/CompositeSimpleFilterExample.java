package be.isach.filterlib.examples;

import be.isach.filterlib.filters.AdditionFilter;
import be.isach.filterlib.filters.CompositeFilter;
import be.isach.filterlib.filters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

/**
 * Demonstrates a simple composite filter.
 * Showed in the project slides.
 */
public class CompositeSimpleFilterExample {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Wrong number of arguments.");
            System.err.println("Expected: 2. Got: " + args.length);
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Creates the Echo Filter composite filter.
            CompositeFilter reverbFilter = new CompositeFilter(1, 1);

            // Creates the basic blocks
            Filter gain = new GainFilter(0.1);
            Filter gain2 = new GainFilter(0.1);
            Filter add = new AdditionFilter();

            // Adds them to the CompositeFilter
            reverbFilter.addBlock(gain);
            reverbFilter.addBlock(gain2);
            reverbFilter.addBlock(add);

            // Connects the blocks together
            reverbFilter.connectInputToBlock(0, gain, 0);
            reverbFilter.connectInputToBlock(0, gain2, 0);
            reverbFilter.connectBlockToBlock(gain, 0, add, 0);
            reverbFilter.connectBlockToBlock(gain2, 0, add, 1);
            reverbFilter.connectBlockToOutput(add, 0, 0);

            // Applies the filter
            TestAudioFilter.applyFilter(reverbFilter, inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}