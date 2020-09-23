package tree;


import lombok.AllArgsConstructor;
import lombok.Data;
import timeword.LogicAction;
import timeword.LogicTimeWord;

@Data
@AllArgsConstructor
public class Track {

    private LogicTimeWord source;
    private LogicTimeWord target;
    private LogicAction action;


    @Override
    public int hashCode(){
        return source.hashCode()+action.hashCode();
    }

    @Override
    public boolean equals(Object o){
        Track guard = (Track)o;
        boolean var1 = source.equals(guard.source);
        boolean var3 = action.equals(guard.action);
        return var1 && var3;
    }

    @Override
    public String toString() {
        return "Track{" +
                "source=" + source +
                ", target=" + target +
                ", word=" + action +
                '}';
    }
}
