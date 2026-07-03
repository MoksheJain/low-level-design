package state;

import model.BookItem;
import model.Member;

public interface BookState {

    void issue(BookItem bookItem, Member member);
    void reserve(BookItem bookItem, Member member);
    void returnBook(BookItem bookItem);
    void reportLost(BookItem bookItem);
    String getName();
}

