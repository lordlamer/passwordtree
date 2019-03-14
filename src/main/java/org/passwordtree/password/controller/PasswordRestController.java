package org.passwordtree.password.controller;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.password.Password;
import org.passwordtree.password.PasswordFilter;
import org.passwordtree.password.PasswordService;
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
public class PasswordRestController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordRestController.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Autowired
    private OrikaBeanMapper mapper;

    @Autowired
    private PasswordService passwordService;

    /**
     * get all passwords
     * @return
     */
    @GetMapping("/password")
    public ResponseEntity<List<PasswordDto>> listAllPasswords(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "page_id", required = false) Integer pageId,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "url", required = false) String url,
            @RequestParam(name = "icon", required = false) String icon,

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

        PasswordFilter passwordFilter = new PasswordFilter();

        // set filter values
        passwordFilter.setId(id);
        passwordFilter.setPageId(pageId);
        passwordFilter.setTitle(title);
        passwordFilter.setDescription(description);
        passwordFilter.setUsername(username);
        passwordFilter.setUrl(url);
        passwordFilter.setIcon(icon);

        passwordFilter.setActive(active);
        passwordFilter.setCreatedBy(createdBy);
        passwordFilter.setChangedBy(changedBy);
        passwordFilter.setDeleted(deleted);
        passwordFilter.setLimit(limit);
        passwordFilter.setStart(start);

        try {
            if (timeStartBegin != null)
                passwordFilter.setTimeStartBegin(LocalDateTime.parse(timeStartBegin, formatter));

            if (timeStartEnd != null)
                passwordFilter.setTimeStartEnd(LocalDateTime.parse(timeStartEnd, formatter));

            if (timeEndBegin != null)
                passwordFilter.setTimeEndBegin(LocalDateTime.parse(timeEndBegin, formatter));

            if (timeEndEnd != null)
                passwordFilter.setTimeEndEnd(LocalDateTime.parse(timeEndEnd, formatter));

            if (createDateBegin != null)
                passwordFilter.setCreateDateBegin(LocalDateTime.parse(createDateBegin, formatter));

            if (createDateEnd != null)
                passwordFilter.setCreateDateEnd(LocalDateTime.parse(createDateEnd, formatter));

            if (changeDateBegin != null)
                passwordFilter.setChangeDateBegin(LocalDateTime.parse(changeDateBegin, formatter));

            if (changeDateEnd != null)
                passwordFilter.setChangeDateEnd(LocalDateTime.parse(changeDateEnd, formatter));
        } catch(DateTimeParseException e) {
            logger.error("Could not convert date: {}", e.getMessage());
        }

        // get filtered user list
        List<Password> passwords = passwordService.listPasswords(passwordFilter);

        // map to dto
        List<PasswordDto> passwordDtos = mapper.mapAsList(passwords, PasswordDto.class);

        // check for entries
        if(passwordDtos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(passwordDtos, HttpStatus.OK);
    }

    /**
     * get single password by id
     * @param id
     * @return
     */
    @GetMapping(value = "/password/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordDto> getPassword(@PathVariable("id") long id) {
        Password password = passwordService.findById(id);

        PasswordDto passwordDto = mapper.map(password, PasswordDto.class);

        if (passwordDto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(passwordDto, HttpStatus.OK);
    }

    /**
     * create password
     * @param passwordDto
     * @param ucBuilder
     * @return
     */
    @PostMapping("/password")
    public ResponseEntity<Void> createPassword(@RequestBody PasswordDto passwordDto, UriComponentsBuilder ucBuilder) {

        Password password = mapper.map(passwordDto, Password.class);

        if (passwordService.isPasswordExist(password)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        passwordService.createPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/password/{id}").buildAndExpand(password.getId()).toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * update existing password
     * @param id
     * @param passwordDto
     * @return
     */
    @PutMapping("/password/{id}")
    public ResponseEntity<PasswordDto> updatePassword(@PathVariable("id") long id, @RequestBody PasswordDto passwordDto) {
        if(id != passwordDto.getId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Password currentPassword = passwordService.findById(id);

        if (currentPassword == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentPassword = mapper.map(passwordDto, Password.class);

        passwordService.updatePassword(currentPassword);

        return new ResponseEntity<>(passwordDto, HttpStatus.OK);
    }

    /**
     * delete password
     * @param id
     * @return
     */
    @DeleteMapping("/password/{id}")
    public ResponseEntity<PasswordDto> deletePassword(@PathVariable("id") long id) {
        Password password = passwordService.findById(id);

        if (password == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        passwordService.deletePasswordById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * delete all passwords
     * @return
     */
    @DeleteMapping("/password")
    public ResponseEntity<PasswordDto> deleteAllPasswords() {
        passwordService.deleteAllPasswords();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
