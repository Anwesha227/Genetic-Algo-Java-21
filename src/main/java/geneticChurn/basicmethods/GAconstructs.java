package geneticChurn.basicmethods;


import fitness_eval_flask.Fl_client_test;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

public class GAconstructs {

    /*Steady state genetic algorithm
    * Implementation follows the Algorithm No. 34 from the text "Essentials of Meta-heuristics"*/

    /*Roulette wheel selection used here. Modify it as needed.*/
    /*Crossover SBX used here. Modify it as needed*/
    /*Non-uniform mutation used here. Modify it as needed*/

    public Person steadyStateGA(double crossoverRate, double mutationRate, int popSize, int chromosomeLength, int max_iter)
    throws CloneNotSupportedException{
        //Initialize a new population
        Population P = new Population(popSize,chromosomeLength);

        SelectionMethod m = new SelectionMethod();
        CrossoverMethod c = new CrossoverMethod();
        Mutation mutate = new Mutation();

        //Choose best as a random individual
        Random rand = new Random();
        Person best = P.getPerson(rand.nextInt(popSize));
        double best_score = best.findFitScore();
        System.out.println("First random best individual with fit score : "+best_score);
        System.out.println(Arrays.toString(best.returnChrom()));
        System.out.println("\n");

        //evaluate whole population once and choose best
        for(int i=0;i<P.returnPopLength();i++){
            if(P.getPerson(i).findFitScore()>best.getFitScore()){best=P.getPerson(i).clone();}
        }

        System.out.println("Best individual after first evaluation with fit score : "+best.getFitScore());
        System.out.println(Arrays.toString(best.returnChrom()));
        System.out.println("\n");

        int first_iter=1;
        int MAX_GEN = max_iter;

        //evolve population generation wise
        while(max_iter>0){
            //selection
            Person parent1 = m.rouletteWheelSelection(P, 1);
            Person parent2 = m.rouletteWheelSelection(P, 0);

            Person[] children;
            //crossover
            if(crossoverRate>Math.random()) {
                //children = c.blendCrossover(parent1,parent2,0.5);
                children = c.SBX(parent1, parent2, 1);
            }
            else{
                //no crossover
                children = new Person[2];
                children[0] = parent1.clone();
                children[1] = parent2.clone();
            }

            System.out.println("\n");
            System.out.println("Children after crossover at iteration: "+first_iter);
            System.out.println(Arrays.toString(children[0].returnChrom())+" fitness: "+children[0].findFitScore());
            System.out.println(Arrays.toString(children[1].returnChrom())+" fitness: "+children[1].findFitScore());
            System.out.println("\n");

            //mutation
            if(first_iter==1){
                //first time, create a new domain createNewDomainFlag = 1.
                //subsequently, do not create new domain, only set createNewDomainFlag = 0.
                mutate.nonUniformMutation(children[0],P,1,mutationRate,first_iter,MAX_GEN,5);
                mutate.nonUniformMutation(children[1],P,0,mutationRate,first_iter,MAX_GEN,5);
            }
            else{
                //Not first iteration. So only update domain. Set createNewDomainFlag = 0.
                mutate.nonUniformMutation(children[0],P,0,mutationRate,first_iter,MAX_GEN,5);
                mutate.nonUniformMutation(children[1],P,0,mutationRate,first_iter,MAX_GEN,5);
            }


            System.out.println("Children after Mutation at iteration: "+first_iter);
            System.out.println(Arrays.toString(children[0].returnChrom())+" fitness: "+children[0].findFitScore());
            System.out.println(Arrays.toString(children[1].returnChrom())+" fitness: "+children[1].findFitScore());
            System.out.println("\n");


            //updating best
            if(children[0].getFitScore()> best.getFitScore()){best = children[0].clone();}
            if(children[1].getFitScore()>best.getFitScore()){best = children[1].clone();}


            //Choosing two individuals randomly for death
            int individual1 = rand.nextInt(popSize);
            int individual2 = rand.nextInt(popSize);

            System.out.println("Individuals chosen for death at iteration: "+first_iter);
            System.out.println(Arrays.toString(children[0].returnChrom())+" fitness: "+P.getPerson(individual1).getFitScore());
            System.out.println(Arrays.toString(children[1].returnChrom())+" fitness: "+P.getPerson(individual2).getFitScore());
            System.out.println("\n");

            //Updating the population and the domain
            P.setPerson(individual1,children[0]);
            P.setPerson(individual2, children[1]);
            P.updateDomain(children[0].returnChrom());
            P.updateDomain(children[1].returnChrom());

            /*
            //updating best
            if(children[0].findFitScore()> best.getFitScore()){best = children[0].clone();}
            if(children[1].findFitScore()>best.getFitScore()){best = children[1].clone();}
            */

            System.out.println("Best during interation "+first_iter+" : "+Arrays.toString(best.returnChrom())+" fitness :"+best.getFitScore());
            first_iter++;
            max_iter--;
        }
        return best;
    }



