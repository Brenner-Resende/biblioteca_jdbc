package application;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import model.dao.BookDao;
import model.dao.DaoFactory;
import model.dao.LoanDao;
import model.dao.UserDao;
import model.entities.Book;
import model.entities.Loan;
import model.entities.User;


public class Program {

	public static void main(String[] args) throws ParseException {
		
		//Aplicação principal utilizada para testar as funcionalidades
		
		LoanDao loanDao = DaoFactory.createLoanDao();
		UserDao userDao = DaoFactory.createUserDao();
		BookDao bookDao = DaoFactory.createBookDao();
		
		List<Loan> list = new ArrayList<>();
		
		System.out.println("\n----- TEST: find LOAN -----");
		User user = userDao.findById(3);
		list = loanDao.findByUser(user);
		for(Loan obj : list) {
			System.out.println(obj);
		}
		
		/* System.out.println("\n----- TEST: insert LOAN -----");
		Book book = bookDao.findById(7);
		Loan loan = new Loan(null, sdf.parse("24/12/2024"), null, book, user);
		loanDao.insert(loan);
		System.out.println("Loan registered!");
		list = loanDao.findAll();
		for(Loan obj : list) {
			System.out.println(obj);
		}
		
		System.out.println("\n----- TEST: update LOAN -----");
		loan = loanDao.findById(1);
		loan.setReturnDate(sdf.parse("24/12/2024"));
		loanDao.update(loan);
		System.out.println("Updated successfully!");
		System.out.println(loan); */
		
		
		System.out.println("\n----- TEST: LOAN historic by book -----");
		Book searchBook = bookDao.findById(1);
		list = loanDao.findByBook(searchBook);
		for(Loan obj : list) {
			System.out.println(obj);
		}
	}

}
