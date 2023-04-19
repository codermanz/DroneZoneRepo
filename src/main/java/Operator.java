import java.util.Date;

public class Operator{
    int stress;
    int attentiveness;
    int timestamp;

    public Operator(int stress, int attentiveness) {
        this.stress = stress;
        this.attentiveness = attentiveness;
        // To convert to a date object: date = new Date(((long)this.timestamp)*1000L);
        this.timestamp = (int) (new Date().getTime()/1000);
    }

    @Override
    public String toString() {
        return ("S: " + this.stress + "--A: " + this.attentiveness + "--T: " + this.timestamp);
    }

}
