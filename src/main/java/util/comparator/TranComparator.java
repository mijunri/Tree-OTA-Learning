package util.comparator;

import ota.Transition;

import java.util.Comparator;

public class TranComparator implements Comparator<Transition> {

    @Override
    public int compare(Transition o1, Transition o2) {
        int var1 = o1.getSourceId() - o2.getSourceId();
        if(var1 != 0){
            return var1;
        }
        int var2 = o1.getSymbol().compareTo(o2.getSymbol());
        if(var2 != 0){
            return var2;
        }
        int var3 = o1.getLowerBound() - o2.getLowerBound();
        if(var3 !=0){
            return var3;
        }
        int var4 = o1.getUpperBound() - o2.getUpperBound();
        if(var4 != 0){
            return var4;
        }
        return -1;
    }
}
