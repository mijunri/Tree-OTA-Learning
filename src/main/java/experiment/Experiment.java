package experiment;

import equivalence.DeterministicEQ;
import equivalence.EquivalenceQuery;
import membership.SmartMembership;
import observationTable.ObservationTable;
import observationTable.ObservationTableLearner;
import ota.OTA;
import tree.ClassificationTree;
import tree.TreeLearner;
import util.OTAUtil;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Experiment {
    public static void main(String[] args) throws IOException {
        String base = ".\\src\\main\\resources\\4_4_20\\";
        double tranSize = 0;
        double membershipCount1 = 0;
        double equivalenceCount1 = 0;

        double membershipCount2 = 0;
        double equivalenceCount2 = 0;

        for(int i = 1; i <= 10; i++){
            String path = base+"4_4_20-"+i+".json";
            OTA teacher = OTAUtil.getOTAFromJsonFile(path);
            OTAUtil.completeOTA(teacher);
            System.out.println(i+"th ota");
            SmartMembership membership1 = new SmartMembership(teacher);
            SmartMembership membership2 = new SmartMembership(teacher);
            EquivalenceQuery equivalenceQuery1 = new DeterministicEQ(teacher);
            EquivalenceQuery equivalenceQuery2 = new DeterministicEQ(teacher);
            ObservationTable observationTable = new ObservationTable("hypothesis1",membership1,teacher.getSigma());
            ClassificationTree dTree = new ClassificationTree("hypothesis2",membership2,teacher.getSigma(),teacher);
            ObservationTableLearner observationTableLearner = new ObservationTableLearner(membership1,equivalenceQuery1,observationTable,teacher);
            TreeLearner tttLearner = new TreeLearner(membership2,equivalenceQuery2,dTree,teacher);

            observationTableLearner.learn();
            tttLearner.learn();

            System.out.println(teacher.getName()+"-"+i);
            System.out.println("observationTable:");
            System.out.println("membership:"+observationTableLearner.getMembershipCount());
            System.out.println("equivalence:"+observationTableLearner.getEquivalenceQueryCount());
            System.out.println("dTree:");
            System.out.println("membership:"+tttLearner.getMembershipCount());
            System.out.println("equivalence:"+tttLearner.getEquivalenceQueryCount());
            System.out.println("**********************************");
            tranSize += teacher.getTransitionList().size();
            membershipCount1+=observationTableLearner.getMembershipCount();
            membershipCount2+=tttLearner.getMembershipCount();
            equivalenceCount1+=observationTableLearner.getEquivalenceQueryCount();
            equivalenceCount2+=tttLearner.getEquivalenceQueryCount();
        }
        membershipCount1 /= 10;
        membershipCount2 /= 10;
        equivalenceCount1 /= 10;
        equivalenceCount2 /= 10;
        tranSize /= 10;
        StringBuilder sb = new StringBuilder();
        sb.append("the mean number of transition is:").append(tranSize).append("\n")
                .append("observationTable:: \n")
                .append("\tmembership:").append(membershipCount1).append(",\n")
                .append("\tequivalence:").append(equivalenceCount1).append("\n")
                .append("Dtree:: \n")
                .append("\tmembership:").append(membershipCount2).append(",\n")
                .append("\tequivalence:").append(equivalenceCount2).append("\n");
        String resultPath = base+"result\\result.txt";

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath)));
        bw.write(sb.toString());
        bw.close();
    }
}
