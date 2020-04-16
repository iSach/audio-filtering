package be.isach.filterlib.filters;

import be.isach.filterlib.BlockData;
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
     * Stores the current output being built recursively.
     */
    private Double[] output;

    /**
     * Contains the visited filters, to avoid calling the same filter
     * twice in a same cycle.
     */
    private Set<Filter> visited;

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

        this.filtersData = new HashMap<>();

        this.inputFilters = new HashMap<>();
        this.outputFilters = new HashMap<>();

        this.visited = new HashSet<>();
    }

    /**
     * Adds a block to the composite filter.
     * Needs to be connected afterward.
     *
     * @param filter The filter to add
     */
    public void addBlock(Filter filter) {
        if (filtersData.containsKey(filter)) return;

        BlockData blockData = new BlockData(filter);
        filtersData.put(filter, blockData);
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
        if (!filtersData.containsKey(f1) || !filtersData.containsKey(f2)) {
            return;
        }

        BlockData data1 = filtersData.get(f1);
        BlockData data2 = filtersData.get(f2);
        data1.setFilterAsOutput(f2, i2);
        data2.setFilterAsInput(f1, o1);

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
        if (!filtersData.containsKey(f1)) {
            return;
        }

        BlockData data = filtersData.get(f1);
        data.setFilterAsOutput(this, o2);
        outputFilters.putIfAbsent(f1, o1);

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
        if (!filtersData.containsKey(f2)) {
            return;
        }

        BlockData data = filtersData.get(f2);
        data.setFilterAsInput(this, i1);
        inputFilters.putIfAbsent(f2, i2);

        checkIfValid();
    }

    /**
     * Checks whether or not son is a following block of "of".
     * Used to check if a delay filter is in a loop for an addition filter.
     *
     * @param son The filter to check if it's a next vector
     * @param of  The parent filter
     * @return {true} if "son" can be accessed from the childrens of "of",
     * {false} otherwise.
     */
    private boolean isSonOf(Filter son, Filter of) {
        BlockData blockData = filtersData.get(of);
        boolean result = false;
        for (Filter f : blockData.getOutputFilters().keySet()) {
            if (!(f instanceof CompositeFilter)) {
                if (f == son) {
                    return true;
                } else if (f != of) {
                    result = result || isSonOf(f, of);
                }
            }
        }
        return result;
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

        if (!isValid()) {
            throw new FilterException("Filter is not valid, missing " +
                    "connections.");
        }

        // Check if the output array already exists or not.
        if (this.output == null) {
            this.output = new Double[nbOutputs()];
        } else {
            Arrays.fill(this.output, null);
        }

        // Call next filters, after giving them the input.
        for (Filter f : inputFilters.keySet()) {
            BlockData data = filtersData.get(f);
            int compositeInputIndex = data.getInputFilters().get(this);
            data.addToBuffer(input[compositeInputIndex], inputFilters.get(f));
            computeAux(f);
        }

        visited.clear();

        // Convert the array of references to one of primitives.
        double[] finalOutput = new double[output.length];
        for (int i = 0; i < finalOutput.length; i++)
            finalOutput[i] = output[i];

        return finalOutput;
    }

    /**
     * Recursive function for calling subfilters subsequently between
     * each other.
     * <p>
     * Inputs and outputs are passed through the corresponding Block Data.
     *
     * @param f The filter to compute.
     * @throws FilterException if the computed output threw an error.
     */
    public void computeAux(Filter f) throws FilterException {
        if (visited.contains(f)) return;

        BlockData data = filtersData.get(f);

        // This means the called block is still waiting for another
        // input to come from another call. Cancel this call.
        if (!data.allInputsAvailable()) {
            fetchMissingInputsFromDelays(f);
            return;
        }

        double[] input = data.consumeBuffer();

        // Read the output.
        // If the filter is a delay filter that needs updating (because it's
        // in a loop and was already popped), then just enqueue a new value.
        // Otherwise, just process it normally.
        double[] output;
        if (f instanceof DelayFilter && ((DelayFilter) f).needsUpdating()) {
            ((DelayFilter) f).enqueue(input);
            return;
        } else {
            output = f.computeOneStep(input);
        }

        // Mark the filter as visited in the current cycle.
        visited.add(f);

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
    public void fetchMissingInputsFromDelays(Filter f) throws FilterException {
        BlockData data = filtersData.get(f);

        if (data.allInputsAvailable()) return;

        for (Filter delay : filtersData.keySet()) {
            if (delay instanceof DelayFilter) {
                BlockData delayData = filtersData.get(delay);
                Filter next = delayData.getOutputFilters().keySet().
                        iterator().next();

                while (next != f && !(next instanceof CompositeFilter)) {
                    BlockData d = filtersData.get(next);
                    next = d.getOutputFilters().keySet().iterator().next();
                }

                if (next == f
                        && isSonOf(f, delay)
                        && isSonOf(delay, f)) {
                    next = delayData.getOutputFilters().keySet().
                            iterator().next();
                    BlockData d = filtersData.get(next);
                    int bufferIndex = delayData.getOutputFilters().get(next);
                    double popped = ((DelayFilter) delay).pop();
                    d.addToBuffer(popped, bufferIndex);
                    computeAux(next);
                }
            }
        }
    }

    /**
     * Checks if the composite filter is valid, and updates the flag.
     */
    private void checkIfValid() {
        // Check if each input of the composite is connected.
        if (inputFilters.size() != nbInputs()) {
            valid = false;
            return;
        }

        // Check if each output of the composite is connected.
        if (outputFilters.size() != nbOutputs()) {
            valid = false;
            return;
        }

        // Check if each subfilter has all its outputs and inputs connected.
        for (Filter f : inputFilters.keySet()) {
            BlockData data = filtersData.get(f);
            if (data.getInputFilters().size() != f.nbInputs()
                    || data.getOutputFilters().size() < f.nbOutputs()) {
                valid = false;
                return;
            }
        }
        valid = true;
    }

    /**
     * @return {true} if the composite filter is correct, {false} otherwise.
     */
    public boolean isValid() {
        return valid;
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
        for (Filter filter : filtersData.keySet())
            filter.reset();
        output = new Double[nbOutputs()];
        visited.clear();
    }
}
