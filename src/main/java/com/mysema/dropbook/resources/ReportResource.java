package com.mysema.dropbook.resources;

import com.mysema.dropbook.persistence.WisdomDao;
import com.mysema.dropbook.views.ReportView;
import org.eclipse.jetty.http.MimeTypes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/report")
@Produces(MimeTypes.TEXT_HTML)
public class ReportResource {

    private final WisdomDao dao;

    public ReportResource(WisdomDao dao) {
        this.dao = dao;
    }

    @GET
    public ReportView getReport() {
        return new ReportView(dao.findAllQuotes());
    }

}
