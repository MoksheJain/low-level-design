package model;
import java.time.LocalDate;

public class Reservation {

    private Member member;
    private Book book;
    private LocalDate reservationDate;

    public Reservation(Member member, Book book, LocalDate reservationDate) {
        this.member = member;
        this.book = book;
        this.reservationDate = reservationDate;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }
}