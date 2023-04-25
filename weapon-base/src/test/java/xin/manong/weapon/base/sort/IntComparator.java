package xin.manong.weapon.base.sort;

import java.util.Comparator;

/**
 * @author frankcl
 * @date 2023-04-25 14:33:15
 */
public class IntComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer left, Integer right) {
        if (left == null && right == null) return 0;
        if (left == null) return -1;
        if (right == null) return 1;
        return left - right;
    }
}
