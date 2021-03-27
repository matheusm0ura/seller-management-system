package model.dao;

import model.entities.Phone;

import java.util.List;

public interface PhoneDao {
    void insert(Phone obj);
    void update(Phone obj);
    void deleteById(Integer id);

    Phone findById(Integer id);
    List<Phone> findAll();
    List<Phone> findByPhone(Phone phone);
}

