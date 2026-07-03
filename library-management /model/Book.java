package model;

import java.util.ArrayList;
import java.util.List;

public class Book {
    
    private String isbn;
    private String title;
    private String author;
    private String category;

    private List<BookItem> copies;

    public Book(String isbn, String title, String author, String category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.copies = new ArrayList<>();
    }

    public void addCopy(BookItem item) {
        copies.add(item);
    }

    public List<BookItem> getCopies() {
        return copies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getIsbn() {
        return isbn;
    }
}