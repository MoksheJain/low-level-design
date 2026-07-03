package model;

import state.AvailableState;
import state.BookState;

public class BookItem {
    
    private String barcode;
    private Book book;
    private BookState state;

    public BookItem(String barcode, Book book) {
        this.barcode = barcode;
        this.book = book;
        this.state = new AvailableState();
    }

    public String getBarcode() {
        return barcode;
    }

    public Book getBook() {
        return book;
    }

    public BookState getState() {
        return state;
    }

    public void setState(BookState state) {
        this.state = state;
    }

    public boolean isAvailable() {
        return state.getName().equals("AVAILABLE");
    }
}