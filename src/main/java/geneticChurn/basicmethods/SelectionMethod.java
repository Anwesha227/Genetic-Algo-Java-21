package geneticChurn.basicmethods;

import javafx.util.Pair;
import java.util.Arrays;
import java.util.Random;

public class SelectionMethod {


    /*tournament selection with tournament size t*/
    public Person tournamentSelection(Population P, int t) {
        Population Temp = new Population();
        Random rand = new Random();
        int popsize = P.returnPopLength();

        for(int i=0;i<t;i++) {
            int loc = rand.nextInt(popsize);
            Temp.addPerson(P.getPerson(loc));
        }
        Person Best = Temp.findFittest();
        return Best;
    }






    /*Create new CDF only when required and flag should be set to 1*/
    private double[] CDF;

    /*Roulette wheel selection according to Essentials of Metaheuristics Pg-43*/
    public Person rouletteWheelSelection(Population P, int createCDFflag) {


        if (createCDFflag==1) {
            CDF = new double[P.returnPopLength()];
            for(int i=0; i <P.returnPopLength();i++) {
                CDF[i] = P.getPerson(i).getFitScore();
            }
            /*Dealing with all zero fitness*/
            int flag=0;
            for(int i=0; i <P.returnPopLength();i++) {
                if (CDF[i] != 0.0) {
                    flag=1;
                    break;
                }
            }
            if (flag==0) {
                for(int i=0; i <P.returnPopLength();i++) {
                    CDF[i]=1.0;
                }
            }
            /*creating the CDF*/
            for(int i=1; i <P.returnPopLength();i++) {
                CDF[i] = CDF[i]+CDF[i-1];
            }
        }

        /*getting the parent*/
        double n = new Random().nextDouble() * CDF[P.returnPopLength()-1];
        int location = BinSearch(CDF,1,P.returnPopLength()-1,n);

        /*System.out.println(Arrays.toString(CDF));*/

        return P.getPerson(location);
    }





    /*Binary Search for RWS and Rank Selection*/
    private int BinSearch(double[] arr, int l, int r, double n){
        while(l<=r){
            int mid = (l+r)/2;
            if (arr[mid-1]<n && n<=arr[mid]){
                return mid;
            }
            else if(n<=arr[mid-1]){
                return BinSearch(arr,l,mid-1,n);
            }
            else if(n>arr[mid]){
                return BinSearch(arr,mid+1,r,n);
            }
        }
        return 0;
    }





    /*Rank Selection*/
    /*Create new CDF only when required and flag should be set to 1*/
    private double[] CDF_Rank;

    /*Rank Selection*/
    public Person rankSelection(Population P, int createCDFflag) {


        if (createCDFflag==1) {
            P.sortpop();
            CDF_Rank = new double[P.returnPopLength()];
            CDF_Rank[0] = 1.0;
            double rank_count=1.0;
            for(int i=1; i <P.returnPopLength();i++) {
                if (P.getPerson(i).getFitScore()==P.getPerson(i-1).getFitScore()) {
                    CDF_Rank[i]=rank_count;
                }
                else {
                    rank_count=rank_count+1.0;
                    CDF_Rank[i]=rank_count;
                }
            }

            /*creating the CDF*/
            for(int i=1; i <P.returnPopLength();i++) {
                CDF_Rank[i] = CDF_Rank[i]+CDF_Rank[i-1];
            }
        }

        /*getting the parent*/
        double n = new Random().nextDouble() * CDF_Rank[P.returnPopLength()-1];
        int location = BinSearch(CDF_Rank,1,P.returnPopLength()-1,n);

        /*System.out.println(Arrays.toString(CDF));*/

        return P.getPerson(location);
    }





    /*Stochastic Universal Sampling*/

    public Population SUS(Population P, int N) {

        Population Parents = new Population();

        double[] CDF_SUS = new double[P.returnPopLength()];

        for(int i=0; i <P.returnPopLength();i++) {
            CDF_SUS[i] = P.getPerson(i).getFitScore();
        }
        /*Dealing with all zero fitness*/
        int flag=0;
        for(int i=0; i <P.returnPopLength();i++) {
            if (CDF_SUS[i] != 0.0) {
                flag=1;
                break;
            }
        }
        if (flag==0) {
            for(int i=0; i <P.returnPopLength();i++) {
                CDF_SUS[i]=1.0;
            }
        }
        /*creating the CDF for SUS*/
        for(int i=1; i <P.returnPopLength();i++) {
            CDF_SUS[i] = CDF_SUS[i]+CDF_SUS[i-1];
        }

        int index = 0;
        double value = new Random().nextDouble() * (CDF_SUS[P.returnPopLength()-1]/N);

        for (int i=0;i<N;i++) {
            while(CDF_SUS[index]<value) {
                index = index+1;
            }
            value=value+(CDF_SUS[P.returnPopLength()-1]/N);
            Parents.addPerson(P.getPerson(index));
        }
        Parents.shufflePop();
        return Parents;
    }



