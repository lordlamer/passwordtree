package org.passwordtree.page.controller;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.page.Page;
import org.passwordtree.page.PageFilter;
import org.passwordtree.page.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
public class PageRestController {
    private static final Logger logger = LoggerFactory.getLogger(PageRestController.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Autowired
    private OrikaBeanMapper mapper;

    @Autowired
    private PageService pageService;

    /**
     * get all pages
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<List<PageDto>> listAllPages(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "parent", required = false) Integer parent,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "subtitle", required = false) String subtitle,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "tooltip", required = false) String tooltip,
            @RequestParam(name = "icon", required = false) String icon,
            @RequestParam(name = "alias", required = false) String alias,
            @RequestParam(name = "content_collapse", required = false) Boolean contentCollapse,
            @RequestParam(name = "content_position", required = false) String contentPosition,
            @RequestParam(name = "show_content_description", required = false) Boolean showContentDescription,
            @RequestParam(name = "show_table_of_content", required = false) Boolean showTableOfContent,
            @RequestParam(name = "sorting", required = false) Integer sorting,

            @RequestParam(name = "time_start.begin", required = false) String timeStartBegin,
            @RequestParam(name = "time_start.end", required = false) String timeStartEnd,
            @RequestParam(name = "time_end.begin", required = false) String timeEndBegin,
            @RequestParam(name = "time_end.end", required = false) String timeEndEnd,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "created_by", required = false) Integer createdBy,
            @RequestParam(name = "create_date.begin", required = false) String  createDateBegin,
            @RequestParam(name = "create_date.end", required = false) String  createDateEnd,
            @RequestParam(name = "changed_by", required = false) Integer changedBy,
            @RequestParam(name = "change_date.begin", required = false) String changeDateBegin,
            @RequestParam(name = "change_date.end", required = false) String changeDateEnd,
            @RequestParam(name = "deleted", required = false) Boolean deleted,
            @RequestParam(name = "start", required = false) Integer start,
            @RequestParam(name = "limit", required = false) Integer limit
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        PageFilter pageFilter = new PageFilter();

        // set filter values
        pageFilter.setId(id);
        pageFilter.setParent(parent);
        pageFilter.setName(name);
        pageFilter.setSubtitle(subtitle);
        pageFilter.setDescription(description);
        pageFilter.setTooltip(tooltip);
        pageFilter.setIcon(icon);
        pageFilter.setAlias(alias);
        pageFilter.setContentCollapse(contentCollapse);
        pageFilter.setContentPosition(contentPosition);
        pageFilter.setShowContentDescription(showContentDescription);
        pageFilter.setShowTableOfContent(showTableOfContent);
        pageFilter.setSorting(sorting);

        pageFilter.setActive(active);
        pageFilter.setCreatedBy(createdBy);
        pageFilter.setChangedBy(changedBy);
        pageFilter.setDeleted(deleted);
        pageFilter.setLimit(limit);
        pageFilter.setStart(start);

        try {
            if (timeStartBegin != null)
                pageFilter.setTimeStartBegin(LocalDateTime.parse(timeStartBegin, formatter));

            if (timeStartEnd != null)
                pageFilter.setTimeStartEnd(LocalDateTime.parse(timeStartEnd, formatter));

            if (timeEndBegin != null)
                pageFilter.setTimeEndBegin(LocalDateTime.parse(timeEndBegin, formatter));

            if (timeEndEnd != null)
                pageFilter.setTimeEndEnd(LocalDateTime.parse(timeEndEnd, formatter));

            if (createDateBegin != null)
                pageFilter.setCreateDateBegin(LocalDateTime.parse(createDateBegin, formatter));

            if (createDateEnd != null)
                pageFilter.setCreateDateEnd(LocalDateTime.parse(createDateEnd, formatter));

            if (changeDateBegin != null)
                pageFilter.setChangeDateBegin(LocalDateTime.parse(changeDateBegin, formatter));

            if (changeDateEnd != null)
                pageFilter.setChangeDateEnd(LocalDateTime.parse(changeDateEnd, formatter));
        } catch(DateTimeParseException e) {
            logger.error("Could not convert date: {}", e.getMessage());
        }

        // get filtered user list
        List<Page> pages = pageService.listPages(pageFilter);

        // map to dto
        List<PageDto> pageDtos = mapper.mapAsList(pages, PageDto.class);

        // check for entries
        if(pageDtos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(pageDtos, HttpStatus.OK);
    }

    /**
     * get single page by id
     * @param id
     * @return
     */
    @GetMapping(value = "/page/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageDto> getPage(@PathVariable("id") long id) {
        Page page = pageService.findById(id);

        PageDto pageDto = mapper.map(page, PageDto.class);

        if (pageDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(pageDto, HttpStatus.OK);
    }

    /**
     * create page
     * @param pageDto
     * @param ucBuilder
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<Void> createPage(@RequestBody PageDto pageDto, UriComponentsBuilder ucBuilder) {

        Page page = mapper.map(pageDto, Page.class);

        if (pageService.isPageExist(page)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        pageService.createPage(page);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/page/{id}").buildAndExpand(page.getId()).toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * update existing page
     * @param id
     * @param pageDto
     * @return
     */
    @PutMapping(value = "/page/{id}")
    public ResponseEntity<PageDto> updatePage(@PathVariable("id") long id, @RequestBody PageDto pageDto) {
        if(id != pageDto.getId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Page currentPage = pageService.findById(id);

        if (currentPage==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentPage = mapper.map(pageDto, Page.class);

        pageService.updatePage(currentPage);

        return new ResponseEntity<>(pageDto, HttpStatus.OK);
    }

    /**
     * delete page
     * @param id
     * @return
     */
    @DeleteMapping("/page/{id}")
    public ResponseEntity<PageDto> deletePage(@PathVariable("id") long id) {
        Page page = pageService.findById(id);

        if (page == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        pageService.deletePageById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * delete all pages
     * @return
     */
    @DeleteMapping("/page")
    public ResponseEntity<PageDto> deleteAllPages() {
        pageService.deleteAllPages();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
