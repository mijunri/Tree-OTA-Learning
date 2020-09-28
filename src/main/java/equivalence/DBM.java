package equivalence;

import equivalence.ta.TaTimeGuard;
import lombok.Data;
import ota.TimeGuard;
import equivalence.ta.Clock;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class DBM {

    private final List<Clock> clockList;
    private Value[][] matrix;

    public DBM(List<Clock> clockList, Value[][] matrix) {
        this.clockList = clockList;
        this.matrix = matrix;
    }

    public static DBM getInitDBM(List<Clock> clockList){
        //因为有零时钟，所以矩阵大小为时钟数加一
        int n = clockList.size()+1;
        Value[][] matrix = new Value[n][n];

        //初始化为>=0
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                matrix[i][j] = new Value(0,true);
            }
        }

        //设置初始范围 <正无穷
        for(int i = 1; i < n; i++){
            matrix[i][0] = new Value(Integer.MAX_VALUE,false);
        }
        return new DBM(clockList,matrix);
    }

    //Floyds算法求最短边
    public void canonical(){
        for(int k = 0; k < size(); k++){
            for(int i = 0; i < size(); i++){
                for(int j = 0; j < size(); j++){
                    Value v = Value.add(matrix[i][k],matrix[k][j]);
                    if(matrix[i][j].compareTo(v) > 0){
                        matrix[i][j] = v;
                    }
                }
            }
        }
    }

    //up操作,把上限变为正无穷
    public void up(){
        for(int i = 1; i <size();i++ ){
            matrix[i][0] = new Value(Integer.MAX_VALUE,false);
        }
    }

    //and操作
    public void and(Clock c, TimeGuard timeGuard){
        int index = clockList.indexOf(c);
        Value upperBound = new Value(timeGuard.getUpperBound(),!timeGuard.isUpperBoundOpen());
        if(upperBound.compareTo(matrix[index+1][0]) < 0){
            matrix[index+1][0] = upperBound;
        }
        Value lowerBound = new Value(timeGuard.getLowerBound()*(-1),!timeGuard.isLowerBoundOpen());
        if(lowerBound.compareTo(matrix[0][index+1]) < 0){
            matrix[0][index+1] = lowerBound;
        }
    }

    //and操作，TaTimeGuard
    public void and(TaTimeGuard taTimeGuard){
        for(Map.Entry<TimeGuard, Clock> entry : taTimeGuard.entrySet()){
            and(entry.getValue(), entry.getKey());
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

    //reset操作，reset一个集合
    public void reset(Set<Clock> clockSet){
        for (Clock c : clockSet){
            reset(c);
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

    //包含关系判断
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

