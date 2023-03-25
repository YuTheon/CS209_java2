import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.w3c.dom.ls.LSException;

/**
 * This is just a demo for you, please run it on JDK17 (some statements
 * may be not allowed in lower version).
 * This is just a demo, and you can extend and implement functions
 * based on this demo, or implement it in a different way.
 */
public class OnlineCoursesAnalyzer {
  List<Course> courses = new ArrayList<>();

  /**
  * @param datasetPath path
  */
  public OnlineCoursesAnalyzer(String datasetPath) {
    BufferedReader br = null;
    String line;
    try {
      br = new BufferedReader(new FileReader(datasetPath, StandardCharsets.UTF_8));
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] info = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
        Course course = new Course(info[0], info[1], new Date(info[2]),
                info[3], info[4], info[5],
                Integer.parseInt(info[6]), Integer.parseInt(info[7]),
                Integer.parseInt(info[8]), Integer.parseInt(info[9]),
                Integer.parseInt(info[10]), Double.parseDouble(info[11]),
                Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                Double.parseDouble(info[14]), Double.parseDouble(info[15]),
                Double.parseDouble(info[16]), Double.parseDouble(info[17]),
                Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                Double.parseDouble(info[20]), Double.parseDouble(info[21]),
                Double.parseDouble(info[22]));
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
    Map<String, Long> pcbi = courses
            .stream()
            .sorted(Comparator.comparing(Course::getInstitution).reversed())
            .collect(Collectors.groupingBy(Course::getInstitution,
                    Collectors.summingLong(Course::getParticipants)));
    return pcbi;
  }

  //2
  public Map<String, Long> getPtcpCountByInstAndSubject() {
    Map<String, Long> pcbis = courses
            .stream()
            .collect(Collectors.groupingBy(s -> s.institution + "-"
                    + s.subject, Collectors.summingLong(Course::getParticipants)));
    List<Map.Entry<String, Long>> entryList = new ArrayList<>(pcbis.entrySet());
    entryList.sort(new Comparator<Map.Entry<String, Long>>() {
        @Override
        public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
            return (int) (o2.getValue() - o1.getValue());
        }
    });
    LinkedHashMap<String, Long> spcbi = new LinkedHashMap<String, Long>();
    for (Map.Entry<String, Long> e : entryList
    ) {
      spcbi.put(e.getKey(), e.getValue());
    }
    return spcbi;
  }

  //3
  public Map<String, List<List<String>>> getCourseListOfInstructor() {
    Map<String, List<List<String>>> cloi = new HashMap<>();
    for (Course course : courses) {
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
          if (!cloi.get(in).get(0).contains(course.title)) {
            cloi.get(in).get(0).add(course.title);
          }

        } else {
          if (!cloi.get(in).get(1).contains(course.title)) {
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
      for (List<String> ls : ss) {
        Collections.sort(ls);
      }
      scloi.put(s, ss);
    }
    return scloi;
  }

  //4
  public List<String> getCourses(int topK, String by) {
    List<String> cours = new ArrayList<>();
    if (by.equals("hours")) {
      cours = courses
              .stream()
              .sorted(Comparator.comparing(Course::getTotalHours)
                      .reversed().thenComparing(Course::getTitle))
              .map(s -> s.title)
              .distinct()
              .limit(topK)
              .toList();
    } else if (by.equals("participants")) {
      cours = courses
              .stream()
              .sorted(Comparator.comparing(Course::getParticipants)
                      .reversed().thenComparing(Course::getTitle))
              .map(Course::getTitle)
              .distinct()
              .limit(topK)
              .toList();
    }
    return cours;
  }

  //5
  public List<String> searchCourses(String courseSubject, double percentAudited,
                                    double totalCourseHours) {
    return courses
            .stream()
            .filter(s -> s.subject.toLowerCase().contains(courseSubject.toLowerCase())
                    && s.percentAudited >= percentAudited && s.totalHours <= totalCourseHours)
            .map(s -> s.title)
            .distinct()
            .sorted()
            .toList();
  }


  //6
  public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
    //        number - courses, ensure that number with most updated course
    Map<String, List<Course>> numberCourses = courses
            .stream()
            .sorted(Comparator.comparing(Course::getLaunchDate).reversed())
            .collect(Collectors.groupingBy(Course::getNumber, Collectors.toList()));
    Map<String, Course> numberCourse = numberCourses.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    Map<String, Double> resAge = courses
            .stream()
            .collect(Collectors.groupingBy(Course::getNumber,
                    Collectors.averagingDouble(Course::getMedianAge)));
    Map<String, Double> resGen = courses
            .stream()
            .collect(Collectors.groupingBy(Course::getNumber,
                    Collectors.averagingDouble(Course::getPercentMale)));
    Map<String, Double> resBac = courses
            .stream()
            .collect(Collectors.groupingBy(Course::getNumber,
                    Collectors.averagingDouble(Course::getPercentDegree)));
    //        res : number - sim_value
    Map<String, Double> res = new HashMap<>();
    for (String number : resAge.keySet()) {
      double sim = Math.pow(age - resAge.get(number), 2)
              + Math.pow(gender * 100 - resGen.get(number), 2)
              + Math.pow(isBachelorOrHigher * 100 - resBac.get(number), 2);
      res.put(number, sim);
    }
    return res.entrySet().stream().sorted(
                    Comparator.comparing((Map.Entry<String, Double> e) ->
                            e.getValue()).thenComparing(e ->
                            numberCourse.get(e.getKey()).title))
            .map(e -> numberCourse.get(e.getKey()).title)
            .distinct()
            .limit(10)
            .toList();
  }

  static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
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
    if (title.startsWith("\"")) {
      title = title.substring(1);
    }
    if (title.endsWith("\"")) {
      title = title.substring(0, title.length() - 1);
    }
    this.title = title;
    if (instructors.startsWith("\"")) {
      instructors = instructors.substring(1);
    }
    if (instructors.endsWith("\"")) {
      instructors = instructors.substring(0, instructors.length() - 1);
    }
    this.instructors = instructors;
    if (subject.startsWith("\"")) {
      subject = subject.substring(1);
    }
    if (subject.endsWith("\"")) {
      subject = subject.substring(0, subject.length() - 1);
    }
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