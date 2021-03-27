package model.services;

import model.dao.DaoFactory;
import model.dao.PhoneDao;
import model.dao.SellerDao;
import model.entities.Phone;

import java.util.List;

public class PhoneService {
    private PhoneDao dao = DaoFactory.createPhoneDao();

    public List<Phone> findAll(){
        return dao.findAll();
    }

    public void saveOrUpdate(Phone obj){
        if (obj.getId() == null){
            dao.insert(obj);
        }
        else {
            dao.update(obj);
        }
    }

    public void remove(Phone obj){
        dao.deleteById(obj.getId());
    }
}
