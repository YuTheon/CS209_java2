import javax.print.attribute.URISyntax;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileTypeParser {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String[][] FILE_HEAD = {{"89","50","4e","47"},{"50","4b","03","04"},{"ca","fe","ba","be"}};
    public static void main(String[] args) throws URISyntaxException {
        String file_name = "..\\resources\\" + args[0];
//        URI uri = ByteReader.class.getClassLoader().getResource(args[0]).toURI();
//        System.out.println(uri);
        String filePath = Paths.get(file_name).toString();
//        System.out.println(args[0]);
        try (FileInputStream fis = new FileInputStream(filePath)){
            System.out.printf("File name: %s\n", file_name);

            byte[] buffer = new byte[4];

            int byteNum = fis.read(buffer);


            String[] head = new String[4];
            for (int i = 0; i < 4; i++) {
                char a = HEX_CHAR[buffer[i] >>> 4 & 0xf];
                char b = HEX_CHAR[buffer[i] & 0xf];
                head[i] = String.valueOf(a)+ b;
            }
            System.out.printf("File Header(Hex): %s\n",Arrays.toString(head));
            String type = "UNKNOWN";
            if(Arrays.deepEquals(head, FILE_HEAD[0])){
                type = "png";
            } else if (Arrays.deepEquals(head, FILE_HEAD[1])) {
                type = "zip or jar";
            } else if (Arrays.deepEquals(head, FILE_HEAD[2])) {
                type = "class";
            }
            System.out.printf("File Type: %s\n", type);


        } catch (FileNotFoundException e) {
            System.out.println("The pathname does not exist.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed or interrupted when doing the I/O operations");
            e.printStackTrace();
        }
    }
}
