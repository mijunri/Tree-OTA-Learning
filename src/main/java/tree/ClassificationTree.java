package tree;

import membership.Answer;
import membership.SmartMembership;
import lombok.Data;
import ota.Location;
import ota.OTA;
import ota.TimeGuard;
import ota.Transition;
import timeword.*;
import util.OTAUtil;
import util.TimeWordUtil;
import java.util.*;

@Data
public class ClassificationTree {
    private String name;
    private Node root;
    private boolean isComplete = false;
    private Set<Track> trackSet = new HashSet<>();
    private Map<Node,Location> map;
    private SmartMembership smartMembership;
    private Set<String> sigma;
    private OTA hypothesis;
    private OTA teacher;

    public ClassificationTree(String name, SmartMembership smartMembership, Set<String> sigma, OTA teacher) {
        this.name = name;
        this.smartMembership = smartMembership;
        this.sigma = sigma;
        this.teacher = teacher;
        init();
    }

    public void init(){
        root = new Node(LogicTimeWord.emptyWord(),false,false);

        Key key = answer(LogicTimeWord.emptyWord(), LogicTimeWord.emptyWord());
        Node node = new Node(LogicTimeWord.emptyWord(),true,answer(LogicTimeWord.emptyWord()).isAccept());
        root.add(key, node);
        refineSymbolTrack(LogicTimeWord.emptyWord());
    }




    public SiftAnswer sift(LogicTimeWord word){
        Node currentNode = root;
        Node pre = currentNode;
        while (currentNode != null && !currentNode.isLeaf()){
            LogicTimeWord suffix = currentNode.getLogicTimeWord();
            Key key = answer(word, suffix);
            pre = currentNode;
            currentNode = currentNode.getChild(key);
            if (currentNode == null){
                Node node = new Node(word, word.isEmpty(), answer(word).isAccept());
                pre.add(key, node);
                refineSymbolTrack(word);
                return new SiftAnswer(node, true);
            }
        }
        return new SiftAnswer(currentNode,false);
    }



    public void refine(LogicTimeWord ce){

        //the tree is not complete
//        if(!isComplete){
//            refineComplete(ce);
//            return;
//        }
        //the tree is complete
        refineNotComplete(ce);

    }

    private void refineNotComplete(LogicTimeWord ce) {
        ErrorInformation errorInformation = errLocation(ce);

        int j = errorInformation.getIndex();
        Location qu = hypothesis.getLocation(ce.subWord(0,j));
        LogicTimeWord uWord = qu.getLogicTimeWord();
        LogicAction action = ce.get(j);
        SiftAnswer siftAnswer = sift(TimeWordUtil.concat(uWord,action));
        if (siftAnswer.isRefine()){
            return;
        }
        Node vNode = siftAnswer.getNode();
        Location qv = map.get(vNode);
        LogicTimeWord vWord = vNode.getLogicTimeWord();

        boolean isPass = checkIsPass(qu,qv,errorInformation.getResetLogicAction());

        if(!isPass){
            LogicTimeWord source = qu.getLogicTimeWord();
            LogicTimeWord target = qv.getLogicTimeWord();
            LogicTimeWord word = TimeWordUtil.concat(source,action);
            ResetLogicTimeWord resetLogicTimeWord = TimeWordUtil.tranToReset(teacher,word);
            Track track = new Track(source,target,resetLogicTimeWord.getLastAction());
            trackSet.add(track);
        } else {
            LogicTimeWord suffix = ce.subWord(j+1,ce.size());
            vNode.setLogicTimeWord(suffix);
            LogicTimeWord newWord = TimeWordUtil.concat(uWord,action);
            Node node1 = createNode(vWord);
            Node node2 = createNode(newWord);


            vNode.add(answer(vWord, suffix), node1);
            vNode.add(answer(newWord, suffix), node2);

            //refine transition
            refineNode(vNode,vWord);

            //add transition
            refineSymbolTrack(newWord);

        }
    }


    private boolean checkIsPass(Location qu, Location qv, ResetLogicAction action){
        List<Transition> transitionList = getHypothesis().getTransitions(qu,null,qv);

        boolean isPass = false;
        for(Transition t:transitionList){
            if(t.isPass(action)){
                isPass = true;
                break;
            }
        }

        return isPass;
    }

    private Node createNode(LogicTimeWord logicTimeWord){
        Node node = new Node();
        node.setInit(logicTimeWord.isEmpty());
        node.setAccpted(answer(logicTimeWord).isAccept());
        node.setLogicTimeWord(logicTimeWord);
        return node;
    }

