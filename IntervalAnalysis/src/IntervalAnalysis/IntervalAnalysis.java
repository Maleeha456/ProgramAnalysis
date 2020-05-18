package IntervalAnalysis;

import java.awt.Window.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import polyglot.ast.Block;
import polyglot.ast.Loop;
import polyglot.visit.FlowGraph.Edge;

import java.lang.*;
import soot.Body;
import soot.Local;
import soot.Scene;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.JastAddJ.List;
import soot.JastAddJ.ModExpr;
import soot.dava.toolkits.base.AST.transformations.SimplifyExpressions;
import soot.jimple.*;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.LoopNestTree;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;
import soot.util.Numberable;

public class IntervalAnalysis extends ForwardFlowAnalysis {

	Body b;
//	Local l;//local
	//Local c;//compare
	static int var = 0;
	static Interval[] interval123 = new Interval[20];
	FlowSet inval, outval;
	int base = -1;
	//private String string;
	Double pos_infinity = Double.POSITIVE_INFINITY;
	Double neg_infinity = Double.NEGATIVE_INFINITY;

	public IntervalAnalysis(UnitGraph g) {
		super(g);
		b = g.getBody();
		doAnalysis();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected void flowThrough(Object in, Object unit, Object out) {
		inval = (FlowSet) in;
		outval = (FlowSet) out;
		int n = b.getLocalCount();
		Stmt smt = (Stmt) unit;
		System.out.println(smt.toString());

		// loops

		if (smt instanceof AssignStmt) {
			Value operator1 = ((AssignStmt) smt).getLeftOp();
			String s1 = operator1.toString();

			Value operator2 = ((AssignStmt) smt).getRightOp();
			String s2 = operator2.toString();

			if (operator2 instanceof AddExpr || operator2 instanceof MulExpr || operator2 instanceof DivExpr || operator2 instanceof ModExpr) {
				for (int j = 0; j < var; j++) {
					if ((interval123[j].val).equals(s1)) {

						base = j;
						break;
					}
				}

				if (base != -1) {
					String neginfinity_check = interval123[base].min;
					String posinfinity_check = interval123[base].max;
					String pos_check = Double.toString(pos_infinity);
					String neg_check = Double.toString(neg_infinity);
					if (!(neginfinity_check.equals(neg_check))) {
						if (!(posinfinity_check.equals(pos_check))) {
							BinopExpr binop = (BinopExpr) operator2;
							Value Op2_leftop = binop.getOp1();
							Value Op2_rightop = binop.getOp2();
							String o_right = (Op2_rightop).toString();
							int f = Integer.parseInt(o_right);
							int var_min = Integer.parseInt(interval123[base].min);
							int var_max = Integer.parseInt(interval123[base].max);

							
							if (operator2 instanceof AddExpr) {

								int result = var_min+f;
								interval123[base].min = Integer.toString(result);
								interval123[base].max= Integer.toString(result);
							} else if (operator2 instanceof MulExpr) {
								int result = var_min * f;
								interval123[base].min = Integer.toString(result);
								interval123[base].max = Integer.toString(result);
							} else if (operator2 instanceof DivExpr) {
								int result = var_min / f;
								interval123[base].min = Integer.toString(result);
								interval123[base].max = Integer.toString(result);
							} else if (operator2 instanceof ModExpr) {

								int result = var_min % f;
								interval123[base].min = Integer.toString(result);
								interval123[base].max = Integer.toString(result);
							}

						}
					}

					
				}
			}

			else {
				interval123[var] = new Interval();
				interval123[var].val = s1;
				interval123[var].min = s2;
				interval123[var].max = s2;
				var++;
			}
		}
		LoopFinder myFinder = new LoopFinder();
		myFinder.transform(b);
		Collection<soot.jimple.toolkits.annotation.logic.Loop> myLoops = myFinder.loops();

		// loops
		if (!myLoops.isEmpty()) {
			for (soot.jimple.toolkits.annotation.logic.Loop loop : myLoops) {
				//Stmt loop_head = loop.getHead();
			//	Stmt loop_getjumpback = loop.getBackJumpStmt();
				java.util.List<Stmt> loop_stmt = loop.getLoopStatements();
				Iterator<Stmt> it = loop_stmt.iterator();
			
				
		

				while (it.hasNext()) {

					Stmt block = it.next();
					
					if (block instanceof AssignStmt) {
						Value op1 = ((AssignStmt) block).getLeftOp();
						String s = op1.toString();
						Value op2 = ((AssignStmt) block).getRightOp();
						String s1 = op2.toString();

						if (op2 instanceof AddExpr || op2 instanceof MulExpr || op2 instanceof DivExpr
								|| op2 instanceof ModExpr) {
							for (int j = 0; j < var; j++) {

								if ((interval123[j].val).equals(s)) {
									base = j;
									break;
								}
							}
							if (base != -1) {
								
								BinopExpr binop = (BinopExpr) op2;
								Value Op2_leftop = binop.getOp1();
								Value Op2_rightop = binop.getOp2();
								String o_left = (Op2_leftop).toString();
								String o_right = (Op2_rightop).toString();
							
								int f = Integer.parseInt(o_right);
								if (op2 instanceof AddExpr) {
									interval123[base].max = Double.toString(pos_infinity);
								} else if (op2 instanceof MulExpr) {
									interval123[base].max = Double.toString(pos_infinity);
								} else if (op2 instanceof DivExpr) {
									interval123[base].min = Double.toString(neg_infinity);
									
								}
							
							}
						} else {
							for (int j = 0; j < var; j++) {
								System.out.println(interval123[j].val);
								System.out.println(interval123[j].getMin());
								System.out.println(interval123[j].getMax());

								if ((interval123[j].val).equals(s)) {
									base = j;
									break;
								}
							}

							if (base != -1) {
								String val = interval123[base].min;

								int value = Integer.parseInt(val);
								int value1 = Integer.parseInt(s1);

								if (value != value1) {
									interval123[base].min = s1;
								interval123[base].max = s1;
							

								}
							} else {
								interval123[var] = new Interval();
								interval123[var].val = s;
							interval123[var].min = s1;
							interval123[var].max = s1;
							
								var++;
							}

						}

					}
				}

			}
		}

		

		else {
			if (smt instanceof AssignStmt) {
				Value op1 = ((AssignStmt) smt).getLeftOp();
				String s = op1.toString();
				Value op2 = ((AssignStmt) smt).getRightOp();
				String s1 = op2.toString();

				if (op2 instanceof AddExpr || op2 instanceof MulExpr || op2 instanceof DivExpr
						|| op2 instanceof ModExpr) {
					for (int j = 0; j < var; j++) {
						if ((interval123[j].val).equals(s)) {
							base = j;
							break;
						}
					}

					if (base != -1) {
						BinopExpr binop = (BinopExpr) op2;
						Value Op2_leftop = binop.getOp1();
						Value Op2_rightop = binop.getOp2();
						String o_left = (Op2_leftop).toString();
						String o_right = (Op2_rightop).toString();
						int f = Integer.parseInt(o_right);
						int var_min = Integer.parseInt(interval123[base].min);
						int var_max = Integer.parseInt(interval123[base].max);
						if (op2 instanceof AddExpr) {
							int result = var_min+f;
							interval123[base].min=Integer.toString(result); 
							interval123[base].max = Integer.toString(result);
						} else if (op2 instanceof MulExpr) {
							int result = var_min * f;
							interval123[base].min = Integer.toString(result);
							interval123[base].max = Integer.toString(result);
						} else if (op2 instanceof DivExpr) {
							int result = var_min / f;
							interval123[base].min = Integer.toString(result);
							interval123[base].max = Integer.toString(result);
						} else if (op2 instanceof ModExpr) {

							int result = var_min % f;
							interval123[base].min = Integer.toString(result);
							interval123[base].max = Integer.toString(result);
						}

					}
				}

				else {
					interval123[var] = new Interval();
					interval123[var].val = s;
					interval123[var].min = s1;
					interval123[var].max = s1;
			

					var++;
				}
			}
		}

		for (int m = 0; m < var; m++) {
			System.out.println("Variable:" + interval123[m].val);
			System.out.println("    minimium: " + interval123[m].min+"    maximum: "+interval123[m].max);
			//System.out.println("Var_maximum:" + interval123[m].getMax());
		}

		

	}

	@Override
	protected void copy(Object source, Object dest) {
		FlowSet srcSet = (FlowSet) source;
		FlowSet destSet = (FlowSet) dest;
		srcSet.copy(destSet);

	}

	@Override
	protected Object entryInitialFlow() {
		return new ArraySparseSet();
	}

	@Override
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inval1 = (FlowSet) in1;
		FlowSet inval2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		// May analysis
		inval1.union(inval2, outSet);

	}

	@Override
	protected Object newInitialFlow() {
		return new ArraySparseSet();
	}

}
