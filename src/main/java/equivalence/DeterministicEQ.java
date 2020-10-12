package equivalence;

import equivalence.ta.TaTimeGuard;
import ota.Location;
import ota.OTA;
import equivalence.ta.Clock;
import equivalence.ta.TA;
import equivalence.ta.TaTransition;
import timeword.*;
import util.OTAUtil;
import util.TimeWordUtil;


import java.util.*;

public class DeterministicEQ implements EquivalenceQuery {

    private int count;
    private OTA teacher;

    public DeterministicEQ(OTA teacher) {
        this.teacher = teacher;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public DelayTimeWord findCounterExample(OTA hypothesis) {
        count++;

        OTA negTeacher = OTAUtil.getNegtiveOTA(teacher);
        OTA negHypothesis = OTAUtil.getNegtiveOTA(hypothesis);

        TA ta1 = OTAUtil.getCartesian(teacher, negHypothesis);
        TA ta2 = OTAUtil.getCartesian(negTeacher,hypothesis);

        List<TransitionStateSet> traceList1 = counterTrace(ta1);
        List<TransitionStateSet> traceList2 = counterTrace(ta2);

        List<TransitionStateSet> traceList = shortList(traceList1, traceList2);

        if (traceList == null){
            return null;
        }

//        for (TransitionStateSet transitionStateSet : traceList){
//            System.out.println(transitionStateSet);
//        }

        int len = traceList.size();

        String[] symbolArray = new String[len];
        double[] lowBounds = new double[len];
        double[] highBounds = new double[len];

        for(int i = 0; i < len; i++){
            TransitionStateSet transitionStateSet = traceList.get(i);
            String action = transitionStateSet.getSymbol();
            symbolArray[i] = action;
            Value v = transitionStateSet.getDbm().getMatrix()[0][1];
            if(v.isEqual()){
                lowBounds[i] = v.getValue()*-1;
            }else {
                lowBounds[i] = v.getValue()*-1+0.1;
            }
            Value v1 = transitionStateSet.getDbm().getMatrix()[1][0];
            if(v1.isEqual()){
                highBounds[i] = v1.getValue();
            }else {
                highBounds[i] = v1.getValue()-0.1;
            }
        }

        LogicTimeWord logicTimeWord = LogicTimeWord.emptyWord();

        for(int i = 0; i < len; i++){
            LogicAction logicAction = new LogicAction(symbolArray[i],lowBounds[i]);
            logicTimeWord = TimeWordUtil.concat(logicTimeWord,logicAction);
        }


        while (true){
//            List<Double> doubleList = new ArrayList<>();
            DelayTimeWord delayTimeWord = TimeWordUtil.tranToDelay(teacher,logicTimeWord);
//            System.out.println(delayTimeWord);
            Location a1 = teacher.getLocation(delayTimeWord);
            Location a2 = hypothesis.getLocation(delayTimeWord);
            if(a1.isAccept() != a2.isAccept()){
                return delayTimeWord;
            }
            for(int i = len-1; i >= 0; i-- ){
                LogicAction logicAction = logicTimeWord.get(i);
                double value = logicAction.getValue();
                if(value < highBounds[i]){
                    double v1 = value+0.6;
                    if(v1 - (int)v1 < 0.1){
                        v1 = (int)v1;
                    }
                    if((int)(v1)+1 - v1 < 0.1){
                        v1 = (int)v1+1;
                    }
                    logicAction.setValue(v1);
                    break;
                }else {
                    double v2 = lowBounds[i];
                    if(v2 - (int)v2 < 0.1){
                        v2 = (int)v2;
                    }
                    logicAction.setValue(v2);
                }
            }
        }
    }

    private List<TransitionStateSet> shortList(List<TransitionStateSet> traceList1, List<TransitionStateSet> traceList2){
        if(traceList1 == null  && traceList2 == null ){
            return null;
        }
        if(traceList1 == null){
            return traceList2;
        }
        if(traceList2 == null){
            return traceList1;
        }
        else {
            List<TransitionStateSet> list = traceList1.size() < traceList2.size()? traceList1 : traceList2;
            return list;
        }
    }


    public static List<TransitionStateSet> counterTrace(TA ta){

        //初始化dbm
        List<Clock> clockList = new ArrayList<>(ta.getClockSet());
        DBM initDbm = DBM.getInitDBM(clockList);

        Location initLocation = ta.getInitLocation();

        Set<LocationStateSet> visited = new HashSet<>();
        LinkedList<LocationStateSet> queue = new LinkedList<>();
        LocationStateSet initLocationStateSet = new LocationStateSet(initLocation, initDbm);
        visited.add(initLocationStateSet);
        queue.offer(initLocationStateSet);

        while (!queue.isEmpty()){
            LocationStateSet current = queue.poll();
            Location location = current.getLocation();
            if(location.isAccept()){
                List<TransitionStateSet> transitionStateSetList = new LinkedList<>();
                BaseStateSet stateSet = current;
                while (stateSet != null){
                    if (stateSet instanceof TransitionStateSet){
                        transitionStateSetList.add(0, (TransitionStateSet) stateSet);
                    }
                    stateSet = stateSet.getPreStateSet();
                }
                return transitionStateSetList;
            }
            List<TaTransition> taTransitions = ta.getTransitions(location,null,null);
            for(TaTransition t:taTransitions){
                TaTimeGuard taTimeGuard = t.getTaTimeGuard();
                DBM dbm = current.getDbm().copy();
                dbm.and(taTimeGuard);
                dbm.canonical();
                if(dbm.isConsistent()){
                    //生成迁移的状态集合
                    TransitionStateSet transitionStateSet = new TransitionStateSet(t.getTargetLocation(), dbm, t.getSymbol());
                    transitionStateSet.setPreStateSet(current);

                    //生成到达后的状态机和
                    dbm = dbm.copy();
                    dbm.reset(t.getResetClockSet());
                    dbm.canonical();
                    dbm.up();
                    LocationStateSet locationStateSet = new LocationStateSet(t.getTargetLocation(),dbm);
                    locationStateSet.setPreStateSet(transitionStateSet);

                    if (!checkInclude(visited, locationStateSet)){
                        visited.add(locationStateSet);
                        queue.offer(locationStateSet);
                    }
                }
            }
        }

        return null;
    }

    private static boolean checkInclude(Set<LocationStateSet> locationStateSets, LocationStateSet locationStateSet){
        for(LocationStateSet n: locationStateSets){
            if(n.include(locationStateSet)){
                return true;
            }
        }
        return false;
    }

}
