package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class UserDao {

    public List<Enrollment> findEnrollments(Users user) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Enrollment> list = session.createQuery("FROM Enrollment c WHERE c.student = :student", Enrollment.class)
                    .setParameter("student", user)
                    .list();

            tx.commit();
            return list;
        }
    }

    public List<Course> viewUsersProgress(Users user) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Course> courses = session.createQuery(
                    "FROM Course c WHERE c.teacher = :teacher", Course.class
            ).setParameter("teacher", user).list();

            tx.commit();
            return courses;
        }
    }

    public List<Comment> viewComments(Course course) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Comment> comments = session.createQuery("FROM Comment c WHERE c.course = :course", Comment.class)
                    .setParameter("course", course).list();

            tx.commit();
            return comments;
        }
    }

    public void createTest(Lesson lesson, String testName, Map<String, Map<String, Boolean>> questions) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Test test = new Test();
            test.setName(testName);
            test.setLesson(lesson);

            for (Map.Entry<String, Map<String, Boolean>> entry : questions.entrySet()) {
                String questionText = entry.getKey();
                Map<String, Boolean> answersMap = entry.getValue();

                Question question = new Question();
                question.setText(questionText);
                question.setTest(test);

                for (Map.Entry<String, Boolean> answerEntry : answersMap.entrySet()) {
                    AnswerOption answer = new AnswerOption();
                    answer.setText(answerEntry.getKey());
                    answer.setCorrect(answerEntry.getValue());
                    answer.setQuestion(question);

                    question.getAnswers().add(answer);
                }

                test.getQuestions().add(question);
            }

            session.persist(test);
            tx.commit();
        }
    }

    public Lesson findLesson(Course course, String name) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Lesson lesson = session.createQuery("FROM Lesson c WHERE c.course = :course AND c.name = :name", Lesson.class)
                    .setParameter("course", course)
                    .setParameter("name", name)
                    .uniqueResult();

            tx.commit();
            return lesson;
        }
    }


    public void addLesson(String courseName, String lessonName, String content, String videoUrl) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Course course = session.createQuery("FROM Course c WHERE c.name = :name", Course.class)
                    .setParameter("name", courseName)
                    .uniqueResult();
            Lesson lesson = new Lesson(lessonName, content, videoUrl, course);
            course.getLessons().add(lesson);
            session.persist(lesson);

            tx.commit();
        }
    }

    public void createCourse(String name, String description, String category, CourseLevel level, Users user) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Course course = new Course(name, description, category, level, user);
            List<Course> courses = user.getCourses();
            courses.add(course);
            session.persist(course);

            tx.commit();
        }
    }

    public List<Course> listOfCourses() {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<Course> courses = session.createQuery(
                    "FROM Course", Course.class
            ).list();

            tx.commit();
            return courses;
        }
    }


    public void changeProgress(Progress progress, StatusOfProgress status) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            progress.setStatus(status);
            session.merge(progress);

            tx.commit();
        }
    }

    public void trackProgress(Users user, Lesson lesson, StatusOfProgress status) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Progress progress = new Progress(LocalDateTime.now(), user, lesson, status);
            List<Progress> list = user.getStudentsProgress();
            List<Progress> list2 = lesson.getStudentsProgress();
            list.add(progress);
            list2.add(progress);
            session.persist(progress);

            tx.commit();
        }
    }

    public void enroll(Course course, Users user) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Enrollment enrollment = new Enrollment(user, course);
            List<Enrollment> list = user.getEnrollments();
            list.add(enrollment);
            session.persist(enrollment);

            tx.commit();
        }
    }

    public Course findCourse(String name) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Course course = session.createQuery("FROM Course c WHERE c.name = :name", Course.class)
                    .setParameter("name", name)
                    .uniqueResult();

            tx.commit();
            return course;
        }
    }

    public void manageCourse(Course course, String newName, String description, String category, CourseLevel level, Users teacher) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Course editCourse = session.get(Course.class, course.getId());
            editCourse.setName(newName);
            editCourse.setDescription(description);
            editCourse.setCategory(category);
            editCourse.setLevel(level);
            editCourse.setTeacher(teacher);
            session.merge(editCourse);

            tx.commit();
        }
    }

    public Users findByEmail(String email) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Users user = session.createQuery(
                            "FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .uniqueResult();

            tx.commit();
            return user;
        }
    }

    public Users findByUsername(String username) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Users user = session.createQuery(
                            "FROM Users u WHERE u.username = :username", Users.class)
                    .setParameter("username", username)
                    .uniqueResult();

            tx.commit();
            return user;
        }
    }

    public void removeUser(String username) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Users user = findByUsername(username);
            session.remove(user);

            tx.commit();
        }
    }

    public void saveUser(Users user) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.persist(user);

            tx.commit();
        }
    }

    public void editUser(String email, String username, UserRole role) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Users user = session.createQuery(
                            "FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .uniqueResult();
            user.setUsername(username);
            user.setRole(role);
            session.merge(user);

            tx.commit();
        }
    }

    public void addComment(String email, Course course, String text) {
        Transaction tx;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Users user = session.createQuery("FROM Users c WHERE c.email = :email", Users.class)
                    .setParameter("email", email).uniqueResult();
            List<Comment> comments = user.getComments();
            Comment comment = new Comment(text, user, course);
            comments.add(comment);
            session.persist(comment);

            tx.commit();
        }
    }
}
