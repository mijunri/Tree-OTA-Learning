package timeword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetDelayTimeWord {

    private List<ResetDelayAction> actionList;


    public int size(){
        return actionList.size();
    }

    public ResetDelayAction get(int i){
        return actionList.get(i);
    }

    public static ResetDelayTimeWord emptyWord(){
        return new ResetDelayTimeWord(new ArrayList<>());
    }

    public boolean isEmpty(){
        return actionList.isEmpty();
    }

    public ResetDelayTimeWord subWord(int fromIndex, int toIndex){
        try{
            List<ResetDelayAction> subList = getActionList().subList(fromIndex,toIndex);
            return new ResetDelayTimeWord(subList);
        }catch (Exception e){
            return emptyWord();
        }
    }
}
