package state;

import model.BookItem;
import model.Member;

public class AvailableState implements BookState {

    @Override
    public void issue(BookItem bookItem, Member member) {
        System.out.println("Book issued to: " + member.getName());
        bookItem.setState(new IssuedState());
    }

    @Override
    public void reserve(BookItem bookItem, Member member) {
        System.out.println(member.getName() + " reserved the book.");
        bookItem.setState(new ReservedState());
    }

    @Override
    public void returnBook(BookItem bookItem) {
        System.out.println("Book is already available.");
    }

    @Override
    public void reportLost(BookItem bookItem) {
        System.out.println("Book marked as lost");
        bookItem.setState(new LostState());
    }

    @Override
    public String getName() {
        return "AVAILABLE";
    }
}