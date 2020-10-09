package timeword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetLogicTimeWord {
    private List<ResetLogicAction> actionList;

    public int size(){
        return actionList.size();
    }

    public ResetLogicAction get(int i){
        return actionList.get(i);
    }

    public static ResetLogicTimeWord emptyWord(){
        return new ResetLogicTimeWord(new ArrayList<>());
    }

    public boolean isEmpty(){
        return actionList.isEmpty();
    }

    public ResetLogicTimeWord subWord(int fromIndex, int toIndex){
        try{
            List<ResetLogicAction> subList = getActionList().subList(fromIndex,toIndex);
            return new ResetLogicTimeWord(subList);
        }catch (Exception e){
            return emptyWord();
        }
    }

    public ResetLogicAction getLastAction(){
        return actionList.get(size()-1);
    }

    public boolean isReset(){
        return actionList.get(size()-1).isReset();
    }

    @Override
    public String toString(){
        if (isEmpty()){
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        for (ResetLogicAction resetLogicAction: actionList){
            sb.append(resetLogicAction);
        }
        return sb.toString();
    }
}
