package com.mysema.dropbook.persistence;

import com.mysema.dropbook.core.Wisdom;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.Iterator;

public interface WisdomDao {

    @SqlUpdate("insert into wisdom (id, quote, added_on) values (:id, :quote, :sqlDatetime)")
    void insert(@BindBean Wisdom w);

    @SqlQuery("select id, quote, added_on from wisdom order by random() limit 1")
    @Mapper(WisdomMapper.class)
    Wisdom findRandom();

    @SqlQuery("select quote from wisdom")
    Iterator<String> findAllQuotes();
}
