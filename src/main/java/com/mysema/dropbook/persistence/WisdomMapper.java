package com.mysema.dropbook.persistence;

import com.mysema.dropbook.core.Wisdom;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WisdomMapper implements ResultSetMapper<Wisdom> {

    public Wisdom map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Wisdom(r.getInt("id"), r.getString("quote"), new DateTime(r.getTimestamp("added_on")));
    }

}
