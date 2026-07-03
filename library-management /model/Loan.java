package model;

import java.time.LocalDate;

public class Loan {

    private BookItem bookItem;
    private Member member;

    private LocalDate issueDate;
    private LocalDate dueDate;

    public Loan(BookItem bookItem, Member member, LocalDate issueDate, LocalDate dueDate) {
        this.bookItem = bookItem;
        this.member = member;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

}