    /*Boltzmann Selection based on the text Principles of Soft Computing (2nd Edition)*/
    /*g must be updated in every generation and G must be kept fixed*/

    public Person BMselect(Population P, int g, int G, double T0, double alpha) {
        Person best = P.getPerson(0);
        Population temp = new Population();
        temp.addPerson(best);

        double k = 1+100*g/G;
        double T = T0*Math.pow(1-alpha, k);

        for (int i=1; i<P.returnPopLength();i++) {
            if (P.getPerson(i).getFitScore()>best.getFitScore()) {
                return P.getPerson(i);
            }
            else {
                double prob = Math.exp((P.getPerson(i).getFitScore()-best.getFitScore())/T);
                if(prob>Math.random()) return P.getPerson(i);
                else {
                    temp.addPerson(P.getPerson(i));
                    best = temp.findFittest();
                }
            }
        }
        return best;
    }



    /*
    public void shuffle(Population P) {
        Random rnd = new Random();
        int size = P.returnPopLength();

        for (int i=size; i>1; i--) {
            swap(P, i-1, rnd.nextInt(i));
        }
    }

    private void swap(Population p, int a, int b) {
        Person t = p.getPerson(a);
        p.setPerson(a, p.getPerson(b));
        p.setPerson(b, t);;
    }
    */


    /*this method is exclusive for the Linear crossover operator*/
    public Person[] linearSelect(Population P){
        Person[] children = new Person[2];
        P.sortpop();
        children[0] = P.getPerson(P.returnPopLength()-1);
        children[1] = P.getPerson(P.returnPopLength()-2);
        return children;
    }



    /* checking */
    public static void main(String args[]) {
        Population P = new Population(10,5);
        double y = 0.1;
        for (int i=0;i<10;i++) {
            Person x=P.getPerson(i);
            x.assignFitScore(y);
            y = y + 0.1;
            System.out.println(Arrays.toString(x.returnChrom()));
        }
        System.out.println("\n");
        Person person1 = new Person(5);
        person1.assignFitScore(0.15);
        System.out.println(Arrays.toString(person1.returnChrom()));

        System.out.println("\n");
        P.setPerson(9, person1);
        for (int i=0;i<10;i++) {
            Person x=P.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }

        System.out.println("\n");
        P.assignFitness(80.0);
        System.out.println(P.returnFitness());
        System.out.println(P.returnPopLength());

        System.out.println("\n");
        Person fittest = P.findFittest();
        System.out.println(Arrays.toString(fittest.returnChrom()));

        SelectionMethod RWS = new SelectionMethod();
        System.out.println("\n");
        Person RWSselected = RWS.rouletteWheelSelection(P, 1);
        System.out.println(Arrays.toString(RWSselected.returnChrom()));

        System.out.println("\n");
        Person RWSselected1 = RWS.rouletteWheelSelection(P, 0);
        System.out.println(Arrays.toString(RWSselected1.returnChrom()));

        Person person2 = new Person(5);
        person2.assignFitScore(5);
        P.setPerson(7, person2);

        System.out.println("\n");
        Person RWSselected2 = RWS.rouletteWheelSelection(P, 1);
        System.out.println(Arrays.toString(RWSselected2.returnChrom()));

        Person TournamentSelected = RWS.tournamentSelection(P,4);

        System.out.println("\n");
        for (int i=0;i<10;i++) {
            Person x=P.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }

        System.out.println("\n");
        System.out.println(Arrays.toString(TournamentSelected.returnChrom()));

        System.out.println("\n");

        Population Par = RWS.SUS(P, 10);

        for (int i=0;i<10;i++) {
            Person x=(Par.getPerson(i));
            System.out.println(Arrays.toString(x.returnChrom()));
        }

        System.out.println("\n");

        /*

        BoltzmannSelection b = new BoltzmannSelection(P.returnPopLength());
        int b_loc = b.BoltzmannSelect(P, 10, 0.5, 10);
        System.out.println("\n");
        System.out.println(Arrays.toString(P.getPerson(b_loc).returnChrom()));

         */
        System.out.println("--END--");

        Mutation m = new Mutation();
        System.out.println("\nPerson before mutation");
        System.out.println(Arrays.toString(person2.returnChrom()));
        m.randomMutation(person2,P,1,0.5);
        System.out.println("\nPerson after mutation");
        System.out.println(Arrays.toString(person2.returnChrom()));

    }
    /**/
}
