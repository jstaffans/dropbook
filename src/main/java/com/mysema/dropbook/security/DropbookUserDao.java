package com.mysema.dropbook.security;

import io.ifar.security.dao.UserSecurityDAO;

public interface DropbookUserDao extends UserSecurityDAO {

    Long createUser(DropbookUser user);

    DropbookUser findUser(String username, boolean withRoles);
}
