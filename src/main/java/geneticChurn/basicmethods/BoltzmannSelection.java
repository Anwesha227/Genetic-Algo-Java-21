package geneticChurn.basicmethods;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BoltzmannSelection {
    private int[] round;
    private int pick;

    public BoltzmannSelection(int popsize) {
        round = new int[popsize];
    }

    /* Secondary helper Function:
     * Flip: returns result of a simulated biased coin toss*/


    /*Primary helper Functions:
     * Logistic: the sigmoid function
     * Fthreshold: for calculating objective function threshold from probability gap and temperature
     * Makeshuffle: generating random permutation of specified length
     * Chooseother: Pick individuals from the population that have f values differing from f1 anf f2 by an amount of threshold or more
     */

    /*BoltzmannSelect: Boltzmann tournament selection*/

    private boolean flip(double prob) {
        boolean result;
        Random rand = new Random();
        double r = rand.nextDouble()*1.0;

        if(prob==1) {result=true;}
        else {result=(r<=prob)?true:false;}
        return result;
    }

    /*Primary helpers*/
    public static final double LOGISTIC_LIM = 11.513;

    private double logistic(double x)
    {
        if (x > LOGISTIC_LIM) return 1.0;
        else if (x < LOGISTIC_LIM*(-1)) return 0.0;
        else return (1 / (1 + Math.exp(-x)));
    }


    public static final double DEFAULT_THRESH = 1.0E10;

    @SuppressWarnings("unused")
    private double fthreshold(double gap, double temperature) {
        double probability;

        if (gap<=0) gap = Math.abs(gap);
        probability = 0.5 + gap*0.5;

        if (probability >=1) return DEFAULT_THRESH;
        else return (temperature*(-1)+Math.log(1/probability - 1));
    }

    private void makeshuffle(int n) {
        int j, other, temp;
        for(j=0;j<n;j++) {
            this.round[j]=j+1;
        }
        for(j=0;j<n-1;j++) {
            other = ThreadLocalRandom.current().nextInt(j, n);
            temp = this.round[other];
            this.round[other] = this.round[j];
            this.round[j] = temp;
        }
    }

    private void preselect(int popsize) {
        pick = 1;
        makeshuffle(popsize);
    }


    public static final double CHECK = 0.1;

    private int chooseother(Population P, double f1, double f2, double threshold) {

        int popsize = P.returnPopLength();
        int other, count, checksize;
        count =1;
        checksize = (int)Math.floor(CHECK*popsize);

        while(true) {
            other = ThreadLocalRandom.current().nextInt(0, popsize);
            count=count+1;
            if(((Math.abs(f1 - P.getPerson(other).getFitScore())>threshold)&&(Math.abs(f2 - P.getPerson(other).getFitScore())>threshold))|| (count>checksize))break;
        }
        return other;
    }

    public static final double BIAS = 0.5; //Randomization for third chooseother

    /*in the paper, thresh has been kept fixed at 0.5*/
    /*temperature varied in different runs from 100,10,1,0.1*/
    public int BoltzmannSelect(Population P, int pk, double thresh, double temperature) {
        pick = pk;
        int popsize = P.returnPopLength();
        int first, second, third;
        double probf, deltaf;

        if (pick > popsize-1) preselect(popsize);
        first = round[pick];
        second = chooseother(P,P.getPerson(first).getFitScore(),P.getPerson(first).getFitScore(),thresh);

        if (flip(BIAS)) {
            third = chooseother(P,P.getPerson(first).getFitScore(),P.getPerson(second).getFitScore(),thresh);
        }
        else {
            third = chooseother(P,P.getPerson(first).getFitScore(),P.getPerson(first).getFitScore(),thresh);
        }
        probf = logistic((P.getPerson(third).getFitScore() - P.getPerson(second).getFitScore())/temperature);
        if (flip(probf)) second=third;

        deltaf = P.getPerson(second).getFitScore() - P.getPerson(first).getFitScore();
        probf = logistic(deltaf/temperature);
        pick = pick+1;
        if (flip(probf)) return first;
        else return second;
    }

}
