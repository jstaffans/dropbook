package com.mysema.dropbook.security;

import io.ifar.security.dao.jdbi.DefaultJdbiUserSecurityDAO;
import io.ifar.security.dao.jdbi.DefaultUserImpl;
import io.ifar.security.realm.model.ISecurityRole;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class DefaultDropbookUserDao extends DefaultJdbiUserSecurityDAO
        implements DropbookUserDao, Transactional<DefaultDropbookUserDao> {

    /*
     * JDBI does not seem to work if calling methods that are defined in a super class directly.
     * Annotations do not work automatically in subclasses.
     */
    @Override
    public DropbookUser findUser(String username, boolean withRoles) {
        DefaultUserImpl user = (DefaultUserImpl) (withRoles ? super.findUser(username) : super.findUserWithoutRoles(username));
        return (user == null ? null : new DropbookUser(user.getId(), user.getUsername(), user.getPassword(), user.getRoles()));
    }

    @SqlUpdate("insert into users (username, password) values (:username, :password)")
    protected abstract void createUserOnly(@Bind("username") String username, @Bind("password") String password);

    @Override
    @Transaction
    public Long createUser(DropbookUser user) {
        checkNotNull(user, "createUser(), user parameter cannot be null.");
        checkArgument(user.getId() == null, "createUser(), id must be null.");

        Long userId;
        createUserOnly(user.getUsername(), user.getPassword());
        userId = fetchUserId(user.getUsername());
        user.setId(userId);
        for (ISecurityRole role : user.getRoles()) {
            createUserRole(userId, role.getName());
        }
        return userId;
    }

    @SqlUpdate("insert into users_roles (user_id, role_name) values (:userId, :roleName)")
    protected abstract void createUserRole(@Bind("userId") Long userId, @Bind("roleName") String roleName);

    @SqlQuery("select user_id from users where username = :username")
    protected abstract Long fetchUserId(@Bind("username") String username);
}
