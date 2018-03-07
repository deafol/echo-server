package nl.vinyamar.echo;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple servlet echoing the request path, headers, parameters and body in the response.
 * - Allows status to be set via status parameter (status=500)
 * - Allows content-type to be set via type parameter (type=text/plain)
 * - Allows content to be set via echo parameter (echo=Hello%20World)
 * <p>
 * Original code stolen from https://github.com/pcorliss/Echo-Server
 * Adapted to
 * - record the requests and echo all recorded requests on GET /showAll
 * - clear all recorded requests on GET /clearAll
 */
public class EchoServlet extends HttpServlet {

    private static final String EXCLUDES = "/favicon.ico";
    private List<List<String>> allRequests = new ArrayList<>();

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/showAll".equals(req.getRequestURI())) {
            echoAllRequests(resp);
        } else if ("/clearAll".equals(req.getRequestURI())) {
            allRequests.clear();
        } else {
            handleRequest(req, resp);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp);
    }

    private void echoAllRequests(HttpServletResponse resp) throws IOException {
        for (List<String> request : allRequests) {
            echoRequestInfo(resp, request.get(0), request.get(1), request.get(2), request.get(3));
        }
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(req.getParameter("status") != null ? Integer.parseInt(req.getParameter("status")) : HttpServletResponse.SC_OK);
        resp.setContentType(req.getParameter("type") != null ? req.getParameter("type") : "text/plain");
        if (req.getParameter("echo") != null) {
            resp.getWriter().print(req.getParameter("echo"));
        } else {
            String path = req.getRequestURI();
            String headers = getHeaderStr(req);
            String parameters = getParameterDataStr(req);
            String body = getBodyStr(req);
            resp.getWriter().println("====Echoing request====");
            echoRequestInfo(resp, path, headers, parameters, body);
            saveRequestInfo(path, headers, parameters, body);
        }
    }

    private void echoRequestInfo(HttpServletResponse response, String path, String headers, String parameters, String body) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.println("Path: " + path);
        writer.print("\nHeaders:\n" + headers);
        writer.print("\nParameters:\n" + parameters);
        if (!body.isEmpty()) {
            writer.print("\nBody:\n" + body);
        }
        writer.println("=======================\n");
    }

    private void saveRequestInfo(String path, String headers, String parameters, String body) {
        //TODO BUG: part of path is being excluded too (fi "/") so make it all path-relative
        if (!EXCLUDES.contains(path)) {
            ArrayList<String> requestInfo = new ArrayList<>();
            requestInfo.add(path);
            requestInfo.add(headers);
            requestInfo.add(parameters);
            requestInfo.add(body);
            allRequests.add(requestInfo);
        }
    }

    private String getHeaderStr(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        });
        return headers.toString();
    }


    private String getParameterDataStr(HttpServletRequest request) {
        StringBuilder parameters = new StringBuilder();
        request.getParameterMap().entrySet().forEach(entry -> {
            Arrays.asList(entry.getValue()).forEach(parameterValue -> {
                parameters.append(entry.getKey()).append(": ").append(parameterValue).append("\n");
            });
        });
        return parameters.toString();
    }

    private String getBodyStr(HttpServletRequest request) throws IOException {
        return IOUtils.toString(request.getReader());
    }
}
