import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// this class implements task two of the project
// implements access control with access lists
// run in main with AccessList.run()
public class AccessList {
    // domains
    static int n;
    // files
    static int m;
    static Random random = new Random();
    static ArrayList<objectAccess>[] accessList;
    static String[] files;
    static Lock[] fileLocks;
    static String[] colors = {"Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet"};

    // constructor for a domains access permissions
    private static class objectAccess {
        int domain;
        boolean read;
        boolean write;
        boolean switches;

        objectAccess(int domain, boolean read, boolean write, boolean switches) {
            this.domain = domain;
            this.read = read;
            this.write = write;
            this.switches = switches;
        }
    }


    private static class DomainThread implements Runnable {
        int thread;
        int domain;

        DomainThread(int thread) {
            this.thread = thread;
            this.domain = thread;
        }

        @Override
        public void run() {
            // makes five requests
            for (int i = 0; i < 5; i++) {
                int object;
                // choose a random file or another domain.
                do {
                    object = random.nextInt(m + n);
                } while (object == m + domain);

                if (object < m) {
                    // file access request.
                    int operation = random.nextInt(2);
                    accessFile(domain, object, operation, thread);
                }
                else {
                    // domain switching request.
                    int location = object - m;
                    domain = switchDomain(domain, location, thread);
                }
            }
        }
    }

