package org.passwordtree.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordService {
    @Autowired
    private PasswordDao passwordImpl;

    /**
     * find all passwords
     * @return
     */
    public List<Password> listPasswords(PasswordFilter passwordFilter) {
        return passwordImpl.listPasswords(passwordFilter);
    }

    /**
     * find password by given id
     * @param id
     * @return
     */
    public Password findById(long id) {
        return passwordImpl.findById(id);
    }

    /**
     * check if password exists
     * @param password
     * @return
     */
    public boolean isPasswordExist(Password password) {
        return passwordImpl.isPasswordExist(password);
    }

    /**
     * create password
     * @param password
     */
    public void createPassword(Password password) {
        passwordImpl.createPassword(password);
    }

    /**
     * update existing password
     * @param password
     */
    public void updatePassword(Password password) {
        passwordImpl.updatePassword(password);
    }

    /**
     * delete all passwords
     */
    public void deleteAllPasswords() {
        passwordImpl.deleteAllPasswords();
    }

    /**
     * delete password by id
     * @param id
     */
    public void deletePasswordById(long id) {
        passwordImpl.deletePasswordById(id);
    }
}
