package com.mysema.dropbook.persistence;

import com.mysema.dropbook.core.Wisdom;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.skife.jdbi.v2.StatementContext;

import java.sql.ResultSet;
import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WisdomMapperTest {

    @Test
    public void timestampIsConvertedCorrectly() throws Exception {
        ResultSet rs = Mockito.mock(ResultSet.class);
        int timestamp = 946706400;
        Mockito.when(rs.getTimestamp(Matchers.anyString())).thenReturn(new Timestamp(timestamp));
        WisdomMapper mapper = new WisdomMapper();
        Wisdom wisdom = mapper.map(1, rs, Mockito.mock(StatementContext.class));

        assertThat(wisdom.getAddedOn(), is(new DateTime(timestamp)));
    }
}