    // randomly generates the files and domains and assigns them random access
    private static void createAccessList() {
        // select amount of files and domains randomly
        m = 3 + random.nextInt(5);
        n = 3 + random.nextInt(5);

        // create list of array lists
        accessList = new ArrayList[m+n];
        for (int i = 0; i < m+n; i++) {
            accessList[i] = new ArrayList<>();
        }

        // create array list entries for the files
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int access = random.nextInt(4);
                // read and write permissions
                if (access == 0) {
                    accessList[i].add(new objectAccess(j, true, true, false));
                }
                // read permissions
                else if (access == 1) {
                    accessList[i].add(new objectAccess(j, true, false, false));
                }
                // write permissions
                else if (access == 2) {
                    accessList[i].add(new objectAccess(j, false, true, false));
                }
                // no permission if access == 3
            }
        }

        // create array list entries for the domains (switching)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    int shouldSwitch = random.nextInt(2);
                    if (shouldSwitch == 0) {
                        accessList[m+i].add(new objectAccess(j, false, false, true));
                    }
                }
            }
        }
    }

    // determines whether a given domain is allowed to read, write, or switch
    private static boolean arbitrator(int domain, int object, int attempt) {
        // goes through each domain in the access list of the
        // 1. file the current domain is attempting to be read or write to
        // 2. domain that the current domain wishes to switch to
        for (int i = 0; i < accessList[object].size(); i++) {
            objectAccess permission = accessList[object].get(i);
            if (permission.domain == domain) {
                if (attempt == 0 && permission.read) {
                    return true;
                }
                else if (attempt == 1 && permission.write) {
                    return true;
                }
                else if (attempt == 2 && permission.switches) {
                    return true;
                }
            }
        }
        return false;
    }


    private static void accessFile(int domain, int file, int operation, int thread) {
        // creates prefix for each output as a string
        String threadDomain = "[Thread: " + thread + "(D" + (domain + 1) + ")] ";

        // prints out whether the thread is trying to read or write
        if (operation == 0) {
            System.out.println(threadDomain + "Attempting to read resource: F" + (file + 1));
        }
        else {
            System.out.println(threadDomain + "Attempting to write resource: F" + (file + 1));
        }

        // checks if operation is allowed
        boolean allowed = arbitrator(domain, file, operation);

        if (allowed) {
            // while the operation is allowed, lock the file
            fileLocks[file].lock();
            // excecute the operation, yield for 3-7 cycles, then unlock
            try {
                if (operation == 0) {
                    System.out.println(threadDomain + "Resource F" + (file + 1) + " contains " + files[file]);
                }
                else {
                    String color = colors[random.nextInt(colors.length)];
                    files[file] = color;
                    System.out.println(threadDomain + "Writing '" + color + "' to resource F" + (file + 1));
                }

                int yields = 3 + random.nextInt(5);
                System.out.println(threadDomain + "Yielding " + yields + " times");
                for (int i = 0; i < yields; i++) {
                    Thread.yield();
                }
            }
            finally {
                fileLocks[file].unlock();
            }

            System.out.println(threadDomain + "Operation Complete.");
        }
        // if operation is not allowed, just yield the 3-7 cycles
        else {
            System.out.println(threadDomain + "Operation failed, permission denied");

            int yields = 3 + random.nextInt(5);
            System.out.println(threadDomain + "Yielding " + yields + " times");
            for (int i = 0; i < yields; i++) {
                Thread.yield();
            }
        }
    }


    private static int switchDomain(int domain, int location, int thread) {
        // creates prefix for each output as a string
        String threadDomain = "[Thread: " + thread + "(D" + (domain + 1) + ")] ";

        // prints out where the current domain is attempting to switch to
        System.out.println(threadDomain + "Attemping to switch from D" + (domain + 1) + " to D" + (location + 1) + ".");

        //checks if operation is allowed
        boolean allowed = arbitrator(domain, m + location, 2);

        // if switch is allowed, update current domain and yield 3-6 cycles
        if (allowed) {
            domain = location;
            threadDomain = "[Thread: " + thread + "(D" + (domain + 1) + ")] ";

            System.out.println(threadDomain + "Switched to D" + (domain + 1));

            int yields = 3 + random.nextInt(5);
            System.out.println(threadDomain + "Yielding " + yields + " times");
            for (int i = 0; i < yields; i++) {
                Thread.yield();
            }
            System.out.println(threadDomain + "Operation Complete.");
        }
        // if switch is not allowed, print that the operation fails
        else {
            System.out.println(threadDomain + "Operation failed, permission denied");
        }

        return domain;
    }

    // prints the access lists
    static void printAccessLists() {
        System.out.println("Domains: " + n);
        System.out.print("Files: " + m);

        // files
        for (int i = 0; i < m; i++) {
            System.out.print("\nF" + (i + 1) + " ->");

            if (accessList[i].isEmpty()) {
                System.out.print(" No access");
            }
            for (int j = 0; j < accessList[i].size(); j++) {
                objectAccess permission = accessList[i].get(j);
                if (j > 0) {
                    System.out.print(",");
                }
                System.out.print(" D" + (permission.domain + 1) + ":");
                if (permission.read && permission.write) {
                    System.out.print("R/W");
                }
                else if (permission.read) {
                    System.out.print("R");
                }
                else if (permission.write) {
                    System.out.print("W");
                }
            }
        }

        // domains
        for (int i = 0; i < n; i++) {
            System.out.print("\nD" + (i + 1) + " ->");

            if (accessList[m+i].isEmpty()) {
                System.out.print(" No switch");
            }
            for (int j = 0; j < accessList[m+i].size(); j++) {
                objectAccess permission = accessList[m+i].get(j);
                if (j > 0) {
                    System.out.print(",");
                }
                if (permission.switches) {
                    System.out.print(" D" + (permission.domain + 1) + ":allow");
                }
            }
        }
        System.out.println();
        System.out.println();
    }


    public static void run() {
        createAccessList();
        System.out.println("Access control scheme: Access List");
        printAccessLists();

        files = new String[m];
        fileLocks = new Lock[m];

        // sets the files' strings to a random color and assigns each a lock
        for (int i = 0; i < m; i++) {
            files[i] = colors[random.nextInt(colors.length)];
            fileLocks[i] = new ReentrantLock();
        }

        // array of threads
        Thread[] threads = new Thread[n];

        // forks a new thread for each domain and starts them
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(new DomainThread(i));
        }
        for (int i = 0; i < n; i++) {
            threads[i].start();
        }
        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
