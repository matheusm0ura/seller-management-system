package model.impl;

import dbUtil.DB;
import dbUtil.DbException;
import model.dao.PhoneDao;
import model.entities.Department;
import model.entities.Phone;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneDaoJDBC implements PhoneDao {
    private Connection conn;

    public PhoneDaoJDBC(Connection conn) {
        this.conn = conn;
    }


    @Override
    public void insert(Phone obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("INSERT INTO telefone "
                                    +"(number, seller_Id) "
                                    +"VALUES "
                                    +"(?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, obj.getNumber());
            st.setInt(2, obj.getSeller().getId());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            }
            else {
                throw new DbException("Unexpected error! No rows affected!");
            }

        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Phone obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "UPDATE telefone "
                            + "SET number = ?, seller_Id = ? "
                            + "WHERE Id = ?");

            st.setString(1, obj.getNumber());
            st.setInt(2, obj.getSeller().getId());
            st.setInt(3, obj.getId());

            st.executeUpdate();

        }catch (SQLException e){

            throw new DbException(e.getMessage());

        }finally {

            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM telefone WHERE Id = ?");

            st.setInt(1, id);

            st.executeUpdate();
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }


    @Override
    public List<Phone> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT telefone.*, seller.Name as SellerName "
                            + "FROM telefone INNER JOIN seller "
                            + "ON telefone.seller_Id = seller.Id "
                            + "ORDER BY Name");

            rs = st.executeQuery();

            List<Phone> list = new ArrayList<>();
            Map<Integer, Seller> map = new HashMap<>();

            while (rs.next()) {

                Seller sel = map.get(rs.getInt("seller_Id"));

                if (sel == null) {
                    sel = instantiateSeller(rs);
                    map.put(rs.getInt("seller_Id"), sel);
                }

                Phone obj = instantiatePhone(rs, sel);
                list.add(obj);
            }
            return list;
        }
        catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }

    }

    private Phone instantiatePhone(ResultSet rs, Seller sel) throws SQLException {
        Phone obj = new Phone();
        obj.setId(rs.getInt("id"));
        obj.setNumber(rs.getString("number"));
        obj.setSeller(sel);
        return obj;
    }

    private Seller instantiateSeller(ResultSet rs) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("seller_Id"));
        obj.setName(rs.getString("SellerName"));
        //obj.setEmail(rs.getString("SellerEmail"));
        //obj.setBaseSalary(rs.getDouble("BaseSalary"));
        //seller.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
        return obj;

    }


    @Override
    public Phone findById(Integer id) {
        return null;
    }


    @Override
    public List<Phone> findByPhone(Phone phone) {
        return null;
    }
}
