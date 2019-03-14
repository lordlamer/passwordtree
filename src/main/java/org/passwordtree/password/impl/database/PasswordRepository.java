package org.passwordtree.password.impl.database;

import org.springframework.data.repository.CrudRepository;

public interface PasswordRepository extends CrudRepository<PasswordEntity, Long> {
}
