public class Main {
    public static void main(String[] args) {
        CapabilityList.matrixGeneration();
        Thread thread = new Thread(new CapabilityList.capabilityThread(0));
        thread.start();

    }
}
