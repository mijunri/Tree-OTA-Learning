package timeword;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DelayTimeWord{

    private List<DelayAction> actionList;

    public int size(){
        return actionList.size();
    }

    public DelayAction get(int i){
        return actionList.get(i);
    }

    public static DelayTimeWord emptyWord(){
        return new DelayTimeWord(new ArrayList<>());
    }

    public boolean isEmpty(){
        return actionList.isEmpty();
    }

    public DelayTimeWord subWord(int fromIndex, int toIndex){
        try{
            List<DelayAction> subList = getActionList().subList(fromIndex,toIndex);
            return new DelayTimeWord(subList);
        }catch (Exception e){
            return emptyWord();
        }
    }
}
