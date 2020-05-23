package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.*;

/**
 * Represents a composite filter, which allows to combine filters as chains and
 * loops to create more complex effects, such as an echo filter.
 */
public class CompositeFilter implements Filter {

    /**
     * Stores the amount of inputs of the whole composite filter.
     */
    private final int inputsAmount;

    /**
     * Stores the amount of outputs of the whole composite filter.
     */
    private final int outputsAmount;

    /**
     * Stores the different filters in the composite filter, and their
     * corresponding block data.
     */
    private final Map<Filter, FilterBlock> blocks;

    private final FilterBlock selfBlock;

    /**
     * Stores the current output being built recursively.
     */
    private Double[] output;

    /**
     * Stores validity of the composite filter.
     */
    private boolean valid;

    /**
     * Initializes a composite filter with the specified amount of inputs and
     * outputs.
     *
     * @param inputsAmount  The amount of inputs of the Composite Filter.
     * @param outputsAmount The amount of outputs of the Composite Filter.
     */
    public CompositeFilter(int inputsAmount, int outputsAmount) {
        this.inputsAmount = inputsAmount;
        this.outputsAmount = outputsAmount;

        this.blocks = new HashMap<>();

        this.selfBlock = new FilterBlock(this, this);
    }

    /**
     * Adds a block to the composite filter.
     * Needs to be connected afterward.
     *
     * @param filter The filter to add
     */
    public void addBlock(Filter filter) {
        if (blocks.containsKey(filter)) return;

        FilterBlock blockData = new FilterBlock(filter, this);
        blocks.put(filter, blockData);
    }

    /**
     * Connects output o1 of the filter f1 to the input i2 of the filter f2.
     *
     * @param f1 The filter to connect the output of
     * @param o1 The output of f1
     * @param f2 The filter to connect to the input of
     * @param i2 the input of f2
     */
    public void connectBlockToBlock(Filter f1, int o1, Filter f2, int i2) {
        // Check if both filters are already added, otherwise stop.
        if (!blocks.containsKey(f1) || !blocks.containsKey(f2)) {
            return;
        }

        FilterBlock data1 = blocks.get(f1);
        FilterBlock data2 = blocks.get(f2);
        data1.setFilterAsOutput(data2, i2);
        data2.setFilterAsInput(data1, o1);

        checkIfValid();
    }

    /**
     * Connects the output o1 of a block to the output of the composite filter.
     *
     * @param f1 The filter to connect the output of
     * @param o1 The output of f1
     * @param o2 the output of the composite filter.
     */
    public void connectBlockToOutput(Filter f1, int o1, int o2) {
        // Check if the filter is already added, otherwise stop.
        if (!blocks.containsKey(f1)) {
            return;
        }

        FilterBlock data = blocks.get(f1);
        data.setFilterAsOutput(selfBlock, o2);
        selfBlock.setFilterAsOutput(data, o1);

        checkIfValid();
    }

    /**
     * Connects a block to thec input of the composite filter.
     *
     * @param i1 The input of the composite filter.
     * @param f2 The filter to connect to the input of the composite
     * @param i2 the input of the connected filter.
     */
    public void connectInputToBlock(int i1, Filter f2, int i2) {
        // Check if the filter is already added, otherwise stop.
        if (!blocks.containsKey(f2)) {
            return;
        }

        FilterBlock data = blocks.get(f2);
        data.setFilterAsInput(selfBlock, i1);
        selfBlock.setFilterAsInput(data, i2);

        checkIfValid();
    }

    /**
     * Computes an output and passes it subsequently to all the subfilters.
     *
     * @param input The input to process
     * @return The output, processed from all the filters.
     * @throws FilterException if the input is wrong, or if the composite filter
     *                         is incomplete.
     */
    @Override
    public double[] computeOneStep(double[] input) throws FilterException {
        if (input == null) {
            throw new FilterException("Specified input array points to null.");
        }

        if (input.length != nbInputs()) {
            throw new FilterException("Invalid number of inputs. Expected: "
                    + nbInputs() + ", Got: " + input.length);
        }

        if (isInvalid()) {
            throw new FilterException("Filter is not valid, missing " +
                    "connections.");
        }

        // Check if the output array already exists or not.
        if (this.output == null) {
            this.output = new Double[nbOutputs()];
        } else {
            Arrays.fill(this.output, null);
        }

        // First, pop delay filters to resolve loops.
        for(FilterBlock block : blocks.values()) {
            if(block.getFilter() instanceof DelayFilter) {
                block.compute();
            }
        }

        // Call next filters, after giving them the input.
        for (FilterBlock block : selfBlock.getInputBlocks().keySet()) {
            if (block == selfBlock) { // Special case when there's no other subfilter
                int index = selfBlock.getInputBlocks().get(block);
                output[index] = input[0];
            } else {
                int compositeInputIndex = block.getInputBlocks().get(selfBlock);
                block.addToBuffer(input[compositeInputIndex],
                        selfBlock.getInputBlocks().get(block));
                block.compute();
            }
        }

        for (FilterBlock block : blocks.values())
            block.setVisited(false);

        // Convert the array of references to one of primitives.
        double[] finalOutput = new double[output.length];
        for (int i = 0; i < finalOutput.length; i++) {
            if (output[i] == null) {
                throw new FilterException("Filter is not valid, " +
                        "possibly mising a delay filter in a loop.");
            }

            finalOutput[i] = output[i];
        }

        return finalOutput;
    }

    /**
     * End of chain: give the output to the owning composite filter.
     */
    protected void insertOutput(int index, double value) {
        this.output[index] = value;
    }

    /**
     * Checks if the composite filter is valid, and updates the flag.
     */
    private void checkIfValid() {
        // Check if each input of the composite is connected.
        if (selfBlock.getInputBlocks().size() < nbInputs()) {
            valid = false;
            return;
        }

        // Check if each output of the composite is connected.
        if (selfBlock.getOutputBlocks().size() < nbOutputs()) {
            valid = false;
            return;
        }

        // Check if each subfilter has all its outputs and inputs connected.
        for (FilterBlock block : selfBlock.getInputBlocks().keySet()) {
            Filter f = block.getFilter();
            if (f != this) {
                FilterBlock data = blocks.get(f);
                if (data.getInputBlocks().size() < f.nbInputs()
                        || data.getOutputBlocks().size() < f.nbOutputs()) {
                    valid = false;
                    return;
                }
            }
        }
        valid = true;
    }

    /**
     * @return {@code true} if the composite filter is not correct,
     *         {@code false} otherwise.
     */
    public boolean isInvalid() {
        return !valid;
    }

    /**
     * @return the number of inputs of the composite filter.
     */
    @Override
    public int nbInputs() {
        return inputsAmount;
    }

    /**
     * @return the number of outputs of the composite filter.
     */
    @Override
    public int nbOutputs() {
        return outputsAmount;
    }

    /**
     * Resets the filter by resetting the subfilters, the output and the visited
     * filters list.
     */
    @Override
    public void reset() {
        for (FilterBlock block : blocks.values()) {
            if (block != selfBlock) {
                block.getFilter().reset();
            }
            block.setVisited(false);
        }

        output = new Double[nbOutputs()];
    }

    public FilterBlock getSelfBlock() {
        return selfBlock;
    }
}
