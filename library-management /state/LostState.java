package state;

import model.BookItem;
import model.Member;

public class LostState implements BookState {

    @Override
    public void issue(BookItem bookItem, Member member) {
        System.out.println("Lost book cannot be issued.");
    }    

    @Override
    public void reserve(BookItem bookItem, Member member) {
        System.out.println("Lost book cannot be reserved.");
    }

    @Override
    public void returnBook(BookItem bookItem) {
        System.out.println("Lost book cannot be returned.");
    }

    @Override
    public void reportLost(BookItem bookItem) {
        System.out.println("Book is already marked as lost.");
    }

    @Override
    public String getName() {
        return "LOST";
    }
}