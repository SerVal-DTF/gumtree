package edu.lu.uni.serval.gumtree.regroup;

import java.util.ArrayList;
import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Addition;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.tree.ITree;

import edu.lu.uni.serval.gumtree.utils.ASTNodeMap;
import edu.lu.uni.serval.utils.ListSorter;

/**
 * Regroup GumTree results to a hierarchical construction.
 * 
 * @author kui.liu
 *
 */
public class HierarchicalRegrouper {
	
	public static List<HierarchicalActionSet> regroupGumTreeResults(List<Action> actionsArgu) {
		/*
		 * First, sort actions by their positions.
		 */
		List<Action> actions = new ListSorter<Action>(actionsArgu).sortAscending();
		if (actions == null) {
			actions = actionsArgu;
		}
		
		/*
		 * Second, group actions by their positions.
		 */
		List<HierarchicalActionSet> actionSets = new ArrayList<>();
		HierarchicalActionSet actionSet = null;
		for(Action act : actions){
			Action parentAct = findParentAction(act, actions);
			if (parentAct == null) {
				actionSet = createActionSet(act, parentAct);
				actionSets.add(actionSet);
			} else {
				if (!addToAactionSet(act, parentAct, actionSets)) {
					// The index of the parent action in the actions' list is larger than the index of this action.
					actionSet = createActionSet(act, parentAct);
					actionSets.add(actionSet);
				}
			}
		}
		
		/*
		 * Third, add the subActionSet to its parent ActionSet.
		 */
		List<HierarchicalActionSet> reActionSets = new ArrayList<>();
		for (HierarchicalActionSet actSet : actionSets) {
			Action parentAct = actSet.getParentAction();
			if (parentAct != null) {
				addToActionSets(actSet, parentAct, actionSets);
			} else {
				reActionSets.add(actSet);
			}
		}
		return reActionSets;
	}

	private static HierarchicalActionSet createActionSet(Action act, Action parentAct) {
		HierarchicalActionSet actionSet = new HierarchicalActionSet();
		actionSet.setAction(act);
		actionSet.setActionString(parseAction(act.toString()));
		actionSet.setParentAction(parentAct);
		actionSet.setNode(act.getNode());
		actionSet.setParent(null);
		return actionSet;
	}

	private static String parseAction(String actStr1) {
		// UPD 25@@!a from !a to isTrue(a) at 69
		String[] actStrArrays = actStr1.split("@@");
		String actStr = "";
		int length = actStrArrays.length;
		for (int i =0; i < length - 1; i ++) {
			String actStrFrag = actStrArrays[i];
			int index = actStrFrag.lastIndexOf(" ") + 1;
			String nodeType = actStrFrag.substring(index);
			if (!"".equals(nodeType)) {
				try {
					nodeType = ASTNodeMap.map.get(Integer.parseInt(nodeType));
				} catch (NumberFormatException e) {
					nodeType = actStrFrag.substring(index);
				}
			}
			actStrFrag = actStrFrag.substring(0, index) + nodeType + "@@";
			actStr += actStrFrag;
		}
		actStr += actStrArrays[length - 1];
		return actStr;
	}
	
	private static void addToActionSets(HierarchicalActionSet actionSet, Action parentAct, List<HierarchicalActionSet> actionSets) {
		for (HierarchicalActionSet actSet : actionSets) {
			if (actSet.equals(actionSet)) continue;
			if (actSet.getAction().equals(parentAct)) { // actSet is the parent of actionSet.
				actionSet.setParent(actSet);
				actSet.getSubActions().add(actionSet);
				ListSorter<HierarchicalActionSet> sorter = new ListSorter<HierarchicalActionSet>(actSet.getSubActions());
				actSet.setSubActions(sorter.sortAscending());
				break;
			} else {
				addToActionSets(actionSet, parentAct, actSet.getSubActions());
			}
		}
	}

	private static boolean addToAactionSet(Action act, Action parentAct, List<HierarchicalActionSet> actionSets) {
		ITree parentTree = parentAct.getNode();
		
		for(HierarchicalActionSet actionSet : actionSets) {
			Action action = actionSet.getAction();
			
			ITree tree = action.getNode();
			if (tree.equals(parentTree)) { // actionSet is the parent of actSet.
				if (action instanceof Move && !(act instanceof Move)) {
					continue;
				}
				
				HierarchicalActionSet actSet = createActionSet(act, actionSet.getAction());
				actSet.setParent(actionSet);
				actionSet.getSubActions().add(actSet);
				return true;
			} else {
				if ((!(act instanceof Insert) && !(action instanceof Insert)) 
						|| (act instanceof Insert && action instanceof Insert)) {
					int startPosition = act.getPosition();
					int length = act.getLength();
					int startP = action.getPosition();
					int leng = action.getLength();
					if (!(startP <= startPosition) || !(length <= leng)) {
						continue;
					} else if (startP > startPosition + length) {
						break;
					}
				}
				List<HierarchicalActionSet> subActionSets = actionSet.getSubActions();
				if (subActionSets.size() > 0) {
					boolean added = addToAactionSet(act, parentAct, subActionSets);
					if (added) {
						return true;
					} else {
						continue;
					}
				}
			}
		}
		return false;
	}

	private static Action findParentAction(Action action, List<Action> actions) {
		
		ITree parent = action.getNode().getParent();
		if (action instanceof Addition) {
			parent = ((Addition) action).getParent();
		}
		for (Action act : actions) {
			if (act.getNode().equals(parent)) {
				if (act instanceof Move && !(action instanceof Move)) {
					continue;
				}
				return act;
			}
		}
		return null;
	}
}
