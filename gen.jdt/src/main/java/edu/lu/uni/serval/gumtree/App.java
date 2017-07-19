package edu.lu.uni.serval.gumtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.lu.uni.serval.gumtree.utils.CUCreator;
import edu.lu.uni.serval.gumtree.utils.FileHelper;
import edu.lu.uni.serval.gumtree.regroup.SimplifyTree;
import edu.lu.uni.serval.gumtree.regroup.ActionFilter;
import edu.lu.uni.serval.gumtree.regroup.HierarchicalActionSet;
import edu.lu.uni.serval.gumtree.regroup.SimpleTree;
import edu.lu.uni.serval.gumtree.regroup.Traveler;

public class App {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		/**
		 * Notes:
		 * Position of actions:
		 * DEL, UPD, MOV: the positions of changed source code are the positions of these source code in previous java file.
		 * INS: the positions of changed source code is the position of the source code in revised java file.
		 */
//		String a = "int a = 0;if (!isSymlink(file)) { size = size.add(BigInteger.valueOf(sizeOf(file)));}";
//		String b = "int a = 1;if (!isSymlink(file)) { size = size.add(BigInteger.valueOf(sizeOf(file)));}";
//		List<HierarchicalActionSet> gumTreeResults = compareTwoFilesWithGumTree(a, b);
//		for (HierarchicalActionSet str : gumTreeResults) {
//			System.out.println(str);
//		}
		/**
		 * TODO list:
		 * 1. Separate the modification by elements of Statements.
		 * 2. AST node sequence: AST node1 --> AST node2 --> AST node3 ...
		 * 3. Pure raw token Source code Tree --> Semi-abstract/pseudo-code tree --> Pure AST node type tree. (DONE)
		 * 4. Modified/Changed Expressions.
		 * 
		 * Ignore the modification of StringLiteral, CharacterLiteral, and serial number (DONE)
		 * Abstract ITree: 
		 * 		Iterate the tree, identify the label is final node or not.
		 * 		UPD node, but this node is not final node and without sub actions. ignore it .
		 * 
		 * 
		 * 
		 * Reduce Interference for Deep Learning:  only UPD ACTIONS?
		 * UPD ACTION: 1 line ==> FieldDeclaration, Statement ==> FieldDeclaration: element & expression, Statement: expression.
		 * INS ACTION: if of try statement.
		 * MOV ACTION:  
		 * DEL ACTION:
		 */
		String testDataPath = "Dataset/commons-io/";
		File revisedFileFolder = new File(testDataPath + "revFiles/");
		File[] revisedFiles = revisedFileFolder.listFiles();
		
		FileHelper.deleteDirectory("OUTPUT/GumTreeResults_Exp/");
		FileHelper.deleteDirectory("OUTPUT/GumTreeResults_Exp_ASTNode/");
		FileHelper.deleteDirectory("OUTPUT/GumTreeResults_Exp_RawCode/");
		
