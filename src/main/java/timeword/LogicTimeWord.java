package timeword;

import lombok.AllArgsConstructor;
import lombok.Data;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class LogicTimeWord{
    private List<LogicAction> actionList;

    public int size(){
        return actionList.size();
    }

    public LogicAction get(int i){
        return actionList.get(i);
    }

    public boolean isEmpty(){
        return actionList.isEmpty();
    }

    public static LogicTimeWord emptyWord(){
        return new LogicTimeWord(new ArrayList<>());
    }

    public LogicTimeWord subWord(int fromIndex, int toIndex){
        try{
            List<LogicAction> subList = getActionList().subList(fromIndex,toIndex);
            return new LogicTimeWord(subList);
        }catch (Exception e){
            return emptyWord();
        }
    }
}
