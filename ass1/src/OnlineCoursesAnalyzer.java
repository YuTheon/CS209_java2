import org.w3c.dom.ls.LSException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * This is just a demo for you, please run it on JDK17 (some statements may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {

    List<Course> courses = new ArrayList<>();

    public OnlineCoursesAnalyzer(String datasetPath) {
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4], info[5],
                        Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                        Integer.parseInt(info[9]), Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                        Double.parseDouble(info[12]), Double.parseDouble(info[13]), Double.parseDouble(info[14]),
                        Double.parseDouble(info[15]), Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                        Double.parseDouble(info[18]), Double.parseDouble(info[19]), Double.parseDouble(info[20]),
                        Double.parseDouble(info[21]), Double.parseDouble(info[22]));
                courses.add(course);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //1
    public Map<String, Long> getPtcpCountByInst() {
//        TODO 如何按key字母排序，除了下面这种，还可以通过sort中间函数来尝试
        Map<String, Long> pcbi = courses
                .stream()
                .sorted(Comparator.comparing(Course::getInstitution).reversed())
                .collect(Collectors.groupingBy(Course::getInstitution, Collectors.summingLong(Course::getParticipants)));
//        ArrayList<String> keyList = new ArrayList<>(pcbi.keySet());
//        Collections.sort(keyList);
//        LinkedHashMap<String, Long> spcbi = new LinkedHashMap<>();
//        for (String s : keyList) {
//            spcbi.put(s, pcbi.get(s));
//        }
        return pcbi;
    }

    //2
    public Map<String, Long> getPtcpCountByInstAndSubject() {
        Map<String, Long> pcbis = courses
                .stream()
//                .sorted(Comparator.comparing(Course::getParticipants).reversed())
                .collect(Collectors.groupingBy(s->s.institution+"-"+s.subject, Collectors.summingLong(Course::getParticipants)));
        List<Map.Entry<String, Long>> entryList = new ArrayList<>(pcbis.entrySet());
        entryList.sort(new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });
        //遍历排序好的list，一定要放进LinkedHashMap，因为只有LinkedHashMap是根据插入顺序进行存储
        LinkedHashMap<String, Long> spcbi = new LinkedHashMap<String, Long>();
        for (Map.Entry<String,Long> e : entryList
        ) {
            spcbi.put(e.getKey(),e.getValue());
        }
        return spcbi;
    }

    //3
//    TODO 如何将指导者分开计算，并且在后面形成两个list，感觉应该用groupby，但是前面的怎么提取单个指导者呢(虽然也没说一定用stream)
    public Map<String, List<List<String>>> getCourseListOfInstructor() {
//        Map<String, List<String>> cloi = courses.stream().collect(Collectors.groupingBy(Course::getInstructors,
//                Collectors.filtering()))
        Map<String, List<List<String>>> cloi = new HashMap<>();
        for (Course  course :courses) {
            String[] ins = course.instructors.split(", ");
            for (int i = 0; i < ins.length; i++) {
                ins[i] = ins[i].strip();
            }
            for (String in : ins) {
                if (!cloi.containsKey(in)) {
                    List<List<String>> ss = new ArrayList<>();
                    ss.add(new ArrayList<>());
                    ss.add(new ArrayList<>());
                    cloi.put(in, ss);
                }
                if (ins.length == 1) {
                    if(!cloi.get(in).get(0).contains(course.title)){
                        cloi.get(in).get(0).add(course.title);
                    }

                } else {
                    if(!cloi.get(in).get(1).contains(course.title)){
                        cloi.get(in).get(1).add(course.title);
                    }
                }
            }
        }
        ArrayList<String> keyList = new ArrayList<>(cloi.keySet());
        Collections.sort(keyList);
        LinkedHashMap<String, List<List<String>>> scloi = new LinkedHashMap<>();
        for (String s : keyList) {
            List<List<String>> ss = cloi.get(s);
            for(List<String> ls : ss){
                Collections.sort(ls);
            }
            scloi.put(s, ss);
        }
//        System.out.println(scloi.toString());
        return scloi;
    }

    //4
    public List<String> getCourses(int topK, String by) {
        List<String> cours = new ArrayList<>();
        if(by.equals("hours")){
            cours = courses.stream().sorted(Comparator.comparing(Course::getTotalHours).reversed().thenComparing(Course::getTitle)).map(s->s.title).distinct().limit(topK).toList();
        }else if(by.equals("participants")){
            cours = courses.stream().sorted(Comparator.comparing(Course::getParticipants).reversed().thenComparing(Course::getTitle)).map(Course::getTitle).distinct().limit(topK).toList();
        }
        return cours;
    }

    //5
    public List<String> searchCourses(String courseSubject, double percentAudited, double totalCourseHours) {
        return courses
                .stream()
                .filter(s->s.subject.toLowerCase().contains(courseSubject.toLowerCase()) && s.percentAudited >= percentAudited && s.totalHours <= totalCourseHours)
                .map(s->s.title)
                .distinct()
                .sorted()
                .toList();
    }


    //6
    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
