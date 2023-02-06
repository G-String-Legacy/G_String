[Return](professionals.md)
## Calculating Coefficients ##
#### Continuing with the previous example: ####
<table><tr><td width = "40%"><img src="img/VCOR.png"></td><td>
Let us start by defining a few variables, we are going to need for our calculations. We have 14 different effects in this example. <UL>
<LI>'E<sub>e</sub>' will stand for an effect, where 'e' is an index 0..13. </LI>
<LI>'V<sub>e</sub>' stands for the corresponding corrected variance components 'VCOR' in the table.</LI>
<LI>'f' stands as index for the five facets F<sub>f</sub> (f = 0..4).</LI>
<LI>B<sub>&kappa;</sub>(E) is a bit like a boolean, it takes on the values 0 or 1 depending on the subscript &kappa; (to be defined below), and the facets within E.</LI>
<LI>A<sub>f</sub> is the 'average' sample size for F<sub>f</sub> (to be defined below).</LI>
</UL>
</td></tr></table>
<table><tr><td width = "60%">
We classify facets thus:<ul>
<li>'facet of differentiation (d)':  the primary subject of interest;</li>
<li>'facets of generalization (g)':  components of the measurement process;</li>
<li>'facets of stratification (s)':  categories of primary subjects.</li></ul><br>
<b>Brennan's Rules</b>
(<a href="../workbench/GS_L/src/utilities/VarianceComponent.java#L162">code</a>) for B<sub>&kappa;</sub>(E) state, that if effect E contains:<ul>
<li>a 'd', but no 'g' facet, B<sub>&tau;</sub>(E) = 1, otherwise 0;</li>
<li>a 'd', and at least one 'g' facet, B<sub>&delta;</sub>(E) = 1, otherwise 0;</li>
<li>at least one 'g' facet, B<sub>&Delta;</sub>(E) = 1, otherwise 0.</li></ul><br>
<b>Brennan's Rules</b> for A<sub>f</sub> state that if the facet F<sub>f</sub> is:<ul>
<li>a 'd' or 's' facet, A<sub>f</sub> = 1.0;</li>
<li>a crossed 'g' facet, A<sub>f</sub> = the sample size of that facet;</li>
<li>a 'g' facet, nested once, A<sub>f</sub> = the arithmetic mean of sample sizes;</li>
<li>a 'g' facet, nested deeper, A<sub>f</sub> = the harmonic mean of sample sizes;</li></ul>
</td><td> (see Brennan, Generalizability Theory, 2001; p. 122)<br><br>
<!-- $$
\sigma^{2}(\kappa) = \sum\limits_{e}\frac{B_{\kappa}(E_{e})\times V_{e}}{\prod\limits_{F_{f}\;\in\;E_{e}}A_{f}}
$$ --> 

<div align="center"><img style="background: white;" src="svg/4q6av6gncI.svg"></div>
Where &kappa; can be one of &tau;, &delta;, or &Delta;<br><br>
<b>Generalizability coefficient</b?>:<br>
<!-- $$
E\rho^2 = \frac{\sigma^2(\tau)}{\sigma^2(\tau)+ \sigma^2(\delta)}
$$ --> 

<div align="center"><img style="background: white;" src="svg/nqRDQ7ZE6L.svg"></div>
<b>Index of dependability</b>:
<!-- $$
\Phi = \frac{\sigma^2(\tau)}{\sigma^2(\tau)+ \sigma^2(\Delta)}
$$ --> 
<div align="center"><img style="background: white;" src="svg/WwGt6gruxi.svg"></div>
<br> </b><a href="../workbench/GS_L/src/model/Nest.java#L566">(code)</a>

 </td></tr></table>
 
[Next](D_Study.md)
