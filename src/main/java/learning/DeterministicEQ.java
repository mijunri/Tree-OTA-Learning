package learning;

import ota.Location;
import ota.OTA;
import ota.OTABuilder;
import ota.TimeGuard;
import ta.Clock;
import ta.TA;
import ta.TaTransition;
import timeword.*;


import javax.xml.ws.handler.LogicalHandler;
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
        OTA negTeacher = OTABuilder.getNegtiveOTA(teacher);
        OTA negHypothesis = OTABuilder.getNegtiveOTA(hypothesis);

        TA ta1 = OTABuilder.getCartesian(teacher, negHypothesis);
        TA ta2 = OTABuilder.getCartesian(negTeacher,hypothesis);


        List<Node> nodeList1 = counterTrace(ta1);
        List<Node> nodeList2 = counterTrace(ta2);

        List<Node> nodeList = null;
        if(nodeList1 == null  && nodeList2 == null ){
            return null;
        }
        if(nodeList1 == null){
            nodeList = nodeList2;
        }
        else if(nodeList2 == null){
            nodeList = nodeList1;
        }
        else {
            nodeList = nodeList1.size() < nodeList2.size()?nodeList1:nodeList2;
        }


        int len = nodeList.size();
        String[] actionArray = new String[len];
        double[] lowBounds = new double[len];
        double[] highBounds = new double[len];

        for(int i = 0; i < len; i++){
            Node node = nodeList.get(i);
            String action = node.getAction();
            actionArray[i] = action;
            Value v = node.getDbm().getMatrix()[0][1];
            if(v.isEqual()){
                lowBounds[i] = v.getValue()*-1;
            }else {
                lowBounds[i] = v.getValue()*-1+0.1;
            }
            Value v1 = node.getDbm().getMatrix()[1][0];
            if(v1.isEqual()){
                highBounds[i] = v1.getValue();
            }else {
                highBounds[i] = v1.getValue()-0.1;
            }
        }

        LogicTimeWord logicTimeWord = LogicTimeWord.getEmpty();

        for(int i = 0; i < len; i++){
            LogicAction logicAction = new LogicAction(actionArray[i],lowBounds[i],true);
            logicTimeWord = TimeWordUtil.concat(logicTimeWord,logicAction);
        }


        while (true){
            DelayTimeWord delayTimeWord = TimeWordUtil.tranToDelay(teacher,logicTimeWord);
            Location a1 = teacher.getLocation(delayTimeWord);
            Location a2 = hypothesis.getLocation(delayTimeWord);
            if(a1.isAccept() != a2.isAccept()){
                return delayTimeWord;
            }
            else {
                for(int i = len-1; i >= 0; i-- ){
                    LogicAction logicAction = logicTimeWord.get(i);
                    double value = logicAction.getValue();
                    if(value < highBounds[i]){
                        double v1 = value+0.1;
                        if(v1 - (int)v1 < 0.01){
                            v1 = (int)v1;
                        }
                        if((int)(v1)+1 - v1 < 0.01){
                            v1 = (int)v1+1;
                        }
                        logicAction.setValue(v1);
                        break;
                    }else {
                        double v2 = lowBounds[i];
                        if(v2 - (int)v2 < 0.01){
                            v2 = (int)v2;
                        }
                        logicAction.setValue(v2);
                    }
                }
            }
        }
    }


    public static List<Node> counterTrace(TA ta){

        List<Clock> clockList = new ArrayList<>(ta.getClockSet());
        DBM initDbm = DBM.getInitDBM(clockList);

        Location initLocation = ta.getInitLocation();

        Set<Node> visited = new HashSet<>();
        LinkedList<Node> queue = new LinkedList<>();
        Node node = new Node(initLocation,initDbm);
        visited.add(node);
        queue.offer(node);

        while (!queue.isEmpty()){
            Node current = queue.poll();
            Location location = current.getLocation();
            if(location.isAccept() || location.getName().equals("false")){
                List<Node> nodes = new ArrayList<>();
                nodes.add(current);
                while (current.getPreNode()!=null){
                    current = current.getPreNode();
                    nodes.add(current);
                }
                List<Node> list = new ArrayList<>();
                for(int i = nodes.size()-1; i>=0; i--){
                    if(i%2 == 1){
                        list.add(nodes.get(i));
                    }
                }
                return list;
            }
            List<TaTransition> taTransitions = ta.getTransitions(location,null,null);
            for(TaTransition t:taTransitions){
                Map<TimeGuard,Clock> timeGuardClockMap = t.getTimeGuardClockMap();
                DBM dbm = current.getDbm().copy();
                for(Map.Entry<TimeGuard,Clock> entry: timeGuardClockMap.entrySet()){
                    dbm.and(entry.getValue(),entry.getKey());
                }
                dbm.canonical();

//                System.out.println(dbm);

                if(dbm.isConsistent()){
                    Node guardNode = new Node(t.getTargetLocation(),dbm);
                    guardNode.setAction(t.getAction());
                    guardNode.setPreNode(current);
                    dbm = dbm.copy();
                    for(Clock c: t.getResetClockSet()){
                        dbm.reset(c);
                    }
                    dbm.canonical();
                    dbm.up();
                    Node newNode = new Node(t.getTargetLocation(),dbm);
                    newNode.setPreNode(guardNode);
                    boolean flag = false;
                    for(Node n: visited){
                        if(n.include(newNode)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag == true){
                        continue;
                    }else {
                        visited.add(newNode);
                        queue.offer(newNode);
                    }
                }
            }
        }

        return null;
    }


}
