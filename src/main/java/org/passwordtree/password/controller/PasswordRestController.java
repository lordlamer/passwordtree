package org.passwordtree.password.controller;

import org.passwordtree.config.OrikaBeanMapper;
import org.passwordtree.page.controller.PageDto;
import org.passwordtree.password.Password;
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
     * get all pages
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<List<PageDto>> listAllPages(
    ) {
        return null;
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
    @PutMapping(value = "/page/{id}")
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
