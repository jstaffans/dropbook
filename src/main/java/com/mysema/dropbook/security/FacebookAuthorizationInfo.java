package com.mysema.dropbook.security;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;

import java.util.Collection;

public class FacebookAuthorizationInfo implements AuthorizationInfo {
    @Override
    public Collection<String> getRoles() {
        return null;
    }

    @Override
    public Collection<String> getStringPermissions() {
        return null;
    }

    @Override
    public Collection<Permission> getObjectPermissions() {
        return null;
    }
}
