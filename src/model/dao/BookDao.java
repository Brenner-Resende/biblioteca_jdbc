package model.dao;

import java.util.List;

import model.entities.Book;

public interface BookDao {
	void insert(Book obj);
	void update(Book obj);
	void deleteById(Integer id);
	Book findById(Integer id);
	List<Book> findAll();
	List<Book> availableBooks();
}
