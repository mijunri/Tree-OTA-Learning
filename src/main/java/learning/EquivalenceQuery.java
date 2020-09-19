package learning;



import ota.OTA;
import timeword.TimeWord;


public interface EquivalenceQuery {
    int getCount();
    TimeWord findCounterExample(OTA ota);
}
