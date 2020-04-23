package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;

public class AllPassFilter extends CompositeFilter {

    /**
     * Initializes an all-pass filter, a special type of composite filter.
     *
     * @param gain  The gain of the all-pass filter.
     * @param delay The delay of the all-pass filter.
     */
    public AllPassFilter(double gain, int delay) {
        super(1, 1);

        // Build the composite filter:
        Filter positiveGainFilter = new GainFilter(gain);
        Filter negativeGainFilter = new GainFilter(-gain);
        Filter delayFilter = new DelayFilter(delay);
        Filter firstAddFilter = new AdditionFilter();
        Filter secondAddFilter = new AdditionFilter();

        addBlock(positiveGainFilter);
        addBlock(negativeGainFilter);
        addBlock(delayFilter);
        addBlock(firstAddFilter);
        addBlock(secondAddFilter);

        connectInputToBlock(0, negativeGainFilter, 0);
        connectInputToBlock(0, firstAddFilter, 0);
        connectBlockToBlock(firstAddFilter, 0, delayFilter, 0);
        connectBlockToBlock(delayFilter, 0, secondAddFilter, 0);
        connectBlockToBlock(negativeGainFilter, 0, secondAddFilter, 1);
        connectBlockToBlock(secondAddFilter, 0, positiveGainFilter, 0);
        connectBlockToBlock(positiveGainFilter, 0, firstAddFilter, 1);
        connectBlockToOutput(secondAddFilter, 0, 0);
    }
}
