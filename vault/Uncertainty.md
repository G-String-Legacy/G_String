[Return](About.md)
## Uncertainty ##

In a November 1789 letter to Jean-Baptiste Le Roy, Benjamin Franklin wrote: 
> "Our new Constitution is now established, everything seems to promise it will be durable; but, in this world, nothing is certain except death and taxes".

This statement too can be challenged - some people manage to avoid paying taxes, and so far, nobody has managed to live for ever; but establishing the exact moment of death mostly escapes medical science. 

> Thus, to paraphrase Benjamin Franklin: Only uncertainty is certain!

 In classical physics [^1] there are no uncertainties. For example, Newton's Second Law of Motion states that:
> Force = Mass x Acceleration

The variables 'Force', 'Mass' and 'Acceleration' have absolute values. The uncertainty arises, when we attempt to measure them. Thus, I will define statistics as: **the Science of estimating uncertainty.**

You may counter that the problem lies with the accuracy of the measurement instrument. If we could add more digits behind the decimal point, results should become more certain. But the opposite is true. The finer the measurement instrument, the more different results we will get on repeated measurements. The best estimate for the 'true' value of a variable 'x' is the average of the measured values by calculating their **mean** value like this:

<div align="center"><img style="background: black;" src="svg/YUyhMYmlbD.svg"></div>

where the mean <img style="transform: translateY(0.1em); background: white;" src="svg/lvTG095mPi.svg"> is equal to the sum over all N measurements x<sub>i</sub> divided by N, the number of measurements.

So we now know how to estimate the quantitative value of a real entity, but we still don't know anything about its uncertainty. 'Uncertainty' really isn't a scientific term, what we really are looking for is a quantity called 'variance'  (&sigma;<sup>2</sup>) of a measurement:

<div align="center"><img style="background: black;" src="svg/uGWR4aBeBY.svg"></div>

It is something like the mean of the square differences between the individually measured values, and the calculated mean of those values. But you may ask: "why are we dividing by (N - 1) rather than by N?". Well, we have measured N different values x<sub>i</sub>. From those we have calculated the mean, so there are really only N-1 variables left to average.

Now that we understand, how to estimate the mean and variance of a measured entity, we are ready for the next step: [Analyzing Variance](Variance.md)


[^1]: Statistical Mechanics and Thermodynamics excluded
