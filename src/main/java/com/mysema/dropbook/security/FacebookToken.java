package com.mysema.dropbook.security;

import org.apache.shiro.authc.AuthenticationToken;

public class FacebookToken implements AuthenticationToken {

    private final String code;

    public FacebookToken(String code) {
        this.code = code;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public String getCode() {
        return code;
    }
}
