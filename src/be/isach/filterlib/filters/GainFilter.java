package be.isach.filterlib.filters;

import be.uliege.montefiore.oop.audio.Filter;
import be.uliege.montefiore.oop.audio.FilterException;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Implements a gain filter.
 * <p>
 * When applied, multiplies the input samples by a value called gain.
 * </p>
 */
public class GainFilter implements Filter {

    /**
     * The value by which each input is multiplied before being output.
     */
    private double gain;

    /**
     * Initializes a new Gain Filter with the specified gain.
     *
     * @param gain The value by which each input is multiplied before being
     *            output.
     */
    public GainFilter(double gain) {
        this.gain = gain;
    }

    /**
     * A gain filter requires only 1 input.
     *
     * @return the number of inputs of the filter.
     */
    @Override
    public int nbInputs() {
        return 1;
    }

    /**
     * A gain filter produces only 1 output.
     *
     * @return the number of outputs of the filter.
     */
    @Override
    public int nbOutputs() {
        return 1;
    }

    /**
     * Computes one step of the filtering.
     * Simply multiplies the specified input by the gain.
     *
     * @param input contains the input sample to compute the filter on.
     *              Has to contain only one input for this filter.
     * @return an array containing the single output, which is the input
     *         multiplied by the gain.
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

        double output = gain * input[0];

        return new double[]{output};
    }

    /**
     * Resets the filter.
     * No specific action is required for this filter.
     */
    @Override
    public void reset() {
    }

    /**
     * @return the gain parameter of the filter.
     */
    public double getGain() {
        return gain;
    }
}
