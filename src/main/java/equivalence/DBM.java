package equivalence;

import ota.TimeGuard;
import equivalence.ta.Clock;

import java.util.List;

public class DBM {

    private final List<Clock> clockList;
    private Value[][] matrix;

    public DBM(List<Clock> clockList, Value[][] matrix) {
        this.clockList = clockList;
        this.matrix = matrix;
    }

    public static DBM getInitDBM(List<Clock> clockList){
        int n = clockList.size()+1;
        Value[][] matrix = new Value[n][n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                matrix[i][j] = new Value(0,true);
            }
        }
        for(int i = 1; i < n; i++){
            matrix[i][0] = new Value(Integer.MAX_VALUE,false);
        }
        return new DBM(clockList,matrix);
    }

    public List<Clock> getClockList() {
        return clockList;
    }

    public Value[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(Value[][] matrix) {
        this.matrix = matrix;
    }

    //Floyds 算法
    public void canonical(){
        for(int k = 0; k < size(); k++){
            for(int i = 0; i < size(); i++){
                for(int j = 0; j < size(); j++){
                    Value v1 = matrix[i][j];
                    Value v = Value.add(matrix[i][k],matrix[k][j]);
                    if(matrix[i][j].compareTo(v) > 0){
                        matrix[i][j] = v;
                    }
                }
            }
        }
    }

    //up操作
    public void up(){
        for(int i = 1; i <size();i++ ){
            matrix[i][0] = new Value(Integer.MAX_VALUE,false);
        }
    }

    //and操作
    public void and(Clock c, TimeGuard timeGuard){
        int index = clockList.indexOf(c);
        Value right = new Value(timeGuard.getUpperBound(),!timeGuard.isUpperBoundOpen());
        if(right.compareTo(matrix[index+1][0]) < 0){
            matrix[index+1][0] = right;
        }
        Value left = new Value(timeGuard.getLowerBound()*-1,!timeGuard.isLowerBoundOpen());
        if(left.compareTo(matrix[0][index+1]) < 0){
            matrix[0][index+1] = left;
        }
    }

    //reset操作
    public void reset(Clock c){
        int index = clockList.indexOf(c)+1;
        for(int i = 0; i < size(); i++){
            matrix[index][i] = matrix[0][i];
            matrix[i][index] = matrix[i][0];
        }
    }

    public int size(){
        return clockList.size()+1;
    }

    //必须是canonical的才能判断
    public Boolean isConsistent(){
        for(int i = 0; i < size(); i++){
            if(matrix[i][i].compareTo(new Value(0,true)) < 0){
                return false;
            }
        }
        return true;
    }

    public boolean include(DBM dbm){
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                if(matrix[i][j].compareTo(dbm.matrix[i][j]) < 0){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("the dbm matrix is:\n");
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(matrix[i][j].getValue() == Integer.MAX_VALUE){
                    sb.append("∞").append("<").append(" \t");
                }else {
                    sb.append(matrix[i][j].getValue());
                    if(matrix[i][j].isEqual()){
                        sb.append(" <=");
                    }else {
                        sb.append(" <");
                    }
                    sb.append(" \t");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public DBM copy(){
        Value[][] matrix1 = new Value[size()][size()];
        for(int i = 0; i < size(); i++){
            for(int j = 0; j < size(); j++){
                matrix1[i][j] = new Value(matrix[i][j].getValue(),matrix[i][j].isEqual());
            }
        }
        return new DBM(clockList,matrix1);
    }
}

