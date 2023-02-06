[Return](Block_Diagram.md)
## steps ##
[Block Diagram of G_String](img/block.png)  

The package 'steps' contains the three major work horses of GS_L: 
['AnaGroups.java'](../../../blob/main/workbench/GS_L/src/steps/AnaGroups.java), 
['SynthGroups.java'](../../../blob/main/workbench/GS_L/src/steps/SynthGroups.java), and ['gSetup.java'](../../../blob/main/workbench/GS_L/src/gSetup.java).

['AnaGroups'](AnaBird.md) and ['SynthGroups'](SynBird.md) share an identical overall structure. They are based on one big 'switch case: iStep' ladder (<a href="../workbench/GS_L/src/steps/AnaGroups.java#L147">code</a>). The first serves analysis, the second generates synthetic data files. Both use the same graphical input strategy for collecting the design information.

On the other hand, 'gSetup' is a little runt, mainly used for the init setup.
