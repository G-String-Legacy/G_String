package utilities;

import model.Nest;

public class process {

	// provides step specific processing routines

	private Nest myNest;

	public process(Nest _nest)
	{
		myNest = _nest;
	}

	public void getStep(Boolean bDown)
	{
		if(bDown)
			myNest.decrementSteps();
		else
			myNest.incrementSteps();
	}
}
