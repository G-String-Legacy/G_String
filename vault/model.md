[Return](Block_Diagram.md)
## model ##
[Block Diagram of G_String](img/block.png)  

The package 'model' contains the three central components of a design: the ['Facet.java'](../../../blob/main/workbench/GS_L/src/model/Facet.java), the ['Nest.java'](../../../blob/main/workbench/GS_L/src/model/Nest.java), and the ['SampleSizeTree.java'](../../../blob/main/workbench/GS_L/src/model/SampleSizeTree.java). 

The names of the objects describe their functions, and details are contained in the comments of the source code. The meaning of 'Facet' is obvious in the context of Generalizability Analysis. The object 'Nest' encapsulates the details of how the facets are arranged, and various obvious parameters and methods relevant to the design. The 'SampleSizeTree' contains the values and relationships between the sample sizes of the nested facet arrangement. It also keeps track of the facet indices.

All three objects 'Facet', 'Nest', and 'SampleSizeTree' are used by both 'AnaGroups', and 'SynthGroups'.

