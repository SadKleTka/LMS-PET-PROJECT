package org.example;

import java.util.Scanner;

class AdminService {
    private final UserDao userDao = new UserDao();

    public void editUser(Scanner scan) {
        UserRole r = null;
        System.out.print("Find user by email: ");
        String email = scan.nextLine();
        if (!email.endsWith("@gmail.com")) {
            System.out.println("Wrong type of email");
            return;
        }
        else if (userDao.findByEmail(email) != null)
            System.out.print("Enter username for a user: ");
        String username = scan.nextLine();
        System.out.print("Enter role for a user (STUDENT/TEACHER: )");
        String role = scan.nextLine().toUpperCase();
        if (role.equalsIgnoreCase("STUDENT")) {
            r = UserRole.STUDENT;
        }
        else if (role.equalsIgnoreCase("TEACHER")) {
            r = UserRole.TEACHER;
        }
        userDao.editUser(email, username, r);
    }

    public void removeUser(Scanner scan) {
        System.out.print("Enter username: ");
        String username = scan.nextLine();
        if (userDao.findByUsername(username) == null) {
            System.out.println("User does not exist");
            return;
        }
        userDao.removeUser(username);
        System.out.println("User has been removed");
    }

    public void manageCourse(Scanner scan) {
        CourseLevel level;
        System.out.println("Enter course name: ");
        String courseName = scan.nextLine();
        Course course = userDao.findCourse(courseName);
        if (course == null) {
            System.out.println("Course does not exist");
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
        System.out.println("Enter the teacher for the course level or press \"ENTER\" to leave it be: ");
        String teacherName = scan.nextLine();
        if (teacherName.isEmpty()) {
            teacherName = course.getTeacher().getUsername();
        }
        Users user = userDao.findByUsername(teacherName);
        if (user == null) {
            System.out.println("User does not exist");
            return;
        }
        else if (user.getRole() != UserRole.TEACHER) {
            System.out.println("This user is not a teacher");
            return;
        }
        userDao.manageCourse(course, newName, courseDescription,courseCategory, level, user);
    }

}

