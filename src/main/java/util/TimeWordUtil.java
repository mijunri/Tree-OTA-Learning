package util;

import ota.Location;
import ota.OTA;
import ota.Transition;
import timeword.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeWordUtil {
    public static Set<DelayTimeWord> getAllPrefixes(DelayTimeWord delayTimeWord){
        int len = delayTimeWord.size();
        Set<DelayTimeWord> prefixes = new HashSet<>();
        List<DelayAction> actionList = new ArrayList<>();
        for(int i = 0; i < len; i ++){
            actionList.add(delayTimeWord.get(i));
            DelayTimeWord prefixWord = new DelayTimeWord(actionList);
            prefixes.add(prefixWord);
        }
        return prefixes;
    }

    public static Set<LogicTimeWord> getAllPrefixes(LogicTimeWord logicTimeWord) {
        int len = logicTimeWord.size();
        Set<LogicTimeWord> prefixes = new HashSet<>();
        List<LogicAction> actionList = new ArrayList<>();
        for(int i = 0; i < len; i ++){
            actionList.add(logicTimeWord.get(i));
            LogicTimeWord prefixWord = new LogicTimeWord(new ArrayList<>(actionList));
            prefixes.add(prefixWord);
        }
        return prefixes;
    }


    public static DelayTimeWord concat(DelayTimeWord prefix, DelayTimeWord suffix){
        List<DelayAction> prefixList = prefix.getActionList();
        List<DelayAction> suffixList = suffix.getActionList();
        List<DelayAction> list = new ArrayList<>();
        list.addAll(prefixList);
        list.addAll(suffixList);
        return new DelayTimeWord(list);
    }

    public static LogicTimeWord concat(LogicTimeWord prefix, LogicTimeWord suffix){
        List<LogicAction> prefixList = prefix.getActionList();
        List<LogicAction> suffixList = suffix.getActionList();
        List<LogicAction> list = new ArrayList<>();
        list.addAll(prefixList);
        list.addAll(suffixList);
        return new LogicTimeWord(list);
    }


    public static DelayTimeWord concat(DelayTimeWord prefix, DelayAction suffixWord){
        List<DelayAction> suffixList = new ArrayList<>();
        suffixList.add(suffixWord);
        DelayTimeWord suffix = new DelayTimeWord(suffixList);
        return concat(prefix,suffix);
    }

    public static LogicTimeWord concat(LogicTimeWord prefix, LogicAction suffixWord){
        List<LogicAction> suffixList = new ArrayList<>();
        suffixList.add(suffixWord);
        LogicTimeWord suffix = new LogicTimeWord(suffixList);
        return concat(prefix,suffix);
    }

    public static DelayTimeWord concat(DelayAction action, DelayTimeWord suffixWord){
        List<DelayAction> actionList = new ArrayList<>();
        actionList.add(action);
        DelayTimeWord prefix = new DelayTimeWord(actionList);
        return concat(prefix,suffixWord);
    }

    public static LogicTimeWord concat(LogicAction action, LogicTimeWord suffixWord){
        List<LogicAction> actionList = new ArrayList<>();
        actionList.add(action);
        LogicTimeWord prefix = new LogicTimeWord(actionList);
        return concat(prefix,suffixWord);
    }

    public static DelayTimeWord concat(DelayAction action1, DelayAction action2){
        List<DelayAction> actionList  = new ArrayList<>();
        actionList.add(action1);
        actionList.add(action2);
        return new DelayTimeWord(actionList);
    }

    public static LogicTimeWord concat(LogicAction action1, LogicAction action2){
        List<LogicAction> actionList  = new ArrayList<>();
        actionList.add(action1);
        actionList.add(action2);
        return new LogicTimeWord(actionList);
    }

    private static ResetLogicTimeWord concat(ResetLogicTimeWord resetLogicTimeWord, ResetLogicAction resetLogicAction) {
        List<ResetLogicAction> actionList = new ArrayList<>();
        actionList.addAll(resetLogicTimeWord.getActionList());
        actionList.add(resetLogicAction);
        return new ResetLogicTimeWord(actionList);
    }


    public static LogicTimeWord tranToLogic(OTA ota, DelayTimeWord delayTimeWord){
        List<DelayAction> actionList = delayTimeWord.getActionList();
        LogicTimeWord word = LogicTimeWord.emptyWord();
        double baseValue = 0;
        Location location = ota.getInitLocation();

        for(DelayAction action: actionList){
            String symbol = action.getSymbol();
            double value = action.getValue() + baseValue;

            List<Transition> transitions = ota.getTransitions(location,action.getSymbol(),null);
            for(Transition t: transitions){
                if(t.isPass(symbol,value)){
                    LogicAction logicAction = new LogicAction(symbol,value);
                    word = TimeWordUtil.concat(word,logicAction);
                    baseValue = t.isReset()?0:value;
                    location = t.getTargetLocation();
                    continue;
                }
            }

        }
        return word;
    }

    public static DelayTimeWord tranToDelay(OTA ota, LogicTimeWord logicTimeWord){
        List<LogicAction> logicActionList = logicTimeWord.getActionList();
        DelayTimeWord word = DelayTimeWord.emptyWord();
        double baseValue = 0;
        Location location = ota.getInitLocation();
        for(LogicAction logicAction: logicActionList){
            List<Transition> transitions = ota.getTransitions(location,logicAction.getSymbol(),null);
            String symbol = logicAction.getSymbol();
            double value = logicAction.getValue();
            for(Transition t: transitions){
                if(t.isPass(symbol,value)){
                    DelayAction delayAction = new DelayAction(symbol,value-baseValue);
                    word = TimeWordUtil.concat(word,delayAction);
                    baseValue = t.isReset()?0:value;
                    location = t.getTargetLocation();
                    break;
                }
            }
        }
        return word;
    }

    public static DelayTimeWord tranToDelay(LogicTimeWord logicTimeWord){
        List<LogicAction> logicActionList = logicTimeWord.getActionList();
        DelayTimeWord word = DelayTimeWord.emptyWord();
//        double clock = 0;
//        for(LogicAction logicAction: logicActionList){
//            String symbol = logicAction.getSymbol();
//            double value = logicAction.getValue()-clock;
//            DelayAction delayAction = new DelayAction(symbol,value);
//            word = TimeWordUtil.concat(word, delayAction);
//            if (logicAction.isReset()){
//                clock = 0;
//            }else {
//                clock = logicAction.getValue();
//            }
//        }
        return word;
    }

    public static ResetLogicTimeWord tranToReset(OTA ota, LogicTimeWord logicTimeWord){
        if (logicTimeWord.isEmpty()){
            return ResetLogicTimeWord.emptyWord();
        }
        List<LogicAction> logicActionList = logicTimeWord.getActionList();
        ResetLogicTimeWord resetLogicTimeWord = ResetLogicTimeWord.emptyWord();
        Location location = ota.getInitLocation();
        for(LogicAction logicAction: logicActionList){
            List<Transition> transitions = ota.getTransitions(location,logicAction.getSymbol(),null);
            for(Transition t: transitions){
                if(t.isPass(logicAction)){
                    ResetLogicAction resetLogicAction = new ResetLogicAction(logicAction,t.isReset());
                    resetLogicTimeWord = TimeWordUtil.concat(resetLogicTimeWord,resetLogicAction);
                    location = t.getTargetLocation();
                    break;
                }
            }
        }
        return resetLogicTimeWord;
    }


}
