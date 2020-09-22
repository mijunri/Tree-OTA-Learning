package learning;

import timeword.DelayTimeWord;
import ota.Location;
import ota.OTA;
import timeword.LogicTimeWord;
import util.TimeWordUtil;


public class SmartMembership {
    private OTA teacher;
    private int count;

    public SmartMembership(OTA teacher){
        this.teacher = teacher;
        count = 0;
    }


    public Answer answer(DelayTimeWord delayTimeWords) {
        count++;
        LogicTimeWord logicTimeWord = TimeWordUtil.tranToLogic(teacher, delayTimeWords);
        Location location = teacher.getLocation(logicTimeWord);
        return new Answer(logicTimeWord,location.isAccept());
    }

    public boolean answer(LogicTimeWord logicTimeWord){
        count++;
        Location location = teacher.getLocation(logicTimeWord);
        return location.isAccept();
    }

    public int getCount() {
        return count;
    }
}
