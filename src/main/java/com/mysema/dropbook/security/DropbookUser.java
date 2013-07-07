package com.mysema.dropbook.security;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.ifar.security.dao.jdbi.DefaultRoleImpl;
import io.ifar.security.dao.jdbi.DefaultUserImpl;
import io.ifar.security.realm.model.ISecurityRole;

import java.util.Collections;
import java.util.Set;

@JsonDeserialize(using=DropbookUserDeserializer.class)
public class DropbookUser extends DefaultUserImpl {

    public static Set<ISecurityRole> DEFAULT_ROLES = Collections.<ISecurityRole>singleton(new DefaultRoleImpl("user"));

    public DropbookUser(Long id, String username, String password, Set<ISecurityRole> roles) {
        super(id, username, password, roles);
    }
}
