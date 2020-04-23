package be.isach.filterlib;

import be.isach.filterlib.filters.EchoFilter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

public class Demo {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Wrong number of arguments.");
            System.err.println("Expected: 2. Got: " + args.length);
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            EchoFilter echoFilter = new EchoFilter(0.6, 10000);

            // Applies the filter
            TestAudioFilter.applyFilter(echoFilter, inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}