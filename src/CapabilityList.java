import java.lang.reflect.Array;
import java.util.Random;
import java.util.ArrayList;
public class CapabilityList {

    public static class accessObject{
        int domain;
        String access;
        public accessObject(int domain, String access){
            this.domain = domain;
            this.access = access;
        }
    }
    //Matrix Creation
    public void matrixGeneration(){
        //STep 1 get dimesnions of matrix

        Random rand = new Random();

        int N = rand.nextInt(5) + 3;
        System.out.println(N);


        int M = rand.nextInt(5) + 3;
        System.out.println(M);

        accessObject[] capabilityMatrix = new accessObject[M * N];

        for(int i = 0; i < N; i ++) {
            for(int j = 0; j < M; j++){
                int accessControl = rand.nextInt(2);
                if (accessControl == 0)
                    capabilityMatrix[i] = (new accessObject(j, "Access"));
                else
                    capabilityMatrix[i] = (new accessObject(j, "No Access"));
            }
        }
        for (accessObject o : capabilityMatrix)
            System.out.println(o);
    }


}
