package timeword;

import timeword.TimeWord;

import java.util.ArrayList;
import java.util.List;

public class DelayTimeWord extends TimeWord<DelayAction> {

    public DelayTimeWord(List<DelayAction> delayActionList){
        setActionList(delayActionList);
    }

    public static DelayTimeWord getEmpty(){
        return new DelayTimeWord(new ArrayList<>());
    }


    public DelayTimeWord subWord(int fromIndex, int toIndex){
        try{
            List<DelayAction> subList = getActionList().subList(fromIndex,toIndex);
            return new DelayTimeWord(subList);
        }catch (Exception e){
            return getEmpty();
        }
    }
}
