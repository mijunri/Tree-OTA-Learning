package equivalence.ta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import ota.Location;
import ota.Transition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TA {
    private String name;
    private Set<Clock> clockSet;
    private Set<String> sigma;
    private List<Location> locationList;
    private List<TaTransition> taTransitionList;


    public Location getInitLocation(){
        for(Location location:locationList){
            if(location.isInit()){
                return location;
            }
        }
        return null;
    }

    public List<Location> getAcceptedLocations(){
        List<Location> list = new ArrayList<>();
        for(Location location:locationList){
            if(location.isAccept()){
                list.add(location);
            }
        }
        return list;
    }

    public List<TaTransition> getTransitions(Location fromLocation,
                                             String symbol,
                                             Location toLocation){
        List<TaTransition> list = new ArrayList<>(taTransitionList);
        Iterator<TaTransition> iterator = list.iterator();
        while(iterator.hasNext()){
            TaTransition t = iterator.next();
            int tSourceId = t.getSourceId();
            int tTargetId = t.getTargetId();
            String tSymbol = t.getSymbol();

            if (fromLocation != null){
                int fromId = fromLocation.getId();
                if (tSourceId != fromId){
                    iterator.remove();
                    continue;
                }
            }

            if (symbol != null){
                if (!StringUtils.equals(tSymbol,symbol)){
                    iterator.remove();
                    continue;
                }
            }

            if (toLocation != null){
                int toId = toLocation.getId();
                if (toId != tTargetId){
                    iterator.remove();
                    continue;
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

        for(int i = 0; i < getTaTransitionList().size();i++){
            TaTransition t = getTaTransitionList().get(i);
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
