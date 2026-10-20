import java.util.Random;
import java.util.concurrent.Semaphore;
public class AccessMatrix implements Runnable{

    private static String[][] aMatrix;
    private static int n;
    private static int m;
    private static String[] files;
    private static Semaphore[] fileSem;
    private static Random rand = new Random();

    //Basic thread function will/need to update, Assign its domain in this function
    private static void thread(int numThreads, String aMatrix){
        Thread[] thread = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++){
            thread[i] = new Thread();
            thread[i].start();
        }

    }


    //Yield is done completely
    private void yield(){
        int time = rand.nextInt(5) + 3;
        System.out.println(Thread.currentThread().getName() + " yielding " + time + " times.");
        for (int i = 0; i < time; i++){
            Thread.yield();
        }
    }

    /*
        Going to have to create a new function for the permission checking, so original idea of
            permission checking will not work.
        Going to update matrix creation in the process as well
        A switch statement is probably best for permission checking, so update the matrix
            for the values

         Generate a random number X in the range [0, M+N) to correspond to a column in the access matrix.
            Note the range notation: the lower bound is inclusive and the upper bound is exclusive, so X should
            be no larger than M+N–1.
         If X < M, generate another number [0,1]. Depending on this number, attempt to either read from or write to
            file object X.

     */

    /*
        A function for domain switching would be good
     */
    private void domainSwitch(int domain){

    }

    private void read(){

    }
    private void write(){

    }
    public int [][] accessMatrix(){
        fileSem = new Semaphore[m];
        for (int i = 0; i < m; i++){
            fileSem[i] = new Semaphore(1, true);
        }
        int n = rand.nextInt(7 - 3) + 3;
        int m = rand.nextInt(7 - 3) + 3;

        int [][] aMatrix = new int[n][m + n];
        //N/A's first
        aMatrix[0][0] = 0;
        for (int i = 1; i < n; i++){
            for (int j = m; j <= (m + n); j++){
                aMatrix[i][j + 1] = 0;
            }
        }
        for (int i = 0; i < n; i++){
            for (int j = 0; j < (m +n); j++){
                if (aMatrix[i][j] == 0){
                    continue;
                }
                else {
                    aMatrix[i][j] = rand.nextInt(5) + 1;
                }
            }
        }

        return aMatrix;
    }

    @Override
    public void run(){

    }


}
