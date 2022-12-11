package com.urise.webapp;

public class Deadlock {
    public static class Student {
        private final String name;
        private final String course;

        public Student(String name, String course) {
            this.name = name;
            this.course = course;
        }

        private synchronized void printAllInfo(Student student) {
            System.out.println(student.name);
            student.printCourse(this);
        }

        private synchronized void printCourse(Student student) {
            System.out.println(this.course);
        }
    }


    public static void main(String[] args) {

        Student student1 = new Student("name1", "course1");
        Student student2 = new Student("name2", "course2");

        new Thread(new Runnable() {
            @Override
            public void run() {
                student1.printAllInfo(student2);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                student2.printAllInfo(student1);
            }
        }).start();
    }
}
