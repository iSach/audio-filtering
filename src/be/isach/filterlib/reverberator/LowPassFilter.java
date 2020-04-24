package be.isach.filterlib.reverberator;

import be.isach.filterlib.filters.AdditionFilter;
import be.isach.filterlib.filters.CompositeFilter;
import be.isach.filterlib.filters.DelayFilter;
import be.isach.filterlib.filters.GainFilter;
import be.uliege.montefiore.oop.audio.Filter;

public class LowPassFilter extends CompositeFilter {

    /**
     * Initializes an all-pass filter, a special type of composite filter.
     *
     * @param gain  The gain of the all-pass filter.
     * @param delay The delay of the all-pass filter.
     */
    public LowPassFilter(double gain, int delay) {
        super(1, 1);

        // Build the composite filter:
        Filter gainFilter = new GainFilter(gain);
        Filter antiGainFilter = new GainFilter(1 - gain);
        Filter delayFilter = new DelayFilter(delay);
        Filter addFilter = new AdditionFilter();

        addBlock(gainFilter);
        addBlock(antiGainFilter);
        addBlock(delayFilter);
        addBlock(addFilter);

        connectInputToBlock(0, antiGainFilter, 0);
        connectBlockToBlock(delayFilter, 0, gainFilter, 0);
        connectBlockToBlock(gainFilter, 0, addFilter, 1);
        connectBlockToBlock(antiGainFilter, 0, addFilter, 0);
        connectBlockToBlock(addFilter, 0, delayFilter, 0);
        connectBlockToOutput(addFilter, 0, 0);
    }
}
