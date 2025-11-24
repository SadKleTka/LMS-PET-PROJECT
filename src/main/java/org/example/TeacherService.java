package org.example;

import java.util.*;

public class TeacherService {
    private final UserDao userDao = new UserDao();

    public void viewStudents(Scanner scan, Users user) {
        List<Course> courses = userDao.viewUsersProgress(user);
        for (Course course : courses) {
            System.out.println("Name of the course: " + course.getName());
            for (Lesson lesson : course.getLessons()) {
                System.out.println("Lesson: " + lesson.getName());
                for (Enrollment el : course.getEnrollments()) {
                    Progress progress = null;
                    for (Progress p : lesson.getStudentsProgress()) {
                        if (p.getStudent().equals(el.getStudent())) {
                            progress = p;
                        }
                    }
                    String prog;
                    if (progress == null) {
                        prog = ", Progress - NOT STARTED";
                    }
                    else if (progress.getStatus() == StatusOfProgress.FINISHED) {
                        prog = ", Progress - FINISHED";
                    } else {
                        prog = ", Progress - STARTED";
                    }
                    System.out.println(el.getStudent().getUsername() + prog);
                }
            }
        }
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    public void createTest(Scanner scan, Users user) {
        Map<String, Map<String, Boolean>> questionsData = new LinkedHashMap<>();

        System.out.println("Enter name of the course: ");
        String courseName = scan.nextLine();
        if (courseName.equalsIgnoreCase("back")) return;

        Course course = userDao.findCourse(courseName);
        if (course == null || !course.getTeacher().getUsername().equalsIgnoreCase(user.getUsername())) {
            System.out.println("Course does not exist or it's not your course");
            return;
        }
        for (Lesson lesson : course.getLessons()) {
            System.out.println("Name of lesson: " + lesson.getName());
        }
        System.out.println();
        System.out.println("Enter name of the lesson: ");
        String lessonName = scan.nextLine();
        if (lessonName.equalsIgnoreCase("back")) return;

        Lesson lesson = userDao.findLesson(course, lessonName);
        if (lesson == null) {
            System.out.println("Lesson does not exist!");
            return;
        }

        System.out.println("Enter name of the test: ");
        String testName = scan.nextLine();
        if (testName.equalsIgnoreCase("back")) return;

        System.out.println("Enter \"stop\" to finish adding questions");

        while (true) {
            System.out.println("Enter the question: ");
            String questionText = scan.nextLine();
            if (questionText.equalsIgnoreCase("stop")) break;

            Map<String, Boolean> answerOptions = new LinkedHashMap<>();
            String[] letters = {"A", "B", "C", "D"};

            for (String letter : letters) {
                System.out.println("Enter answer " + letter + " for the question: ");
                String answerText = scan.nextLine();

                System.out.println("Enter 'R' if correct, any other key if not correct:");
                String correctInput = scan.nextLine();
                boolean isCorrect = correctInput.equalsIgnoreCase("R");

                answerOptions.put(letter + ") " + answerText, isCorrect);
            }

            questionsData.put(questionText, answerOptions);
        }

        userDao.createTest(lesson, testName, questionsData);
        System.out.println("Test created successfully!");
    }


    public void createCourse(Scanner scan, Users user) {
        CourseLevel level = null;
        System.out.println("Enter name for the course:");
        String name = scan.nextLine();
        if (name.equalsIgnoreCase("back"))
            return;
        else if (userDao.findCourse(name) != null) {
            System.out.println("Course already exists!");
            return;
        }
        System.out.println("Enter description for the course:");
        String description = scan.nextLine();
        if (description.equalsIgnoreCase("back"))
            return;
        System.out.println("Enter category for the course:");
        String category = scan.nextLine();
        if (category.equalsIgnoreCase("back"))
            return;
        System.out.println("Enter level of the course:");
        String levelN = scan.nextLine().toUpperCase();
        if (levelN.equalsIgnoreCase("back"))
            return;
        switch (levelN) {
            case "BEGINNER" -> level = CourseLevel.BEGINNER;
            case "INTERMEDIATE" -> level = CourseLevel.INTERMEDIATE;
            case "ADVANCED" -> level = CourseLevel.ADVANCED;
            default -> System.out.println("Invalid level!");
        }
        if (level == null) {
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }
        userDao.createCourse(name, description, category, level, user);
        System.out.println("Course created!");

    }

    public void changeCourse(Scanner scan, Users user) {
        CourseLevel level;
        System.out.println("Enter course name: ");
        String courseName = scan.nextLine();
        Course course = userDao.findCourse(courseName);
        if (course == null) {
            System.out.println("Course does not exist");
            return;
        }
        else if (course.getTeacher() != user) {
            System.out.println("This is not your course");
            return;
        }
        System.out.println("Enter new name for the course or press \"ENTER\" to leave it be: ");
        String newName = scan.nextLine();
        if (newName.isEmpty()) {
            newName = course.getName();
        }
        System.out.println("Enter course description or press \"ENTER\" to leave it be: ");
        String courseDescription = scan.nextLine();
        if (courseDescription.isEmpty()) {
            courseDescription = course.getDescription();
        }
        System.out.println("Enter course category or press \"ENTER\" to leave it be: ");
        String courseCategory = scan.nextLine();
        if (courseCategory.isEmpty()) {
            courseCategory = course.getCategory();
        }
        System.out.println("Enter course level: ");
        String courseLevel = scan.nextLine().toUpperCase();
        switch (courseLevel) {
            case "BEGINNER" -> level = CourseLevel.BEGINNER;
            case "INTERMEDIATE" -> level = CourseLevel.INTERMEDIATE;
            case "ADVANCED" -> level = CourseLevel.ADVANCED;
            default -> level = null;
        }
        userDao.manageCourse(course, newName, courseDescription,courseCategory, level, user);
    }

    public void addLesson(Scanner scan, Users user) {
        System.out.println("Enter course name: ");
        String courseName = scan.nextLine();
        if (courseName.equalsIgnoreCase("back"))
            return;
        else if (userDao.findCourse(courseName) == null) {
            System.out.println("Course does not exist");
            return;
        }
        else if (userDao.findCourse(courseName).getTeacher() != user) {
            System.out.println("This is not your course");
            return;
        }
        System.out.println("Enter lesson name: ");
        String lessonName = scan.nextLine();
        if (lessonName.equalsIgnoreCase("back"))
            return;
        System.out.println("Enter lesson content: ");
        String lessonContent = scan.nextLine();
        if (lessonContent.equalsIgnoreCase("back"))
            return;
        System.out.println("Enter url for lesson's video: ");
        String lessonUrl = scan.nextLine();
        if (lessonUrl.equalsIgnoreCase("back"))
            return;
        if (!lessonUrl.startsWith("https://")) {
            System.out.println("Invalid URL");
            return;
        }
        userDao.addLesson(courseName, lessonName, lessonContent, lessonUrl);

    }
}
