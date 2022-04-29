## The Sigmas, &tau;, &delta;, and &Delta; ##
You are justified in complaining, that &sigma;, &tau;, &delta; and &Delta; all sound Greek to you. I haven't found a good explanation or justification for the central choice of these symbols in Generalizability Theory either. It is true that &sigma;<sup>2</sup> is commonly used as symbol for variance in statistics, which is equal to the "*mean **s**quare difference between variable values and their mean*". And '&sigma;' is Greek for '**s**'. The letter '&tau;' follows '&sigma;' in the Greek alphabet. The letters '&delta;' and '&Delta;', finally correspond to our letters 'd' and 'D', which might stand for 'difference'. But I won't blame you, if you scoff at this explanation.

Anyhow, here are the accepted definitions:

> &sigma;<sup>2</sup>(&tau;): 'variance of the 'universe score', i.e. of the facet of differentiation.
> &sigma;<sup>2</sup>(&delta;): 'variance of the relative error score'.
> &sigma;<sup>2</sup>(&Delta;): 'variance of the absolute error score'.

Now we can calculate these variances from the variance components provided by urGenova for the simple  's x q' case, according to Brennan's rules derived from page 10 of '*Generalizability Theory*'.
> 'student' (s) is the facet of differentiation, thus:
> 
><img style="transform: translateY(0.1em); background: white;" src="svg/sIhCbFel6o.svg">
>
> <img style="transform: translateY(0.1em); background: white;" src="svg/U9p2JmeTWs.svg">
>    &sigma;<sup>2</sup>(&tau;) = the variance component of the facet of differentiation.

Next we have to look at the variance of the relative error score:
> The only facet of generalization is 'question' (q):
>
> <img style="transform: translateY(0.1em); background: white;" src="svg/bdRKz3qz3Z.svg">
>
> <img style="transform: translateY(0.1em); background: white;" src="svg/R0WcDy3G5V.svg">
>    &sigma;<sup>2</sup>(&delta;) = the sum of all normalized variance components involving facets of generalization, but not of differentiation.

And finally, the variance of the absolute error score:
> Here VC(q) and VC(qs) meet Brennan's criteria:
>
> <img style="transform: translateY(0.1em); background: white;" src="svg/mzQSIW1vQz.svg">
>
> <img style="transform: translateY(0.1em); background: white;" src="svg/qBSn26uVZr.svg">
>    &sigma;<sup>2</sup>(&Delta;) = the sum of all normalized variance components except for  the variance component of the facet of differentiation.

That leaves us with explaining, what is meant by 'normalized variance components'. Each variance component has been calculated for a specific configuration of facets. To normalize a given variance component, you divide it by the product of factors for each of its facets. For the facet of differentiation this factor is 1.0. For the facets of generalization and stratification it is 'the average' of the sample size. In case a facet is crossed, that makes it simple - it is the unique sample size for that facet. For nested facets it is a bit more complicated. for simple nested facets of level one, this average is equal to the arithmetic mean. For facets nested more highly, the average is equal to the harmonic mean. I am not sure, if this choice is based on hard mathematical derivations, or on empirics.
