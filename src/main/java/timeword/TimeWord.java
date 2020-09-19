package timeword;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeWord<T extends Action> {

    private List<T> actionList;

    public int size(){
        return actionList.size();
    }

    public T get(int i){
        return actionList.get(i);
    }

    public boolean isEmpty(){
        return actionList.isEmpty();
    }

    @Override
    public String toString(){
        if(isEmpty()){
            return "empty";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < size(); i ++){
            stringBuilder.append(get(i));
        }
        return stringBuilder.toString();
    }

}

