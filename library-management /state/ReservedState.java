package state;

import model.BookItem;
import model.Member;

public class ReservedState implements BookState {

    @Override
    public void issue(BookItem bookItem, Member member) {
        System.out.println("Reserved book issued to " + member.getName());
        bookItem.setState(new IssuedState());
    }

    @Override
    public void reserve(BookItem bookItem, Member member) {
        System.out.println("Book is already reserved");
    }

    @Override
    public void returnBook(BookItem bookItem) {
        System.out.println("Reserved book cannot be returned.");
    }

    @Override
    public void reportLost(BookItem bookItem) {
        System.out.println("Reserved book marked as lost");
        bookItem.setState(new LostState());
    }

    @Override
    public String getName() {
        return "RESERVED";
    }
}