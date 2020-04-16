package be.isach.filterlib.filters;

import be.isach.filterlib.BlockData;
import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CompositeFilter implements Filter {

    /**
     * Stores the amount of inputs of the whole composite filter.
     */
    private int inputsAmount;

    /**
     * Stores the amount of outputs of the whole composite filter.
     */
    private int outputsAmount;

    /**
     * Stores the different filters in the composite filter, and their
     * corresponding block data.
     */
    private Map<Filter, BlockData> filtersData;

    /**
     * Filters the inputs of the composite filter point to.
     * key: the filter.
     * value: the input of the block pointed by the input of the composite.
     */
    private HashMap<Filter, Integer> inputFilters;

    /**
     * Filters it points to
     * key: the filter.
     * value: the output of the block that points to the outputs
     * of the composite.
     */
    private HashMap<Filter, Integer> outputFilters;

    /**
     * Current output being built.
     */
    private double[] output;

    public CompositeFilter(int inputsAmount, int outputsAmount) {
        this.inputsAmount = inputsAmount;
        this.outputsAmount = outputsAmount;

        this.filtersData = new HashMap<>();

        this.inputFilters = new HashMap<>();
        this.outputFilters = new HashMap<>();
    }

    public void addBlock(Filter f) {
        if (filtersData.containsKey(f)) return;

        BlockData blockData = new BlockData(f);
        filtersData.put(f, blockData);
    }

    public void connectBlockToBlock(Filter f1, int o1, Filter f2, int i2) {
        // Check if both filters are already added, otherwise stop.
        if (!filtersData.containsKey(f1) || !filtersData.containsKey(f2)) {
            return;
        }

        BlockData data1 = filtersData.get(f1);
        BlockData data2 = filtersData.get(f2);
        data1.setFilterAsOutput(f2, i2);
        data2.setFilterAsInput(f1, o1);
    }

    public void connectBlockToOutput(Filter f1, int o1, int o2) {
        // Check if the filter is already added, otherwise stop.
        if (!filtersData.containsKey(f1)) {
            return;
        }

        BlockData data = filtersData.get(f1);
        data.setFilterAsOutput(this, o2);
        outputFilters.putIfAbsent(f1, o1);
    }

    public void connectInputToBlock(int i1, Filter f2, int i2) {
        // Check if the filter is already added, otherwise stop.
        if (!filtersData.containsKey(f2)) {
            return;
        }

        BlockData data = filtersData.get(f2);
        data.setFilterAsInput(this, i1);
        inputFilters.putIfAbsent(f2, i2);
    }

    @Override
    public double[] computeOneStep(double[] input) throws FilterException {
        if (input == null) {
            throw new FilterException("Specified input array points to null.");
        }

        if (input.length != nbInputs()) {
            throw new FilterException("Invalid number of inputs. Expected: "
                    + nbInputs() + ", Got: " + input.length);
        }

        if (this.output == null) {
            this.output = new double[nbOutputs()];
        } else {
            Arrays.fill(this.output, -1);
        }

        for (Filter f : inputFilters.keySet()) {
            BlockData data = filtersData.get(f);
            int compositeInputIndex = data.getInputFilters().get(this);
            data.addToBuffer(input[compositeInputIndex], inputFilters.get(f));
            computeAux(f);
        }

        for (int i = 0; i < nbOutputs(); i++) {
            if (output[i] == -1) {
                //  System.out.println("?????????");
                //  System.out.println(Collections.singletonList(output));
            }
        }

        return output;
    }

    public void computeAux(Filter f) throws FilterException {
        fetchMissingInputsFromDelays(f);

        BlockData data = filtersData.get(f);

        // This means the called block is still waiting for another
        // input to come from another call. Cancel this call.
        if (!data.allInputsAvailable()) {
            return;
        }

        double[] output = f.computeOneStep(data.consumeBuffer());

        for (Filter next : data.getOutputFilters().keySet()) {
            if (next != this) {
                BlockData nextData = filtersData.get(next);
                int outputIndex = nextData.getInputFilters().get(f);
                nextData.addToBuffer(output[outputIndex],
                        data.getOutputFilters().get(next));

                computeAux(next);
            } else {
                Integer outputIndex = outputFilters.get(f);
                this.output[data.getOutputFilters().get(next)] =
                        output[outputIndex];
                // end of sub-cycle
            }
        }
    }

    /**
     * Calls inputs from delay filters pointing to this block.
     * Should only happen when a loop points back to this block.
     * <p>
     * Does nothing if all inputs are available.
     */
    public void fetchMissingInputsFromDelays(Filter f) {
        BlockData data = filtersData.get(f);

        if (data.allInputsAvailable()) return;

        for (Filter delay : filtersData.keySet()) {
            if (delay instanceof DelayFilter) {
                BlockData delayData = filtersData.get(delay);
                Filter next = delayData.getOutputFilters().keySet().
                        iterator().next();
                int bufferIndex = 0;

                //System.out.println(next);

                while(!(next instanceof CompositeFilter)
                        && next != f) {
                    BlockData d = filtersData.get(next);
                    bufferIndex = d.getOutputFilters().get(next);
                    next = d.getOutputFilters().keySet().iterator().next();
                }

                if(next == f) {
                    System.out.println("Popping!");
                    data.addToBuffer(((DelayFilter) delay).pop(), bufferIndex);
                }
            }
        }

       /*for (int i = 0; i < data.getInputBuffer().length; i++) {
            if (data.getInputBuffer()[i] != -1) continue;

            for (Filter fil : data.getInputFilters().keySet()) {
                if(fil instanceof CompositeFilter) continue;

                if (!(fil instanceof DelayFilter)) continue;

                BlockData blockData = filtersData.get(fil);

                if (blockData.getOutputFilters().containsKey(f)
                        && blockData.getOutputFilters().get(f) == i) {
                    data.addToBuffer(((DelayFilter) fil).pop(), i);
                }
            }
        }*/
    }

    @Override
    public int nbInputs() {
        return inputsAmount;
    }

    @Override
    public int nbOutputs() {
        return outputsAmount;
    }

    @Override
    public void reset() {
        for (Filter filter : filtersData.keySet())
            filter.reset();
        output = new double[nbOutputs()];
    }
}
