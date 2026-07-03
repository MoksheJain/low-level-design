package state;

import model.BookItem;
import model.Member;

/**
 * IssuedState
 */
public class IssuedState implements BookState {

    @Override
    public void issue(BookItem bookItem, Member member) {
        System.out.println("Book is already issued.");
    }

    @Override
    public void reserve(BookItem bookItem, Member member) {
        System.out.println("Book is currently issued. Reservation can be added separately.");
    }

    @Override
    public void returnBook(BookItem bookItem) {
        System.out.println("Book returned successfully");
        bookItem.setState(new AvailableState());
    }

    @Override
    public void reportLost(BookItem bookItem) {
        System.out.println("Issued book marked as lost.");
        bookItem.setState(new LostState());
    }

    @Override
    public String getName() {
        return "ISSUED";
    }
}