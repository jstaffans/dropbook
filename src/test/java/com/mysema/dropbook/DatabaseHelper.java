package com.mysema.dropbook;

import com.mysema.dropbook.security.DefaultDropbookUserDao;
import com.mysema.dropbook.security.DropbookUserDao;
import io.ifar.security.dao.UserSecurityDAO;
import io.ifar.security.dao.jdbi.DefaultJdbiUserSecurityDAO;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.io.ResourceUtils;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class DatabaseHelper {

    protected static final String DB_PROPERTIES_FILE_PATH = "classpath:test.db.properties";

    protected static final String DB_DRIVER_CLASSNAME_KEY = "jdbc.driver.className";
    protected static final String DB_CONNECTION_URL_KEY = "jdbc.connection.url";
    protected static final String DB_CONNECTION_USERNAME_KEY = "jdbc.connection.username";
    protected static final String DB_CONNECTION_PASSWORD_KEY = "jdbc.connection.password";

    private static final String CHANGE_LOG = "migrations.xml";
    private static final String TEST_DATA_CHANGE_LOG = "test-dataset.sql";

    protected Properties dbProperties = null;
    protected DBI dbi;
    protected DropbookUserDao userDao;
    protected DefaultJdbiUserSecurityDAO userSecurityDao;

    private void loadProperties(String resourcePath) {
        InputStream propStream = null;
        try {
            propStream = ResourceUtils.getInputStreamForPath(resourcePath);
        } catch (IOException iox) {
            log.warn("No properties file found at {}, using default values.", resourcePath);
        }

        if (propStream != null) {
            dbProperties = new Properties();
            try {
                dbProperties.load(propStream);
            } catch (IOException iox) {
                log.error("Error loading properties from: " + resourcePath);
                throw new RuntimeException(iox);
            }
        }
    }

    public String getJdbcDriverClassname()
    {
        return (dbProperties != null && dbProperties.containsKey(DB_DRIVER_CLASSNAME_KEY)) ?
                dbProperties.getProperty(DB_DRIVER_CLASSNAME_KEY) : null;
    }

    public String getJdbcConnectionString()
    {
        return (dbProperties != null && dbProperties.containsKey(DB_CONNECTION_URL_KEY)) ?
                dbProperties.getProperty(DB_CONNECTION_URL_KEY) : null;
    }

    public String getDbUsername()
    {
        return (dbProperties != null && dbProperties.containsKey(DB_CONNECTION_USERNAME_KEY)) ?
                dbProperties.getProperty(DB_CONNECTION_USERNAME_KEY) : null;
    }

    public String getDbPassword()
    {
        return (dbProperties != null && dbProperties.containsKey(DB_CONNECTION_PASSWORD_KEY)) ?
                dbProperties.getProperty(DB_CONNECTION_PASSWORD_KEY) : null;
    }

    private void performDatabaseSetupOrClean(DBI jdbi, boolean setup) {
        try {
            Handle h = jdbi.open();
            Liquibase liquibase = new Liquibase(CHANGE_LOG, new ClassLoaderResourceAccessor(), new JdbcConnection(h.getConnection()));
            liquibase.dropAll();
            if (setup) {
                liquibase.update("test");
                liquibase = new Liquibase(TEST_DATA_CHANGE_LOG, new ClassLoaderResourceAccessor(), new JdbcConnection(h.getConnection()));
                liquibase.update("test");
            }

            h.close();
        } catch (Exception ex) {
            String msg = setup ? "Error during database initialization" : "Error during database clean-up";
            log.error(msg, ex);
            throw new RuntimeException(msg, ex);
        }
    }

    public void setUp() {
        loadProperties(DB_PROPERTIES_FILE_PATH);

        dbi = new DBI(getJdbcConnectionString(), getDbUsername(), getDbPassword());
        performDatabaseSetupOrClean(dbi, true);

        userDao = dbi.onDemand(DefaultDropbookUserDao.class);
        userSecurityDao = dbi.onDemand(DefaultJdbiUserSecurityDAO.class);
    }

    /**
     * Play nice with other test classes
     */
    public void tearDown()
    {
        if (dbProperties == null) {
            loadProperties(DB_PROPERTIES_FILE_PATH);
        }

        performDatabaseSetupOrClean(dbi, false);
        dbi.close(userSecurityDao);
        dbi.close(userDao);
    }

    public DBI getDbi() {
        return dbi;
    }

    public DropbookUserDao getUserDao() {
        return userDao;
    }

    public UserSecurityDAO getUserSecurityDao() {
        return userSecurityDao;
    }
}