//        TODO 按照时间排出最后一个 number-title,可能在这里出问题了，执行筛选任务
        List<Course> number_title0 = courses
                .stream()
                .sorted(Comparator.comparing(Course::getLaunchDate).reversed())
                .filter(distinctByKey(s->s.number))
                .toList();
        Map<String, String> number_title = number_title0
                .stream()
                .collect(Collectors.toMap(Course::getNumber, Course::getTitle));
        Map<String, Double> res_age = courses
                .stream()
                .collect(Collectors.groupingBy(Course::getNumber, Collectors.averagingDouble(Course::getMedianAge)));
        Map<String, Double> res_gen = courses
                .stream()
                .collect(Collectors.groupingBy(Course::getNumber, Collectors.averagingDouble(Course::getPercentMale)));
        Map<String, Double> res_bac = courses
                .stream()
                .collect(Collectors.groupingBy(Course::getNumber, Collectors.averagingDouble(Course::getPercentDegree)));
        Map<String, Double> res = new HashMap<>();
        for(String number : res_age.keySet()){
            double sim = Math.pow(age-res_age.get(number), 2) + Math.pow(gender - res_gen.get(number), 2) +
                    Math.pow(isBachelorOrHigher - res_bac.get(number), 2);
//            List<Double> val = Arrays.asList(res_age.get(number), res_gen.get(number), res_bac.get(number));
            res.put(number, -sim);
        }
        List<String> res_sim = res.entrySet().stream().sorted(
                Comparator.comparing((Map.Entry<String, Double> e)->e.getValue()).thenComparing(e->number_title.get(e.getKey())))
                .map(e->number_title.get(e.getKey()))
                .distinct()
                .limit(10)
                .toList();
        Map<String, String> title_number = number_title0
                .stream()
                .filter(distinctByKey(s->s.title))
                .collect(Collectors.toMap(Course::getTitle, Course::getNumber));
        for (int i = 0; i < res_sim.size(); i++) {
            System.out.printf("title-sim : %s = %f\n", res_sim.get(i), res.get(title_number.get(res_sim.get(i))));
        }

        return res_sim;
    }
    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor){
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}

class Course {
    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    public Course(String institution, String number, Date launchDate,
                  String title, String instructors, String subject,
                  int year, int honorCode, int participants,
                  int audited, int certified, double percentAudited,
                  double percentCertified, double percentCertified50,
                  double percentVideo, double percentForum, double gradeHigherZero,
                  double totalHours, double medianHoursCertification,
                  double medianAge, double percentMale, double percentFemale,
                  double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) title = title.substring(1);
        if (title.endsWith("\"")) title = title.substring(0, title.length() - 1);
        this.title = title;
        if (instructors.startsWith("\"")) instructors = instructors.substring(1);
        if (instructors.endsWith("\"")) instructors = instructors.substring(0, instructors.length() - 1);
        this.instructors = instructors;
        if (subject.startsWith("\"")) subject = subject.substring(1);
        if (subject.endsWith("\"")) subject = subject.substring(0, subject.length() - 1);
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public String getInstitution() {
        return institution;
    }

    public String getNumber() {
        return number;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructors() {
        return instructors;
    }

    public String getSubject() {
        return subject;
    }

    public int getYear() {
        return year;
    }

    public int getHonorCode() {
        return honorCode;
    }

    public int getParticipants() {
        return participants;
    }

    public int getAudited() {
        return audited;
    }

    public int getCertified() {
        return certified;
    }

    public double getPercentAudited() {
        return percentAudited;
    }

    public double getPercentCertified() {
        return percentCertified;
    }

    public double getPercentCertified50() {
        return percentCertified50;
    }

    public double getPercentVideo() {
        return percentVideo;
    }

    public double getPercentForum() {
        return percentForum;
    }

    public double getGradeHigherZero() {
        return gradeHigherZero;
    }

    public double getTotalHours() {
        return totalHours;
    }

    public double getMedianHoursCertification() {
        return medianHoursCertification;
    }

    public double getMedianAge() {
        return medianAge;
    }

    public double getPercentMale() {
        return percentMale;
    }

    public double getPercentFemale() {
        return percentFemale;
    }

    public double getPercentDegree() {
        return percentDegree;
    }
}