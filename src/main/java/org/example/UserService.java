package org.example;

import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

class UserService {
    private final UserDao userDao = new UserDao();

    public void viewProfile(Scanner scan, Users user) {
        System.out.println("Logged in as " + user.getRole() +
                "\nUsername: " + user.getUsername() +
                "\nEmail: " + user.getEmail() +
                "\nDateCreated: " + user.getDateCreated());
        System.out.println("Press any key to continue");
        scan.nextLine();
    }

    public void viewComments(Scanner scan) {
        System.out.print("Enter course name: ");
        String courseName = scan.nextLine();
        Course course = userDao.findCourse(courseName);
        if (course == null) {
            System.out.println("Course does not exist");
            return;
        }
        List<Comment> comments = userDao.viewComments(course);
        if (comments.isEmpty()) {
            System.out.println("This course has no comments yet");
        }
        System.out.println("Name of the Course: " + course.getName());
        for (Comment comment : comments) {
            System.out.println("Username: " + comment.getUser().getUsername()
                    + "\nText: " + comment
                    + "\nDate of creation: " + comment.getCreatedAt());
        }
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }

    public void register(Scanner scan) {
        UserRole r;
        System.out.println("Enter role (STUDENT/TEACHER)");
        String role = scan.nextLine().toUpperCase();
        if (role.equalsIgnoreCase("back")) {
            return;
        }
        else if (role.equalsIgnoreCase("STUDENT")) {
            r = UserRole.STUDENT;
        }
        else if (role.equalsIgnoreCase("TEACHER")) {
            r = UserRole.TEACHER;
        }
        else if (role.equalsIgnoreCase("ADMIN")) {
            r = UserRole.ADMIN;
        }
        else {
            System.out.println("Wrong role");
            return;
        }
        System.out.print("Enter email: ");
        String email = scan.nextLine();
        if (email.equalsIgnoreCase("back")) {
            return;
        }
        if (!email.endsWith("@gmail.com"))
            return;
        System.out.print("Enter username: ");
        String username = scan.nextLine();
        if (username.equalsIgnoreCase("back")) {
            return;
        }
        System.out.print("Enter password: ");
        String password = scan.nextLine();
        if (password.equalsIgnoreCase("back")) {
            return;
        }
        Users existing = userDao.findByUsername(username);
        if (existing != null) {
            System.out.println("User already exists");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Users newUser = new Users(username, email, hashedPassword, r);
        newUser.setDateCreated(LocalDateTime.now());

        userDao.saveUser(newUser);
        System.out.println("Press any key to continue...");
        scan.nextLine();
    }


    public Users login(Scanner scan) {
        System.out.print("Enter username: ");
        String username = scan.nextLine();
        if (username.equalsIgnoreCase("back")) {
            return null;
        }
        System.out.print("Enter password: ");
        String password = scan.nextLine();
        if (password.equalsIgnoreCase("back")) {
            return null;
        }
        Users user = userDao.findByUsername(username);
        if (user == null) {
            System.out.println("User has not been found");
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            System.out.println("Wrong password");
            return null;
        }
        System.out.println("Welcome " + user.getUsername());
        System.out.println("Press any key to continue...");
        scan.nextLine();
        return user;
    }

    public void findProfile(Scanner scan) {
        System.out.print("Enter username: ");
        String username = scan.nextLine();
        Users user = userDao.findByUsername(username);
        if (user != null) {
        if (user.getRole() == UserRole.STUDENT) {
            List<Enrollment> courses = userDao.findEnrollments(user);
            System.out.println("Username: " + user.getUsername()
                    + "\nRole: " + user.getRole()
                    + "\nDate of creation: " + user.getDateCreated()
                    + "\nCourses enrolled:");
            for (Enrollment course : courses) {
                System.out.println(course.getCourse().getName());
            }
        } else if (user.getRole() == UserRole.TEACHER) {
            List<Course> courses = userDao.viewUsersProgress(user);
            System.out.println("Username: " + user.getUsername()
                    + "\nRole: " + user.getRole()
                    + "\nDate of creation: " + user.getDateCreated()
                    + "\nCourses created:");
            for (Course course : courses) {
                System.out.println("Name: " + course.getName() + " Category: " + course.getCategory());
            }
        }
    }
        else {
            System.out.println("User has not been found");
            return;
        }
        System.out.println("Press \"ENTER\" to continue...");
        scan.nextLine();

    }

}

