package de.nico_assfalg.apps.android.clausura;

import java.sql.Timestamp;

public class Exam implements Comparable {

    private Date date;
    private String name;
    private String id;
    private boolean lectureEnd;
    private boolean obsolete;

    //private String url;

    private static int numberOfAttributes = 4;

    public Exam (int day, int month, int year, String name, String id, boolean obsolete) {
        this.date = new Date(day, month, year);
        this.name = name;
        if (id.equals("")) {
            this.id = "0";
        } else {
            this.id = id;
        }
        this.lectureEnd = false;
        this.obsolete = obsolete;
    }

    public String toString() {
        int lectureEndInt;
        if (isLectureEnd()) {
            lectureEndInt = 1;
        } else {
            lectureEndInt = 0;
        }
        return date.toString() + " " + name + " " + id + " " + lectureEndInt;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isLectureEnd() {
        return lectureEnd;
    }

    public static int getNumberOfAttributes() {
        return numberOfAttributes;
    }

    public boolean isObsolete() {
        return obsolete;
    }

    @Override
    public int compareTo(Object o) {
        Exam compare = (Exam) o;
        if (date.toMs() < compare.getDate().toMs()) {
            return -1;
        }
        if (date.toMs() > compare.getDate().toMs()) {
            return 1;
        }
        if (date.toMs() == compare.getDate().toMs()) {
            return 0;
        }
        return 0;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLectureEnd(boolean lectureEnd) {
        this.lectureEnd = lectureEnd;
    }

    public void setObsolete(boolean obsolete) {
        this.obsolete = obsolete;
    }

    @Override
    public boolean equals(Object obj) {
        Exam compare = (Exam) obj;
        if (getDate().equals(compare.getDate())) {
            if (getName().equals(compare.getName())) {
                if (getId().equals(compare.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
