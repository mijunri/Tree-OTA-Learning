package normalteacher;

import equivalence.DeterministicEQ;
import equivalence.EquivalenceQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import membership.NormalMembership;
import membership.SmartMembership;
import ota.OTA;
import tree.ClassificationTree;
import util.OTAUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormalLearner {
    private NormalMembership membership;
    private EquivalenceQuery equivalenceQuery;
    private String name;
    private Set<String> sigma;
    private OTA teacher;

    public void learn(){
        LinkedList<ClassificationTree> queue = new LinkedList<>();

    }

    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\";
        String path = base+"14_4_20\\14_4_20-1.json";
        OTA ota = OTAUtil.getOTAFromJsonFile(path);
        OTAUtil.completeOTA(ota);
        System.out.println(ota);
        NormalMembership membership = new NormalMembership(ota);
        EquivalenceQuery equivalenceQuery = new DeterministicEQ(ota);
        NormalLearner normalLearner = new NormalLearner(membership,equivalenceQuery,"h",ota.getSigma(),ota);
        normalLearner.learn();
    }
}
