[Return](professionals.md)
## Variance Components in Analysis ##
<table><tr><td width = "50%"><img src="img/ControlInput.png" style="width:350px;height:280px;"></td><td>
For Analysis, the step-by-step workflow leads the user to enter all the data required by Brennan's 'urGENOVA', and formats them in a specic way. We are using here a virtual example, based on a design by Le Quoc Bao from the Center for Advanced Training in Clinical Simulation, University of Medicine and Pharmacy at Ho Chi Minh City, Vietnam<br>
It first sets a header, followed by a list of comments including the facet names, and their designations.<br>
Next it specifies some default urGENOVA parameters, which can actually be altered in G_String's preferences.<br>This is followed by a list of 'EFFECTS', the nesting pattern of the experimental design.<br>Finally, it tells 'urGENOVA' the name of the data file.
</td></tr></table>
<br>
<table><tr>
<td width = "40%">Based on the above control input and the data, 'urGENOVA' generates, among other output, an ANOVA table of estimated variance coefficients for each of the possible direct and crossed 'EFFECTs'.</td><td><img src="img/ANOVA_Table.png"></td></tr></table>