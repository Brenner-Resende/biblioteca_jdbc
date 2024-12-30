package model.dao;

import java.util.List;

import model.entities.Book;
import model.entities.Loan;
import model.entities.User;

public interface LoanDao {
	void insert(Loan obj);
	void update(Loan obj);
	void deleteById(Integer id);
	Loan findById(Integer id);
	List<Loan> findAll();
	List<Loan> findByUser(User user);
	List<Loan> findByBook(Book book);
}
