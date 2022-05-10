[Return](professionals.md)
## Analysis, the Bird's Eye View ##
<TABLE>
	<TR>
		<TD width = "50%">
			<img src = "img/AnaBird.png" style="width:350px;height:600px;">
		</TD>
		<TD width = "50%">
			This Bird's eye view is important for IT professionals, in order to
			understand the code.</BR></BR>
			The first part of the step-by-Step workflow is straightforward.
			It eventually leads to GS creating two files, a control file '~control.txt',
			and a data file '~data.txt', both of which are placed into the working directory,
			where urGENOVA is activated to produce an output file '~control.txt.lis'.</BR></BR>
			GS picks up the urGENOVA output file, copies the content to its own collective
			output, and interprets it. Specifically, it extracts the configurations and values of the
			individual variance components, and uses them o calculate &sigma;<sup>2</sup>(&tau;),
			&sigma;<sup>2</sup>(&delta;), and &sigma;<sup>2</sup>(&Delta;).</BR></BR>
			From these it calculates &Phi;, and E&rho;<sup>2</sup>, the generalizability coefficients.
</TABLE>
