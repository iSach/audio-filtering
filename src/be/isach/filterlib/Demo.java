package be.isach.filterlib;

import be.isach.filterlib.filters.*;
import be.isach.filterlib.reverberator.ReverberatorFilter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

public class Demo {

    private enum DemoMode {
        ECHO, REVERB
    }

    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            System.err.println("Wrong number of arguments.");
            System.err.println("Expected: 2. Got: " + args.length);
            return;
        }

        if(args.length == 3 && !args[0].equalsIgnoreCase("reverb")) {
            System.err.println("Wrong demo mode.");
            return;
        }

        DemoMode mode = args.length == 2 ? DemoMode.ECHO : DemoMode.REVERB;

        if(mode == DemoMode.ECHO) {
            String inputFile = args[0];
            String outputFile = args[1];

            try {
                EchoFilter echoFilter = new EchoFilter(0.6, 10000);

                // Applies the filter
                TestAudioFilter.applyFilter(echoFilter, inputFile, outputFile);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        } else {
            String inputFile = args[1];
            String outputFile = args[2];

            try {
                ReverberatorFilter reverbFilter = new ReverberatorFilter();

                // Applies the filter
                TestAudioFilter.applyFilter(reverbFilter, inputFile, outputFile);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}