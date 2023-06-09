package models;

import java.util.Date;

public class Operator {

    private static Operator INSTANCE = null;

    private static long stress;
    private static long attentiveness;



    private Operator() {
        this.stress = 0;
        this.attentiveness = 0;
    }

    public static synchronized Operator getInstance() {

        if (INSTANCE == null)
            INSTANCE = new Operator();

        return INSTANCE;
    }

    public static String getCurrentOperatorState() {
        return INSTANCE.toString();
    }
    
    public static long getstress(){
        return stress;
    }
    
    public static long getStress(){
        return INSTANCE.getstress();
    }
    
    public static long getattentiveness(){
        return attentiveness;
    }
    
    public static long getAttentiveness(){
        return INSTANCE.getattentiveness();
    }

    public boolean updateStress(long updatedStress) {
        this.stress = updatedStress;
        return true;
    }

    public boolean updatedAttentiveness(long updatedAttentiveness) {
        this.attentiveness = updatedAttentiveness;
        return true;
    }

    @Override
    public String toString() {
        return ("S: " + this.stress + "--A: " + this.attentiveness + "--T: " + (int) (new Date().getTime()/1000));
        // To convert to a date object: date = new Date(((long)this.timestamp)*1000L);
    }


    // To convert to a date object: date = new Date(((long)this.timestamp)*1000L);
}