package de.nico_assfalg.apps.android.clausura;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ExamHandler {

    private static List<Exam> exams = new ArrayList<>();

    public static void add(Exam ex) {
        exams.add(ex);
        sort();
    }

    public static void remove(int index) {
        exams.remove(index);
        sort();
    }

    public static Exam get(int index) {
        return exams.get(index);
    }

    public static int indexOf(Exam exam) {
        for (int i = 0; i < exams.size(); i++) {
            if (exams.get(i).equals(exam)) {
                return i;
            }
        }
        return -1;
    }

    public static void sort() {
        Collections.sort(exams);
    }

    public static void clean() {
        for (int i = 0; i < exams.size(); i++) {
            Exam ex = exams.get(i);
            if (ex.isObsolete()) {
                remove(i);
            }
        }
    }

    public static String getAsString() {
        if (exams.size() > 0) {
            String s = "";
            for (int i = 0; i < exams.size(); i++) {
                s = s + exams.get(i).toString() + " ";
            }
            return s;
        } else {
            return "";
        }
    }

    public static void set(String s) {
        clear();
        String[] arrayList = s.split(" ");
        int i = 0;
        while (!arrayList[0].equals("") && i < arrayList.length) {
            //get date
            String[] date = arrayList[i].split("-");
            int day = Integer.parseInt(date[0]);
            int month = Integer.parseInt((date[1]));
            int year = Integer.parseInt(date[2]);

            //get name
            String name = arrayList[i+1];

            //get ID
            String id = arrayList[i+2];

            //is it lecture end date?
            boolean lectureEnd;
            if (Integer.parseInt(arrayList[i+3]) == 0) {
                lectureEnd = false;
            } else {
                lectureEnd = true;
            }

            //Initialize Exam
            Exam exam = new Exam(day, month, year, name, id, false);
            exam.setLectureEnd(lectureEnd);

            //Add to List
            add(exam);

            //increase Counter
            i += Exam.getNumberOfAttributes();
        }
        Collections.sort(exams);
    }

    private static void clear() {
        exams.clear();
    }

    public static int getNumberOfExams() {
        return exams.size();
    }
}
