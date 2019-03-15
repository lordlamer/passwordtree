package org.passwordtree.page.impl.database;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.page.Page;
import org.passwordtree.page.PageDao;
import org.passwordtree.page.PageFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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
public class PageImpl implements PageDao {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrikaBeanMapper mapper;

    @Autowired
    private PageRepository pageRepository;

    /**
     * find pages
     *
     * @param pageFilter
     * @return
     */
    @Override
    public List<Page> listPages(PageFilter pageFilter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> query = cb.createQuery(PageEntity.class);
        Root<PageEntity> pageEntityRoot = query.from(PageEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if(pageFilter.getId() != null)
            predicates.add(cb.equal(pageEntityRoot.get("id"), pageFilter.getId()));

        if(pageFilter.getParent() != null)
            predicates.add(cb.equal(pageEntityRoot.get("parent"), pageFilter.getParent()));

        if(pageFilter.getName() != null)
            predicates.add(cb.like(pageEntityRoot.get("name"), "%" + pageFilter.getName() + "%"));

        if(pageFilter.getSubtitle() != null)
            predicates.add(cb.like(pageEntityRoot.get("subtitle"), "%" + pageFilter.getSubtitle() + "%"));

        if(pageFilter.getDescription() != null)
            predicates.add(cb.like(pageEntityRoot.get("description"), "%" + pageFilter.getDescription() + "%"));

        if(pageFilter.getTooltip() != null)
            predicates.add(cb.like(pageEntityRoot.get("tooltip"), "%" + pageFilter.getTooltip() + "%"));

        if(pageFilter.getIcon() != null)
            predicates.add(cb.like(pageEntityRoot.get("icon"), "%" + pageFilter.getIcon() + "%"));

        if(pageFilter.getAlias() != null)
            predicates.add(cb.like(pageEntityRoot.get("alias"), "%" + pageFilter.getAlias() + "%"));

        if(pageFilter.getContentCollapse() != null)
            predicates.add(cb.equal(pageEntityRoot.get("contentCollapse"), pageFilter.getContentCollapse()));

        if(pageFilter.getContentPosition() != null)
            predicates.add(cb.like(pageEntityRoot.get("contnetPosition"), "%" + pageFilter.getContentPosition() + "%"));

        if(pageFilter.getShowContentDescription() != null)
            predicates.add(cb.equal(pageEntityRoot.get("showContentDescription"), pageFilter.getShowContentDescription()));

        if(pageFilter.getShowTableOfContent() != null)
            predicates.add(cb.equal(pageEntityRoot.get("showTableOfContent"), pageFilter.getShowTableOfContent()));

        if(pageFilter.getSorting() != null)
            predicates.add(cb.equal(pageEntityRoot.get("sorting"), pageFilter.getSorting()));

        if(pageFilter.getDeleted() != null)
            predicates.add(cb.equal(pageEntityRoot.get("deleted"), pageFilter.getDeleted()));

        if(pageFilter.getActive() != null)
            predicates.add(cb.equal(pageEntityRoot.get("active"), pageFilter.getActive()));

        if(pageFilter.getCreatedBy() != null)
            predicates.add(cb.equal(pageEntityRoot.get("createdBy"), pageFilter.getCreatedBy()));

        if(pageFilter.getChangedBy() != null)
            predicates.add(cb.equal(pageEntityRoot.get("changedBy"), pageFilter.getChangedBy()));

        if(pageFilter.getTimeStartBegin() != null)
            predicates.add(cb.greaterThan(pageEntityRoot.get("timeStart"), pageFilter.getTimeStartBegin()));

        if(pageFilter.getTimeStartEnd() != null)
            predicates.add(cb.lessThan(pageEntityRoot.get("timeStart"), pageFilter.getTimeStartEnd()));

        if(pageFilter.getTimeEndBegin() != null)
            predicates.add(cb.greaterThan(pageEntityRoot.get("timeEnd"), pageFilter.getTimeEndBegin()));

        if(pageFilter.getTimeEndEnd() != null)
            predicates.add(cb.lessThan(pageEntityRoot.get("timeEnd"), pageFilter.getTimeEndEnd()));

        if(pageFilter.getCreateDateBegin() != null)
            predicates.add(cb.greaterThan(pageEntityRoot.get("createDate"), pageFilter.getCreateDateBegin()));

        if(pageFilter.getCreateDateEnd() != null)
            predicates.add(cb.lessThan(pageEntityRoot.get("createDate"), pageFilter.getCreateDateEnd()));

        if(pageFilter.getChangeDateBegin() != null)
            predicates.add(cb.greaterThan(pageEntityRoot.get("changeDate"), pageFilter.getChangeDateBegin()));

        if(pageFilter.getChangeDateEnd() != null)
            predicates.add(cb.lessThan(pageEntityRoot.get("changeDate"), pageFilter.getChangeDateEnd()));

        query.select(pageEntityRoot).where(predicates.toArray(new Predicate[0]));

        TypedQuery typedQuery = entityManager.createQuery(query);

        // set limit
        if(pageFilter.getLimit() != null)
            typedQuery = typedQuery.setMaxResults(pageFilter.getLimit());

        // set start position
        if(pageFilter.getStart() != null)
            typedQuery = typedQuery.setFirstResult(pageFilter.getStart());

        List<PageEntity> pageEntities = typedQuery.getResultList();

        return mapper.mapAsList(pageEntities, Page.class);
    }

    /**
     * find page by given id
     *
     * @param id
     * @return
     */
    @Override
    public Page findById(long id) {
        Optional<PageEntity> pageEntity = pageRepository.findById(id);

        Page page = null;

        if(pageEntity.isPresent())
            page = mapper.map(pageEntity.get(), Page.class);

        return page;
    }

    /**
     * check if page exists
     *
     * @param page
     * @return
     */
    @Override
    public boolean isPageExist(Page page) {
        boolean found = false;

        Optional<PageEntity> pageEntity = pageRepository.findById(Long.valueOf(page.getId()));

        if(pageEntity.isPresent())
            found = true;

        return found;
    }

    /**
     * create page from domain object
     *
     * @param page
     */
    @Override
    public void createPage(Page page) {
        PageEntity pageEntity = mapper.map(page, PageEntity.class);

        pageRepository.save(pageEntity);
    }

    /**
     * update existing page
     *
     * @param page
     */
    @Override
    public void updatePage(Page page) {
        // create page entity
        PageEntity pageEntity = mapper.map(page, PageEntity.class);

        // save to database
        pageRepository.save(pageEntity);
    }

    /**
     * delete all pages
     */
    @Override
    public void deleteAllPages() {
        pageRepository.deleteAll();
    }

    /**
     * delete page by given id
     *
     * @param id
     */
    @Override
    public void deletePageById(long id) {
        pageRepository.deleteById(id);
    }
}
