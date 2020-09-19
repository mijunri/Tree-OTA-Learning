package timeword;

import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.List;

public class LogicTimeWord extends TimeWord<LogicAction> {

    public LogicTimeWord(List<LogicAction> logicActionList){
        setActionList(logicActionList);
    }

    public static LogicTimeWord getEmpty(){
        return new LogicTimeWord(new ArrayList<>());
    }


    public LogicTimeWord subWord(int fromIndex, int toIndex){
        try{
            List<LogicAction> subList = getActionList().subList(fromIndex,toIndex);
            return new LogicTimeWord(subList);
        }catch (Exception e){
            return getEmpty();
        }
    }
}
