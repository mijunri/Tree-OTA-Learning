package equivalence.ta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ota.Location;
import ota.TimeGuard;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaTransition {
    private Location sourceLocation;
    private Location targetLocation;
    private String symbol;
    private TaTimeGuard taTimeGuard;
    private Set<Clock> resetClockSet;

    public boolean isReset(Clock clock){
        return resetClockSet.contains(clock);
    }

    public int getSourceId(){
        return sourceLocation.getId();
    }

    public int getTargetId(){
        return targetLocation.getId();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sourceLocation.getId()).append(", ").append(symbol).append(",");
        for(Map.Entry<TimeGuard,Clock> entry:taTimeGuard.entrySet()){
            sb.append(entry.getKey())
                    .append("-")
                    .append(entry.getValue().getName())
                    .append(",")
                    .append(isReset(entry.getValue())?"r":"n")
                    .append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }
}
