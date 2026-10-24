import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

// this class implements task three of the project
// implements access control with capability lists
// run in main with CapabilityList.run();
public class CapabilityList {
    static ArrayList<accessObject>[] capabilityMatrix;
    static int N;
    static int M;
    static ReentrantLock[] fileLocks;
    static String[] filesColor;
    static Random rand = new Random();

    static String[] colors = {"Yellow", "Blue", "Green", "Orange", "Red", "Pink", "Violet"};

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

        N = rand.nextInt(5) + 3;

        M = rand.nextInt(5) + 3;

        //Make an arraylist array of access objects of size N [domains]
        capabilityMatrix = new ArrayList[N];
        for(int i = 0; i < N; i++){
            capabilityMatrix[i] = new ArrayList<>();
        }


        for(int i = 0; i < N; i ++) { //Iterate through each Domain or Column
            for(int j = 0; j < M + N; j++){ //Iterate though all Files and all Domains
                int accessControl = rand.nextInt(2);
                if(i == j - M)
                    //Put N/A where domain is equal to itself in order to not attempt to switch to itself
                    capabilityMatrix[i].add(new accessObject(i, "N/A"));
                if(accessControl == 0)
                    capabilityMatrix[i].add(new accessObject(i, "Access"));
                else
                    capabilityMatrix[i].add(new accessObject(i, "No Access"));
            }

        }
    }

    private static void printAccessLists() {
        for (int i = 0; i < N; i++) {
            System.out.print("D" + (i+1) + " --> ");
            for (int j = 0; j < M + N; j++) {
                if(capabilityMatrix[i].get(j).access.equals("Access")) {
                    if (j < M)
                        System.out.print("F" + (j + 1) + ": " + capabilityMatrix[i].get(j).access + " ");
                    if (j >= M)
                        System.out.print("D" + (j - M + 1) + ": " + capabilityMatrix[i].get(j).access + " ");
                }
            }
            System.out.println();
        }
    }

    public static class capabilityThread implements Runnable{
        int threadId;
        int domain;

        public capabilityThread(int id){
            threadId = id;
            domain = id;
        }


        @Override
        public void run() {
            for(int i = 0; i < 5; i++) {
                int object = rand.nextInt(M + N);

                while (object - M == domain) {
                    object = rand.nextInt(M + N);
                }

                if (object < M) {
                    int operation = rand.nextInt(2);
                    fileAccess(domain, object, threadId, operation);
                }
                if (object >= M) {
                    domain = domainAccess(domain, object, threadId);
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
    public static void fileAccess(int domain, int object, int threadID, int operation){
        if(operation == 0)
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Attempting to read resource: F" + (object+1));
        else
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Attempting to write resource: F" + (object+1));

        boolean allowed = arbitrator(domain, object);
        if (allowed){
            if(operation == 0)
                System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] contains " + filesColor[object]);
            else {
                String color = colors[rand.nextInt(colors.length)];
                filesColor[object] = color;
                System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Writing " + color + " to resource F" + (object + 1));
            }

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

            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Operation Complete");
        }
        else{
            System.out.println("[Thread: " + threadID + "(D" + (domain + 1) + ")" + "] Switch failed, permission denied");
        }
        return domain;
    }
    public static void run(){
        matrixGeneration();

        System.out.println("Domain Count: " + N);
        System.out.println("Object Count:" + M);

        printAccessLists();
        System.out.println();

        fileLocks = new ReentrantLock[M];
        for(int i = 0; i < fileLocks.length; i++)
            fileLocks[i] = new ReentrantLock();

        filesColor = new String[M];
        for(int i = 0; i < M; i++){
            filesColor[i] = colors[rand.nextInt(colors.length)];
        }

        Thread[] threads = new Thread[N];
        for(int i = 0; i < N; i++){
            threads[i] = new Thread(new capabilityThread(i));
        }
        for(int i = 0; i < N; i++){
            threads[i].start();
        }
    }
}