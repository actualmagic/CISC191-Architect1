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

    public Trigram(long id, String letters) {
        this.id = id;
        this.letters = letters;
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

    @Override
    public String toString() {
        return "Trigram{" +
                "id=" + id +
                ", letters='" + letters + '\'' +
                '}';
    }
}

