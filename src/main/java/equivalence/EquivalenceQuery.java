package equivalence;



import ota.OTA;
import timeword.DelayTimeWord;


public interface EquivalenceQuery {
    int getCount();
    DelayTimeWord findCounterExample(OTA ota);
}
