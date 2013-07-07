package com.mysema.dropbook.resources;

import com.mysema.dropbook.core.Wisdom;
import com.mysema.dropbook.persistence.WisdomDao;
import org.apache.shiro.SecurityUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/wisdom")
@Produces(APPLICATION_JSON)
public class WisdomResource {

    private final WisdomDao dao;

    public WisdomResource(WisdomDao dao) {
        this.dao = dao;
    }

    @GET
    @Path("/random")
    public Wisdom fetchRandom() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new NotAuthorizedException("Not authorized");
        }

        return dao.findRandom();
    }

}
