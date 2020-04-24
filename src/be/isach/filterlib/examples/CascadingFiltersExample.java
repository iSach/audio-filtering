package be.isach.filterlib.examples;

import be.isach.filterlib.filters.CompositeFilter;
import be.isach.filterlib.util.CascadingAllPassFiltersBuilder;
import be.isach.filterlib.util.CascadingFiltersBuilder;
import be.uliege.montefiore.oop.audio.TestAudioFilter;

/**
 * Demonstrates a way of cascading all pass filters, as suggested
 * on the project page.
 */
public class CascadingFiltersExample {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Wrong number of arguments.");
			System.err.println("Expected: 2. Got: " + args.length);
			return;
		}

		String inputFile = args[0];
		String outputFile = args[1];

		try {
			CascadingAllPassFiltersBuilder builder = new
					CascadingAllPassFiltersBuilder();
			builder.add(0.5, 1323);
			builder.add(0.7, 11907);
			builder.add(0.2, 32847);
			CompositeFilter cascadeFilter = builder.build();

			// Applies the filter
			TestAudioFilter.applyFilter(cascadeFilter, inputFile, outputFile);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
