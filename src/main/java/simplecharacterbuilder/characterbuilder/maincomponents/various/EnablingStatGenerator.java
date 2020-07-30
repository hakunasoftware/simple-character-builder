package simplecharacterbuilder.characterbuilder.maincomponents.various;

import simplecharacterbuilder.characterbuilder.util.holder.ApplicationFrameHolder;
import simplecharacterbuilder.common.statgenerator.StatGenerator;

public class EnablingStatGenerator extends StatGenerator {
	
	public EnablingStatGenerator() {
		super(false);
	}
	
	@Override
	public void enable() {
		super.enable();
		ApplicationFrameHolder.setApplicationFrameTitle("Stats");
	}
	
}