    /*Generational GA with the possibility of elitism*/
    public Person GenGA(double crossoverRate, double mutationRate, int popSize, int chromosomeLength, int max_iter, int eliteCount)
    throws CloneNotSupportedException{
        //Initialize a new population
        Population P = new Population(popSize,chromosomeLength);

        SelectionMethod m = new SelectionMethod();
        CrossoverMethod c = new CrossoverMethod();
        Mutation mutate = new Mutation();

        //Choose best as a random individual
        Random rand = new Random();
        Person best = P.getPerson(rand.nextInt(popSize));
        double best_score = best.findFitScore();
        System.out.println("First random best individual with fit score : "+best_score);
        System.out.println(Arrays.toString(best.returnChrom()));
        System.out.println("\n");

        int first_iter=1;
        int MAX_GEN = max_iter;


        while(max_iter>0){

            //evaluate whole population once and choose best
            for(int i=0;i<P.returnPopLength();i++){
                if(P.getPerson(i).findFitScore()>best.getFitScore()){best=P.getPerson(i).clone();}
            }

            System.out.println("Best individual after evaluation with fit score in iteration "+first_iter+" is :"+best.getFitScore());
            System.out.println(Arrays.toString(best.returnChrom()));
            System.out.println("\n");

            //choose the 'eliteCount' fittest individuals in the population
            Population Q = new Population();
            P.sortpop();
            for(int i = popSize-eliteCount;i<popSize;i++){
                Q.addPerson(P.getPerson(i));
            }
            P.shufflePop();


            System.out.println("Elite individuals in interation "+first_iter);
            for(int i=0;i<eliteCount;i++){
                System.out.println(Arrays.toString(Q.getPerson(i).returnChrom())+" fitness:"+Q.getPerson(i).getFitScore());
            }
            System.out.println("\n");

            //dealing with remaining individuals
            int rem = (popSize - eliteCount)/2;
            int inner_iter = 1;
            while(rem>0){
                //selection
                Person parent1 = m.rouletteWheelSelection(P, 1);
                Person parent2 = m.rouletteWheelSelection(P, 0);

                //crossover
                Person[] children;
                if(crossoverRate>Math.random()) {
                    //children = c.blendCrossover(parent1,parent2,0.5);
                    children = c.SBX(parent1, parent2, 1);
                    //children = c.simpleCrossover(parent1, parent2);
                }
                else{
                    //no crossover
                    children = new Person[2];
                    children[0] = parent1.clone();
                    children[1] = parent2.clone();
                }

                if(inner_iter==1){
                    //first time, create a new domain createNewDomainFlag = 1.
                    //subsequently, do not create new domain, only set createNewDomainFlag = 0.
                    mutate.nonUniformMutation(children[0],P,1,mutationRate,first_iter,MAX_GEN,5);
                    mutate.nonUniformMutation(children[1],P,0,mutationRate,first_iter,MAX_GEN,5);
                }
                else{
                    //Not first iteration. Set createNewDomainFlag = 0.
                    mutate.nonUniformMutation(children[0],P,0,mutationRate,first_iter,MAX_GEN,5);
                    mutate.nonUniformMutation(children[1],P,0,mutationRate,first_iter,MAX_GEN,5);
                }
                Q.addPerson(children[0]);
                Q.addPerson(children[1]);
                inner_iter++;
                rem--;
            }
            P = new Population(Q);

            System.out.println("Best during interation "+first_iter+" : "+Arrays.toString(best.returnChrom())+" fitness :"+best.getFitScore());
            System.out.println("\n");
            first_iter++;
            max_iter--;
        }

        return best;
    }



    public static void main(String args[]) throws CloneNotSupportedException {

        GAconstructs newGA = new GAconstructs();
        int length_of_chrom = 66;//DO NOT MODIFY as it depends on DATASET
        int sizep = 300; //population size
        double w1 = 0.5, w2 = 0.5;
            for (int iterno = 500; iterno <= 500; iterno += 10) {

                Person best2 = newGA.steadyStateGA(0.9,0.1,sizep,length_of_chrom,iterno);
                //Person best2 = newGA.GenGA(0.9, 0.1, sizep, length_of_chrom, iterno, 2);
                //System.out.println("Best : " + Arrays.toString(best2.returnChrom()));

                Fl_client_test tester = new Fl_client_test();
                double[] Scores = tester.FlaskClientTest(best2.returnChrom());

                System.out.println("Accuracy :" + Scores[0] + "\nF-Score :" + Scores[1] + "\nPrecision :" + Scores[2] + "\nRecall :" + Scores[3] + "\nSpecificity :" + Scores[4]);

                //String strres = "lambda :0.001" + " Popsize :" + sizep + " max_iter :" + iterno + " Accuracy :" + Scores[0] + " F-Score :" + Scores[1] + " Precision :" + Scores[2] + " Recall :" + Scores[3] + " Specificity :" + Scores[4] + "\n";
                //String strres = "lambda :0.001" +" w1 :"+w1+" w2 :"+w2+ " Popsize :" + sizep + " max_iter :" + iterno + " Accuracy :" + Scores[0] + " F-Score :" + Scores[1] + " Precision :" + Scores[2] + " Recall :" + Scores[3] + " Specificity :" + Scores[4] + "\n";


                //String fileName = "C:\\Users\\ANWESHA\\Desktop\\TCS Internship Details\\GA\\Customer Churn\\dataset cell2cell new\\StdAccResults.txt";

                /*
                File file = new File(fileName);
                try {
                    FileUtils.writeStringToFile(file, strres, StandardCharsets.UTF_8, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
    }


}
