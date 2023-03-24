import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
//        ArrayList list = new ArrayList();
//        list.add("hi");
////        list.add(2022);
//        for (int i = 0; i < list.size(); i++) {
//            String s = (String) list.get(i);
//            System.out.println(s);
//        }
//        Main m = new Main();
//        m.test();
        List<String> stringList = new ArrayList<String>();
        stringList.add("123");
        stringList.add("456");
        stringList.add("str");
        stringList.stream()
                .map(Integer::parseInt)
                .forEach(System.out::println);

    }
    Supplier<String> textSupplier = () -> "Hello world";
    public void test(){
        System.out.println(textSupplier.get());
    }
}