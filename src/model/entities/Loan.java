package model.entities;

import java.util.Date;
import java.util.Objects;

public class Loan {
	private Integer id;
	private Date checkoutDate;
	private Date returnDate;
	private Book book;
	private User user;
	
	public Loan() {
	}

	public Loan(Integer id, Date date, Date date2, Book book, User user) {
		this.id = id;
		this.checkoutDate = date;
		this.returnDate = date2;
		this.book = book;
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCheckoutDate() {
		return checkoutDate;
	}

	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Loan other = (Loan) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Loan [id=" + id + ", checkoutDate=" + checkoutDate + ", returnDate=" + returnDate + ", book=" + book.getTitle()
				+ ", user=" + user.getName() + "]";
	}
	
}
