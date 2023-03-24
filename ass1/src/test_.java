import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test_ {
    public static void main(String[] args) {
        String state = "Aaron Bernstein == [[], [Human Health and Global Environmental Change]]";
        String[] ss = state.split("==");
        for (int i = 0; i < ss.length; i++) {
            ss[i] = ss[i].strip();
        }
//        System.out.println(ss[1].length());
        ss[1] = ss[1].substring(1, ss[1].length()-1);
        String[] stringList = ss[1].split(",");
        for (int i = 0; i < stringList.length; i++) {
            stringList[i] = stringList[i].strip();
            stringList[i] = stringList[i].substring(1, stringList[i].length()-1);
        }
        List<List<String>> map_value = new ArrayList<>();
        List<String> a = Arrays.stream(stringList[0].split(",")).toList();
        a.forEach(String::strip);


        System.out.println(ss[0] + "\n" + ss[1]);
    }
}
