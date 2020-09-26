package util;

import org.apache.commons.lang.StringUtils;
import ota.Location;
import ota.TimeGuard;
import ota.Transition;

import java.util.ArrayList;
import java.util.List;

public class TransitionUtil {

    //相同location出发的，相同的symbol且互不包含的迁移的补集 , 迁移不能为空集
    public static List<Transition> complementary(List<Transition> transitionList, Location targetLocation){
        Transition pre = transitionList.get(0);
        for(int i = 1; i < transitionList.size(); i++){
            Transition cur = transitionList.get(i);
            if (!StringUtils.equals(cur.getSymbol(),pre.getSymbol())){
                throw new RuntimeException("迁移数组出错");
            }
            pre = cur;
        }

        String symbol = pre.getSymbol();
        Location sourceLocation = pre.getSourceLocation();
        List<TimeGuard> timeGuardList = TimeGuardUtil.obtainGuardList(transitionList);
        List<TimeGuard> complementaryGuardList = TimeGuardUtil.complementary(timeGuardList);

        List<Transition> complementaryTranList = new ArrayList<>();
        for(TimeGuard timeGuard: complementaryGuardList){
            Transition t = new Transition(sourceLocation, targetLocation, timeGuard,symbol, "r");
            complementaryTranList.add(t);
        }

        return complementaryTranList;
    }
}
