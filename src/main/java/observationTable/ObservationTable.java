package observationTable;


import learning.Answer;
import learning.SmartMembership;
import lombok.Data;
import ota.Location;
import ota.OTA;
import ota.TimeGuard;
import ota.Transition;
import sun.rmi.runtime.Log;
import timeword.LogicAction;
import timeword.LogicTimeWord;
import timeword.ResetLogicTimeWord;
import util.OTAUtil;
import util.TimeWordUtil;
import util.comparator.TranComparator;

import java.util.*;

@Data
public class ObservationTable {
    private String name;
    private Set<String> sigma;
    private SmartMembership membership;
    private List<LogicTimeWord> suffixes;
    private Set<LogicTimeWord> s;
    private Set<LogicTimeWord> r;
    private Map<LogicTimeWord,ResetLogicTimeWord> wordMap;
    private Map<Pair, Boolean> answers;
    private OTA hypothesis;

    public ObservationTable(String name, SmartMembership membership , Set<String> sigma) {
        this.name = name;
        this.sigma = sigma;
        this.membership = membership;
        this.suffixes = new ArrayList<>();
        wordMap = new HashMap<>();
        suffixes.add(LogicTimeWord.emptyWord());
        s = new HashSet<>();
        s.add(LogicTimeWord.emptyWord());
        r = new HashSet<>();
        for(String action:sigma){
            LogicAction logicAction = new LogicAction(action,0);
            LogicTimeWord timeWord = new LogicTimeWord(logicAction);
            r.add(timeWord);
        }
        answers = new HashMap<>();
    }

    public void learning(){
        fillTable();
        while (!isPrepared()){
            if(!isClosed()){
                makeClosed();
            }
            if(!isConsistent()){
                makeConsistent();
            }
            if(!isEvidClosed()){
                makeEvidClosed();
            }
        }
    }



    public void fillTable(){
        fillTable(s);
        fillTable(r);
    }

    private void fillTable(Set<LogicTimeWord> set){
        for(LogicTimeWord prefixWord:set){
            for(LogicTimeWord suffixWord:suffixes){
                Pair pair = new Pair(prefixWord,suffixWord);
                if(!answers.containsKey(pair)){
                    LogicTimeWord timeWords = pair.timeWord();
                    Answer answer = membership.answer(timeWords);
                    int len = suffixWord.size();
                    int size = answer.getResetLogicTimeWord().size();
                    ResetLogicTimeWord resetLogicTimeWord = answer.getResetLogicTimeWord().subWord(0,size-len);
                    wordMap.put(prefixWord,resetLogicTimeWord);
                    answers.put(pair,answer.isAccept());
                }
            }
        }
    }


    public boolean isPrepared(){
        return isClosed()&&isConsistent()&&isEvidClosed();
    }


    public Row getRow(LogicTimeWord logicTimeWord){
        if(!s.contains(logicTimeWord) && !r.contains(logicTimeWord)){
            return null;
        }
        Row row = new Row();
        for(LogicTimeWord suffixWord: suffixes){
            Pair pair = new Pair(logicTimeWord,suffixWord);
            boolean answer = answers.get(pair);
            row.add(answer);
        }
        return row;
    }

    public Set<Row> getRowSet(Set<LogicTimeWord> timeWordSet){
        Set<Row> rowSet = new HashSet<>();
        if(timeWordSet != null && !timeWordSet.isEmpty()){
            for(LogicTimeWord word: timeWordSet){
                rowSet.add(getRow(word));
            }
        }
        return rowSet;
    }

    public boolean isClosed() {
        Set<Row> sRowSet = getRowSet(s);
        Set<Row> rRowSet = getRowSet(r);
        return sRowSet.containsAll(rRowSet);
    }

    public void makeClosed(){
        Set<Row> sRowSet = getRowSet(s);
        for(LogicTimeWord word:r){
            Row row = getRow(word);
            if(!sRowSet.contains(row)){
                s.add(word);
                r.remove(word);
                for(String action: sigma){
                    LogicAction logicAction = new LogicAction(action,0);
                    LogicTimeWord logicTimeWord = TimeWordUtil.concat(word,logicAction);
                    if(!s.contains(logicTimeWord) && !r.contains(logicTimeWord)){
                        r.add(logicTimeWord);
                    }
                }
                break;
            }
        }
        fillTable(r);
    }

