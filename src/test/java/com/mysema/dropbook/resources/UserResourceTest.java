package com.mysema.dropbook.resources;

import com.mysema.dropbook.security.AbstractShiroTest;
import com.mysema.dropbook.security.DropbookUser;
import com.mysema.dropbook.security.DropbookUserDao;
import io.ifar.security.realm.model.ISecurityRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.subject.Subject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class UserResourceTest extends AbstractShiroTest {

    private final DropbookUserDao dao = mock(DropbookUserDao.class);
    private final PasswordService passwordService = mock(PasswordService.class);
    private final UserResource resource = new UserResource(dao, passwordService);

    @Before
    public void setUp() {
        Subject subject = mock(Subject.class);
        setSubject(subject);
    }

    @After
    public void tearDown() {
        clearSubject();
    }

    @Test
    public void register() {
        when(dao.findUser(anyString(), anyBoolean())).thenReturn(null);
        when(dao.createUser(any(DropbookUser.class))).thenReturn(1L);
        when(passwordService.encryptPassword(anyString())).thenReturn("encrypted password");

        DropbookUser user = new DropbookUser(null, "testuser", "testpass", Collections.<ISecurityRole>emptySet());
        final DropbookUser response = resource.register(user);
        assertThat(response.getId(), is(1L));
    }
}
