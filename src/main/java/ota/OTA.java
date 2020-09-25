package ota;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import timeword.*;
import util.OTAUtil;
import util.TimeWordUtil;
import java.util.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class OTA {

    private String name;
    private Set<String> sigma;
    private List<Location> locationList;
    private List<Transition> transitionList;

    public int size(){
        return locationList.size();
    }

    public int indexof(Location location){
        for(int i = 0; i < locationList.size(); i ++){
            if(locationList.get(i) == location){
                return i;
            }
        }
        return -1;
    }

    public Location getLocation(int id){
        for(Location location:locationList){
            if(location.getId()==id){
                return location;
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

    public Location getInitLocation(){
        for(Location l:locationList){
            if(l.isInit()){
                return l;
            }
        }
        return null;
    }


    public List<Transition> getTransitions(Location fromLocation, String symbol, Location toLocation){
        List<Transition> list = new ArrayList<>(transitionList);
        Iterator<Transition> iterator = list.iterator();
        while(iterator.hasNext()){
            Transition t = iterator.next();
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
                if (StringUtils.equals(tSymbol,symbol)){
                    iterator.remove();
                    continue;
                }
            }

            if (toLocation != null){
                int toId = toLocation.getId();
                if (toId == tTargetId){
                    iterator.remove();
                    continue;
                }
            }
        }
        return list;
    }

    public Location getLocation(LogicTimeWord logicTimeWord){
        Location location = getInitLocation();
        for(LogicAction logicAction : logicTimeWord.getActionList()){
            boolean flag = false;
            List<Transition> transitionList = getTransitions(location,null,null);
            for(Transition t:transitionList){
                if(t.isPass(logicAction.getSymbol(), logicAction.getValue())){
                    location = t.getTargetLocation();
                    flag = true;
                    break;
                }
            }
            if(flag == false){
                return null;
            }
        }
        return location;
    }

    public Location getLocation(DelayTimeWord delayTimeWord){
        LogicTimeWord logicTimeWord = TimeWordUtil.tranToLogic(this,delayTimeWord);
        return getLocation(logicTimeWord);
    }






    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t").append("\"sigma\":[");
        for(String action: getSigma()){
            sb.append("\""+action+"\",");
        }
        sb.deleteCharAt(sb.length()-1).append("],\n\t").append("\"init\":");
        String init = getInitLocation().getName();
        sb.append(init).append(",\n\t").append("\"name\":\"").append(getName()).append("\"\n\t");
        sb.append("\"s\":[");
        for(Location l:getLocationList()){
            sb.append(l.getName()).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]\n\t\"tran\":{\n");

        OTAUtil.sortTran(getTransitionList());
        for(int i = 0; i < getTransitionList().size();i++){
            Transition t = getTransitionList().get(i);
            sb.append("\t\t\"").append(i).append("\":[")
                    .append(t.getSourceName()).append(",")
                    .append("\"").append(t.getSymbol()).append("\",")
                    .append("\"").append(t.getTimeGuard()).append("\",")
                    .append(t.getTargetName()).append(", ").append(t.getReset()).append("]").append(",\n");
        }
        sb.deleteCharAt(sb.length()-2);
        sb.append("\t},\n\t").append("\"accpted\":[");
        for(Location l:getAcceptedLocations()){
            sb.append(l.getId()).append(",");
        }
        sb.deleteCharAt(sb.length()-1).append("]\n}");
        return sb.toString();
    }

    public OTA copy(){
        String name1 = name;
        Set<String> sigma1 = new HashSet<>(sigma);
        List<Location> locationList1 = new ArrayList<>();
        Map<Location, Location> locationMap = new HashMap<>();
        for(Location l:locationList){
            Location l1 = new Location(l.getId(),l.getName(),l.isInit(),l.isAccept());
            locationMap.put(l,l1);
            locationList1.add(l1);
        }
        List<Transition> transitionList1 = new ArrayList<>();
        for(Transition t:transitionList){
            Location source = locationMap.get(t.getSourceLocation());
            Location target = locationMap.get(t.getTargetLocation());
            TimeGuard guard = t.getTimeGuard().copy();
            Transition t1 = new Transition(source,target,guard,t.getSymbol(),t.getReset());
            transitionList1.add(t1);
        }
        return new OTA(name1,sigma1,locationList1,transitionList1);
    }

//
//    public Transition getTransitionByIndexAndTimeWords(int index, TimeWords timeWords){
//        Location location = getInitLocation();
//        List<TimeWord> timeWordList = timeWords.getWordList();
//        boolean isReset = true;
//        TimeWord pre = null;
//        Transition transition = null;
//
//        for(int i = 0; i < index; i++){
//            TimeWord word = new TimeWord(timeWordList.get(i).getAction(),timeWordList.get(i).getValue());
//            if(!isReset){
//                double value = word.getValue();
//                value = value + pre.getValue();
//                word.setValue(value);
//            }
//            List<Transition> transitionList = getTransitions(location,word.getAction(),null);
//            for(Transition t: transitionList){
//                if(t.isPass(word)){
//                    location = t.getTargetLocation();
//                    transition = t;
//                    if(t.isReset()){
//                        isReset = true;
//                    }else {
//                        isReset = false;
//                        pre = word;
//                    }
//                    break;
//                }
//            }
//        }
//        return transition;
//    }
//
//
//    public void refineRest(TimeWords words, int len){
//        Location location = getInitLocation();
//        List<TimeWord> timeWordList = words.getWordList();
//
//        TimeWord pre = null;
//        for(int i = 0; i < len; i++){
//            TimeWord word = timeWordList.get(i);
//
//            List<Transition> transitionList = getTransitions(location,word.getAction(),null);
//            for(Transition t: transitionList){
//                if(t.isPass(word)){
//                    location = t.getTargetLocation();
//                    if(t.isReset()){
//                        word.setReset(true);
//                    }else {
//                        word.setReset(false);
//                    }
//                    break;
//                }
//            }
//        }
//    }


}
