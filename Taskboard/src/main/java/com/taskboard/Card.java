package com.taskboard;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDate creationDate;
    private boolean blocked;
    private String blockReason;
    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private TaskColumn column;

    // Construtores
    public Card() {}
    public Card(String title, String description, TaskColumn column) {
        this.title = title;
        this.description = description;
        this.creationDate = LocalDate.now();
        this.blocked = false;
        this.column = column;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
    public TaskColumn getColumn() { return column; }
    public void setColumn(TaskColumn column) { this.column = column; }
}