package be.isach.filterlib.examples;

import be.isach.filterlib.filters.*;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

/**
 * Demonstrates the CompositeFilter with an All-Pass Filter.
 */
public class ExampleAllPassFilterExample {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong number of arguments.");
            System.err.println("Expected: 2. Got: " + args.length);
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            AllPassFilter allPassFilter = new AllPassFilter(0.5, 3323);

            // Applies the filter
            TestAudioFilter.applyFilter(allPassFilter, inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}