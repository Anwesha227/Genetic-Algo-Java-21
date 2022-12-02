package geneticChurn.basicmethods;

import fitness_eval_flask.Fl_client;

import java.util.Arrays;
import java.util.Random;

public class Person implements Cloneable{
    private double[] chrom;
    private double fit_score = 0;

    /* We need two options to initialize a new
     * person. First, we need random initialization in the beginning.
     * Second, we also need to assign a child later on to a person object.
     */

    public Person(int chrom_length) {
        chrom = new double[chrom_length];

        for(int i = 0; i<chrom_length; i++) {
            Random r = new Random();
            double gene = r.nextDouble();
            chrom[i] = gene;
        }
    }

    public Person(double [] chrom) {
        this.chrom = chrom;
    }

    /*
     * Returning the chromosome and chromosome length
     */

    public double[] returnChrom() {
        return chrom;
    }

    public int returnLength() {
        return chrom.length;
    }

    /*
     * Assigning and returning fitness score of person
     */
    public void assignFitScore(double fit_score) {
        this.fit_score=fit_score;
    }

    public double getFitScore() {
        return fit_score;
    }


    /*Accessing single gene*/

    public double getGene(int pos)
    {
        return chrom[pos];
    }
    public void assignGene(int pos, double val)
    {
        chrom[pos]=val;
    }



    /*This is just a dummy function, to be replaced by our fitness evaluation algorithm later*/
    public double findFitScore(){
        /*double sum = 0.0;
        for(int i=0; i<this.returnLength(); i++){
            sum = sum + this.getGene(i);
        }
        */
        Fl_client fl = new Fl_client();
        double sum = fl.FlaskClient(this.returnChrom());
        this.assignFitScore(sum);
        //System.out.println("Fitness of "+ Arrays.toString(this.returnChrom())+" is "+sum);
        return sum;
    }


    public Person clone() throws CloneNotSupportedException {
        Person p = (Person) super.clone();

        // create new objects for any non-primitive, mutable fields
        p.chrom = this.chrom.clone();

        return p;
    }

	/*
	 * checking

	public static void main(String args[]) throws CloneNotSupportedException{
		Person person1 = new Person(5);
		double [] chromosome2 = {0.8,0,0.2,0.1,0.1};
		Person person2 = new Person(chromosome2);

		int len1 = person1.returnLength();
		int len2 = person2.returnLength();

		System.out.println(Arrays.toString(person1.returnChrom()));
		System.out.println(Arrays.toString(person2.returnChrom()));
		System.out.println(len1);
		System.out.println(len2);

		person1.assignFitScore(0.8);
		person2.assignFitScore(0.9);

		System.out.println(person1.getFitScore());
		System.out.println(person2.getFitScore());

	}
	*/
}
