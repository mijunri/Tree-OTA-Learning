package tree;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ota.Location;
import timeword.LogicTimeWord;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node{
    private LogicTimeWord LogicTimeWord;
    private Node leftChild;
    private Node rightChild;
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

    public void setChilds(Node left, Node right){
        this.leftChild = left;
        this.rightChild = right;
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
        if(leftChild!=null){
            return false;
        }
        if(rightChild!=null){
            return false;
        }
        return true;
    }



    @Override
    public String toString(){
        return getLogicTimeWord().toString();
    }


}
