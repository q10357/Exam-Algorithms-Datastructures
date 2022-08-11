import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Ex04 {
    public ArrayList<String> ex04Stream(Program program){
        //I understand that this method may seem a little strange, but it works
        StringBuilder string = new StringBuilder();
        return program.courses.stream()
                //Clean the stringBuilder, so we don't get replicates
                .map(c -> string.replace(0, string.length(), (c.courseCode + " - " + getAverageAge(c))).toString())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public int getAverageAge(Course course) {
        return (int) course.students.values().stream()
                .distinct()
                .mapToInt(student -> student.age)
                .average()
                .getAsDouble();

    }

    public static class Course {
        public String courseName;
        public String courseCode;
        public HashMap<Integer, Student> students;
    }

    public static class Program {
        public String programName;
        public ArrayList<Course> courses;

        public ArrayList<Course> getCourses() {
            return courses;
        }
    }

    public static class Student {
        public String firstName;
        public String lastName;
        public Integer studentId;
        public Integer age;

        public Student(String firstName, String lastName, Integer
                studentId, Integer age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.studentId = studentId;
            this.age = age;
        }
    }
}

