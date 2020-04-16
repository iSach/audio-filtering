package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Implements an addition filter.
 * <p>
 * When applied, produces a single output which is the sum of the two
 * given inputs.
 * </p>
 */
public class AdditionFilter implements Filter {

    /**
     * An addition filter requires 2 inputs, to sum them.
     *
     * @return the number of inputs of the filter.
     */
    @Override
    public int nbInputs() {
        return 2;
    }

    /**
     * An addition filter produces only 1 output.
     *
     * @return the number of outputs of the filter.
     */
    @Override
    public int nbOutputs() {
        return 1;
    }

    /**
     * Computes one step of the filtering.
     * Simply sums the two given inputs into the desired output.
     *
     * @param input contains the two samples to sum.
     * @return an array containing one output, which is the sum of the two
     *          input samples.
     * @throws FilterException if the input array is null or of wrong length.
     */
    @Override
    public double[] computeOneStep(double[] input) throws FilterException {
        if(input == null) {
            throw new FilterException("Specified input array points to null.");
        }

        if(input.length != nbInputs()) {
            throw new FilterException("Invalid number of inputs. Expected: "
                    + nbInputs() + ", Got: " + input.length);
        }

        double output = input[0] + input[1];

        return new double[]{output};
    }

    /**
     * Resets the filter.
     * No specific action is required for this filter.
     */
    @Override
    public void reset() {
    }
}
