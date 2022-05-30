[Return](What_is_G_String.md)

## Step by Step Workflow ##
Software professionals like to endow their products with all the functional bells and whistles they can think of. That is great for the expert user. But occasional users frequently get confused, if there are too many different ways of solving a problem. G_String is used for three purposes: 

<ol type="1">
  <li>analyzing experimental data (scores) from a performance assessment to determine its reliability; </li>
  <li>D-Studies in planning further studies;
  <li>generating a synthetic set of scores for teaching, learning, or experimenting. </li>
</ol>

Each of these three goals can be achieved in one of two ways:

<ol type="a">
  <li>for a totally new problem, parameters are entered manually;</li>
  <li>for a recurring problem a script with manual override can relieve the tedium.</li>
</ol>

Consequently, G_String has two main work horses - [AnaGroups.java](../../../blob/main/workbench/GS_L/src/steps/AnaGroups.java) for analysis, and [SynthGroups.java](../../../blob/main/workbench/GS_L/src/steps/SynthGroups.java) for synthesis. Both are constructed similarly, enforcing a step-by-step workflow that leads to the desired result. The method to look for in either is called '**getGroup**'.

The code of 'getGroup' is straight forward:
```
switch (iStep) {

	case 0: . . . . .;
	case 1: Do something;
		return 'a group';    // JavaFX code defining a screen
	case 2; . . . . .;
	.
	.
	default: whatever;
}
```

The stepping is controlled by method 'stepUp' in [Main](../../../blob/main/workbench/GS_L/src/application/Main.java). The groups go back to Main, where they are passed by method 'show' to the GUI. When the user has entered the responses appropriate to the step, he or she clicks on 'Next', signaling to 'stepUp' in 'Main' to proceed to the next step.

If the user has selected to use a script, 'startUp ' will set a switch ''getDoOver'. In step 1 'getGroup' , method 'setTitle' checks that switch, which would be 'true' in that case. At each step then,  'getGroup' first will fill in the data from the script, before handing the screen to the user for potential changes.

