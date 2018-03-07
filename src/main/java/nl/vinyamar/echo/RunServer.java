package nl.vinyamar.echo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RunServer {

    public static void main(String[] args) throws Exception {
        launchEchoServletServer(args);
    }

    private static void launchEchoServletServer(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new EchoServlet()), "/*");
        Server server = new Server(Integer.valueOf(args[0]));
        server.setHandler(context);
        server.setStopAtShutdown(true);
        server.start();
    }

}
