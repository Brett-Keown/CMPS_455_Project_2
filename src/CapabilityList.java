import java.util.Random;
import java.util.ArrayList;
public class CapabilityList {
    static ArrayList<accessObject>[] capabilityMatrix;
    static int N;
    static int M;

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

        Random rand = new Random();

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
                if (accessControl == 0)
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
            Random rand = new Random();
            int object = rand.nextInt(M+N);  //Testing
            System.out.println(object);
            if (object < M)
                System.out.println(arbitrator(domain, object)); //File switcg
            if (object >= M) {
                while (object - M == domain) {
                    System.out.println("OOPS");
                    object = rand.nextInt(M + N);
                    System.out.println(object);
                }
                System.out.println(arbitrator(domain, object - M)); //Domain switch if object - M != domain
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
    public void fileAccess(int request){

    }

}


//When printing just add 1 to the file and domain number
//Add the logic that puts N/A for when a file could swutcg to itself.