		System.out.println(revisedFiles.length);
		int a = 0, b = 0, c= 0;
		for (File revisedFile : revisedFiles) {
			if (revisedFile.toString().endsWith(".java")) {
				String revisedFileName = revisedFile.getName();
				File previousFile = new File(testDataPath + "prevFiles/prev_" + revisedFileName);
				
				List<HierarchicalActionSet> gumTreeResults = new GumTreeComparer().compareTwoFilesWithGumTree(previousFile, revisedFile);
				if (gumTreeResults.size() > 0) a ++;
				// Filter out modified actions of changing method names, method parameters, variable names and field names in declaration part.
				gumTreeResults = new ActionFilter().filterOutUselessActions(gumTreeResults);
				
				if (gumTreeResults.size() > 0) {
					File diffentryFile = new File(testDataPath + "DiffEntries/" + revisedFileName.replace(".java", ".txt"));
		            StringBuilder builder = new StringBuilder();
		            builder.append(FileHelper.readFile(diffentryFile) + "\n");
		            StringBuilder astNodeBuilder = new StringBuilder();
		            astNodeBuilder.append(FileHelper.readFile(diffentryFile) + "\n");
		            StringBuilder rawCodeBuilder = new StringBuilder();
		            rawCodeBuilder.append(FileHelper.readFile(diffentryFile) + "\n");
		            
		            CUCreator cuCreator = new CUCreator();
		            CompilationUnit prevUnit = cuCreator.createCompilationUnit(previousFile);
		            CompilationUnit revUnit = cuCreator.createCompilationUnit(revisedFile);
		            
		            b ++;
		            for (HierarchicalActionSet gumTreeResult : gumTreeResults) {
		            	c ++;
		            	// set line numbers
		            	String actionStr = gumTreeResult.getActionString();
		            	CompilationUnit unit;
		            	if (actionStr.startsWith("INS")) {
		            		unit = revUnit;
		            	} else {
		            		unit = prevUnit;
		            	}
	            		int position = gumTreeResult.getStartPosition();
	            		int startLineNum = unit.getLineNumber(position);
	            		int endLineNum = unit.getLineNumber(position + gumTreeResult.getLength());
	            		if (startLineNum != endLineNum) { // only single buggy line statements.
	            			continue;
	            		}
	            		c ++;
	            		gumTreeResult.setStartLineNum(startLineNum);
	            		gumTreeResult.setEndLineNum(endLineNum);
		            	
	            		// convert the ITree of buggy code to a simple tree.
	            		SimplifyTree abstractIdentifier = new SimplifyTree();
	            		abstractIdentifier.abstractTree(gumTreeResult);
	            		SimpleTree simpleTree = gumTreeResult.getSimpleTree();
	            		SimpleTree abstractSimpleTree = gumTreeResult.getAbstractSimpleTree();
	            		clearITree(gumTreeResult); 
	            		
	            		// output Regrouped GumTree results
	            		builder.append("@@ " + gumTreeResult.getStartLineNum() + " -- " + gumTreeResult.getEndLineNum() + "\n");
		            	builder.append((simpleTree == null ? "null" : simpleTree.toString()) + "\n");
		            	builder.append((abstractSimpleTree == null ?"null" : abstractSimpleTree.toString())+ "\n");
		            	builder.append(gumTreeResult + "\n");
		            	
		            	astNodeBuilder.append(gumTreeResult.toASTNodeLevelAction() + "\n");
		            	Traveler traveller = new Traveler();
		            	traveller.travelActionSetDeepFirstToASTNodeQueue(gumTreeResult, null);
		            	List<List<String>> list = traveller.list;
		            	for (List<String> l : list) {
		            		astNodeBuilder.append(l.toString() + "\n");
		            	}
		            	astNodeBuilder.append("\n");
		            	
		            	rawCodeBuilder.append(gumTreeResult.toRawCodeLevelAction() + "\n");
		            	
		            	// select edit scripts for deep learning.
		            	// First level: AST node type.
		            	String astEditScripts = getASTEditScripts(gumTreeResult);
		            	System.out.println(astEditScripts);
		            	// source code
		            	// abstract identifiers
		            	// semi-source code.
		            }
		            
		            FileHelper.outputToFile("OUTPUT/GumTreeResults_Exp/" + revisedFileName.replace(".java", ".txt"), builder, false);
		            FileHelper.outputToFile("OUTPUT/GumTreeResults_Exp_ASTNode/" + revisedFileName.replace(".java", ".txt"), astNodeBuilder, false);
		            FileHelper.outputToFile("OUTPUT/GumTreeResults_Exp_RawCode/" + revisedFileName.replace(".java", ".txt"), rawCodeBuilder, false);
				}
			}
		}
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
	}

	private static String getASTEditScripts(HierarchicalActionSet actionSet) {
		String editScript = "";
		
		List<HierarchicalActionSet> actionSets = new ArrayList<>();
		actionSets.add(actionSet);
		while (actionSets.size() != 0) {
			List<HierarchicalActionSet> subSets = new ArrayList<>();
			for (HierarchicalActionSet set : actionSets) {
				subSets.addAll(set.getSubActions());
				String actionStr = set.getActionString();
				int index = actionStr.indexOf("@@");
				String singleEdit = actionStr.substring(0, index).replace(" ", "");
				if (singleEdit.endsWith("SimpleName")) {
					actionStr = actionStr.substring(index + 2);
					if (actionStr.startsWith("MethodName")) {
						singleEdit = singleEdit.replace("SimpleName", "MethodName");
					} else {
						if (actionStr.startsWith("Name")) {
							actionStr = actionStr.substring(5, 6);
							if (!actionStr.equals(actionStr.toLowerCase())) {
								singleEdit = singleEdit.replace("SimpleName", "Name");
							} else {
								singleEdit = singleEdit.replace("SimpleName", "Variable");
							}
						} else {
							singleEdit = singleEdit.replace("SimpleName", "Variable");
						}
					}
				}
				editScript += singleEdit + " ";
			}
			actionSets.clear();
			actionSets.addAll(subSets);
		}
		return editScript;
	}

	private static void clearITree(HierarchicalActionSet actionSet) {
		actionSet.getAction().setNode(null);
		for (HierarchicalActionSet subActionSet : actionSet.getSubActions()) {
			clearITree(subActionSet);
		}
	}

	@SuppressWarnings("unused")
	private static void addToMap(String stmtType, HierarchicalActionSet gumTreeResult,
			Map<String, List<HierarchicalActionSet>> actionSetMap) {
		if (actionSetMap.containsKey(stmtType)) {
			actionSetMap.get(stmtType).add(gumTreeResult);
		} else {
			List<HierarchicalActionSet> actionSets = new ArrayList<>();
			actionSets.add(gumTreeResult);
			actionSetMap.put(stmtType, actionSets);
		}
	}

	@SuppressWarnings("unused")
	private static String readStatementType(String actionString) {
		String stmtType = actionString.substring(0, actionString.indexOf("@@"));
		stmtType = stmtType.substring(stmtType.indexOf(" ")  + 1);
		return stmtType;
	}

}
