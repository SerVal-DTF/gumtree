package com.github.gumtreediff.matchers.heuristic.cd;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeUtils;

import java.util.List;

public class ChangeDistillerBottomUpMatcher extends Matcher {

    public static final double STRUCT_SIM_THRESHOLD_1 = 0.6D;

    public static final double STRUCT_SIM_THRESHOLD_2 = 0.4D;

    public ChangeDistillerBottomUpMatcher(ITree src, ITree dst, MappingStore store) {
        super(src, dst, store);
    }

    @Override
    public void match() {
        List<ITree> poDst = TreeUtils.postOrder(dst);
        for (ITree src: this.src.postOrder()) {
            int l = numberOfLeafs(src);
            for (ITree dst: poDst) {
                if (src.isMatchable(dst) && !(src.isLeaf() || dst.isLeaf())) {
                    double sim = chawatheSimilarity(src, dst);
                    if ((l > 4 && sim >= STRUCT_SIM_THRESHOLD_1) || (l <= 4 && sim >= STRUCT_SIM_THRESHOLD_2)) {
                        addMapping(src, dst);
                        break;
                    }
                }
            }
        }
    }

    private int numberOfLeafs(ITree root) {
        int l = 0;
        for (ITree t : root.getDescendants())
            if (t.isLeaf())
                l++;
        return l;
    }
}
