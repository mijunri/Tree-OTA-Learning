package membership;

import ota.Location;
import ota.OTA;
import timeword.LogicTimeWord;
import timeword.ResetLogicTimeWord;
import util.TimeWordUtil;

public class NormalMembership {
    private OTA teacher;
    private int count;

    public NormalMembership(OTA teacher){
        this.teacher = teacher;
        count = 0;
    }

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
