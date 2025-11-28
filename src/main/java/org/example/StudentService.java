package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

class StudentService {
    private final UserDao userDao = new UserDao();

    public void listOfCourses(Scanner scan) {
        List<Course> courses = userDao.listOfCourses();
        System.out.println("Courses that you can enroll:");
        for (Course course : courses) {
            System.out.println("Name: " + course.getName()
                    + " Category: " + course.getCategory()
                    + " Level: " + course.getLevel()
                    + "\nTeacher: " + course.getTeacher().getUsername());
        }
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    public void enroll(Scanner scan, Users user) {
        System.out.println("Enter course name: ");
        String courseName = scan.nextLine();
        Course course = userDao.findCourse(courseName);
        if (course == null) {
            System.out.println("Invalid course name!");
            return;
        }
        List<Enrollment> enrollments = userDao.findEnrollments(user);
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getCourse().getId().equals(course.getId())) {
                System.out.println("Enrollment already exists!");
                return;
            }
        }
        userDao.enroll(course, user);
        List<Lesson> lessons = course.getLessons();
        for (Lesson lesson : lessons) {
            userDao.trackProgress(user, lesson, StatusOfProgress.STARTED);
        }
        System.out.println("You have enrolled course " + courseName);
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    public void listOfLessons(Scanner scan, Users user) {
        System.out.println();
        List<Enrollment> courses = userDao.findEnrollments(user);
        if (courses.isEmpty()) {
            System.out.println("You have not enrolled any courses!");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }
        for (Enrollment course : courses) {
            System.out.println("Name of the course: " + course.getCourse().getName());
            for (Lesson lesson : course.getCourse().getLessons()) {
                System.out.println("Lesson name: " + lesson.getName());
            }
        }
        System.out.println("Enter name of lesson that you want to see: ");
        String lessonName = scan.nextLine();
        outer:
        for (Enrollment course : courses) {
            for (Lesson lesson : course.getCourse().getLessons()) {
                if (lesson.getName().equals(lessonName) && userDao.findLesson(lessonName).getCourse().getId().equals(course.getCourse().getId())) {
                    System.out.println("Lesson name: " + lesson.getName()
                    + "\nContent: " + lesson.getContent()
                    + "\nOnline lessons: " + lesson.getVideoUrl());
                    System.out.println("Press \"yes\" if you want to pass it's test");
                    if (scan.nextLine().equals("yes")) {
                        boolean passed = test(scan, lesson);
                        if (passed) {
                            System.out.println("You have passed it's test");
                            List<Progress> progresses = lesson.getStudentsProgress();
                            for (Progress progress : progresses) {
                                userDao.changeProgress(progress, StatusOfProgress.FINISHED);
                            }
                        }
                        else
                            System.out.println("You have failed it's test");
                    }
                }
                else continue outer;
            }
            System.out.println();
            System.out.println("Do you want to leave a comment for this course? (YES/NO)");
            String comment = scan.nextLine();
            if (comment.equals("yes")) {
                addComment(scan, user, course.getCourse());
            }
            else if  (comment.equals("no")) {
                return;
            }
        }
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    public void viewProgress(Scanner scan, Users user) {
        List<Enrollment> courses = userDao.findEnrollments(user);
        if (courses.isEmpty()) {
            System.out.println("You have not enrolled any courses!");
            System.out.println("Press any key to continue...");
            scan.nextLine();
            return;
        }
        for (Enrollment course : courses) {
            System.out.println("Name of the course: " + course.getCourse().getName());
            for (Lesson lesson : course.getCourse().getLessons()) {
                System.out.println();
                System.out.print("Lesson name: " + lesson.getName());
                for (Progress progress : lesson.getStudentsProgress()) {
                    if (!progress.getStudent().getId().equals(user.getId())) {
                        continue;
                    }
                    System.out.print(" - " + progress.getStatus() + "\n");
                    if (progress.getStatus() == StatusOfProgress.FINISHED) {
                        System.out.println("Finished at: " + progress.getCompletedAt());
                    }
                    else {
                        System.out.println("Not finished yet");
                    }
                }
            }
        }
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    public void addComment(Scanner scan, Users user, Course course) {
        System.out.println("Enter comment: ");
        String comment = scan.nextLine();
        userDao.addComment(user.getEmail(), course, comment);
        System.out.println("Adding comment...");
        System.out.println("Comment has been added!");
        System.out.println("Press any key to continue...");
        scan.nextLine();

    }

    public boolean test(Scanner scan, Lesson lesson) {
        List<Integer> answers = new ArrayList<>();
        HashMap<String, Boolean> results = new HashMap<>();
        int i = 0;
        int t = 1;
        Test test = lesson.getTest();
        System.out.println("To pass the test you have to get at least 70%");
        System.out.println("Test name: " + test.getName());
        for (Question question : test.getQuestions()) {
            i++;
            System.out.println("Question " + i + "\n" + question.getText());
            for (AnswerOption answerOption : question.getAnswers()) {
                System.out.println();
                System.out.println("Answers: ");
                System.out.print(answerOption.getText() + " ");
                if (t == 1) {
                    results.put("A", answerOption.getCorrect());
                }
                else if (t == 2) {
                    results.put("B", answerOption.getCorrect());
                }
                else if (t == 3) {
                    results.put("C", answerOption.getCorrect());
                }
                else if (t == 4) {
                    results.put("D", answerOption.getCorrect());
                }
                t++;
            }
            System.out.println();
            String answer = scan.nextLine().toUpperCase();
                switch (answer) {
                    case "A", "B", "C", "D" -> {
                        if (results.get(answer)) {
                            System.out.println("That's right!");
                            answers.add(1);
                        } else {
                            System.out.println("Wrong answer!");
                            answers.add(0);
                        }
                        results.clear();
                        t = 1;
                    }
                    default -> System.out.println("Wrong answer!");
                }
            }
        int count = answers.size();
        int correct = 0;
        for (int o :  answers) {
            if (o == 1) {
                correct++;
            }
        }
        double percentage = (double) 100 / count;
        boolean passed = false;
        double passedPercentage = correct * percentage;
        if (passedPercentage >= 70) {
            passed = true;
        }
        return passed;
    }

}
