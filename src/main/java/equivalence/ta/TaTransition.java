package equivalence.ta;

import ota.Location;
import ota.TimeGuard;

import java.util.Map;
import java.util.Set;

public class TaTransition {
    private Location sourceLocation;
    private Location targetLocation;
    private String action;
    private Map<TimeGuard, Clock> timeGuardClockMap;
    private Set<Clock> resetClockSet;

    public TaTransition(Location sourceLocation, Location targetLocation, String action, Map<TimeGuard, Clock> timeGuardClockMap, Set<Clock> resetClockSet) {
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
        this.action = action;
        this.timeGuardClockMap = timeGuardClockMap;
        this.resetClockSet = resetClockSet;
    }


    public int getSourceId() {
        return sourceLocation.getId();
    }

    public int getTargetId() {
        return targetLocation.getId();
    }

    public String getSourceName() {
        return sourceLocation.getName();
    }


    public String getTargetName() {
        return targetLocation.getName();
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }


    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public Map<TimeGuard, Clock> getTimeGuardClockMap() {
        return timeGuardClockMap;
    }

    public Set<Clock> getResetClockSet() {
        return resetClockSet;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }




    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sourceLocation.getId()).append(", ").append(action).append(",");
        for(Map.Entry<TimeGuard,Clock> entry:timeGuardClockMap.entrySet()){
            sb.append(entry.getKey()).append("-").append(entry.getValue().getName()).append(" & ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(", ").append(targetLocation.getId()).append("]");
        return sb.toString();
    }
}
