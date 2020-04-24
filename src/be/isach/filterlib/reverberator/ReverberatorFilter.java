package be.isach.filterlib.reverberator;

import be.isach.filterlib.filters.*;

public class ReverberatorFilter extends CompositeFilter {
    /**
     * Initializes a composite filter with the specified amount of inputs and
     * outputs.
     *
     * @param inputsAmount  The amount of inputs of the Composite Filter.
     * @param outputsAmount The amount of outputs of the Composite Filter.
     */
    public ReverberatorFilter() {
        super(1, 1);

        AdditionFilter additionFilter1 = new AdditionFilter();
        AllPassFilter allPassFilter = new AllPassFilter(0.3, 353);
        AllPassFilter allPassFilter2 = new AllPassFilter(0.3, 529);
        DelayFilter delay4 = new DelayFilter(176);
        GainFilter gain034 = new GainFilter(0.34);
        DelayFilter delay17 = new DelayFilter(750);


        // First nested filter.
        CompositeFilter firstNested = addFirstNestedFilter();

        DelayFilter delay31 = new DelayFilter(1367);
        GainFilter gain014 = new GainFilter(0.14);
        AdditionFilter additionFilter2 = new AdditionFilter();
        DelayFilter delay3 = new DelayFilter(132);

        CompositeFilter secondNested = addSecondNestedFilter();

        GainFilter gain014b = new GainFilter(0.14);
        AdditionFilter additionFilter3 = new AdditionFilter();
        LowPassFilter lowPassFilter = new LowPassFilter(0.7133, 88); // 2ms
        GainFilter gain01 = new GainFilter(0.1);

        addBlock(additionFilter1);
        addBlock(allPassFilter);
        addBlock(allPassFilter2);
        addBlock(delay4);
        addBlock(gain034);
        addBlock(delay17);
        addBlock(firstNested);
        addBlock(delay31);
        addBlock(gain014);
        addBlock(additionFilter2);
        addBlock(delay3);
        addBlock(secondNested);
        addBlock(gain014b);
        addBlock(additionFilter3);
        addBlock(lowPassFilter);
        addBlock(gain01);

        connectInputToBlock(0, additionFilter1, 0);
        connectBlockToBlock(gain01, 0, additionFilter1, 1);
        connectBlockToBlock(additionFilter1, 0, allPassFilter, 0);
        connectBlockToBlock(allPassFilter, 0, allPassFilter2, 0);
        connectBlockToBlock(allPassFilter2, 0, delay4, 0);
        connectBlockToBlock(delay4, 0, gain034, 0);
        connectBlockToBlock(gain034, 0, additionFilter2, 0);
        connectBlockToBlock(delay4, 0, delay17, 0);
        connectBlockToBlock(delay17, 0, firstNested, 0);
        connectBlockToBlock(firstNested, 0, delay31, 0);
        connectBlockToBlock(delay31, 0, gain014, 0);
        connectBlockToBlock(gain014, 0, additionFilter2, 1);
        connectBlockToBlock(delay31, 0, delay3, 0);
        connectBlockToBlock(delay3, 0, secondNested, 0);
        connectBlockToBlock(secondNested, 0, gain014b, 0);
        connectBlockToBlock(additionFilter2, 0, additionFilter3, 0);
        connectBlockToBlock(gain014b, 0, additionFilter3, 1);
        connectBlockToBlock(secondNested, 0, lowPassFilter, 0);
        connectBlockToBlock(lowPassFilter, 0, gain01, 0);
        connectBlockToOutput(additionFilter3, 0, 0);
    }

    private CompositeFilter addFirstNestedFilter() {
        CompositeFilter nested = new CompositeFilter(1, 1);

        AdditionFilter startAdd = new AdditionFilter();
        AllPassFilter nestedAll = new AllPassFilter(0.25, 2734); // 62ms
        DelayFilter delay = new DelayFilter(3837); // 87 ms
        GainFilter gainForward = new GainFilter(-0.5);
        GainFilter gainBackward = new GainFilter(0.5);
        AdditionFilter endAdd = new AdditionFilter();

        nested.addBlock(startAdd);
        nested.addBlock(nestedAll);
        nested.addBlock(delay);
        nested.addBlock(gainForward);
        nested.addBlock(gainBackward);
        nested.addBlock(endAdd);

        nested.connectInputToBlock(0, startAdd, 0);
        nested.connectBlockToBlock(gainBackward, 0, startAdd, 1);
        nested.connectInputToBlock(0, gainForward, 0);
        nested.connectBlockToBlock(gainForward, 0, endAdd, 0);
        nested.connectBlockToBlock(endAdd, 0, gainBackward, 0);
        nested.connectBlockToBlock(startAdd, 0, nestedAll, 0);
        nested.connectBlockToBlock(nestedAll, 0, delay, 0);
        nested.connectBlockToBlock(delay, 0, endAdd, 1);
        nested.connectBlockToOutput(endAdd, 0, 0);

        addBlock(nested);
        return nested;
    }

    private CompositeFilter addSecondNestedFilter() {
        CompositeFilter nested = new CompositeFilter(1, 1);

        AdditionFilter startAdd = new AdditionFilter();
        AllPassFilter firstAll = new AllPassFilter(0.25, 3352); // 76ms
        DelayFilter firstDelay = new DelayFilter(2646); // 87 ms
        AllPassFilter secondAll = new AllPassFilter(0.25, 1323); // 30ms
        DelayFilter secondDelay = new DelayFilter(2646); // 87 ms
        GainFilter gainForward = new GainFilter(-0.5);
        GainFilter gainBackward = new GainFilter(0.5);
        AdditionFilter endAdd = new AdditionFilter();

        nested.addBlock(startAdd);
        nested.addBlock(firstAll);
        nested.addBlock(firstDelay);
        nested.addBlock(secondAll);
        nested.addBlock(secondDelay);
        nested.addBlock(gainForward);
        nested.addBlock(gainBackward);
        nested.addBlock(endAdd);

        nested.connectInputToBlock(0, startAdd, 0);
        nested.connectBlockToBlock(gainBackward, 0, startAdd, 1);
        nested.connectInputToBlock(0, gainForward, 0);
        nested.connectBlockToBlock(gainForward, 0, endAdd, 0);
        nested.connectBlockToBlock(endAdd, 0, gainBackward, 0);
        nested.connectBlockToBlock(startAdd, 0, firstAll, 0);
        nested.connectBlockToBlock(firstAll, 0, firstDelay, 0);
        nested.connectBlockToBlock(firstDelay, 0, secondAll, 0);
        nested.connectBlockToBlock(secondAll, 0, secondDelay, 0);
        nested.connectBlockToBlock(secondDelay, 0, endAdd, 1);
        nested.connectBlockToOutput(endAdd, 0, 0);

        addBlock(nested);
        return nested;
    }
}
