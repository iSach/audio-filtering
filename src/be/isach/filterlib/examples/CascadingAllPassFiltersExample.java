package be.isach.filterlib.examples;

import be.isach.filterlib.filters.AllPassFilter;
import be.isach.filterlib.filters.CompositeFilter;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

/**
 * Demonstrates a way of cascading all pass filters, as suggested
 * on the project page.
 */
public class CascadingAllPassFiltersExample {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Wrong number of arguments.");
			System.err.println("Expected: 2. Got: " + args.length);
			return;
		}

		String inputFile = args[0];
		String outputFile = args[1];

		try {
			// Create the filter that will chain the 3 all-pass filters.
			CompositeFilter totalFilter = new CompositeFilter(1, 1);

			AllPassFilter firstAllPass = new AllPassFilter(0.5, 1323);
			AllPassFilter secondAllPass = new AllPassFilter(0.7, 11907);
			AllPassFilter thirdAllPass = new AllPassFilter(0.2, 32847);

			totalFilter.addBlock(firstAllPass);
			totalFilter.addBlock(secondAllPass);
			totalFilter.addBlock(thirdAllPass);

            totalFilter.connectInputToBlock(0, firstAllPass, 0);
            totalFilter.connectBlockToBlock(firstAllPass, 0, secondAllPass, 0);
            totalFilter.connectBlockToBlock(secondAllPass, 0, thirdAllPass, 0);
            totalFilter.connectBlockToOutput(thirdAllPass, 0, 0);

			// Applies the filter
			TestAudioFilter.applyFilter(totalFilter, inputFile, outputFile);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
