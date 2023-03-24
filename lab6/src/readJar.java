
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 *
 * @author hy
 * @createTime 2021-06-20 09:41:55
 * @description 读取jar的内容并获得解析(使用jarInputStream方式)
 *
 */
public class readJar {
    public static void main(String[] args) throws MalformedURLException {
        String jarFilePath = "D:\\SUSTech_A\\grade3\\Java2\\lab\\java2_lab\\lab6\\resources\\rt.jar";

        File file = new File(jarFilePath);

        if (!file.exists()) {
            System.out.println("文件不存在！");
            return;
        }
        if (!file.isFile()) {
            System.out.println("读取的为文件夹而非文件！");
            return;
        }
        if (!file.canRead()) {
            System.out.println("当前文件不可读！");
            return;
        }
        URL url1 = file.toURI().toURL();
        // 必须使用urlClassLoader,才能实例化当前加载的类
        URLClassLoader jarUrlClassLoader = new URLClassLoader(new URL[] { url1 },
                Thread.currentThread().getContextClassLoader());
        int count_class = 0;
        StringBuilder count_clz = new StringBuilder();
        try (JarInputStream jis = new JarInputStream(new FileInputStream(file))) {
//            Manifest manifest = jis.getManifest();
//            pringManifestFile(manifest);
            JarEntry nextJarEntry = jis.getNextJarEntry();
            while (nextJarEntry != null) {
                String className = getClassName(nextJarEntry);
                assert className != null;
//                System.out.println(className);
                if(className.startsWith("java/io") | className.startsWith("java/nio")){
                    count_class++;
                    count_clz.append(className).append("\n");
                }
                nextJarEntry = jis.getNextJarEntry();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.print(count_clz);
        System.out.printf("# of .class files in java.io/java.nio: %d\n", count_class);
    }


    private static String getClassName(JarEntry entry) {
        String name = entry.getName();
        // 这个获取的就是一个实体类class java.util.jar.JarFile$JarFileEntry
        // Class<? extends JarEntry> class1 = nextElement.getClass();
//        System.out.println("entry name=" + name);
        // 加载某个class文件，并实现动态运行某个class
        if (name.endsWith(".class")) {
//            String replace = name.replace(".class", "").replace("/", ".");
//            return replace;
            return name;
        }
        return null;
    }

    /**
     *
     * @author hy
     * @createTime 2021-06-20 10:32:16
     * @description 输出当前的manifest文件中的信息内容
     * @param manifest
     *
     */
//    private static void pringManifestFile(Manifest manifest) {
//        Attributes mainAttributes = manifest.getMainAttributes();
//        Set<Entry<Object, Object>> entrySet = mainAttributes.entrySet();
//        Iterator<Entry<Object, Object>> iterator = entrySet.iterator();
//        // 打印并显示当前的MAINFEST.MF文件中的信息
//        while (iterator.hasNext()) {
//            Entry<Object, Object> next = iterator.next();
//            Object key = next.getKey();
//            Object value = next.getValue();
//            // 这里可以获取到Class-Path,或者某个执行的Main-Class
//            System.out.println(key + ": " + value);
//        }
//    }
}

