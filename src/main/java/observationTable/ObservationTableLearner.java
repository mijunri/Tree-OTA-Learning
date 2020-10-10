package observationTable;

import equivalence.DeterministicEQ;
import equivalence.EquivalenceQuery;
import membership.SmartMembership;
import lombok.AllArgsConstructor;
import lombok.Data;
import ota.OTA;
import timeword.DelayTimeWord;
import timeword.LogicTimeWord;
import util.OTAUtil;
import util.TimeWordUtil;

import java.io.IOException;


@Data
@AllArgsConstructor
public class ObservationTableLearner {
    private SmartMembership membership;
    private EquivalenceQuery equivalenceQuery;
    private ObservationTable observationTable;
    private OTA teacher;

    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\";
        String path = base+"7_2_10\\7_2_10-10.json";
        OTA ota = OTAUtil.getOTAFromJsonFile(path);
        OTAUtil.completeOTA(ota);
        System.out.println(ota);
        SmartMembership membership = new SmartMembership(ota);
        EquivalenceQuery equivalenceQuery = new DeterministicEQ(ota);
        ObservationTable observationTable = new ObservationTable("h",membership,ota.getSigma());

        ObservationTableLearner lerner= new ObservationTableLearner(membership,equivalenceQuery,observationTable,ota);
        lerner.learn();
    }

    public void learn() {
        long start = System.currentTimeMillis();
        DelayTimeWord delayCe = null;
        observationTable.learning();
        observationTable.buildHypothesis();
        OTA hypothesis = observationTable.getHypothesis();
//        observationTable.show();
//        System.out.println(hypothesis);
        while ((delayCe = equivalenceQuery.findCounterExample(hypothesis)) != null){
//            System.out.println(delayCe);
            LogicTimeWord ce;
            if (teacher.getLocation(delayCe).isAccept()){
                ce = TimeWordUtil.tranToLogic(teacher,delayCe);
            }else {
                ce = TimeWordUtil.tranToLogic(hypothesis, delayCe);
            }
//            System.out.println(ce);
            observationTable.refine(ce);
//            observationTable.show();
            observationTable.buildHypothesis();
            hypothesis = observationTable.getHypothesis();
//            System.out.println(hypothesis);
        }
        System.out.println("table learn success");
    }


    public double getMembershipCount() {
        return membership.getCount();
    }

    public double getEquivalenceQueryCount() {
        return equivalenceQuery.getCount();
    }
}
