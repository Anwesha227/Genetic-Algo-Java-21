package geneticChurn.basicmethods;

import java.util.*;

public class Population  implements Cloneable{

    private List<Person> population;
    private double pop_fitness = 0;
    double[][] popDomain;

    /*initialize an empty population*/

    public Population() {
        population = new ArrayList<Person>();
    }


    /*initialize a population with random individuals*/

    public Population(int pop_size, int chrom_len) {
        population = new ArrayList<Person>();

        for(int i=0; i < pop_size;i++)
        {
            Person p = new Person(chrom_len);
            population.add(i,p);
        }
    }

    /*initialize a population with given individuals*/
    public Population(Population P){
        this.population = P.population;
        this.pop_fitness = P.pop_fitness;
        this.popDomain = P.popDomain;

    }

    /*Adding member to an empty population*/
    public void addPerson(Person per) {
        population.add(per);
    }

    /*Setting and retrieving population member at some location*/

    public void setPerson(int loc, Person per) {
        population.set(loc,per);
    }

    public Person getPerson(int loc) {
        return population.get(loc);
    }

    /*Getting the entire population*/
    public List<Person> getPopulation(){
        return population;
    }

    /*Get and assign fitness score to population*/

    public double returnFitness() {
        return pop_fitness;
    }

    public void assignFitness(double f) {
        pop_fitness = f;
    }

    /*Get no of persons in population*/

    public int returnPopLength() {
        return population.size();
    }

    /*getting fittest member - by search in linear time.
     * For elitism too, no need to sort.
     * If elitism count is 'k', then complexity for linearly searching each time
     * becomes {n(n+1) - (n-k)(n-k+1)}/2 which, for sufficiently low constant k, is
     * still O(n) */

    public Person findFittest() {
        Person max_fit =  Collections.max(population, Comparator.comparing(s -> s.getFitScore()));
        return max_fit;
    }

    public void sortpop(){
        Collections.sort(population, Comparator.comparing(s -> s.getFitScore()));
    }




    public double[][] domainFinder(int createNewDomainFlag){
        if(createNewDomainFlag==1) {
            int popsize = this.returnPopLength();
            int chromsize = this.getPerson(0).returnLength();
            double[][] domain = new double[chromsize][2];

            for (int i = 0; i < chromsize; i++) {
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;

                for (int j = 0; j < popsize; j++) {
                    if (min > this.getPerson(j).getGene(i)) {
                        min = this.getPerson(j).getGene(i);
                    }
                    if (max < this.getPerson(j).getGene(i)) {
                        max = this.getPerson(j).getGene(i);
                    }
                }
                domain[i][0] = min;
                domain[i][1] = max;
            }
            this.popDomain = domain;
            return domain;
        }
        else{
            return this.popDomain;
        }
    }

    public void updateDomain(double[] new_chromosome){
        for(int i=0; i < new_chromosome.length; i++){
            if(popDomain[i][0]>new_chromosome[i]){popDomain[i][0] = new_chromosome[i];}
            if(popDomain[i][1]<new_chromosome[i]){popDomain[i][1] = new_chromosome[i];}
        }
    }

    public void shufflePop() {
        Random rnd = new Random();
        int size = this.returnPopLength();

        for (int i=size; i>1; i--) {
            this.swap( i-1, rnd.nextInt(i));
        }
    }

