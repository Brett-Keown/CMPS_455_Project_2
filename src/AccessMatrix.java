import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
public class AccessMatrix implements Runnable{

    private static String[][] aMatrix;
    private static int n;
    private static int m;
    private static String[] files;
    private static Random rand = new Random();
    private static ReentrantLock[] lock;
    private int currentDomain;


    public AccessMatrix(int domain){
        this.currentDomain = domain;
    }

    //Basic thread function will/need to update, Assign its domain in this function
    private static void thread(int numThreads){
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++){
            AccessMatrix user = new AccessMatrix(i);

            threads[i] = new Thread(user, "Thread " + (i +1));
            threads[i].start();
        }

        for (Thread thread : threads){
            try {
                thread.join();
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }

    }


    private void yield(){
        int time = rand.nextInt(5) + 3;
        System.out.println(Thread.currentThread().getName() + " yielding " + time + " times.");
        for (int i = 0; i < time; i++){
            Thread.yield();
        }
    }

    private void domainSwitch(int domain){

        System.out.println(Thread.currentThread().getName() + " Requesting switch from D" +
                (currentDomain + 1) + " to D " + (domain + 1));

        if (arbitrator(m + domain, "switch")) {
            currentDomain = domain;
        }
    }

    private boolean arbitrator(int index, String operation){

        String permission = aMatrix[currentDomain][index];
        if ( operation.equals("switch")){
            System.out.printf("%s checking permission to switch to D%d%n", Thread.currentThread().getName(),
                    index - m + 1);
        }
        else {
            System.out.println(Thread.currentThread().getName() +
                    " permission checking " + operation +
                    " on F " + (index + 1));
        }


        boolean allowed = false;

        switch (operation) {
            case "read": allowed = permission.equals("R")
                    || permission.equals("R/W"); break;

            case "write": allowed = permission.equals("W") || permission.equals("R/W"); break;

            case "switch": allowed = permission.equals("allow"); break;
        }

        if (allowed) {
            System.out.println(Thread.currentThread().getName() +" Permission Granted");
        }
        else {
            System.out.println(Thread.currentThread().getName() + " Permission Denied");
        }

        return allowed;

    }

    private void read(int file){
        System.out.println(Thread.currentThread().getName() + " Trying to read");

        if (arbitrator(file, "read")) {
            System.out.println(Thread.currentThread().getName() +
                    " waiting for lock on F " + (file + 1));
            lock[file].lock();
            try {
                System.out.println(Thread.currentThread().getName() +
                        " acquired lock on F " + (file + 1));
                System.out.println(Thread.currentThread().getName() + " Reading " + files[file]);
                yield();
            } finally {
                System.out.println(Thread.currentThread().getName() +
                        " releasing lock on F " + (file + 1));
                lock[file].unlock();
            }
        }
        else {
            yield();
            }
    }
    private void write(int file){

        System.out.println(Thread.currentThread().getName() + " Trying to write on F" + (file + 1));

        if (arbitrator(file, "write")) {
            System.out.println(Thread.currentThread().getName() +
                    " waiting for lock on F" + (file + 1));
            lock[file].lock();

            try {
                System.out.println(Thread.currentThread().getName() +
                        " acquired lock on F" + (file + 1));
                files[file] = "Wrote by " + Thread.currentThread().getName();
                System.out.println(files[file]);
                yield();
            }
            finally {
                System.out.println(Thread.currentThread().getName() +
                        " releasing lock on F" + (file + 1));
                lock[file].unlock();
            }
        }
        else {
            yield();
        }
    }
    public static void accessMatrix(){
        n = rand.nextInt(5) + 3;
        m = rand.nextInt(5) + 3;
        lock = new ReentrantLock[m];
        aMatrix = new String[n][m + n];
        files = new String[m];

        for (int i = 0; i < m; i++){
            files[i] = "Contents of F: " + (i + 1);
            lock[i] = new ReentrantLock(true);
        }

        for (int i = 0; i < n; i++){
            for (int j = 0; j < (m + n); j++){
                if (j < m){
                    int permission = rand.nextInt(4);

                    switch (permission){
                        case 0: aMatrix[i][j] = "R"; break;
                        case 1: aMatrix[i][j] = "W"; break;
                        case 2: aMatrix[i][j] = "R/W"; break;
                        case 3: aMatrix[i][j] = ""; break;
                    }
                }
                else {
                    if (j - m == i){
                        aMatrix[i][j] = "N/A";
                    }
                    else {
                        if (rand.nextBoolean()){
                            aMatrix[i][j] = "allow";
                        }
                        else {
                            aMatrix[i][j] = "";
                        }
                    }
                }
            }
        }
        printMatrix();
        thread(n);
    }

    private static void printMatrix(){
        System.out.printf("%-10s", "Domain");
        for (int i = 0; i < m; i++){
            System.out.printf("%-10s", "F" + (i + 1));
        }
        for (int i = 0; i < n; i++){
            System.out.printf("%-10s", "D" + (i + 1));
        }
        System.out.println();

        for (int i = 0; i < n; i++){
            System.out.printf("%-10s", "D" + (i + 1));
            for (int j = 0; j < m+n; j++){
                System.out.printf("%-10s", aMatrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public void run(){

        for (int i = 0; i < 5; i++){
            int r;

            do {
                r = rand.nextInt(m + n);
            }
            while (r >= m && r - m == currentDomain);

            if (r < m){
                int operation = rand.nextInt(2);

                switch (operation){
                    case 0: read(r);break;
                    case 1: write(r); break;
                }
            }
            else {
                domainSwitch(r - m);
            }
        }

    }
}
