package geneticChurn.basicmethods;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class CrossoverMethod {

    /* Flat Crossover */
    public Person flatCrossover(Person p1, Person p2){
        double min, max;
        double[] child_chromosome;
        child_chromosome = new double[p1.returnLength()];

        for(int i=0; i<p1.returnLength();i++){
            if(p1.getGene(i)<p2.getGene(i)){min = p1.getGene(i);max=p1.getGene(i);}
            else{min = p2.getGene(i);max = p1.getGene(i);}
            double random = ThreadLocalRandom.current().nextDouble(min, max);
            child_chromosome[i]=random;
        }
        Person child = new Person(child_chromosome);
        return child;
    }

    /*Simple crossover*/
    public Person[] simpleCrossover(Person p1, Person p2){

        Random r = new Random();
        double[] child_chromosome1, child_chromosome2;
        child_chromosome1 = new double[p1.returnLength()];
        child_chromosome2 = new double[p1.returnLength()];
        int pos = r.nextInt(p1.returnLength()+1);

        for(int i=0; i<pos; i++){
            child_chromosome1[i] = p1.getGene(i);
            child_chromosome2[i] = p2.getGene(i);
        }
        for(int i=pos;i<p1.returnLength();i++){
            child_chromosome1[i]=p2.getGene(i);
            child_chromosome2[i] =p1.getGene(i);
        }
        Person child1 = new Person(child_chromosome1);
        Person child2 = new Person(child_chromosome2);

        Person[] children = new Person[2];
        children[0] = child1; children[1] = child2;
        return children;
    }


    /*Arithmetic crossover*/
    /*keep lambda constant for uniform arithmetic crossover
      change lambda according to generation number for non-uniform arithmetic crossover*/

    public Person[] uniform_arithmetic_crossover(Person p1, Person p2, double lambda){


        double[] child_chromosome1, child_chromosome2;
        child_chromosome1 = new double[p1.returnLength()];
        child_chromosome2 = new double[p1.returnLength()];

        for(int i=0; i<p1.returnLength(); i++){
            child_chromosome1[i] = lambda*p1.getGene(i)+(1-lambda)*p2.getGene(i);
            child_chromosome2[i] = lambda*p2.getGene(i)+(1-lambda)*p1.getGene(i);
        }

        Person child1 = new Person(child_chromosome1);
        Person child2 = new Person(child_chromosome2);

        Person[] children = new Person[2];
        children[0] = child1; children[1] = child2;
        return children;
    }

    public Person[] nonuniform_arithmetic_crossover(Person p1, Person p2){

        Random r = new Random();
        double lambda = r.nextDouble();

        double[] child_chromosome1, child_chromosome2;
        child_chromosome1 = new double[p1.returnLength()];
        child_chromosome2 = new double[p1.returnLength()];

        for(int i=0; i<p1.returnLength(); i++){
            child_chromosome1[i] = lambda*p1.getGene(i)+(1-lambda)*p2.getGene(i);
            child_chromosome2[i] = lambda*p2.getGene(i)+(1-lambda)*p1.getGene(i);
        }

        Person child1 = new Person(child_chromosome1);
        Person child2 = new Person(child_chromosome2);

        Person[] children = new Person[2];
        children[0] = child1; children[1] = child2;
        return children;
    }


    /*Linear Crossover*/
    /*Please use linearSelect from SelectionMethod after this to select the two best candidates*/

    public Population linearCrossover(Person p1, Person p2){
        Population candidates = new Population();
        candidates.addPerson(p1); candidates.addPerson(p2);

        double[] chrom1,chrom2,chrom3;
        chrom1=new double[p1.returnLength()];chrom2=new double[p1.returnLength()];chrom3=new double[p1.returnLength()];

        for(int i=0;i<p1.returnLength();i++){
            chrom1[i] = 0.5*p1.getGene(i) + 0.5*p2.getGene(i);
            chrom2[i] = 1.5*p1.getGene(i) - 0.5*p2.getGene(i);
            chrom3[i] = -0.5*p1.getGene(i) + 1.5*p2.getGene(i);
        }
        Person c1 = new Person(chrom1);
        Person c2 = new Person(chrom2);
        Person c3 = new Person(chrom3);

        candidates.addPerson(c1);
        candidates.addPerson(c2);
        candidates.addPerson(c3);

        return candidates;
    }


    /*Blend-alpha crossover*/
    /*A choice for alpha, often used is 0.5*/

    public Person[] blendCrossover(Person p1, Person p2, double alpha){
        double[] childchromosome1 = new double[p1.returnLength()];
        double[] childchromosome2 = new double[p1.returnLength()];

        for(int i=0; i< p1.returnLength(); i++){
            double c_min, c_max, I;
            Random rand = new Random();

            c_min = Math.min(p1.getGene(i),p2.getGene(i));
            c_max = Math.max(p1.getGene(i),p2.getGene(i));

            I = c_max - c_min;
            double random1 = c_min + (c_max - c_min) * rand.nextDouble();
            double random2 = c_min + (c_max - c_min) * rand.nextDouble();
            childchromosome1[i] = random1;
            childchromosome2[i] = random2;

        }
        Person child1 = new Person(childchromosome1);
        Person child2 = new Person(childchromosome2);
        Person[] children = new Person[2];
        children[0] = child1;
        children[1] = child2;
        return children;
    }



    /*Simulated Binary Crossover- SBX*/
    /*n: distribution index. Higher n tends to generate children closer to parents.
      For eg n=5 generates closer chidren than n=2*/

    public Person[] SBX(Person p1, Person p2, int n){
        Random r = new Random();
        double u = r.nextDouble();
        double beta = calculate_beta_SBX(u,n);

        double[] chrom1, chrom2;
        chrom1= new double[p1.returnLength()]; chrom2 = new double[p1.returnLength()];

        for(int i=0; i<p1.returnLength(); i++){
            chrom1[i] = 0.5*(((1+beta)*p1.getGene(i))+((1-beta)*p2.getGene(i)));
            chrom2[i] = 0.5*(((1-beta)*p1.getGene(i))+((1+beta)*p2.getGene(i)));
        }

        Person c1 = new Person(chrom1); Person c2 = new Person(chrom2);
        Person[] children = new Person[2];
        children[0] = c1; children[1] = c2;
        return children;
    }

     private double calculate_beta_SBX(double u, double n){
        double beta;
        double exp = 1/(n+1);
        if(u<=0.5){beta = Math.pow(2*u,exp);}
        else{beta = Math.pow((1/(2-2*u)),exp);}
        return beta;
     }


     /*Laplace crossover*/
    /*can keep a = 0
    * higher the value of b, closer the children are to parents
    * b=0.5 results in children that are farther from parents than children from b=1.0*/

    public Person[] LX(Person p1, Person p2, int a, int b){
        Random r = new Random();
        double alpha = r.nextDouble();
        double beta;

        if(alpha<=0.5){beta = a - b*Math.log(alpha);}
        else{beta = a + b*Math.log(alpha);}

        double[] chrom1 = new double[p1.returnLength()];
        double[] chrom2 = new double[p2.returnLength()];

        for(int i=0; i<p1.returnLength(); i++){
            chrom1[i] = p1.getGene(i) + Math.abs(p1.getGene(i)-p2.getGene(i));
            chrom2[i] = p2.getGene(i) + Math.abs(p1.getGene(i)-p2.getGene(i));
        }

        Person child1 = new Person(chrom1); Person child2 = new Person(chrom2);
        Person[] children = new Person[2];
        children[0] = child1; children[1] = child2;

        return children;
    }


    /*Discrete crossover*/
    public Person discreteCrossover(Person p1, Person p2){
        Random r = new Random();
        double[] chrom = new double[p1.returnLength()];

        for (int i=0;i<p1.returnLength();i++){
            int rand = r.nextInt(2);
            if(rand==0){chrom[i]=p1.getGene(i);}
            else{chrom[i]=p2.getGene(i);}
        }

        Person child = new Person(chrom);
        return child;
    }


    /*Extended line crossover*/
    /*Extended intermediate crossover is BLX-0.25*/

    public Person extendedLineCrossover(Person p1, Person p2){
        double[] chrom = new double[p1.returnLength()];
        double alpha = ThreadLocalRandom.current().nextDouble(-0.25,1.25);
        for(int i=0;i<p1.returnLength();i++){
            chrom[i] = p1.getGene(i)+alpha*(p2.getGene(i)-p1.getGene(i));
        }
        Person child = new Person(chrom);
        return child;
    }


    /*Wright's heuristic crossover*/

    public Person wrightsCrossover(Person p1, Person p2){
        double[] chrom = new double[p1.returnLength()];
        Random rand = new Random(); double r= rand.nextDouble();
        for(int i=0;i<p1.returnLength();i++){
            chrom[i] = p1.getGene(i)+r*(p1.getGene(i)-p2.getGene(i));
        }
        Person child = new Person(chrom);
        return child;
    }


    /*Linear BGA Crossover*/
    /*This function uses the domain finder first to find lower and upper limits of each gene's domain*/

    public Person linearBGA (Person p1, Person p2, Population P, int createNewDomainFlag){
        double[][] domain = P.domainFinder(createNewDomainFlag); //dimension: chromsize x 2
        double param1 = calculateParam1LBGA();
        double sign_probability = 0.9;
        double[] childchrom = new double[p1.returnLength()];

        for(int i=0;i<p1.returnLength();i++){
            double param2 = (p2.getGene(i) - p1.getGene(i))/Math.abs(p2.getGene(i) - p1.getGene(i));
            double rang_i = 0.5*(domain[i][1]-domain[i][0]);

            if(sign_probability>Math.random()){
                childchrom[i] = p1.getGene(i) - rang_i*param1*param2;
            }
            else{
                childchrom[i] = p1.getGene(i) + rang_i*param1*param2;
            }
        }
        Person child = new Person(childchrom);
        return child;
    }


    private double calculateParam1LBGA(){
        double rate = 1/16;
        int alpha;
        double sum=0.0;

        for(int k=0;k<=15;k++) {
            if (rate > Math.random()) {
                alpha = 1;
            } else {
                alpha = 0;
            }
            sum = sum + alpha*(1/Math.pow(2,k));
        }
        return sum;
    }



}
