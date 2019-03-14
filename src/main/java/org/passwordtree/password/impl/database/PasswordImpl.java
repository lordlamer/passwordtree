package org.passwordtree.password.impl.database;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.password.Password;
import org.passwordtree.password.PasswordDao;
import org.passwordtree.password.PasswordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class PasswordImpl implements PasswordDao {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrikaBeanMapper mapper;

    /**
     * find password
     *
     * @param passwordFilter
     * @return
     */
    @Override
    public List<Password> listPasswords(PasswordFilter passwordFilter) {
        return null;
    }

    /**
     * find password by given id
     *
     * @param id
     * @return
     */
    @Override
    public Password findById(long id) {
        return null;
    }

    /**
     * check if password exists
     *
     * @param password
     * @return
     */
    @Override
    public boolean isPasswordExist(Password password) {
        return false;
    }

    /**
     * create password from domain object
     *
     * @param password
     */
    @Override
    public void createPassword(Password password) {

    }

    /**
     * update existing password
     *
     * @param password
     */
    @Override
    public void updatePassword(Password password) {

    }

    /**
     * delete all passwords
     */
    @Override
    public void deleteAllPasswords() {

    }

    /**
     * delete password by given id
     *
     * @param id
     */
    @Override
    public void deletePasswordById(long id) {

    }
}
