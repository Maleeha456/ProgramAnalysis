package Interproc;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import soot.*;
import soot.options.*;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.baf.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;

/**
 * 
 * @author Devika
 *
 */
public class InterprocAnalysis {
	/*
	 * Note: You may want to change the structure of how the method summary is stored
	 * HashMap<String, Boolean> may not suffice for sign flow analysis
	 */
	
	static HashMap<String, Boolean> summary=new HashMap<String,Boolean> ();
    
	public static void main(String[] args) {
		final String classname=args[0]; //class whose main method would be analysed
    	Options.v().set_keep_line_number(true);
    	Options.v().setPhaseOption("jb", "use-original-names:true");
    	List<String> argsList = new ArrayList<String>(Arrays.asList(args));
 	   argsList.addAll(Arrays.asList(new String[]{
 			   "-w","-no-bodies-for-excluded",
 			   "-main-class",
 			   classname,//main-class
 			   classname//argument classes
 	   }));
 	
//To perform transformation on whole application(interprocedural)
 	   PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {
 		  @Override
 			protected void internalTransform(String phaseName, Map options) {
 			       CHATransformer.v().transform();
 	               SootClass a = Scene.v().getSootClass(classname);
 	               SootMethod src = Scene.v().getMainClass().getMethodByName("main");
 			       CallGraph cg = Scene.v().getCallGraph();
 			       Stack<SootMethod> stack=new CallGraphBuilder().obtaincallgraph(cg,src);		
 			       new InterprocAnalysis().buildsummary(stack);
 			       
 			}
 			   
 		   }));

 	//To perform transformation on method(intraprocedural)    
 	    PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter", new BodyTransformer() {
		protected void internalTransform(Body body, String phase, Map options) {
			
			
			if(body.getMethod().getName().equals("main"))
			{
				Analysis analysis = new Analysis((new ExceptionalUnitGraph(body)),summary);
				
				/*
				 * add logic to fetch final results from 
				 * analysis run on main method
				 */
				
				
			}
			
		}
	    }));
 	    
 	args = argsList.toArray(new String[0]);
	Options.v().set_output_format(Options.output_format_jimple);
	soot.Main.main(args);
    }

    HashMap<String,Boolean> buildsummary(Stack<SootMethod> stack)
    {
    	
    	
    	while(!stack.isEmpty()) 
    	{
    		   SootMethod sm=stack.pop();
    		   Analysis analysis = new Analysis((new ExceptionalUnitGraph(sm.getActiveBody())),summary);
    		   /*
    		    * add logic to compute summary for every invoked method
    		    */
    	}
    	return summary;
    	
    }


}

class Analysis extends ForwardFlowAnalysis<Unit, Set<Value>>  {
	Body b;
	public Map<Unit, Set<Set<Value>>> sink;
    public Map<Tag, Boolean> lines;
    HashMap<String, Boolean> summary;
        
    public Analysis(UnitGraph graph, HashMap<String, Boolean> methodsummary) {
	
	    super(graph);
	    b=graph.getBody();
		sink = new HashMap<Unit, Set<Set<Value>>>();
		lines=new HashMap<Tag,Boolean> ();
		summary=methodsummary;
		doAnalysis();
    }
    /**
     * Define the analysis specific methods- merge, flowthrough etc. 
     */
	@Override
	protected void flowThrough(Set<Value> in, Unit d, Set<Value> out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Set<Value> newInitialFlow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Set<Value> entryInitialFlow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void merge(Set<Value> in1, Set<Value> in2, Set<Value> out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void copy(Set<Value> source, Set<Value> dest) {
		// TODO Auto-generated method stub
		
	}


}