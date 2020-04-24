NOTE: THE BONUS REVERBERATOR LIBRARY *HAS BEEN IMPLEMENTED*.
Exclusively related to the reverberator:
* `be.isach.filterlib.reverberator.ReverberatorFilter`
* `be.isach.filterlib.reverberator.NestedFilter`
* `be.isach.filterlib.reverberator.LowPassFilter`

# OOP Project - Filters Library

- Academic Year: 2019-2020.
- Submission Limit: 26th of April.
- Student: LEWIN Sacha (S181947)

## Running the library
First, you need an input file. Let's consider we have an input file `input.wav`.

Then, you can compile:
`javac -d bin -cp audio.jar src/be/isach/filterlib/*/*.java src/be/isach/filterlib/Demo.java`

Now, there are two demo modes to run the code:
* Echo: `java -cp bin:audio.jar be.isach.filterlib.Demo input.wav output.wav`
* Reverberator: `java -cp bin:audio.jar be.isach.filterlib.Demo Reverb input.wav output.wav`

## Packages Descriptions
### `examples` package
The `example` package contains some example files, demonstrating some
implemented filters, and some combinations of them. For example, the cascade
of 3 all-pass filters as it was suggested to implement it.

### `filters` package
It contains all the basic filters, plus some more complex implementations
combining them.

### `reverberator` package
This package contains stuff exclusively implemented for the reverberator bonus.
This was separated from the rest for ease of reading for the corrector.

### `util` package
This package contains some utility stuff used for other classes, such as 
`BlockData.java`, used in `CompositeFilter.java`.