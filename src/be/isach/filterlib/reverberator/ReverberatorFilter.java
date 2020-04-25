package be.isach.filterlib.reverberator;

import be.isach.filterlib.filters.*;
import be.isach.filterlib.util.CascadingAllPassFiltersBuilder;
import be.isach.filterlib.util.CascadingFiltersBuilder;

/**
 * Implements the bonus reverberator filter.
 */
public class ReverberatorFilter extends CompositeFilter {

    /**
     * Initializes a reverberator filter.
     */
    public ReverberatorFilter() {
        super(1, 1);

        AdditionFilter entryAdditionFilter = new AdditionFilter();
        CompositeFilter subFilter1 = createFirstSubFilter();
        GainFilter gain034 = new GainFilter(0.34);
        CompositeFilter subFilter2 = createSecondSubFilter();
        GainFilter gain014 = new GainFilter(0.14);
        AdditionFilter intermediateAdditionFilter = new AdditionFilter();
        CompositeFilter subFilter3 = createThirdSubFilter();
        GainFilter gain014b = new GainFilter(0.14);
        AdditionFilter endAdditionFilter = new AdditionFilter();
        LowPassFilter lowPassFilter = new LowPassFilter(0.7133, 88);
        GainFilter gain01 = new GainFilter(0.1);
        DelayFilter delay03 = new DelayFilter(132); // 3ms

        // Add all the filters.
        addBlock(entryAdditionFilter);
        addBlock(gain034);
        addBlock(gain014);
        addBlock(intermediateAdditionFilter);
        addBlock(gain014b);
        addBlock(endAdditionFilter);
        addBlock(lowPassFilter);
        addBlock(gain01);
        addBlock(subFilter1);
        addBlock(subFilter2);
        addBlock(subFilter3);
        addBlock(delay03);

        // Construct the filter's links.
        connectInputToBlock(0, entryAdditionFilter, 0);
        connectBlockToBlock(gain01, 0, entryAdditionFilter, 1);
        connectBlockToBlock(entryAdditionFilter, 0, subFilter1, 0);
        connectBlockToBlock(subFilter1, 0, subFilter2, 0);
        connectBlockToBlock(subFilter1, 0, gain034, 0);
        connectBlockToBlock(gain034, 0, intermediateAdditionFilter, 0);
        connectBlockToBlock(subFilter2, 0, delay03, 0);
        connectBlockToBlock(delay03, 0, subFilter3, 0);
        connectBlockToBlock(subFilter2, 0, gain014, 0);
        connectBlockToBlock(gain014, 0, intermediateAdditionFilter, 1);
        connectBlockToBlock(subFilter3, 0, gain014b, 0);
        connectBlockToBlock(subFilter3, 0, lowPassFilter, 0);
        connectBlockToBlock(intermediateAdditionFilter, 0,
                endAdditionFilter, 0);
        connectBlockToBlock(gain014b, 0, endAdditionFilter, 1);
        connectBlockToBlock(lowPassFilter, 0, gain01, 0);
        connectBlockToOutput(endAdditionFilter, 0, 0);
    }

    /**
     * Separates the creation of this sub-block for better readability.
     *
     * @return the first sub-block, consisting of a double all-pass filter
     * followed by a delay filter.
     */
    private CompositeFilter createFirstSubFilter() {
        CascadingFiltersBuilder builder = new CascadingFiltersBuilder();

        CascadingAllPassFiltersBuilder allPassBuilder = new
                CascadingAllPassFiltersBuilder();
        allPassBuilder.add(0.3, 353);
        allPassBuilder.add(0.3, 529);

        builder.add(allPassBuilder.build());
        builder.add(new DelayFilter(176)); // 4ms

        return builder.build();
    }

    /**
     * Separates the creation of this sub-block for better readability.
     *
     * @return the second sub-block, consisting of a delay filter, followed by
     * a single all-pass filter nested inside another, followed by
     * a second delay filter.
     */
    private CompositeFilter createSecondSubFilter() {
        CascadingFiltersBuilder builder = new CascadingFiltersBuilder();

        builder.add(new DelayFilter(750)); // 17ms

        AllPassFilter firstNestedFilter = new AllPassFilter(0.25,
                2734);
        CompositeFilter firstNested = new NestedFilter(firstNestedFilter,
                3837, 0.5);

        builder.add(new DelayFilter(1367)); // 4ms

        return builder.build();
    }

    /**
     * Separates the creation of this sub-block for better readability.
     *
     * @return the third sub-block, consisting of a double all-pass filter
     * nested inside an all-pass filter.
     */
    private CompositeFilter createThirdSubFilter() {
        CascadingFiltersBuilder builder = new CascadingFiltersBuilder();

        // Create the cascade double all-pass filter.
        CascadingAllPassFiltersBuilder nestedCascadeBuilder = new
                CascadingAllPassFiltersBuilder();
        nestedCascadeBuilder.add(0.25, 3352);
        nestedCascadeBuilder.add(0.25, 1323);

        CompositeFilter nestedCascadeFilter = nestedCascadeBuilder.build();
        CompositeFilter secondNested = new NestedFilter(nestedCascadeFilter,
                5292, 0.5);
        builder.add(secondNested);

        return builder.build();
    }
}
