package edu.sdccd.cisc191.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Trigram {
    @Id
    @Column(name = "most_common")
    private long id;
    @Column(name = "combinations",
            nullable = false,
            columnDefinition = "TEXT")
    private String letters;
    @Column(name = "frequencies")
    private double frequency;

    public Trigram(long id, String letters, double frequency) {
        this.id = id;
        this.letters = letters;
        this.frequency = frequency;
    }

    public Trigram() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Trigram{" +
                "id=" + id +
                ", letters='" + letters + '\'' +
                '}';
    }
}