    //把指向Node的迁移指向到它的儿子
    private void refineNode(Node oldNode, LogicTimeWord oldSuffix){
        LogicTimeWord suffix = oldNode.getLogicTimeWord();
        Iterator<Track> iterator = trackSet.iterator();
        List<LogicTimeWord> newNodeWordList = new ArrayList<>();
        while (iterator.hasNext()){
            Track track = iterator.next();
            if(track.getTarget().equals(oldSuffix)){
                ResetLogicAction resetLogicAction = track.getAction();
                LogicAction logicAction = new LogicAction(resetLogicAction.getSymbol(), resetLogicAction.getValue());
                LogicTimeWord timeWord = TimeWordUtil.concat(track.getSource(), logicAction);

                Key key = answer(timeWord,suffix);
                Node targetNode = oldNode.getChild(key);
                if (targetNode == null){
                    Node node = new Node(timeWord, timeWord.isEmpty(), answer(timeWord).isAccept());
                    oldNode.add(key, node);
                    newNodeWordList.add(timeWord);
                    targetNode = node;
                }
                track.setTarget(targetNode.getLogicTimeWord());
            }
        }
        for(LogicTimeWord timeWord: newNodeWordList){
            refineSymbolTrack(timeWord);
        }
    }

    public void buildHypothesis(){
        map = new HashMap<>();

        List<Location> locationList = buildLocationList();
        List<Transition> transitionList = buildTransitionList();

        OTA evidenceRTA = new OTA(getName(),getSigma(),locationList,transitionList);
        OTA hypothesis = OTAUtil.evidToOTA(evidenceRTA);
        setHypothesis(hypothesis);
    }

    private List<Transition> buildTransitionList() {
        List<Transition> transitionList = new ArrayList<>();
        Map<LogicTimeWord, Node> leafMap = getLeafMap();
        for(Track track: trackSet){
            Node sourceNode = leafMap.get(track.getSource());
            Node targetNode = leafMap.get(track.getTarget());
            Location sourceLocation = map.get(sourceNode);
            Location targetLocation = map.get(targetNode);
            ResetLogicAction action = track.getAction();
            String symbol = action.getSymbol();
            String reset = action.isReset()?"r":"n";
            TimeGuard timeGuard = TimeGuard.bottomGuard(action);
            Transition transition = new Transition(sourceLocation,targetLocation,timeGuard,symbol,reset);
            transitionList.add(transition);
        }
        return transitionList;
    }

    private List<Location> buildLocationList() {
        List<Node> nodeList = leafList();
        List<Location> locationList = new ArrayList<>();
        for(int i = 0; i < nodeList.size(); i++){
            Node node = nodeList.get(i);
            Location location = new Location(i+1,getName()+(i+1),node.isInit(),node.isAccpted(),node.getLogicTimeWord());
            locationList.add(location);
            map.put(node,location);
        }
        return locationList;
    }

    private ErrorInformation errLocation(LogicTimeWord ce){
        for(int i = 1; i <= ce.size(); i++){
            LogicTimeWord prefix = ce.subWord(0,i);
//            ResetLogicTimeWord resetLogicTimeWord1 = TimeWordUtil.tranToReset(teacher, prefix);
//            ResetLogicTimeWord resetLogicTimeWord2 = TimeWordUtil.tranToReset(hypothesis, prefix);
//            if (!resetLogicTimeWord1.equals(resetLogicTimeWord2)){
//                return new ErrorInformation(i-1,true);
//            }
            LogicTimeWord u = getLocationMapWord(prefix);
            LogicTimeWord suffix = ce.subWord(i,ce.size());

            Boolean reset1 = TimeWordUtil.tranToReset(teacher, prefix).isReset();
            Boolean reset2 = TimeWordUtil.tranToReset(hypothesis, prefix).isReset();
            LogicAction logicAction = ce.get(i-1);
            ResetLogicAction resetLogicAction = new ResetLogicAction(logicAction, reset1);
            if (reset1 != reset2){
                return new ErrorInformation(i-1,resetLogicAction);
            }
            Key key1 = answer(prefix,suffix);
            Key key2 = answer(u, suffix);
            if (!key1.equals(key2)){
                return new ErrorInformation(i-1,resetLogicAction);
            }
        }
        return null;
    }

