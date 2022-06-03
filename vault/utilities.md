[Return](Block_Diagram.md)
## utilities ##
[Block Diagram of G_String](img/block.png)
Package ['utilities'](../../../tree/main/workbench/GS_L/src/utilities) contains a collection of ten smaller objects that provide specific functions to various other parts of G_String.
#### ['About.java'](../../../blob/main/workbench/GS_L/src/utilities/About.java) is a short function to display  'AboutXX.txt' files ####
#### ['CompConstrct.java'](../../../blob/main/workbench/GS_L/src/utilities/CompConstrct.java) constructs the various facet combinations for each of the configurations that has a separate variance component. It is used in ['SynthGroups.java'](../../../blob/main/workbench/GS_L/src/steps/SynthGroups.java), line 889. ####
#### ['constructSimulation,java'](../../../blob/main/workbench/GS_L/src/utilities/constructSimulation,java) assembles the synthetic data file from the random configuration components. It is used in ['SynthGroups.java'](../../../blob/main/workbench/GS_L/src/steps/SynthGroups.java), line 168. ####
#### ['FacetModView.java'](../../../blob/main/workbench/GS_L/src/utilities/FacetModView.java) provides the JavaFX snippet allowing to change the facet levels for D-Studies in  ['AnaGroups.java'](../../../blob/main/workbench/GS_L/src/steps/AnaGroups.java) in line 1151. ####
#### ['Factor.java'](../../../blob/main/workbench/GS_L/src/utilities/Factor.java) is used for synthesis. It calculates the total number of possible states, each factor making up a configuration, can assume for all possible indices of its constituent facets. It is used in ['SampleSizeTree.java'](../../../blob/main/workbench/GS_L/src/model/SampleSizeTree.java) in line 824. ####
#### ['Filer.java'](../../../blob/main/workbench/GS_L/src/utilities/Filer.java) is an I/O utility, wherever G_String performs file input or output. ####
#### ['Lehmer.java'](../../../blob/main/workbench/GS_L/src/utilities/Lehmer.java) is a small routine that signs synthetic data sets, so they can be distinguished from genuine experimental data. It is used in ['SynthGroups.java'](../../../blob/main/workbench/GS_L/src/SynthGroups.java) in line 1205. ####
#### ['Popup.java'](../../../blob/main/workbench/GS_L/src/utilities/Popup.java) handles error logging and alerts throughout GS. ####
#### ['SampleSizeView.java'](../../../blob/main/workbench/GS_L/src/utilities/SampleSizeView.java) is a specialized text field for entering sample size values. ####
#### ['VarianceComponent.java'](../../../blob/main/workbench/GS_L/src/utilities/VarianceComponent.java) uses Brennan's rules when calculating , &sigma;<sup>2</sup>(&tau;), &sigma;<sup>2</sup>(&delta;), and &sigma;<sup>2</sup>(&Delta;) .


