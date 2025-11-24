package org.example;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        UserService fun = new UserService();
        Users log;
        while (true) {
            System.out.println("==== Welcome to LMS ====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Check others profile");
            System.out.println("4. Exit");
            System.out.println("Choose your action: ");
            byte choice = scan.nextByte();
            scan.nextLine();
            switch (choice) {
                case 1 -> fun.register(scan);
                case 2 -> {
                    log = fun.login(scan);
                    if (log == null) {
                        continue;
                    }
                    if (log.getRole() == UserRole.ADMIN) {
                        System.out.println("Welcome Administrator " + log.getUsername());
                        System.out.println("Press any key to continue...");
                        scan.nextLine();
                        adminMenu(scan, log, fun);
                    }
                    else if (log.getRole() == UserRole.STUDENT) {
                        System.out.println("Welcome Student " + log.getUsername());
                        System.out.println("Press any key to continue...");
                        scan.nextLine();
                        studentMenu(scan, log, fun);
                    }
                    else if (log.getRole() == UserRole.TEACHER) {
                        System.out.println("Welcome Teacher " + log.getUsername());
                        System.out.println("Press any key to continue...");
                        scan.nextLine();
                        teacherMenu(scan, log, fun);
                    }
                }
                case 3 -> fun.findProfile(scan);
                case 4 -> System.exit(0);
                default -> System.out.println("Wrong choice");
            }
        }

    }

    private static void teacherMenu(Scanner scan, Users user, UserService userService) {
        TeacherService ts = new TeacherService();
        while (true) {
            System.out.println("\n=== Teacher Menu ===");
            System.out.println("1. Create a course");
            System.out.println("2. Edit your course");
            System.out.println("3. Add a lesson to the course");
            System.out.println("4. Add a test to the lesson");
            System.out.println("5. View students and their progress");
            System.out.println("6. View course comments");
            System.out.println("7. Your profile");
            System.out.println("8. Logout");
            System.out.print("Choose your action: ");
            byte choice = scan.nextByte();
            scan.nextLine();
            switch (choice) {
                case 1 -> ts.createCourse(scan, user);
                case 2 -> ts.changeCourse(scan, user);
                case 3 -> ts.addLesson(scan, user);
                case 4 -> ts.createTest(scan, user);
                case 5 -> ts.viewStudents(scan, user);
                case 6 -> userService.viewComments(scan);
                case 7 -> userService.viewProfile(scan, user);
                case 8 -> {
                    return;
                }
                default -> System.out.println("Wrong choice");
            }
        }
    }

    private static void adminMenu(Scanner scan, Users user, UserService userService) {
        AdminService admin = new AdminService();
        while (true) {
            System.out.println("\n==== Administrator menu ====");
            System.out.println("1. Edit users");
            System.out.println("2. Remove users");
            System.out.println("3. Manage courses");
            System.out.println("4. View course comments");
            System.out.println("5. Your profile");
            System.out.println("6. Logout");
            System.out.println("Choose your action");
            byte choice = scan.nextByte();
            scan.nextLine();
            switch (choice) {
                case 1 -> admin.editUser(scan);
                case 2 -> admin.removeUser(scan);
                case 3 -> admin.manageCourse(scan);
                case 4 -> userService.viewComments(scan);
                case 5 -> userService.viewProfile(scan, user);
                case 6 -> {return;}
                default -> System.out.println("Wrong choice");
            }
        }
    }

    private static void studentMenu(Scanner scan, Users user, UserService userService) {
        StudentService student = new StudentService();
        while (true) {
            System.out.println("\n=== Student menu ===");
            System.out.println("1. List of courses");
            System.out.println("2. Enroll to the course");
            System.out.println("3. View your lessons");
            System.out.println("4. View the progress");
            System.out.println("5. View your profile");
            System.out.println("6. View course comments");
            System.out.println("7. Logout");
            System.out.print("Choose your action: ");
            byte choice = scan.nextByte();
            scan.nextLine();
            switch (choice) {
                case 1 -> student.listOfCourses(scan);
                case 2 -> student.enroll(scan, user);
                case 3 -> student.listOfLessons(scan, user);
                case 4 -> student.viewProgress(scan, user);
                case 5 -> userService.viewProfile(scan, user);
                case 6 -> userService.viewComments(scan);
                case 7 -> {return;}
                default -> System.out.println("Wrong choice");
            }
        }
    }
}