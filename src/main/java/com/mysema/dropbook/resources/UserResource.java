package com.mysema.dropbook.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.dropbook.security.DropbookUser;
import com.mysema.dropbook.security.DropbookUserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.subject.Subject;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Slf4j
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private final DropbookUserDao dao;
    private final PasswordService passwordService;

    public UserResource(DropbookUserDao dao, PasswordService passwordService) {
        this.dao = dao;
        this.passwordService = passwordService;
    }

    @POST
    @Path("/register")
    public DropbookUser register(@Valid DropbookUser user) {
        if (dao.findUser(user.getUsername(), false) != null) {
            throw new BadRequestException("User already exists");
        }

        UsernamePasswordToken loginToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());

        String hashedPassword = passwordService.encryptPassword(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRoles(DropbookUser.DEFAULT_ROLES);
        Long id = dao.createUser(user);
        user.setId(id);

        doLogin(loginToken);

        return user;
    }

    private Subject doLogin(UsernamePasswordToken authToken) {
        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(authToken);
        }
        catch (UnknownAccountException | IncorrectCredentialsException e) {
            throw new NotAuthorizedException("Login failed");
        }
        catch (LockedAccountException e) {
            throw new NotAuthorizedException("Account locked");
        }
        return currentUser;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public DropbookUser login(String json) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonObj;
        try {
            jsonObj = mapper.readTree(json);
        } catch (IOException e) {
            throw new WebApplicationException();
        }

        if (jsonObj.has("username")) {
            UsernamePasswordToken loginToken = new UsernamePasswordToken(jsonObj.get("username").asText(),
                    jsonObj.get("password").asText());
            doLogin(loginToken);
            final DropbookUser loggedInUser = dao.findUser(loginToken.getUsername(), false);
            return loggedInUser;
        } else if (jsonObj.has("code")) {



            return null;
        } else {
            throw new NotAuthorizedException("Unknown authentication method");
        }
    }

    @GET
    @Path("/logout")
    public Response logout() {
        final Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }

        return Response.ok().build();
    }

}
