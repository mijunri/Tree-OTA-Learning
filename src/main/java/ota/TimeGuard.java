package ota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import timeword.LogicAction;
import timeword.ResetLogicAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeGuard {

    public static final int MAX_TIME = 30;

    private boolean lowerBoundOpen;

    private boolean upperBoundOpen;

    private int lowerBound;

    private int upperBound;

    public boolean isLowerBoundClose(){
        return !lowerBoundOpen;
    }

    public boolean isUpperBoundClose(){
        return !upperBoundOpen;
    }

    public TimeGuard(String pattern){
        pattern = pattern.trim();
        int size = pattern.length();
        char firstChar = pattern.charAt(0);
        char lastChar = pattern.charAt(size-1);

        switch (firstChar){
            case '[':
                setLowerBoundOpen(false);
                break;
            case '(':
                setLowerBoundOpen(true);
                break;
            default:
                throw new RuntimeException("guard pattern error");
        }

        switch (lastChar){
            case ']':
                setUpperBoundOpen(false);
                break;
            case ')':
                setUpperBoundOpen(true);
                break;
            default:
                throw new RuntimeException("guard pattern error");
        }

        String[] numbers = pattern.split("\\,|\\[|\\(|\\]|\\)");
        if (numbers.length != 3){
            throw new RuntimeException("guard pattern error");
        }

        int lowerBound, upperBound = 0;

        try{
            String num1 = numbers[1];
            String num2 = numbers[2];
            lowerBound = Integer.parseInt(num1);
            upperBound = num2.equals("+")?MAX_TIME:Integer.parseInt(num2);
        }catch (Exception e){
            throw new RuntimeException("guard pattern error");
        }

        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }

    public static TimeGuard bottomGuard(LogicAction action){
        double time = action.getValue();
        boolean leftOpen,rightOpen;
        int left,right;
        if(time == (int)time){
            leftOpen = false;
            left = (int)time;
            rightOpen = false;
            right = (int)time;
        }
        else {
            leftOpen = true;
            left = (int)time;
            rightOpen = true;
            right = (int)time+1;
        }
        return new TimeGuard(leftOpen,rightOpen,left,right);
    }

    public static TimeGuard bottomGuard(ResetLogicAction action){
        double time = action.getValue();
        boolean leftOpen,rightOpen;
        int left,right;
        if(time == (int)time){
            leftOpen = false;
            left = (int)time;
            rightOpen = false;
            right = (int)time;
        }
        else {
            leftOpen = true;
            left = (int)time;
            rightOpen = true;
            right = (int)time+1;
        }
        return new TimeGuard(leftOpen,rightOpen,left,right);
    }


    //转成整型再比较
    public boolean isPass(double doubleValue){
        int intValue = (int)((doubleValue+0.05)*10);

        int lowerBound = getLowerBound()*10;
        int upperBound = getUpperBound()*10;

        if(isLowerBoundOpen() && isUpperBoundOpen()){
            if(intValue > lowerBound && intValue < upperBound){
                return true;
            }
        }
        if(isLowerBoundClose() && isUpperBoundOpen()){
            if(intValue >= lowerBound && intValue < upperBound){
                return true;
            }
        }
        if(isLowerBoundOpen() && isUpperBoundClose()){
            if(intValue > lowerBound && intValue <= upperBound){
                return true;
            }
        }
        if(isLowerBoundClose() && isUpperBoundClose()){
            if(intValue >= lowerBound && intValue <= upperBound){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(isLowerBoundOpen()){
            stringBuilder.append("(");
        }
        else {
            stringBuilder.append("[");
        }
        stringBuilder.append(lowerBound).append(",").append(upperBound);
        if(isUpperBoundOpen()){
            stringBuilder.append(")");
        }else {
            stringBuilder.append("]");
        }
        return stringBuilder.toString();
    }

    public TimeGuard copy(){
        return new TimeGuard(lowerBoundOpen,upperBoundOpen,lowerBound,upperBound);
    }

}
