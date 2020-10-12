package tree;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ota.Location;
import timeword.LogicTimeWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node{
    private LogicTimeWord LogicTimeWord;
    private Map<Key, Node> nodeMap = new HashMap<>();
    private boolean init;
    private boolean accpted;

    public Node(LogicTimeWord logicTimeWord) {
        LogicTimeWord = logicTimeWord;
    }

    public Node(LogicTimeWord logicTimeWord, boolean init, boolean accpted) {
        LogicTimeWord = logicTimeWord;
        this.init = init;
        this.accpted = accpted;
    }

    public void add(Key key, Node node){
        nodeMap.put(key,node);
    }

    public Node getChild(Key key){
        Node node =  nodeMap.get(key);
        return node;
    }

    public List<Node> getChildList(){
        return new ArrayList<>(nodeMap.values());
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public  boolean equals(Object o){
        return super.equals(o);
    }

    public boolean isLeaf(){
        return nodeMap.isEmpty();
    }


    @Override
    public String toString(){
        return getLogicTimeWord().toString();
    }


}
