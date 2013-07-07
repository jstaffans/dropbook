package com.mysema.dropbook;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.google.common.base.Optional;
import com.mysema.dropbook.core.Wisdom;
import com.mysema.dropbook.persistence.WisdomDao;
import com.mysema.dropbook.resources.ReportResource;
import com.mysema.dropbook.resources.UserResource;
import com.mysema.dropbook.resources.WisdomResource;
import com.mysema.dropbook.security.DefaultDropbookUserDao;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.FilterBuilder;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.views.ViewBundle;
import io.ifar.dropwizard.shiro.ShiroBundle;
import io.ifar.dropwizard.shiro.ShiroConfiguration;
import io.ifar.security.web.JdbiRealmLoaderListener;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DropbookService extends Service<DropbookConfiguration> {

    private final ShiroBundle<DropbookConfiguration> shiroBundle =
            new ShiroBundle<DropbookConfiguration>() {
                @Override
                public Optional<ShiroConfiguration> getShiroConfiguration(DropbookConfiguration configuration) {
                    return Optional.<ShiroConfiguration>fromNullable(configuration.getShiro());
                }
            };

    public static void main(String[] args) throws Exception {
        new DropbookService().run(args);
    }

    @Override
    public void initialize(Bootstrap<DropbookConfiguration> bootstrap) {
        bootstrap.setName("dropbook");
        // enable dropwizard-views
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/", "index.html"));
    }

    @Override
    public void run(DropbookConfiguration configuration, Environment environment) throws Exception {
        // setup DBI
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabase(), "h2");
        migrateDatabase(jdbi);
        insertData(configuration, jdbi);

        // hook up our conventional resources
        final WisdomDao dao = jdbi.onDemand(WisdomDao.class);
        environment.addResource(new WisdomResource(dao));
        environment.addResource(new ReportResource(dao));

        // configure security (Apache Shiro) and add the resource associated with authentication
        shiroBundle.run(configuration, environment);
        environment.addServletListeners(new JdbiRealmLoaderListener(jdbi));
        environment.addResource(new UserResource(jdbi.onDemand(DefaultDropbookUserDao.class), new DefaultPasswordService()));

        // enable cross-origin ajax
        FilterBuilder filterBuilder= environment.addFilter(CrossOriginFilter.class, "/*");
        filterBuilder.setInitParam(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,PUT,DELETE");
        filterBuilder.setInitParam(CrossOriginFilter.PREFLIGHT_MAX_AGE_PARAM, String.valueOf(60*60*24));
    }

    private void migrateDatabase(DBI jdbi) throws LiquibaseException {
        Handle h = jdbi.open();
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(h.getConnection()));
        liquibase.update(null);
        h.close();
    }

    private void insertData(DropbookConfiguration configuration, DBI jdbi) throws LiquibaseException {
        // insert scaffolding data from sql file
        Handle h = jdbi.open();
        Liquibase liquibase = new Liquibase("initial-data.sql", new ClassLoaderResourceAccessor(), new JdbcConnection(h.getConnection()));
        liquibase.update(null);
        h.close();

        h = jdbi.open();
        // insert some data from the .yml configuration file
        AtomicInteger index = new AtomicInteger(1);
        WisdomDao dao = h.attach(WisdomDao.class);
        for (String quote : configuration.getQuotes()) {
            Wisdom w = new Wisdom(index.getAndIncrement(), quote, new DateTime(new Date().getTime()));
            dao.insert(w);
        }
        h.close();
    }
}
