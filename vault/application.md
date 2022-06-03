
[Return](Block_Diagram.md)
## application ##
[Block Diagram of G_String](img/block.png)
The package 'application' contains the source ['Main.java'](../../../blob/main/workbench/GS_L/src/application/Main.java), and the associated style sheet ['application.css'](../../../blob/main/workbench/GS_L/src/application/application.css). As shown in the [Block Diagram](img/block.png), 'Main' is the central hub between the graphical user interface, and the workers AnaGroups (Analysis), SynthGroups (Synthesis), and gSetup (Setup).

Besides providing basic housekeeping functions, 'Main' houses the 'primaryStage', on which the GUI displays its scenes. Since both 'AnaGroups', which is responsible for GA analysis, and 'SynthGroups', which generates synthetic data files for simulations, operate in a stepwise fashion, 'Main' controls the stepping together with the GUI for either of the two main working objects.

The method <a href="../workbench/GS_L/src/application/Main.java#L210">'show'</a> takes a group (called '_display'), creates a 'scene' with the '_display' centered in a frame, and hands it to the GUI as <a href="../workbench/GS_L/src/application/Main.java#L220">'primaryStage.show()'</a>. The method <a href="../workbench/GS_L/src/application/Main.java#L155">'stepUp'</a> handles the stepping for 'AnaGroups', and 'SynthGroups'.

