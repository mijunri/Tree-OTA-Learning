package learning;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedTransducedAccessorFactory;
import equivalence.DeterministicEQ;
import equivalence.EquivalenceQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import ota.OTA;
import timeword.DelayTimeWord;
import timeword.LogicTimeWord;
import tree.ClassificationTree;
import util.OTAUtil;

import java.io.IOException;

@Data
@AllArgsConstructor
public class Learner {
    private SmartMembership membership;
    private EquivalenceQuery equivalenceQuery;
    private ClassificationTree classificationTree;



    public void learn() {
        DelayTimeWord delayCe = null;
        classificationTree.buildHypothesis();
        OTA hypothesis = classificationTree.getHypothesis();
        while ((delayCe = equivalenceQuery.findCounterExample(hypothesis)) != null){
            System.out.println(delayCe);
            LogicTimeWord ce = membership.answer(delayCe).getLogicTimeWord();
            classificationTree.refine(ce);
            classificationTree.buildHypothesis();
            hypothesis = classificationTree.getHypothesis();
            System.out.println(hypothesis);
        }
        System.out.println("learn success");
    }

    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\";
        String path = base+"example.json";
        OTA ota = OTAUtil.getOTAFromJsonFile(path);
        OTAUtil.completeOTA(ota);
        SmartMembership membership = new SmartMembership(ota);
        EquivalenceQuery equivalenceQuery = new DeterministicEQ(ota);
        ClassificationTree classificationTree = new ClassificationTree("h",membership,ota.getSigma());
        Learner learner = new Learner(membership,equivalenceQuery,classificationTree);
        learner.learn();
    }

}
