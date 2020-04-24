package be.isach.filterlib.examples;

import be.isach.filterlib.filters.*;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

/**
 * Demonstrates an alternative echo filter, without a loop so the reverb
 * only happens once.
 */
public class CompositeAltEchoFilterExample {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.err.println("Wrong number of arguments.");
            System.err.println("Expected: 2. Got: " + args.length);
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // Creates the Alt Echo Filter composite filter.
            CompositeFilter altEchoFilter = new CompositeFilter(1, 1);

            // Creates the basic blocks
            Filter gain = new GainFilter(0.6);
            Filter delay = new DelayFilter(44100*2);
            Filter add = new AdditionFilter();

            // Adds them to the CompositeFilter
            altEchoFilter.addBlock(gain);
            altEchoFilter.addBlock(delay);
            altEchoFilter.addBlock(add);

            // Connects the blocks together
            altEchoFilter.connectInputToBlock(0, add, 0);
            altEchoFilter.connectInputToBlock(0, delay, 0);
            altEchoFilter.connectBlockToBlock(delay, 0, gain, 0);
            altEchoFilter.connectBlockToBlock(gain, 0, add, 1);
            altEchoFilter.connectBlockToOutput(add, 0, 0);

            // Applies the filter
            TestAudioFilter.applyFilter(altEchoFilter, inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
