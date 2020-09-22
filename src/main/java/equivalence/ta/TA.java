package equivalence.ta;

import ota.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TA {
    private String name;
    private Set<Clock> clockSet;
    private Set<String> sigma;
    private List<Location> locationList;
    private List<TaTransition> taTransitionList;

    public TA(String name, Set<Clock> clockSet, Set<String> sigma, List<Location> locationList, List<TaTransition> taTransitionList) {
        this.name = name;
        this.clockSet = clockSet;
        this.sigma = sigma;
        this.locationList = locationList;
        this.taTransitionList = taTransitionList;
    }

    public String getName() {
        return name;
    }

    public Set<Clock> getClockSet() {
        return clockSet;
    }

    public Set<String> getSigma() {
        return sigma;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public List<TaTransition> getTransitionList() {
        return taTransitionList;
    }

    public Location getInitLocation(){
        for(Location l:locationList){
            if(l.isInit()){
                return l;
            }
        }
        return null;
    }

    public List<Location> getAcceptedLocations(){
        List<Location> list = new ArrayList<>();
        for(Location l:locationList){
            if(l.isAccept()){
                list.add(l);
            }
        }
        return list;
    }

    public List<TaTransition> getTransitions(Location fromLocation, String action, Location toLocation){
        List<TaTransition> list = new ArrayList<>(taTransitionList);
        if(fromLocation != null){
            Iterator<TaTransition> iterator = list.iterator();
            while(iterator.hasNext()){
                TaTransition t = iterator.next();
                int tSourceId  = t.getSourceId();
                int fromId = fromLocation.getId();
                if(tSourceId != fromId){
                    iterator.remove();
                }
            }
        }

        if(action != null){
            Iterator<TaTransition> iterator = list.iterator();
            while(iterator.hasNext()){
                TaTransition t = iterator.next();
                String tAction  = t.getAction();
                if(!tAction.equals(action)){
                    iterator.remove();
                }
            }
        }

        if(toLocation != null){
            Iterator<TaTransition> iterator = list.iterator();
            while(iterator.hasNext()){
                TaTransition t = iterator.next();
                int tTargetId  = t.getTargetId();
                int toId = toLocation.getId();
                if(tTargetId != toId){
                    iterator.remove();
                }
            }
        }
        return list;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t").append("\"sigma\":[");
        for(String action: getSigma()){
            sb.append("\""+action+"\",");
        }
        sb.deleteCharAt(sb.length()-1).append("],\n\t").append("\"init\":");
        int init = getInitLocation().getId();
        sb.append(init).append(",\n\t").append("\"name\":\"").append(getName()).append("\"\n\t");
        sb.append("\"s\":[");
        for(Location l:getLocationList()){
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]\n\t\"tran\":{\n");

//        OTABuilder.sortTaTran(getTransitionList());
        for(int i = 0; i < getTransitionList().size();i++){
            TaTransition t = getTransitionList().get(i);
            sb.append("\t\t\"").append(i).append(t.toString()).append(",\n");
        }
        sb.deleteCharAt(sb.length()-2);
        sb.append("\t},\n\t").append("\"accpted\":[");
        for(Location l:getAcceptedLocations()){
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]\n}");
        return sb.toString();
    }
}
