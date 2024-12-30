package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.LoanDao;
import model.entities.Book;
import model.entities.Loan;
import model.entities.User;

public class LoanDaoJDBC implements LoanDao{
	private Connection conn;
	
	public LoanDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Loan obj) {
	    PreparedStatement st = null;
	    try {

	        PreparedStatement checkSt = null;
	        ResultSet checkRs = null;
	        try {
	            checkSt = conn.prepareStatement("SELECT num_exemplares FROM livro WHERE id_livro = ?");
	            checkSt.setInt(1, obj.getBook().getId());
	            checkRs = checkSt.executeQuery();
	            
	            if (checkRs.next()) {
	                int numAvailable = checkRs.getInt("num_exemplares");
	                if (numAvailable <= 0) {
	                    throw new DbException("No available copies for the selected book!");
	                }
	            } else {
	                throw new DbException("Book not found!");
	            }
	        } finally {
	            DB.closeResultSet(checkRs);
	            DB.closeStatement(checkSt);
	        }

	        if (obj.getReturnDate() != null && !obj.getReturnDate().after(obj.getCheckoutDate())) {
	            throw new DbException("Return date must be after the checkout date!");
	        }


	        st = conn.prepareStatement(
	            "INSERT INTO emprestimo (id_usuario, id_livro, data_emprestimo, data_devolucao) VALUES (?, ?, ?, ?)",
	            Statement.RETURN_GENERATED_KEYS
	        );
	        st.setInt(1, obj.getUser().getId());
	        st.setInt(2, obj.getBook().getId());
	        st.setDate(3, new java.sql.Date(obj.getCheckoutDate().getTime()));
	        if (obj.getReturnDate() != null) {
	            st.setDate(4, new java.sql.Date(obj.getReturnDate().getTime()));
	        } else {
	            st.setNull(4, java.sql.Types.DATE);
	        }

	        int rowsAffected = st.executeUpdate();

	        if (rowsAffected > 0) {
	            ResultSet rs = st.getGeneratedKeys();
	            if (rs.next()) {
	                int id = rs.getInt(1);
	                obj.setId(id);
	            }
	            DB.closeResultSet(rs);
	        } else {
	            throw new DbException("Unexpected error! No rows affected!");
	        }


	        if (obj.getReturnDate() == null) {
	            PreparedStatement updateSt = null;
	            try {
	                updateSt = conn.prepareStatement(
	                    "UPDATE livro SET num_exemplares = num_exemplares - 1 WHERE id_livro = ?"
	                );
	                updateSt.setInt(1, obj.getBook().getId());
	                int rowsUpdated = updateSt.executeUpdate();
	                
	                if (rowsUpdated == 0) {
	                    throw new DbException("Error updating book availability!");
	                }
	            } finally {
	                DB.closeStatement(updateSt);
	            }
	        }

	    } catch (SQLException e) {
	        throw new DbException(e.getMessage());
	    } finally {
	        DB.closeStatement(st);
	    }
	}

	@Override
	public void update(Loan obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE emprestimo "
					+"SET data_emprestimo = ?, data_devolucao = ?, "
					+"id_livro = ?, id_usuario = ? "
					+"WHERE id_emprestimo = ?");
			st.setDate(1, new java.sql.Date(obj.getCheckoutDate().getTime()));
			st.setDate(2, new java.sql.Date(obj.getReturnDate().getTime()));
			st.setInt(3, obj.getBook().getId());
			st.setInt(4, obj.getUser().getId());
			st.setInt(5, obj.getId());

	        if (obj.getReturnDate() != null && !obj.getReturnDate().after(obj.getCheckoutDate())) {
	            throw new DbException("Return date must be after the checkout date!");
	        }

	        if(obj.getReturnDate() != null) {
	        	PreparedStatement updateSt = null;
	            try {
	                updateSt = conn.prepareStatement(
	                    "UPDATE livro SET num_exemplares = num_exemplares + 1 WHERE id_livro = ?"
	                );
	                updateSt.setInt(1, obj.getBook().getId());
	                int rowsUpdated = updateSt.executeUpdate();
	                
	                if (rowsUpdated == 0) {
	                    throw new DbException("Error updating book availability!");
	                }
	            } finally {
	                DB.closeStatement(updateSt);
	            }
	        }
			
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
			st = conn.prepareStatement("DELETE FROM emprestimo WHERE id_emprestimo = ?");
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
	public Loan findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT "
					+ "emprestimo.*, "
					+ "usuario.*, "
					+ "livro.* "
					+ "FROM "
					+ "emprestimo "
					+ "INNER JOIN "
					+ "usuario ON emprestimo.id_usuario = usuario.id_usuario "
					+ "INNER JOIN "
					+ "livro ON emprestimo.id_livro = livro.id_livro "
					+ "WHERE "
					+ "emprestimo.id_emprestimo = ?;"
					);
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) {
				Book bk = instantiateBook(rs);
				User ur = instantiateUser(rs);
				Loan obj = instantiateLoan(rs, bk, ur);
				return obj;
			}
			return null;
		} catch(SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	private Book instantiateBook(ResultSet rs) throws SQLException {
		Book bk = new Book();
		bk.setId(rs.getInt("id_livro"));
		bk.setTitle(rs.getString("titulo"));
		bk.setAuthor(rs.getString("autor"));
		bk.setGenre(rs.getString("genero"));
		bk.setQuantity(rs.getInt("num_exemplares"));
		return bk;
	}
	private User instantiateUser(ResultSet rs) throws SQLException {
		User ur = new User();
		ur.setId(rs.getInt("id_usuario"));
		ur.setName(rs.getString("nome"));
		ur.setEmail(rs.getString("email"));
		ur.setPhone(rs.getString("telefone"));
		return ur;
	}
	private Loan instantiateLoan(ResultSet rs, Book bk, User ur) throws SQLException {
		Loan obj = new Loan();
		obj.setId(rs.getInt("id_emprestimo"));
		obj.setCheckoutDate(rs.getDate("data_emprestimo"));
		obj.setReturnDate(rs.getDate("data_devolucao"));
		obj.setBook(bk);
		obj.setUser(ur);
		
		return obj;
	}

	@Override
	public List<Loan> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT emprestimo.*, usuario.*, livro.* "
					+"FROM emprestimo INNER JOIN usuario "
					+"ON emprestimo.id_usuario = usuario.id_usuario "
					+"INNER JOIN livro ON emprestimo.id_livro = livro.id_livro "
					+"ORDER BY id_emprestimo");
			rs = st.executeQuery();
			List<Loan> list = new ArrayList<>();
			Map<Integer, Book> mapBk = new HashMap<>();
			Map<Integer, User> mapUr = new HashMap<>();
			
			while(rs.next()) {
				Book bk = mapBk.get(rs.getInt("id_livro"));
				User ur = mapUr.get(rs.getInt("id_usuario"));
				if(bk == null) {
					bk = instantiateBook(rs);
					mapBk.put(rs.getInt("id_livro"), bk);
				}
				if(ur == null) {
					ur = instantiateUser(rs);
					mapUr.put(rs.getInt("id_usuario"), ur);
				}
				Loan obj = instantiateLoan(rs, bk, ur);
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
	public List<Loan> findByUser(User user) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT emprestimo.*, usuario.*, livro.* "
					+"FROM emprestimo INNER JOIN livro "
					+"ON emprestimo.id_livro = livro.id_livro "
					+"INNER JOIN usuario "
					+"ON emprestimo.id_usuario = usuario.id_usuario "
					+"WHERE usuario.id_usuario=? "
					+"ORDER BY livro.titulo;"
					);
			st.setInt(1, user.getId());
			rs = st.executeQuery();
			List<Loan> list = new ArrayList<>();
			Map<Integer, Book> mapBk = new HashMap<>();
			Map<Integer, User> mapUr = new HashMap<>();
			
			while(rs.next()) {
				Book bk = mapBk.get(rs.getInt("id_livro"));
				User ur = mapUr.get(rs.getInt("id_usuario"));
				if(bk == null) {
					bk = instantiateBook(rs);
					mapBk.put(rs.getInt("id_livro"), bk);
				}
				if(ur == null) {
					ur = instantiateUser(rs);
					mapUr.put(rs.getInt("id_usuario"), ur);
				}
				Loan obj = instantiateLoan(rs, bk, ur);
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
	public List<Loan> findByBook(Book book) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT emprestimo.*, usuario.*, livro.* "
					+"FROM emprestimo INNER JOIN livro "
					+"ON emprestimo.id_livro = livro.id_livro "
					+"INNER JOIN usuario "
					+"ON emprestimo.id_usuario = usuario.id_usuario "
					+"WHERE livro.id_livro=? "
					+"ORDER BY livro.titulo;"
					);
			st.setInt(1, book.getId());
			rs = st.executeQuery();
			List<Loan> list = new ArrayList<>();
			Map<Integer, Book> mapBk = new HashMap<>();
			Map<Integer, User> mapUr = new HashMap<>();
			
			while(rs.next()) {
				Book bk = mapBk.get(rs.getInt("id_livro"));
				User ur = mapUr.get(rs.getInt("id_usuario"));
				if(bk == null) {
					bk = instantiateBook(rs);
					mapBk.put(rs.getInt("id_livro"), bk);
				}
				if(ur == null) {
					ur = instantiateUser(rs);
					mapUr.put(rs.getInt("id_usuario"), ur);
				}
				Loan obj = instantiateLoan(rs, bk, ur);
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
}
