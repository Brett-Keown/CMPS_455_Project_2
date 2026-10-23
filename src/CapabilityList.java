import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
public class CapabilityList {
    static ArrayList<accessObject>[] capabilityMatrix;
    static int N;
    static int M;

    static ReentrantLock[] fileLocks;
    static Random rand = new Random();



    public static class accessObject{
        int domain;
        String access;
        public accessObject(int domain, String access){
            this.domain = domain;
            this.access = access;
        }
    }
    //Matrix Creation
    public static void matrixGeneration(){
        //STep 1 get dimesnions of matrix

        N = rand.nextInt(5) + 3;
        System.out.println(N);


        M = rand.nextInt(5) + 3;
        System.out.println(M);

        //Make an arraylist array of accessobjects of size N [domains]
        capabilityMatrix = new ArrayList[N];
        for(int i = 0; i < N; i++){
            capabilityMatrix[i] = new ArrayList<>();
        }


        for(int i = 0; i < N; i ++) { //Iterate through each Domain or Column
            for(int j = 0; j < M + N; j++){ //Iterate though all Files and all Domains
                int accessControl = rand.nextInt(2);
                if(i == j - M)
                    capabilityMatrix[i].add(new accessObject(i, "N/A"));
                if(accessControl == 0)
                    capabilityMatrix[i].add(new accessObject(i, "Access"));
                else
                    capabilityMatrix[i].add(new accessObject(i, "No Access"));
            }

            //Make an edge case so that when i and j = the domain it can't have access to switch to its own domain.

        }
        for (int i = 0; i < N; i++) {
            System.out.print("D" + (i+1) + " : ");
            for (int j = 0; j < M + N; j++) {
                if (j < M)
                    System.out.print("F" + (j+1) + ": " +   capabilityMatrix[i].get(j).access + " ");
                if(j >= M)
                    System.out.print("D" + (j-M + 1) + ": " +  capabilityMatrix[i].get(j).access + " ");
            }
            System.out.println();
        }
    }
    public static class capabilityThread implements Runnable{
        int threadId;
        int domain;

        public capabilityThread(int id){
            threadId = id;
            domain = id; //Thread domain is gotten by the threadId + 1 because of the test output which is one more than the thread id ex (Thread 0  = Domain 1)
        }


        @Override
        public void run() {
            for(int i = 0; i < 5; i++) {
                int object = rand.nextInt(M + N);  //Testing


                while (object - M == domain) {
                    object = rand.nextInt(M + N);
                }

                if (object < M)
                    fileAccess(domain, object, threadId); //File switching
                if (object >= M) {
                    domain = domainAccess(domain, object, threadId); //Domain switch if object - M != domain
                }
            }
        }
    }

    public static boolean arbitrator(int domain, int object){
        boolean val;
        // Access the Capability matrix at the domain of the current thread
        if((capabilityMatrix[domain].get(object).access).equals("Access")){
            val = true;
        }
        else
            val = false;
        return val;
    }
    public static void fileAccess(int domain, int object, int threadID){ //Domain to get the domain of the correct domain of the requested object, and threadID for printing info and lock logic
        System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Attempting to access resource: F" + (object+1));

        //Check if Access if allowed
        boolean allowed = arbitrator(domain, object);
        if (allowed){
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Access Granted");

            fileLocks[object].lock();
            try{
                int yields = rand.nextInt(5) + 3;

                System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Yielding " + yields + " times");

                for(int i = 0; i < yields; i++)
                    Thread.yield();
            }finally {
                fileLocks[object].unlock();
            }

            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Operation Complete");
        }
        else{
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Operation failed, permission denied");

            //Yield code
            int yields = rand.nextInt(5) + 3;
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Yielding " + yields + " times");

            for(int i = 0; i < yields; i++)
                Thread.yield();
        }
    }

    public static int domainAccess(int domain, int object, int threadID){
        System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Attempting to Switch from: D" + (domain+1) + " to D" + (object-M+ 1));

        boolean allowed = arbitrator(domain, object);
        if(allowed){
            domain = object - M;
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Switched to D" + (object - M + 1));

            int yields = rand.nextInt(5) + 3;

            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Yielding " + yields + " times");

            for(int i = 0; i < yields; i++)
                Thread.yield();

            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Operation failed, permission denied");
        }
        else{
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Switch failed, permission denied");
        }
        return domain;
    }

    public static void run(){
        matrixGeneration();
        System.out.println();

        //Create a list thatll contain a specefic lock for the specefic file of domain
        //Create locks for all M + N
        fileLocks = new ReentrantLock[M];
        for(int i = 0; i < fileLocks.length; i++)
            fileLocks[i] = new ReentrantLock();

        Thread[] threads = new Thread[N];
        for(int i = 0; i < N; i++){
            threads[i] = new Thread(new capabilityThread(i));
        }
        for(int i = 0; i < N; i++){
            threads[i].start();
        }


    }

}


//When printing just add 1 to the file and domain number
//Add the logic that puts N/A for when a file could swutcg to itself.