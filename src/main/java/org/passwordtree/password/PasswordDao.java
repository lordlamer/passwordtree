package org.passwordtree.password;

import java.util.List;

public interface PasswordDao {
    /**
     * find password
     *
     *
     * @return
     */
    List<Password> listPasswords(PasswordFilter passwordFilter);

    /**
     * find password by given id
     * @param id
     * @return
     */
    Password findById(long id);

    /**
     * check if password exists
     * @param password
     * @return
     */
    boolean isPasswordExist(Password password);

    /**
     * create password from domain object
     * @param password
     */
    void createPassword(Password password);

    /**
     * update existing password
     * @param password
     */
    void updatePassword(Password password);

    /**
     * delete all passwords
     */
    void deleteAllPasswords();

    /**
     * delete password by given id
     * @param id
     */
    void deletePasswordById(long id);
}
