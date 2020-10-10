package membership;

import ota.Location;
import ota.OTA;
import timeword.LogicTimeWord;
import timeword.ResetLogicTimeWord;
import util.TimeWordUtil;


public class SmartMembership {
    private OTA teacher;
    private int count;

    public SmartMembership(OTA teacher){
        this.teacher = teacher;
        count = 0;
    }

//    public Answer answer(DelayTimeWord delayTimeWords) {
//        count++;
//        LogicTimeWord logicTimeWord = TimeWordUtil.tranToLogic(teacher, delayTimeWords);
//        Location location = teacher.getLocation(logicTimeWord);
//        return null;
////        return new Answer(logicTimeWord,location.isAccept());
//    }

    public Answer answer(LogicTimeWord logicTimeWord){
        count++;
        Location location = teacher.getLocation(logicTimeWord);
        ResetLogicTimeWord resetLogicTimeWord = TimeWordUtil.tranToReset(teacher, logicTimeWord);
        Answer answer = new Answer(resetLogicTimeWord,location.isAccept());
        return answer;
    }

    public int getCount() {
        return count;
    }
}
