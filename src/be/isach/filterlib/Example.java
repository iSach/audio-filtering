package be.isach.filterlib;

import be.isach.filterlib.filters.DelayFilter;
import be.isach.filterlib.filters.GainFilter;
import be.uliege.montefiore.oop.audio.*;

/**
 * INFO0062 - Object-Oriented Programming
 * Project basis
 *
 * be.isach.filterlib.Example code to filter a WAV file using audio.jar. The filter has to be implemented first.
 * 
 * @author: J.-F. Grailet (ULiege)
 */

public class Example
{
    public static void main(String[] args)
    {
        try
        {
            Filter testFilter = new DelayFilter(44100 * 5);
            TestAudioFilter.applyFilter(testFilter, "queen.wav", "output.wav");
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
