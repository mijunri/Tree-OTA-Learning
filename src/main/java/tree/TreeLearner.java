package tree;

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
public class TreeLearner {
    private SmartMembership membership;
    private EquivalenceQuery equivalenceQuery;
    private ClassificationTree classificationTree;
    private OTA teacher;

    public void learn() {
        DelayTimeWord delayCe = null;
        classificationTree.buildHypothesis();
        OTA hypothesis = classificationTree.getHypothesis();
//        System.out.println(hypothesis);
        while ((delayCe = equivalenceQuery.findCounterExample(hypothesis)) != null){
//            System.out.println(delayCe);
            LogicTimeWord ce;
            ce = TimeWordUtil.tranToLogic(teacher,delayCe);
//            System.out.println(ce);
            classificationTree.refine(ce);
            classificationTree.buildHypothesis();
            hypothesis = classificationTree.getHypothesis();
//            System.out.println(hypothesis);
            while (teacher.getLocation(ce).isAccept() != hypothesis.getLocation(ce).isAccept()){
                classificationTree.refine(ce);
                classificationTree.buildHypothesis();
                hypothesis = classificationTree.getHypothesis();
//                System.out.println(hypothesis);
            }
        }
        System.out.println("tree learn success");
    }
//
    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\";
        for(int i = 1; i <= 10; i++){
            String path = base+"14_4_20\\14_4_20-"+i+".json";
            OTA ota = OTAUtil.getOTAFromJsonFile(path);
            OTAUtil.completeOTA(ota);
//            System.out.println(ota);
            SmartMembership membership = new SmartMembership(ota);
            EquivalenceQuery equivalenceQuery = new DeterministicEQ(ota);
            ClassificationTree classificationTree = new ClassificationTree("h",membership,ota.getSigma(),ota);
            TreeLearner learner = new TreeLearner(membership,equivalenceQuery,classificationTree,ota);
            learner.learn();
            System.out.println(i +"-th dTree:");
            System.out.println("membership:"+learner.getMembershipCount());
            System.out.println("equivalence:"+learner.getEquivalenceQueryCount());
            System.out.println("**********************************");
        }

    }

    public double getMembershipCount() {
        return membership.getCount();
    }

    public double getEquivalenceQueryCount() {
        return equivalenceQuery.getCount();
    }
}
