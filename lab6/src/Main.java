import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) throws IOException {

            //获取文件输入流
            FileInputStream input = new FileInputStream("D:\\SUSTech_A\\grade3\\Java2\\lab\\java2_lab\\lab6\\resources\\src.zip");

            //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.forName("GBK"));

            //定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
            ZipEntry ze = null;

            //循环遍历
            int count_zip = 0, count_jar = 0;
            StringBuilder sb_java = new StringBuilder();
            while ((ze = zipInputStream.getNextEntry()) != null) {
                String n = ((ZipEntry) ze).getName();
//                System.out.println(n);
                String[] name = n.split("\\.");
                String[] pre = n.split("/");
                int len = name.length;
                if(len == 0){
                    continue;
                }
                if(pre[1].equals("io") | pre[1].equals("nio")) {
                    if (name[len - 1].equals("java")) {
                        count_zip++;
                        sb_java.append(n).append("\n");
                    }
                }

            }

            System.out.println(sb_java.toString());
            System.out.printf("# of .java files in java.io/java.nio: %d\n", count_zip);

            //一定记得关闭流
            zipInputStream.closeEntry();
            input.close();
    }
}