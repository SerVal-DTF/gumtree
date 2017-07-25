package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.tree.ITree;

/**
 * Hierarchical-level results of GumTree results
 * 
 * @author kui.liu
 *
 */
public class HierarchicalActionSet implements Comparable<HierarchicalActionSet> {
	
	private String astNodeType;
	private Action action;
	private Action parentAction;
	private String actionString;
	private int startPosition;
	private int length;
	private int startLineNum;
	private int endLineNum;
	private HierarchicalActionSet parent = null;
	private List<HierarchicalActionSet>	subActions = new ArrayList<>();
	
	private ITree node;
	private SimpleTree abstractSimpleTree =  null;     // semi-source code tree. and AST node type tree
	private SimpleTree abstractIdentifierTree = null;  // abstract identifier tree
	private SimpleTree simpleTree;  				   // source code tree and AST node type tree

	public ITree getNode() {
		return node;
	}

	public void setNode(ITree node) {
		this.node = node;
	}

	public String getAstNodeType() {
		return astNodeType;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Action getParentAction() {
		return parentAction;
	}

	public void setParentAction(Action parentAction) {
		this.parentAction = parentAction;
	}

	public String getActionString() {
		return actionString;
	}

	public void setActionString(String actionString) {
		this.actionString = actionString;
		
		int atIndex = actionString.indexOf("@AT@") + 4;
		int lengthIndex = actionString.indexOf("@LENGTH@");
		if (lengthIndex == -1) {
			this.startPosition = Integer.parseInt(actionString.substring(atIndex).trim());
			this.length = 0;
		} else {
			this.startPosition = Integer.parseInt(actionString.substring(atIndex, lengthIndex).trim());
			this.length = Integer.parseInt(actionString.substring(lengthIndex + 8).trim());
		}
		
		String nodeType = actionString.substring(0, actionString.indexOf("@@"));
		nodeType = nodeType.substring(nodeType.indexOf(" ")  + 1);
		this.astNodeType = nodeType;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getLength() {
		return length;
	}

	public int getStartLineNum() {
		return startLineNum;
	}

	public void setStartLineNum(int startLineNum) {
		this.startLineNum = startLineNum;
	}

	public int getEndLineNum() {
		return endLineNum;
	}

	public void setEndLineNum(int endLineNum) {
		this.endLineNum = endLineNum;
	}

	public HierarchicalActionSet getParent() {
		return parent;
	}

	public void setParent(HierarchicalActionSet parent) {
		this.parent = parent;
	}

	public List<HierarchicalActionSet> getSubActions() {
		return subActions;
	}

	public void setSubActions(List<HierarchicalActionSet> subActions) {
		this.subActions = subActions;
	}

	public SimpleTree getAbstractSimpleTree() {
		return abstractSimpleTree;
	}

	public void setAbstractSimpleTree(SimpleTree simpleTree) {
		this.abstractSimpleTree = simpleTree;
	}

	public SimpleTree getAbstractIdentifierTree() {
		return abstractIdentifierTree;
	}

	public void setAbstractIdentifierTree(SimpleTree abstractIdentifierTree) {
		this.abstractIdentifierTree = abstractIdentifierTree;
	}

	public SimpleTree getSimpleTree() {
		return simpleTree;
	}

	public void setSimpleTree(SimpleTree rawTokenTree) {
		this.simpleTree = rawTokenTree;
	}

	@Override
	public int compareTo(HierarchicalActionSet o) {
		return this.action.compareTo(o.action);
	}
	
	private List<String> strList = new ArrayList<>();

	@Override
	public String toString() {
		String str = actionString;
		if (strList.size() == 0) {
			strList.add(str);
			for (HierarchicalActionSet actionSet : subActions) {
				actionSet.toString();
				List<String> strList1 = actionSet.strList;
				for (String str1 : strList1) {
					strList.add("----" + str1);
				}
			}
		}
		
		str = "";
		for (String str1 : strList) {
			str += str1 + "\n";
		}
		 
		return str;
	}
	
	public String toASTNodeLevelAction() {
		if (strList.size() == 0) {
			toString();
		}
		String astNodeStr = "";
		for (String str : strList) {
			astNodeStr += str.substring(0, str.indexOf("@@")) + "\n";
		}
		return astNodeStr;
	}
	
	public String toRawCodeLevelAction() {
		if (strList.size() == 0) {
			toString();
		}
		String astNodeStr = "";
		for (String str : strList) {
			str = str.substring(0, str.indexOf(" @AT@")) + "\n";
			int index1 = str.indexOf(" ") + 1;
			int index2 = str.indexOf("@@") + 2;
			astNodeStr += str.substring(0, index1) + str.substring(index2);
		}
		return astNodeStr;
	}
}