    private void swap( int a, int b) {
        Person t = this.getPerson(a);
        this.setPerson(a, this.getPerson(b));
        this.setPerson(b, t);;
    }
	/*
	 * checking
	public static void main(String args[]) {
		Population P = new Population(3,5);
		double y = 0.1;
		for (int i=0;i<3;i++) {
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
        double[][] domain = P.domainFinder(1);
        System.out.println("First Domain");
        for(int i=0; i < P.getPerson(0).returnLength();i++){
            System.out.println("Gene:"+i+" min:"+domain[i][0]+" max:"+domain[i][1]);
        }


		System.out.println("\n");
		P.setPerson(2, person1);
		for (int i=0;i<3;i++) {
			Person x=P.getPerson(i);
			System.out.println(Arrays.toString(x.returnChrom()));
		}

        System.out.println("\nFirst Domain with createnewdomainflag set to 0");
		domain = P.domainFinder(0);
        for(int i=0; i < P.getPerson(0).returnLength();i++){
            System.out.println("Gene:"+i+" min:"+domain[i][0]+" max:"+domain[i][1]);
        }

        System.out.println("\nSecond Domain with createnewdomainflag set to 1");
        domain = P.domainFinder(1);
        for(int i=0; i < P.getPerson(0).returnLength();i++){
            System.out.println("Gene:"+i+" min:"+domain[i][0]+" max:"+domain[i][1]);
        }

		System.out.println("\n");
		P.assignFitness(80.0);
		System.out.println(P.returnFitness());
		System.out.println(P.returnPopLength());

		System.out.println("\n");
		Person fittest = P.findFittest();
		System.out.println(Arrays.toString(fittest.returnChrom()));

		System.out.println("\n");

		for (int i=0;i<3;i++) {
			Person x=P.getPerson(i);
			System.out.println(Arrays.toString(x.returnChrom())+Double.toString(x.getFitScore()));
		}

		P.sortpop();
		System.out.println("\n");

		for (int i=0;i<3;i++) {
			Person x=P.getPerson(i);
			System.out.println(Arrays.toString(x.returnChrom()));
		}

		System.out.println("\nChecking Shuffle");
		Population New = new Population(4,5);
        for (int i=0;i<4;i++) {
            Person x=New.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }
        New.shufflePop();
        System.out.println("\nPopulation 'New':");
        for (int i=0;i<4;i++) {
            Person x=New.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }


        System.out.println("\nNew1 Population:");
        Population New1 = new Population(4,5);
        for (int i=0;i<4;i++) {
            Person x=New1.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }
        System.out.println("\nAfter assigning New to New1:");
        New1 = new Population(New);
        for (int i=0;i<4;i++) {
            Person x=New1.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }

        double[] a = {1,2,3,4,5};
        Person pnew = new Person(a);
        New1.setPerson(0,pnew);
        System.out.println("\nNew1 after Modifying first person of New1: ");
        for (int i=0;i<4;i++) {
            Person x=New1.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }

        System.out.println("\nNew after modification of New1:");
        for (int i=0;i<4;i++) {
            Person x=New.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }

        int m = 2;
        Population I = new Population(4,5);
        double[] b1 = {1, 4, 6, 2, 7};
        double[] b2 = {7, 2, 6, 4, 1};
        double[] b3 = {10, 9, 8, 5, 4};
        double[] b4 = {4, 5, 8, 9, 10};
        while(m>0) {

            System.out.println("\nPopulation I :");
            for (int i = 0; i < 4; i++) {
                Person x = I.getPerson(i);
                System.out.println(Arrays.toString(x.returnChrom()));
            }

            int n = 2;

            while (n > 0) {
                Population Q = new Population();

                Person p_b1 = new Person(b1);
                Person p_b2 = new Person(b2);
                Person p_b3 = new Person(b3);
                Person p_b4 = new Person(b4);

                Q.addPerson(p_b1);
                Q.addPerson(p_b2);
                Q.addPerson(p_b3);
                Q.addPerson(p_b4);

                for (int i = 0; i < 5; i++) {
                    b1[i]++;
                    b2[i]--;
                    b3[i]++;
                    b4[i]--;
                }

                System.out.println("\nPopulation Q");
                for (int i = 0; i < 4; i++) {
                    Person x = Q.getPerson(i);
                    System.out.println(Arrays.toString(x.returnChrom()));
                }
                n--;
                I = new Population(Q);
            }
            m--;
        }
        System.out.println("\nPopulation I :");
        for (int i = 0; i < 4; i++) {
            Person x = I.getPerson(i);
            System.out.println(Arrays.toString(x.returnChrom()));
        }


	}
	*/
}
