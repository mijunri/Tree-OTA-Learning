package ota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeGuard {

    public static final int MAX_TIME = 1000;

    private boolean leftOpen;

    private boolean rightOpen;

    private int left;

    private int right;


    public TimeGuard(String pattern){
        pattern = pattern.trim();
        if(pattern.charAt(0) == '['){
            setLeftOpen(false);
        }else if(pattern.charAt(0) == '('){
            setLeftOpen(true);
        }else {
            throw new RuntimeException("guard pattern error");
        }
        int size = pattern.length();
        if(pattern.charAt(size-1) == ']'){
            setRightOpen(false);
        }else if(pattern.charAt(size-1) == ')'){
            setRightOpen(true);
        }else {
            throw new RuntimeException("guard pattern error");
        }
        String[] numbers = pattern.split("\\,|\\[|\\(|\\]|\\)");
        int left = Integer.parseInt(numbers[1]);
        int right;
        if(numbers[2].equals("+")){
            right = MAX_TIME;
        }else {
            right = Integer.parseInt(numbers[2]);
        }
        setLeft(left);
        setRight(right);
    }


    public boolean isPass(double value){
        int val = (int)((value+0.05)*10);
        int le = left*10;
        int ri = right*10;
        if(leftOpen && rightOpen){
            if(val > le && val < ri){
                return true;
            }
            return false;
        }
        if(!leftOpen && rightOpen){
            if(val >= le && val < ri){
                return true;
            }
            return false;
        }
        if(leftOpen && !rightOpen){
            if(val > le && val <= ri){
                return true;
            }
            return false;
        }
        if(!leftOpen && !rightOpen){
            if(val >= le && val <= ri){
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(leftOpen){
            stringBuilder.append("(");
        }
        else {
            stringBuilder.append("[");
        }
        stringBuilder.append(left).append(",").append(right);
        if(rightOpen){
            stringBuilder.append(")");
        }else {
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    //求和另一个timeGuard的交集
    public TimeGuard intersection(TimeGuard timeGuard){
        int l,r;
        boolean lo,ro;
        if(left < timeGuard.left){
            l = timeGuard.left;
            lo = timeGuard.leftOpen;
        }else if(left == timeGuard.left){
            l = timeGuard.left;
            lo = leftOpen || timeGuard.leftOpen;
        }else {
            l = left;
            lo = leftOpen;
        }

        if(right > timeGuard.right){
            r = timeGuard.right;
            ro = timeGuard.rightOpen;
        }else if(right == timeGuard.right){
            r = timeGuard.right;
            ro = rightOpen || timeGuard.rightOpen;
        }else {
            r = right;
            ro = rightOpen;
        }

        if(l > r){
            return null;
        }else if(l == r){
            if(lo || ro){
                return null;
            }else {
                return new TimeGuard(lo,ro,l,r);
            }
        }else {
            return new TimeGuard(lo,ro,l,r);
        }
    }

    public TimeGuard copy(){
        return new TimeGuard(leftOpen,rightOpen,left,right);
    }

}
