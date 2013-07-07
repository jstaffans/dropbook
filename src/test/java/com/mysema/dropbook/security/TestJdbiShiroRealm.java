package com.mysema.dropbook.security;

import com.mysema.dropbook.DatabaseHelper;
import io.ifar.security.dao.jdbi.DefaultRoleImpl;
import io.ifar.security.realm.JdbiShiroRealm;
import io.ifar.security.realm.model.ISecurityRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.junit.*;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

@Slf4j
public class TestJdbiShiroRealm {

    private static DatabaseHelper harness = new DatabaseHelper();

    private static ThreadState subjectThreadState;
    private static PasswordService passwordService;
    private static CredentialsMatcher passwordMatcher;
    private static JdbiShiroRealm realm;

    @BeforeClass
    public static void setUp() {
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory();
        DefaultSecurityManager dsm = (DefaultSecurityManager) factory.getInstance();
        passwordService = (DefaultPasswordService) factory.getBeans().get("passwordService");
        passwordMatcher = (CredentialsMatcher) factory.getBeans().get("passwordMatcher");
        SecurityUtils.setSecurityManager(dsm);

        harness.setUp();
    }

    @AfterClass
    public static void tearDown() {
        clearSubject();
        try {
            SecurityManager securityManager = SecurityUtils.getSecurityManager();
            LifecycleUtils.destroy(securityManager);
        } catch (UnavailableSecurityManagerException e) {
            //we don't care about this when cleaning up the test environment
        }
        SecurityUtils.setSecurityManager(null);

        harness.tearDown();
    }

    private static void clearSubject() {
        if (subjectThreadState != null) {
            subjectThreadState.clear();
            subjectThreadState = null;
        }
    }

    @Before
    public void setupTest() {
        setSubject(new Subject.Builder(SecurityUtils.getSecurityManager()).buildSubject());
        setupRealm();
    }

    private void setupRealm() {
        realm = new JdbiShiroRealm(harness.getDbi());
        realm.setCredentialsMatcher(passwordMatcher);  // NOT NEEDED.  Default works fine for parsing pws.
        log.info("Using principal values: {}", realm.getPrincipalValueFields());
        ((DefaultSecurityManager) SecurityUtils.getSecurityManager()).setRealm(realm);
    }

    private void setSubject(Subject subject) {
        clearSubject();
        subjectThreadState = new SubjectThreadState(subject);
        subjectThreadState.bind();
    }

    @After
    public void teardownTest() {
        clearSubject();
    }

    protected String getUsername() {
        return "testuser";
    }

    protected String getPlainTextPassword() {
        return "wert123";
    }

    protected Set<ISecurityRole> getRoles() {
        // Permissions aren't involved in user creation, just the role name
        return Collections.<ISecurityRole>singleton(new DefaultRoleImpl("user"));
    }

    private DropbookUser fetchOrCreateUser() {
        DropbookUser u = harness.getUserDao().findUser(getUsername(), false);
        if (u == null) {
            String hashedPassword = passwordService.encryptPassword(getPlainTextPassword());
            u = new DropbookUser(null, getUsername(), hashedPassword, getRoles());

            harness.getUserDao().createUser(u);
            assertNotNull(u.getId());
        }
        return u;
    }

    private void checkStoredPrincipal(DropbookUser user, Object p) {
        assertEquals("CurrentUser is expected to store the user's id as the principal.", user.getId(), p);
    }

    @Test
    public void login() {
        DropbookUser user = fetchOrCreateUser();
        assertNotNull(user);
        assertNotNull(user.getId());

        log.info("User under test: " + user);
        setSubject(new Subject.Builder(SecurityUtils.getSecurityManager()).buildSubject());
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            // This is what would be provided on login.
            UsernamePasswordToken upToken = new UsernamePasswordToken(getUsername(), getPlainTextPassword());
            currentUser.login(upToken);
            assertTrue(currentUser.isAuthenticated());
        }
        checkStoredPrincipal(user, currentUser.getPrincipal());
        assertTrue(currentUser.hasRole("user"));
        currentUser.logout();
    }

    @Test(expected = AuthenticationException.class)
    public void wrongPassword() {
        DropbookUser user = fetchOrCreateUser();
        // This is what would be provided on login with the wrong password.
        UsernamePasswordToken upToken = new UsernamePasswordToken(user.getUsername(), "WrongPasssord");
        SecurityUtils.getSecurityManager().authenticate(upToken);
    }
}
