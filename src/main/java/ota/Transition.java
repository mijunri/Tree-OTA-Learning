package ota;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import timeword.LogicAction;
import timeword.LogicTimeWord;
import timeword.ResetLogicAction;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transition {

    private Location sourceLocation;
    private Location targetLocation;
    private TimeGuard timeGuard;
    private String symbol;
    private String reset;


    public boolean isReset(){
        return StringUtils.equals(reset,"r");
    }

    public boolean isPass(String symbol, double value){
        if(StringUtils.equals(getSymbol(),symbol)){
            return timeGuard.isPass(value);
        }
        return false;
    }

    public boolean isPass(LogicAction logicAction){
        if (!StringUtils.equals(getSymbol(),logicAction.getSymbol())){
            return false;
        }
        if (!timeGuard.isPass(logicAction.getValue())){
            return false;
        }
        return true;
    }

    public boolean isPass(ResetLogicAction resetLogicAction) {
        if (!StringUtils.equals(getSymbol(),resetLogicAction.getSymbol())){
            return false;
        }
        if (!timeGuard.isPass(resetLogicAction.getValue())){
            return false;
        }
        if (isReset() != resetLogicAction.isReset()){
            return false;
        }
        return true;
    }

    public int getSourceId(){
        return sourceLocation.getId();
    }

    public int getTargetId(){
        return targetLocation.getId();
    }

    public String getSourceName(){
        return sourceLocation.getName();
    }

    public String getTargetName(){
        return targetLocation.getName();
    }

    public int getLowerBound(){
        return timeGuard.getLowerBound();
    }

    public int getUpperBound(){
        return timeGuard.getUpperBound();
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("<")
                .append(sourceLocation.getId())
                .append(", ")
                .append(symbol)
                .append(",")
                .append(timeGuard)
                .append(",")
                .append(targetLocation.getId())
                .append(",")
                .append(reset)
                .append(">");
        return sb.toString();
    }


}

