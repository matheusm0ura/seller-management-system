package login;

import dbUtil.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginModel {

    Connection conn;

    public LoginModel() {
        this.conn = DB.getConnection();
    }

    public boolean isDataBaseConnected(){
        if (conn != null){
            return true;
        }

        return false;
    }

    public boolean isLogin(String username, String password) throws Exception{
        PreparedStatement pr = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM login where username = ? and password = ?";
        try {
            pr = this.conn.prepareStatement(sql);
            pr.setString(1, username);
            pr.setString(2, password);
            rs = pr.executeQuery();

            boolean boll1;
            if (rs.next()){
                return true;
            }
            return false;

        }catch (SQLException ex){
            return false;
        }
        finally {
            pr.close();
            rs.close();
        }
    }
}
