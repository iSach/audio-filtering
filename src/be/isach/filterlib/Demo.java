package be.isach.filterlib;

import be.isach.filterlib.filters.AdditionFilter;
import be.isach.filterlib.filters.CompositeFilter;
import be.isach.filterlib.filters.DelayFilter;
import be.isach.filterlib.filters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

public class Demo {
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
            CompositeFilter echoFilter = new CompositeFilter(1, 1);

            // Creates the basic blocks
            Filter gain = new GainFilter(0.6);
            Filter delay = new DelayFilter(25000);
            Filter add = new AdditionFilter();

            // Adds them to the CompositeFilter
            echoFilter.addBlock(gain);
            echoFilter.addBlock(delay);
            echoFilter.addBlock(add);

            // Connects the blocks together
            echoFilter.connectInputToBlock(0, add, 0);
            echoFilter.connectBlockToBlock(add, 0, delay, 0);
            echoFilter.connectBlockToBlock(delay, 0, gain, 0);
            echoFilter.connectBlockToBlock(gain, 0, add, 1);
            echoFilter.connectBlockToOutput(add, 0, 0);

            // Applies the filter
            TestAudioFilter.applyFilter(echoFilter, inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}