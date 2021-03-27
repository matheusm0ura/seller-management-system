package model.dao;


import dbUtil.DB;
import model.impl.DepartmentDaoJDBC;
import model.impl.PhoneDaoJDBC;
import model.impl.SellerDaoJDBC;

public class DaoFactory {

	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	}

	public static PhoneDao createPhoneDao(){
		return new PhoneDaoJDBC(DB.getConnection());
	}
}
