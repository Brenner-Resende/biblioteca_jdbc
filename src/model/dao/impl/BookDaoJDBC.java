package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.BookDao;
import model.entities.Book;

public class BookDaoJDBC implements BookDao{
	private Connection conn;

	public BookDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Book obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO livro "
					+ "(titulo, autor, genero, num_exemplares) "
					+ "VALUES "
					+ "(?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getTitle());
			st.setString(2, obj.getAuthor());
			st.setString(3, obj.getGenre());
			st.setInt(4, obj.getQuantity());
			
			int rowsAffected = st.executeUpdate();
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Book obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE livro "
					+"SET titulo=?, autor=?, genero=?, num_exemplares=? " 
					+"WHERE id_livro=?");
			st.setString(1, obj.getTitle());
			st.setString(2, obj.getAuthor());
			st.setString(3, obj.getGenre());
			st.setInt(4, obj.getQuantity());
			st.setInt(5, obj.getId());
			
			st.executeUpdate();
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM livro WHERE id_livro=?");
			st.setInt(1, id);
			int rowsAffected = st.executeUpdate();
			if(rowsAffected == 0) {
				throw new DbException("Invalid id!");
			}
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Book findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT  * FROM livro WHERE id_livro=?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) {
				Book obj = instantiateBook(rs);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	private Book instantiateBook(ResultSet rs) throws SQLException {
		Book obj = new Book();
		obj.setId(rs.getInt("id_livro"));
		obj.setTitle(rs.getString("titulo"));
		obj.setAuthor(rs.getString("autor"));
		obj.setGenre(rs.getString("genero"));
		obj.setQuantity(rs.getInt("num_exemplares"));
		return obj;
	}

	@Override
	public List<Book> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM livro ORDER BY titulo");
			rs = st.executeQuery();
			List<Book> list = new ArrayList<>();
			while(rs.next()) {
				Book obj = instantiateBook(rs);
				list.add(obj);
			}
			return list;
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	@Override
	public List<Book> availableBooks(){
		PreparedStatement st = null;
		ResultSet rs = null;
		List<Book> list = new ArrayList<>();
		try {
			st = conn.prepareStatement("SELECT * FROM livro WHERE num_exemplares > 0 "
					+"ORDER BY titulo");
			rs = st.executeQuery();
			while(rs.next()) {
				Book obj = instantiateBook(rs);
				list.add(obj);
			}
			return list;
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
