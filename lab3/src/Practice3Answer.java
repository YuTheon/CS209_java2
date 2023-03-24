import java.util.*;
import java.util.stream.Collectors;

public class Practice3Answer {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int order = 0;
        while(true){
            System.out.print("Please input the function No:\n" +
                    "1 - Get even numbers\n" +
                    "2 - Get odd numbers\n" +
                    "3 - Get prime numbers\n" +
                    "4 - Get prime numbers that are bigger than 5\n" +
                    "0 - Quit\n");
            order = in.nextInt();
            if(order == 0){
                break;
            }
            System.out.println("Input size of the list:");
            int size = in.nextInt();
            ArrayList<Integer> array = new ArrayList<>();
            System.out.println("Input elements of the list:");
            for (int i = 0; i < size; i++) {
                array.add(in.nextInt());
            }
            System.out.println("Filter results:");
            switch (order){
                case 1:
                    System.out.println(array.stream().filter(value->value%2==0).toList());;break;
                case 2:
                    System.out.println(array.stream().filter(value->value%2!=0).toList());;break;
                case 3:
                    System.out.println(array.stream().filter(Practice3Answer::checkPrime).toList());;break;
                case 4:
                    System.out.println(array.stream().filter(value->value>5 && checkPrime(value)).toList());;break;
            }
        }
    }

    static boolean checkPrime(int in){
        int a = 2;
        if(in == 1){
            return false;
        }
        while(a <= Math.sqrt(in)){
            if(in % a == 0){
                return false;
            }
            a += 1;
        }
        return true;
    }

}