    private LogicTimeWord getLocationMapWord(LogicTimeWord logicTimeWord){
        return hypothesis.getLocation(logicTimeWord).getLogicTimeWord();
    }

//    private List<Boolean> resetList(int j, ResetLogicTimeWord resetLogicTimeWord){
//        List<Boolean> resetList = new ArrayList<>();
//        int len = resetLogicTimeWord.size();
//        for(int i = len - j; i < resetLogicTimeWord.size(); i++){
//            if (i < 0){
//                resetList.add(true);
//            }
//            else {
//                resetList.add(resetLogicTimeWord.get(i).isReset());
//            }
//        }
//        return resetList;
//    }

//    private boolean isSameReset(List<Boolean> list1, List<Boolean> list2){
//        if (list1.size() != list2.size()){
//            throw new RuntimeException("长度出错");
//        }
//        for(int i = 0; i < list1.size(); i++){
//            if (!list1.get(i).equals(list2.get(i))){
//                return false;
//            }
//        }
//        return true;
//    }

//    private GamaAnswer gama(LogicTimeWord word, int i){
//        GamaAnswer gamaAnswer = new GamaAnswer();
//        LogicTimeWord w = word.subWord(0,i);
//        Location location = getHypothesis().getLocation(w);
//        LogicTimeWord prefix = location.getLogicTimeWord();
//        LogicTimeWord suffix = word.subWord(i,word.size());
//        LogicTimeWord timeWord = TimeWordUtil.concat(prefix,suffix);
//        gamaAnswer.setLogicTimeWord(timeWord);
//        return gamaAnswer;
//    }


//    private void refineComplete(LogicTimeWord ce){
//        Node pre = null;
//        Node current = root;
//        while (!current.isLeaf()){
//            pre = current;
//
//            Key key = answer(ce, current.getLogicTimeWord());
//            current = current.getChild(key);
//
//            if(current == null){
//                boolean init = ce.equals(LogicTimeWord.emptyWord());
//                Node node = new Node(ce,init,answer(ce).isAccept());
//                pre.add(key, node);
//                isComplete = true;
//                refineSymbolTrack(ce);
//                break;
//            }
//        }
//    }

//    public LogicAction getAction(LogicTimeWord prefix, String symbol, double value){
//        DelayAction delayAction = new DelayAction(symbol,0);
//        DelayTimeWord delayPrefix = TimeWordUtil.tranToDelay(prefix);
//        DelayTimeWord delayTimeWord = TimeWordUtil.concat(delayPrefix,delayAction);
//        LogicTimeWord word = smartMembership.answer(delayTimeWord).getLogicTimeWord();
//        return word.get(word.size()-1);
//    }

    public void refineSymbolTrack(LogicTimeWord prefix){
        for(String symbol : sigma){
            LogicAction logicAction = new LogicAction(symbol,0);
            LogicTimeWord logicTimeWord = TimeWordUtil.concat(prefix,logicAction);
            ResetLogicTimeWord resetLogicTimeWord = TimeWordUtil.tranToReset(teacher, logicTimeWord);
            ResetLogicAction resetLogicAction = resetLogicTimeWord.getLastAction();
            Node node = sift(logicTimeWord).getNode();
            LogicTimeWord target;
            if(node == null){
                refine(logicTimeWord);
                target = logicTimeWord;
            }else {
                target = node.getLogicTimeWord();
            }
            Track track = new Track(prefix,target,resetLogicAction);
            trackSet.add(track);
        }
    }


    private Answer answer(LogicTimeWord logicTimeWord){
        return smartMembership.answer(logicTimeWord);
    }

    private Key answer(LogicTimeWord prefix, LogicTimeWord suffix){
        LogicTimeWord logicTimeWord = TimeWordUtil.concat(prefix, suffix);
        Answer answer = smartMembership.answer(logicTimeWord);
        List<Boolean> resetList = new ArrayList<>();
        ResetLogicTimeWord resetLogicTimeWord = answer.getResetLogicTimeWord();
        int len = suffix.size();
        int size = resetLogicTimeWord.size();
        for(int i = len; i > 0; i--){
            resetList.add(resetLogicTimeWord.get(size - i).isReset());
        }
        return new Key(resetList, answer.isAccept());
    }



    public Map<LogicTimeWord, Node> getLeafMap(){
        Map<LogicTimeWord, Node> leafMap = new HashMap<>();
        LinkedList<Node> queue = new LinkedList<>();
        queue.add(root);
        while(!queue.isEmpty()){
            Node node = queue.remove();
            if(node.isLeaf()){
                LogicTimeWord suffix= node.getLogicTimeWord();
                leafMap.put(suffix,node);
            }
            else {
                for (Node child: node.getChildList()){
                    if (child != null){
                        queue.add(child);
                    }
                }
            }
        }
        return leafMap;
    }

    private List<Node> leafList(){
        Map<LogicTimeWord, Node> leafMap = getLeafMap();
        return new ArrayList<>(leafMap.values());
    }

}
