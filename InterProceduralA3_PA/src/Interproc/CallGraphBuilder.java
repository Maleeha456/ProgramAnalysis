package Interproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

public class CallGraphBuilder
{	
	Stack<SootMethod> callstack=new Stack<SootMethod>();
	
	public static void main(String[] args) {
		final String classname="Interproc.Sample_test";
	   List<String> argsList = new ArrayList<String>(Arrays.asList(args));
	   argsList.addAll(Arrays.asList(new String[]{
			   "-w","-no-bodies-for-excluded", //-w enables the whole program mode
			   "-main-class",
			   classname,//main-class
			   classname//argument classes
	   }));
	

	   PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", new SceneTransformer() {

		@Override
		protected void internalTransform(String phaseName, Map options) {
		       CHATransformer.v().transform();
               SootClass a = Scene.v().getSootClass(classname);
               SootMethod src = Scene.v().getMainClass().getMethodByName("main");
		       CallGraph cg = Scene.v().getCallGraph();
		       System.out.println(cg);
		       System.out.println((new CallGraphBuilder()).obtaincallgraph(cg,src));
		       
		}
		   
	   }));

           args = argsList.toArray(new String[0]);
           Options.v().set_output_format(Options.output_format_jimple);
           soot.Main.main(args);
	}
	
	public Stack<SootMethod> obtaincallgraph(CallGraph cg, SootMethod src)
	{
		// System.out.println("src: "+src);
		 Iterator<MethodOrMethodContext> targets = new Targets(cg.edgesOutOf(src));
 
	 	if(!targets.hasNext())
			 return callstack;
	       while (targets.hasNext()) {
	    	  
	           SootMethod tgt = (SootMethod)targets.next();
	          // System.out.println(src + " may call from while " + tgt.getName());
	           if(!tgt.isJavaLibraryMethod())
	           {
	        	  if(callstack.contains(tgt))
	        		   callstack.remove(tgt);

	        	  callstack.add(tgt);
	        	  obtaincallgraph(cg,tgt);}
	           }
		  
	       return callstack;
		 
	}
}
