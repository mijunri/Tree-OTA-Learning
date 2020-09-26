package tree;

import learning.SmartMembership;
import lombok.Data;
import ota.Location;
import ota.OTA;
import ota.TimeGuard;
import ota.Transition;
import timeword.DelayTimeWord;
import timeword.LogicAction;
import timeword.LogicTimeWord;
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

    public ClassificationTree(String name, SmartMembership smartMembership, Set<String> sigma) {
        this.name = name;
        this.smartMembership = smartMembership;
        this.sigma = sigma;
        init();
    }

    public void init(){
        root = new Node(LogicTimeWord.emptyWord(),false,false);

        boolean isAccpted = answer(LogicTimeWord.emptyWord());
        if(isAccpted == false){
            Node left = new Node(LogicTimeWord.emptyWord(),true,false);
            root.setLeftChild(left);
        }else {
            Node right = new Node(LogicTimeWord.emptyWord(), true,true);
            root.setRightChild(right);
        }

        refineSymbolTrack(LogicTimeWord.emptyWord());
    }




    public Node sift(LogicTimeWord word){
        Node currentNode = root;
        while (currentNode != null && !currentNode.isLeaf()){
            LogicTimeWord suffix = currentNode.getLogicTimeWord();
            LogicTimeWord timeWord = TimeWordUtil.concat(word,suffix);
            boolean answer = answer(timeWord);
            if(answer){
                currentNode = currentNode.getRightChild();
            }else {
                currentNode = currentNode.getLeftChild();
            }
        }
        return currentNode;
    }



    public void refine(LogicTimeWord ce){

        //the tree is not complete
        if(!isComplete){
            refineComplete(ce);
            return;
        }
        //the tree is complete
        refineNotComplete(ce);

    }

    private void refineNotComplete(LogicTimeWord ce) {

        int j = errLocation(ce);
        Location qu = hypothesis.getLocation(ce.subWord(0,j));
        LogicTimeWord uWord = map.get(qu).getLogicTimeWord();
        LogicAction action = ce.get(j);
        Node vNode = sift(TimeWordUtil.concat(uWord,action));
        Location qv = map.get(vNode);
        LogicTimeWord vWord = vNode.getLogicTimeWord();

        boolean isPass = checkIsPass(qu,qv,action);

        if(!isPass){
            LogicTimeWord source = map.get(qu).getLogicTimeWord();
            LogicTimeWord target = map.get(qv).getLogicTimeWord();
            Track track = new Track(source,target,action);
            trackSet.add(track);
        } else {
            LogicTimeWord suffix = ce.subWord(j+1,ce.size());
            vNode.setLogicTimeWord(suffix);
            LogicTimeWord newWord = TimeWordUtil.concat(uWord,action);
            Node node1 = createNode(vWord);
            Node node2 = createNode(newWord);

            if(answer(TimeWordUtil.concat(vWord,suffix))){
                vNode.setChilds(node2,node1);
            }else {
                vNode.setChilds(node1,node2);
            }

            //refine transition
            refineNode(vWord,suffix,vNode);

            //add transition
            refineSymbolTrack(newWord);

        }
    }

    private boolean checkIsPass(Location qu, Location qv, LogicAction action){
        List<Transition> transitionList = getHypothesis().getTransitions(qu,null,qv);

        boolean isPass = false;
        for(Transition t:transitionList){
            if(t.isPass(action.getSymbol(),action.getValue())){
                isPass = true;
                break;
            }
        }

        return isPass;
    }

    private Node createNode(LogicTimeWord logicTimeWord){
        Node node = new Node();
        node.setInit(logicTimeWord.isEmpty());
        node.setAccpted(answer(logicTimeWord));
        node.setLogicTimeWord(logicTimeWord);
        return node;
    }

    //把指向Node的迁移指向到它的儿子
    private void refineNode(LogicTimeWord oldSuffix, LogicTimeWord suffix, Node node){
        for(Track track:trackSet){
            if(track.getTarget().equals(oldSuffix)){
                LogicTimeWord timeWord = TimeWordUtil.concat(track.getSource(),track.getAction());
                if(answer(TimeWordUtil.concat(timeWord,suffix))){
                    track.setTarget(node.getRightChild().getLogicTimeWord());
                }else {
                    track.setTarget(node.getLeftChild().getLogicTimeWord());
                }
            }
        }
    }

    public void buildHypothesis(){
        map = new HashMap<>();

        List<Location> locationList = buildLocationList();
        List<Transition> transitionList = buildTransitionList();

        OTA evidenceRTA = new OTA(getName(),getSigma(),locationList,transitionList);
        OTA hypothesis = OTAUtil.evidToOTA(evidenceRTA);
        OTAUtil.completeOTA(hypothesis);
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
            LogicAction action = track.getAction();
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
            Location location = new Location(i+1,getName()+i+1,node.isInit(),node.isAccpted(),node.getLogicTimeWord());
            locationList.add(location);
            map.put(node,location);
        }
        return locationList;
    }

    private int errLocation(LogicTimeWord ce){
        boolean a = smartMembership.answer(ce);
        for(int i = 0; i < ce.size(); i++){
            LogicTimeWord w2 = gama(ce,i+1);
            boolean b = smartMembership.answer(w2);
            if(a != b){
                return i;
            }
        }
        return -1;
    }

    private LogicTimeWord gama(LogicTimeWord word, int i){
        LogicTimeWord w = word.subWord(0,i);
        Location location = getHypothesis().getLocation(w);
        LogicTimeWord prefix = map.get(location).getLogicTimeWord();
        LogicTimeWord suffix = word.subWord(i,word.size());
        LogicTimeWord timeWord = TimeWordUtil.concat(prefix,suffix);
        return timeWord;
    }


    private void refineComplete(LogicTimeWord ce){
        Node pre = null;
        Node current = root;
        while (!current.isLeaf()){
            pre = current;
            LogicTimeWord logicTimeWord = TimeWordUtil.concat(ce,current.getLogicTimeWord());
            boolean answer = smartMembership.answer(logicTimeWord);
            current = answer?current.getRightChild():current.getLeftChild();;

            if(current == null){
                boolean init = ce.equals(LogicTimeWord.emptyWord());
                Node node = new Node(ce,init,answer);
                if(answer){
                    pre.setRightChild(node);
                }else {
                    pre.setLeftChild(node);
                }
                isComplete = true;
                refineSymbolTrack(ce);
                break;
            }
        }
    }

    public LogicAction getAction(LogicTimeWord prefix, String symbol, double value){
        LogicAction logicAction = new LogicAction(symbol,0,true);
        LogicTimeWord logicTimeWord = TimeWordUtil.concat(prefix,logicAction);
        DelayTimeWord delayTimeWord = TimeWordUtil.tranToDelay(logicTimeWord);
        LogicTimeWord word = smartMembership.answer(delayTimeWord).getLogicTimeWord();
        return word.get(word.size()-1);
    }

    public void refineSymbolTrack(LogicTimeWord prefix){
        for(String symbol : sigma){
            LogicAction logicAction = getAction(prefix,symbol,0);
            LogicTimeWord logicTimeWord = TimeWordUtil.concat(prefix,logicAction);
            Node node = sift(logicTimeWord);
            LogicTimeWord target;
            if(node == null){
                refine(logicTimeWord);
                target = logicTimeWord;
            }else {
                target = node.getLogicTimeWord();
            }
            Track track = new Track(LogicTimeWord.emptyWord(),target,logicAction);
            trackSet.add(track);
        }
    }


    private boolean answer(LogicTimeWord logicTimeWord){
        return smartMembership.answer(logicTimeWord);
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
                Node left = node.getLeftChild();
                Node right = node.getRightChild();
                if(left!=null){
                    queue.add(left);
                }
                if(right!=null){
                    queue.add(right);
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
