
[Return](Structure.md)
## application ##
[Block Diagram of G_String](img/block.png)  
The package 'application' contains the source ['Main.java'](../../../blob/main/workbench/GS_L/src/application/Main.java), and the associated style sheet ['application.css'](../../../blob/main/workbench/GS_L/src/application/applivation.css). As shown in the [Block Diagram](img/block.png), 'Main' is the central hub between the graphical user interface, and the workers AnaGroups (Analysis), SynthGroups (Synthesis), and gSetup (Setup).

Besides providing basic housekeeping functions, 'Main' houses the 'primaryStage', on which the GUI displays its scenes. Since both 'AnaGroups', which is responsible for GA analysis, and 'SynthGroups', which generates synthetic data files for simulations, operate in a stepwise fashion, 'Main' controls the stepping together with the GUI for either of the two main working objects.

### Suggestion ###
Some of the links lead you directly into source code files. Some of these files are pretty large, but fortunately GitHub provides you with an automatic index. In the upper right quadrant of the screen are two marked buttons. The first says 'Go to file', the second simply say ' . . .'. When you click on these three points, you are offered a choice: 'Go to line (L)', and 'Go to definition (R)'. The first asks for a line number in the code, the second offers an (unsorted) index to the methods in the source file. Pretty handy, isn't it?

