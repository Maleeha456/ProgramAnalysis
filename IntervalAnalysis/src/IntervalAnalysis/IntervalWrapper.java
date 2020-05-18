package IntervalAnalysis;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.SootMethod;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;

public class IntervalWrapper extends BodyTransformer{

	@Override
	protected void internalTransform(Body body, String phase, Map options) {
		// TODO Auto-generated method stub
		SootMethod sootMethod = body.getMethod();	
		UnitGraph g = new BriefUnitGraph(sootMethod.getActiveBody());
		IntervalAnalysis reach = new IntervalAnalysis(g);
	}

}
