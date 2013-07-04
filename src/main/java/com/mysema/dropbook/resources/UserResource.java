package com.mysema.dropbook.resources;

import com.mysema.dropbook.security.DropbookUser;
import com.mysema.dropbook.security.DropbookUserDao;
import freemarker.core.ReturnInstruction;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.Subject;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/user")
@Produces(MediaType.TEXT_PLAIN)
public class UserResource {

    private final DropbookUserDao dao;
    private final PasswordService passwordService;

    public UserResource(DropbookUserDao dao, PasswordService passwordService) {
        this.dao = dao;
        this.passwordService = passwordService;
    }

    @GET
    @Path("/testRegister")
    public String testRegister() {
        DropbookUser user = new DropbookUser(null, "testuser", "asdfoo", DropbookUser.DEFAULT_ROLES);

        UsernamePasswordToken loginToken = new UsernamePasswordToken("testuser", "asdfoo");

        String hashedPassword = passwordService.encryptPassword("asdfoo");
        user.setPassword(hashedPassword);
        user.setRoles(DropbookUser.DEFAULT_ROLES);
        dao.createUser(user);

        doLogin(loginToken);

        return "Registered.";
    }

    @POST
    @Path("/register")
    public Response register(@Valid DropbookUser user) {
        if (dao.findUser(user.getUsername(), false) != null) {
            throw new BadRequestException("User already exists");
        }

        UsernamePasswordToken loginToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());

        String hashedPassword = passwordService.encryptPassword(user.getPassword());
        user.setPassword(hashedPassword);
        user.setRoles(DropbookUser.DEFAULT_ROLES);
        dao.createUser(user);

        doLogin(loginToken);

        return Response.created(UriBuilder.fromResource(UserResource.class)
                                   .build(user.getId())).build();
    }

    private void doLogin(UsernamePasswordToken authToken) {
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
    }

    @GET
    @Path("/login")
    public Response login(@Valid DropbookUser user) {
        UsernamePasswordToken loginToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        doLogin(loginToken);
        return Response.ok().build();
    }

    @GET
    @Path("/loginCheck")
    public String isLoggedIn() {
        final Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            return String.format("Logged in as %s", subject.getPrincipal());
        } else {
            return "Not logged in";
        }
    }

}
