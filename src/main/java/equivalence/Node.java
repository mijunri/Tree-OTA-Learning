package equivalence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ota.Location;
import equivalence.ta.Clock;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Node{
    private Location location;
    private DBM dbm;
    private Node preNode;
    private String action;

    public Node(Location location, DBM dbm) {
        this.location = location;
        this.dbm = dbm;
    }

    public Location getLocation() {
        return location;
    }

    public DBM getDbm() {
        return dbm;
    }

    public Node getPreNode() {
        return preNode;
    }

    public void setPreNode(Node preNode) {
        this.preNode = preNode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean include(Node node){
        if(node.getLocation() != getLocation()){
            return false;
        }
        if(!getDbm().include(node.getDbm())){
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("\nlocation is :").append(location).append("\n");
        sb.append("action is :").append(action).append("\n");
        sb.append("guard is :");
        List<Clock> clockList = dbm.getClockList();
        Clock c1 = clockList.get(0);
        Clock c2 = clockList.get(1);
        sb.append("\n").append(c1.getName()).append(":");
        if(dbm.getMatrix()[0][1].isEqual()){
            sb.append("[");
        }else {
            sb.append("(");
        }
        sb.append(dbm.getMatrix()[0][1].getValue()*-1).append(",").append(dbm.getMatrix()[1][0].getValue());
        if(dbm.getMatrix()[1][0].isEqual()){
            sb.append("]");
        }else {
            sb.append(")");
        }

        sb.append("\n").append(c2.getName()).append(":");
        if(dbm.getMatrix()[0][2].isEqual()){
            sb.append("[");
        }else {
            sb.append("(");
        }
        sb.append(dbm.getMatrix()[0][2].getValue()*-1).append(",").append(dbm.getMatrix()[2][0].getValue());
        if(dbm.getMatrix()[2][0].isEqual()){
            sb.append("]");
        }else {
            sb.append(")");
        }

        sb.append("\n").append(c2.getName()).append("-").append(c1.getName()).append(":");
        if(dbm.getMatrix()[1][2].isEqual()){
            sb.append("[");
        }else {
            sb.append("(");
        }
        sb.append(dbm.getMatrix()[1][2].getValue()*-1).append(",").append(dbm.getMatrix()[2][1].getValue());
        if(dbm.getMatrix()[2][1].isEqual()){
            sb.append("]");
        }else {
            sb.append(")");
        }

        return sb.toString();
    }

}
