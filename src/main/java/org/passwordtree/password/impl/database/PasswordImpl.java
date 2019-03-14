package org.passwordtree.password.impl.database;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.password.Password;
import org.passwordtree.password.PasswordDao;
import org.passwordtree.password.PasswordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PasswordImpl implements PasswordDao {
    @Autowired
    private OrikaBeanMapper mapper;

    @Autowired
    private PasswordRepository passwordRepository;

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
        Optional<PasswordEntity> passwordEntity = passwordRepository.findById(id);

        Password password = null;

        if(passwordEntity.isPresent())
            password = mapper.map(passwordEntity, Password.class);

        return password;
    }

    /**
     * check if password exists
     *
     * @param password
     * @return
     */
    @Override
    public boolean isPasswordExist(Password password) {
        boolean found = false;

        Optional<PasswordEntity> passwordEntity = passwordRepository.findById(Long.valueOf(password.getId()));

        if(passwordEntity.isPresent())
            found = true;

        return found;
    }

    /**
     * create password from domain object
     *
     * @param password
     */
    @Override
    public void createPassword(Password password) {
        PasswordEntity passwordEntity = mapper.map(password, PasswordEntity.class);

        passwordRepository.save(passwordEntity);
    }

    /**
     * update existing password
     *
     * @param password
     */
    @Override
    public void updatePassword(Password password) {
        PasswordEntity passwordEntity = mapper.map(password, PasswordEntity.class);

        passwordRepository.save(passwordEntity);
    }

    /**
     * delete all passwords
     */
    @Override
    public void deleteAllPasswords() {
        passwordRepository.deleteAll();
    }

    /**
     * delete password by given id
     *
     * @param id
     */
    @Override
    public void deletePasswordById(long id) {
        passwordRepository.deleteById(id);
    }
}
