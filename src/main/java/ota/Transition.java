package ota;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;


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

    public int getLeftBound(){
        return timeGuard.getLeft();
    }

    public int getRightBound(){
        return timeGuard.getRight();
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("<")
                .append(sourceLocation.getName())
                .append(", ")
                .append(symbol)
                .append(",")
                .append(timeGuard)
                .append(",")
                .append(targetLocation.getName())
                .append(",")
                .append(reset)
                .append(">");
        return sb.toString();
    }

}

