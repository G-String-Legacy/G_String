## Synthesis ##
The synthetic data set is constructed by a [Monte Carlo process](../../workbench/GS_L/src/utilities/constructSimulation.java). Each Effect can take on s<sub>e</sub> values, i.e, the number of its States. It consists of a different set of Facets, which in turn can be in s<sub>f</sub> different States. We identify a specific State of a Facet f by its index i<sub>f</sub>, and the set of Facet indices corresponding to an Effect e as <!-- $\hat{I}_{e}$ --> <img style="transform: translateY(0.1em); background: white;" src="svg/4agd4v6Vjd.svg">. We designate the total set of all Facet indices at a given point as <!-- $\hat{I}$ --> <img style="transform: translateY(0.1em); background: white;" src="svg/H9g2ThJd1Q.svg">.  But each set $\hat{I}_{e}$ can actually be uniquely mapped to an  integer 0 < i<sub>e</sub> < s<sub>e</sub>. This mapping is based on the order and nesting of the facets, since the the sequence of facet indices is not random, but also corresponds to the sequence of scores in the data file ([code]()).

Next, for each Effect e we generate $s_{e}$ randomly distributed values $\epsilon_{e}(i_{e})$ with a defined variance $\sigma^{2}_{e}$, and a mean of 0 ([code]()). The complete set of indices <!-- $\hat{I}_{e}$ --> <img style="transform: translateY(0.1em); background: white;" src="svg/aN8BeynKWE.svg"> too can be mapped to the integer index i<sub>l</sub> of the last Effect containing all Facets.

With this terminology we can then calculate the simulated score S as:

<!-- $$S(i_{l}) = \mu + \sum_{e}\epsilon_{e}(i_{e})$$ --> 

<div align="center"><img style="background: white;" src="svg/co8OMHEv2T.svg"></div>

where &mu; is the specified grand mean of all the scores ([code]()).
