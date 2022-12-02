package geneticChurn.basicmethods;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.special.Erf;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Mutation {

    /*random mutation*/
    public void randomMutation(Person p1,Population P, int createNewDomainFlag, double mutationRate){
        double[][] domain = P.domainFinder(createNewDomainFlag);
        double val;
        for(int i=0;i<p1.returnLength();i++){
            if(mutationRate>Math.random())
            {
                val= ThreadLocalRandom.current().nextDouble(domain[i][0],domain[i][1]);
                p1.assignGene(i,val);
            }
        }
    }


    /*non-uniform mutation*/
    /*send current generation number and max number of generations as parameter
    * b is a design parameter, for eg. b = 5*/
    public void nonUniformMutation(Person p, Population P, int createNewDomainFlag, double mutationRate, int generation, int max_generations, int b){
        double[][] domain = P.domainFinder(createNewDomainFlag);
        Random rand = new Random();
        for(int i=0; i<p.returnLength();i++) {
            if(mutationRate>Math.random()) {
                int t = rand.nextInt(2);
                if (t == 0) {
                    p.assignGene(i, p.getGene(i) + calculate_delta(generation, domain[i][1] - p.getGene(i), max_generations, b));
                } else {
                    p.assignGene(i, p.getGene(i) - calculate_delta(generation, p.getGene(i) - domain[i][0], max_generations, b));
                }
            }
        }
    }

    private double calculate_delta(int t, double y, int gmax, int b){
        Random rand = new Random();
        double r = rand.nextDouble();

        return y*(1-Math.pow(r,Math.pow(1-(float)t/gmax,b)));
    }


    /*Truncated Gaussian Mutation : https://www.iitk.ac.in/kangal/papers/k2012016.pdf*/
    static final double ERF_ALPHA = 0.140012;
    public void gaussianConvolution(Person p, double[] sigma_i,Population P, int createNewDomainFlag, double mutationRate){

        double[][] domain = P.domainFinder(createNewDomainFlag);
        double s, UL, UR, u_i, u_i_prime;

        for(int i=0; i<p.returnLength();i++) {
            if (mutationRate > Math.random()) {
                s = sigma_i[i] / (domain[i][1] - domain[i][0]);
                UL = 0.5 * (Erf.erf(convertForErf(domain[i][0], domain[i][1], p.getGene(i), s, 0)) + 1);
                UR = 0.5 * (Erf.erf(convertForErf(domain[i][0], domain[i][1], p.getGene(i), s, 1)) + 1);

                u_i = Math.random();
                if (u_i <= 0.5) {
                    u_i_prime = 2 * UL * (1 - 2 * u_i);
                } else {
                    u_i_prime = 2 * UR * (2 * u_i - 1);
                }
                p.assignGene(i, p.getGene(i) + Math.sqrt(2) * s * (domain[i][1] - domain[i][0]) * erfInvert(u_i_prime));
            }
        }

    }

    private double convertForErf(double a, double b, double x, double s, int a_or_b_flag){
        double r2 = Math.sqrt(2);
        if (a_or_b_flag == 0){
            return (a - x)/(r2*s*(b-a));
        }
        else{
            return (b-x)/(r2*s*(b-a));
        }
    }

    private double erfInvert (double x){
        double pi = Math.PI;
        double p = 2/(pi*ERF_ALPHA) + Math.log(1-x*x)/2;
        double q = Math.log(1-x*x)/ERF_ALPHA;
        double r = Math.sqrt(Math.sqrt(p*p - q) - p );

        if(x<0){r = -1 * r;}
        return r;
    }



    /*Muhlebein's mutation*/
    public void muhlenbeinMutation(Person p, Population P, int createNewDomainFlag, double mutationRate){
        double[][] domain = P.domainFinder(createNewDomainFlag);
        double rang_i,gamma;

        for(int i=0;i<p.returnLength();i++) {
            if (mutationRate > Math.random()) {
                rang_i = 0.1 * (domain[i][1] - domain[i][0]);
                gamma = calculateParamMuhlenbein();
                if (0.5 > Math.random()) {
                    p.assignGene(i, p.getGene(i) + rang_i * gamma);
                } else {
                    p.assignGene(i, p.getGene(i) - rang_i * gamma);
                }
            }
        }
    }

    private double calculateParamMuhlenbein(){
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



    /*Mäkinen, Periaux and Toivanen mutation
    * parameter m_exp (mutation exponent) defines the distribution of the mutation*/
    /*If p=1 then the mutation is uniform. The probability of small mutations grows as the value of p grows*/

    public void MPTM(Person p, Population P, int createNewDomainFlag, int m_exp, double mutationRate){
        double[][] domain = P.domainFinder(createNewDomainFlag);
        double t, r, tm;
        for(int i=0;i<p.returnLength();i++) {
            if (mutationRate > Math.random()) {
                t = (p.getGene(i) - domain[i][0]) / (domain[i][1] - domain[i][0]);
                r = Math.random();
                if (r < t) {
                    tm = t - t * Math.pow((t - r) / t, m_exp);
                } else if (r == t) {
                    tm = t;
                } else {
                    tm = t + (1 - t) * Math.pow((r - t) / (1 - t), m_exp);
                }

                p.assignGene(i, (1 - tm) * domain[i][0] + tm * domain[i][1]);
            }
        }
    }



    /*Power Mutation
    * The strength of mutation is governed by the index of the mutation m_index
    * For small values of m_index less disturbance in the solution is expected.
    * For large values of m_index more diversity is achieved.*/

    /*also note power distribution here is a special case of beta distribution
    * with alpha = m_index here and beta = 1*/

    public void PM(Person p, Population P, double m_index, int createNewDomainFlag, double mutationRate){
        double[][] domain = P.domainFinder(createNewDomainFlag);
        double r,s,t;

        BetaDistribution beta = new BetaDistribution(m_index,1);
        for(int i = 0; i < p.returnLength(); i++) {
            if (mutationRate > Math.random()) {
                s = beta.sample();
                t = (p.getGene(i) - domain[i][0]) / (domain[i][1] - domain[i][0]);
                r = Math.random();

                if (t < r) {
                    p.assignGene(i, p.getGene(i) - s * (p.getGene(i) - domain[i][0]));
                } else {
                    p.assignGene(i, p.getGene(i) + s * (domain[i][1] - p.getGene(i)));
                }
            }
        }
    }


    /*Polynomial Mutation
    * Picks up a random site and generates a random number u between 0 to 1 and calculates
    a mutated value. Max_mut is the maximum permissible perturbation
    * n is distribution index
    * performs gene wise mutation*/

    public void PLM(Person p, int n, double max_mut, int mutationRate){
        double u,delta;
        for(int i =0; i < p.returnLength(); i++){
            if(mutationRate<Math.random()) {
                u = Math.random();
                if (u < 0.5) {
                    delta = Math.pow(2 * u, (1.0 / (n + 1))) - 1.0;
                } else {
                    delta = 1.0 - Math.pow(2 * (1.0 - u), (1.0 / (n + 1)));
                }
            }
            else{delta = 0.0;}
            p.assignGene(i,p.getGene(i)+delta*max_mut);
        }
    }

}
