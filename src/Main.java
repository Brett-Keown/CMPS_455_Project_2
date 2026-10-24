public class Main {
    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null
                || !"-S".equals(args[0].trim())) {
            printUsage();
            return;
        }

        switch (args[1].trim()) {
            case "1":
                AccessMatrix.accessMatrix();
                break;
            case "2":
                AccessList.run();
                break;
            case "3":
                CapabilityList.run();
                break;
            default:
                printUsage();
        }
    }

    private static void printUsage() {
        System.err.println("Usage: java Main -S <1|2|3>");
        System.err.println("  -S 1  Access Matrix");
        System.err.println("  -S 2  Access List for Objects");
        System.err.println("  -S 3  Capability List for Domains");
        System.exit(1);
    }
}
