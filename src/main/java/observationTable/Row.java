package observationTable;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Boolean> booleanList;

    public Row(){
        booleanList = new ArrayList<>();
    }

    public int size(){
        return booleanList.size();
    }

    public boolean get(int i){
        return booleanList.get(i);
    }

    public void add(Boolean isAccepted){
        booleanList.add(isAccepted);
    }

    @Override
    public int hashCode(){
        int hash = 0;
        int i = 1;
        for (Boolean b: booleanList){
            if(b == true){
                hash+=i;
                i*=2;
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object o){
        Row row = (Row)o;
        if(this.size() != row.size()){
            return false;
        }
        for(int i = 0; i < size(); i ++){
            if(this.get(i) != row.get(i)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Boolean b: booleanList){
            if(b==true){
                sb.append("+");
            }else {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}
