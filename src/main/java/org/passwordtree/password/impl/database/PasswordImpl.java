package org.passwordtree.password.impl.database;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.password.Password;
import org.passwordtree.password.PasswordDao;
import org.passwordtree.password.PasswordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class PasswordImpl implements PasswordDao {
    @PersistenceContext
    private EntityManager entityManager;

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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PasswordEntity> query = cb.createQuery(PasswordEntity.class);
        Root<PasswordEntity> passwordEntityRoot = query.from(PasswordEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if(passwordFilter.getId() != null)
            predicates.add(cb.equal(passwordEntityRoot.get("id"), passwordFilter.getId()));

        if(passwordFilter.getPageId() != null)
            predicates.add(cb.equal(passwordEntityRoot.get("pageId"), passwordFilter.getPageId()));

        if(passwordFilter.getTitle() != null)
            predicates.add(cb.like(passwordEntityRoot.get("title"), "%" + passwordFilter.getTitle() + "%"));

        if(passwordFilter.getDescription() != null)
            predicates.add(cb.like(passwordEntityRoot.get("description"), "%" + passwordFilter.getDescription() + "%"));

        if(passwordFilter.getUsername() != null)
            predicates.add(cb.like(passwordEntityRoot.get("username"), "%" + passwordFilter.getUsername() + "%"));

        if(passwordFilter.getUrl() != null)
            predicates.add(cb.like(passwordEntityRoot.get("url"), "%" + passwordFilter.getUrl() + "%"));

        if(passwordFilter.getIcon() != null)
            predicates.add(cb.like(passwordEntityRoot.get("icon"), "%" + passwordFilter.getIcon() + "%"));

        if(passwordFilter.getDeleted() != null)
            predicates.add(cb.equal(passwordEntityRoot.get("deleted"), passwordFilter.getDeleted()));

        if(passwordFilter.getActive() != null)
            predicates.add(cb.equal(passwordEntityRoot.get("active"), passwordFilter.getActive()));

        if(passwordFilter.getCreatedBy() != null)
            predicates.add(cb.equal(passwordEntityRoot.get("createdBy"), passwordFilter.getCreatedBy()));

        if(passwordFilter.getChangedBy() != null)
            predicates.add(cb.equal(passwordEntityRoot.get("changedBy"), passwordFilter.getChangedBy()));

        if(passwordFilter.getTimeStartBegin() != null)
            predicates.add(cb.greaterThan(passwordEntityRoot.get("timeStart"), passwordFilter.getTimeStartBegin()));

        if(passwordFilter.getTimeStartEnd() != null)
            predicates.add(cb.lessThan(passwordEntityRoot.get("timeStart"), passwordFilter.getTimeStartEnd()));

        if(passwordFilter.getTimeEndBegin() != null)
            predicates.add(cb.greaterThan(passwordEntityRoot.get("timeEnd"), passwordFilter.getTimeEndBegin()));

        if(passwordFilter.getTimeEndEnd() != null)
            predicates.add(cb.lessThan(passwordEntityRoot.get("timeEnd"), passwordFilter.getTimeEndEnd()));

        if(passwordFilter.getCreateDateBegin() != null)
            predicates.add(cb.greaterThan(passwordEntityRoot.get("createDate"), passwordFilter.getCreateDateBegin()));

        if(passwordFilter.getCreateDateEnd() != null)
            predicates.add(cb.lessThan(passwordEntityRoot.get("createDate"), passwordFilter.getCreateDateEnd()));

        if(passwordFilter.getChangeDateBegin() != null)
            predicates.add(cb.greaterThan(passwordEntityRoot.get("changeDate"), passwordFilter.getChangeDateBegin()));

        if(passwordFilter.getChangeDateEnd() != null)
            predicates.add(cb.lessThan(passwordEntityRoot.get("changeDate"), passwordFilter.getChangeDateEnd()));


        query.select(passwordEntityRoot).where(predicates.toArray(new Predicate[0]));

        TypedQuery typedQuery = entityManager.createQuery(query);

        // set limit
        if(passwordFilter.getLimit() != null)
            typedQuery = typedQuery.setMaxResults(passwordFilter.getLimit());

        // set start position
        if(passwordFilter.getStart() != null)
            typedQuery = typedQuery.setFirstResult(passwordFilter.getStart());

        List<PasswordEntity> passwordEntities = typedQuery.getResultList();

        return mapper.mapAsList(passwordEntities, Password.class);
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
            password = mapper.map(passwordEntity.get(), Password.class);

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
