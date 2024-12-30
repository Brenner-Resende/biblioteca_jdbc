package model.dao;

import db.DB;
import model.dao.impl.BookDaoJDBC;
import model.dao.impl.LoanDaoJDBC;
import model.dao.impl.UserDaoJDBC;

public class DaoFactory {
	public static BookDao createBookDao() {
		return new BookDaoJDBC(DB.getConnection());
	}
	public static UserDao createUserDao() {
		return new UserDaoJDBC(DB.getConnection());
	}
	public static LoanDao createLoanDao() {
		return new LoanDaoJDBC(DB.getConnection());
	}
}