    private List<LogicTimeWord> unConsistentCouple = null;
    private LogicAction key = null;
    public boolean isConsistent() {
        unConsistentCouple = new ArrayList<>();
        Set<LogicAction> logicActionSet = getLastActionSet();
        List<LogicTimeWord> list = getPrefixList();
        for(int i = 0; i < list.size(); i++){
            Row row1 = getRow(list.get(i));
            for(int j = i + 1; j < list.size(); j ++){
                Row row2 = getRow(list.get(j));
                if(row1.equals(row2)){
                    for(LogicAction action: logicActionSet){
                        LogicTimeWord word1 = TimeWordUtil.concat(list.get(i),action);
                        LogicTimeWord word2 = TimeWordUtil.concat(list.get(j),action);
                        Row newRow1 = getRow(word1);
                        Row newRow2 = getRow(word2);
                        if(newRow1!=null && newRow2!=null && !newRow1.equals(newRow2)){
                            unConsistentCouple.add(word1);
                            unConsistentCouple.add(word2);
                            key = action;
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }



    public void makeConsistent(){
        LogicTimeWord word1 = unConsistentCouple.get(0);
        LogicTimeWord word2 = unConsistentCouple.get(1);
        for(LogicTimeWord w:suffixes){
            Pair pair1 = new Pair(word1,w);
            Pair pair2 = new Pair(word2,w);

            boolean answer1 = answers.get(pair1);
            boolean answer2 = answers.get(pair2);
            if(answer1 != answer2){
                LogicTimeWord word = TimeWordUtil.concat(key,w);
                suffixes.add(word);
                break;
            }
        }
        fillTable();
    }


    private LogicTimeWord unEvidWords;
    public boolean isEvidClosed() {
        for(LogicTimeWord sPrefix:s){
            for(LogicTimeWord suffix:suffixes){
                LogicTimeWord logicTimeWord= TimeWordUtil.concat(sPrefix,suffix);
                if(!s.contains(logicTimeWord) && !r.contains(logicTimeWord)){
                    unEvidWords = logicTimeWord;
                    return false;
                }
            }
        }
        return true;
    }

    public void makeEvidClosed(){
        r.add(unEvidWords);
        fillTable(r);
    }



    public void refine(LogicTimeWord ce){
        Set<LogicTimeWord> prefixesSet = TimeWordUtil.getAllPrefixes(ce);
        prefixesSet.removeAll(s);
        r.addAll(prefixesSet);
        fillTable(r);
        while (!isPrepared()){
            if(!isClosed()){
                makeClosed();
            }
            if(!isConsistent()){
                makeConsistent();
            }
            if(!isEvidClosed()){
                makeEvidClosed();
            }
        }
    }


    public void buildHypothesis(){
        List<Location> locationList = new ArrayList<>();
        List<Transition> transitionList = new ArrayList<>();

        Map<Row, Location> rowLocationMap = new HashMap<>();
        //根据s中的row来创建Location；
        Set<Row> rowSet = new HashSet<>();
        int id = 1;
        for(LogicTimeWord sWords: s){
            Pair pair = new Pair(sWords, LogicTimeWord.emptyWord());
            Row row = getRow(sWords);
            if(!rowSet.contains(row)){
                rowSet.add(row);
                boolean init = getRow(sWords).equals(getRow(LogicTimeWord.emptyWord()));
                boolean accepted = answers.get(pair)== true;
                Location location = new Location(id,name+id,init,accepted);
                locationList.add(location);
                rowLocationMap.put(row,location);
                id++;
            }
        }

        //根据观察表来创建Transition
        Set<LogicAction> lastActionSet = getLastActionSet();
        List<LogicTimeWord> prefixList = getPrefixList();
        for(LogicTimeWord prefix: prefixList){
            Row row1 = getRow(prefix);
            Location sourceLocation = rowLocationMap.get(row1);
            for(LogicAction action:lastActionSet){
                LogicTimeWord logicTimeWord = TimeWordUtil.concat(prefix,action);
                if(prefixList.contains(logicTimeWord)){
                    Row row2 = getRow(logicTimeWord);
                    if (row2 == null){
                        continue;
                    }
                    Location targetLocation = rowLocationMap.get(row2);
                    String symbol = action.getSymbol();
                    TimeGuard timeGuard = TimeGuard.bottomGuard(action);
                    ResetLogicTimeWord resetLogicTimeWord = wordMap.get(logicTimeWord);
                    String reset = resetLogicTimeWord.isReset()?"r":"n";
                    Transition transition = new Transition(sourceLocation,targetLocation,timeGuard,symbol,reset);
                    if(!transitionList.contains(transition)){
                        transitionList.add(transition);
                    }
                }
            }
        }

        OTA evidenceRTA = new OTA(name,sigma,locationList,transitionList);
        OTA hypothesis = OTAUtil.evidToOTA(evidenceRTA);
//        OTAUtil.completeOTA(hypothesis);
        this.hypothesis = hypothesis;
    }


    public void show(){
        List<String> stringList = new ArrayList<>();
        List<String> suffixStringList = new ArrayList<>();
        List<LogicTimeWord> prefixList = getPrefixList();
        int maxLen = 0;
        for(LogicTimeWord word:prefixList){
            ResetLogicTimeWord resetLogicTimeWord = wordMap.get(word);
            String s = resetLogicTimeWord.toString();
            stringList.add(s);
            maxLen = maxLen > s.length()?maxLen:s.length();
        }
        for(LogicTimeWord words:suffixes){
            String s = words.toString();
            suffixStringList.add(s);
        }


        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < maxLen; i++){
            sb.append(" ");
        }
        sb.append("|");
        for(String s:suffixStringList){
            sb.append(s);
            sb.append("|");
        }
        sb.append("\n");

        for(int i = 0; i < prefixList.size(); i++){
            String prefixString = stringList.get(i);
            sb.append(prefixString);
            int slen = s.size();
            for(int k = 0; k < maxLen-prefixString.length(); k++){
                sb.append(" ");
            }
            sb.append("|");
            for(int j = 0; j < suffixes.size(); j++){
                Pair pair = new Pair(prefixList.get(i),suffixes.get(j));
                boolean b = answers.get(pair);
                String a = b==true?"+":"-";
                sb.append(a);
                String suffixString = suffixStringList.get(j);
                for(int k = 0; k < suffixString.length()-1;k++){
                    sb.append(" ");
                }
                sb.append("|");
            }
            sb.append("\n");

            if(i == slen-1){
                for(int k = 0; k < maxLen; k++){
                    sb.append("-");
                }
                sb.append("|");
                for(String suffixString : suffixStringList){
                    for(int k = 0; k < suffixString.length();k++){
                        sb.append("-");
                    }
                    sb.append("|");
                }
                sb.append("\n");
            }
        }
        System.out.println(sb);
    }



    private Set<LogicAction> getLastActionSet(){
        Set<LogicTimeWord> sr = getPrefixSet();
        Set<LogicAction> lastActionSet = new HashSet<>();
        for(LogicTimeWord logicTimeWord:sr){
            if(!logicTimeWord.isEmpty()){
                int size = logicTimeWord.size();
                LogicAction last = logicTimeWord.get(logicTimeWord.size()-1);
                lastActionSet.add(last);
            }
        }
        return lastActionSet;
    }

    private Set<LogicTimeWord> getPrefixSet(){
        Set<LogicTimeWord> sr = new HashSet<>();
        sr.addAll(s);
        sr.addAll(r);
        return sr;
    }

    private List<LogicTimeWord> getPrefixList(){
        List<LogicTimeWord> logicTimeWordList = new ArrayList<>();
        logicTimeWordList.addAll(s);
        logicTimeWordList.addAll(r);
        return logicTimeWordList;
    }

}
