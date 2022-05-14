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
<!-- $$
\sigma^{2}(\kappa) = \sum\limits_{e}\frac{B_{\kappa}(E_{e})\times V_{e}}{\prod\limits_{F_{f}\;\in\;E_{e}}A_{f}}
$$ --> 

<div align="center"><img style="background: white;" src="svg/4q6av6gncI.svg"></div>

 
</td><td> 

 </td></tr></table>