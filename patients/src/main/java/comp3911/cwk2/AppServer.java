package comp3911.cwk2;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;

public class AppServer {
    public static void main(String[] args) throws Exception {
        Log.setLog(new StdErrLog());

        Server server = new Server(8080);

        // Use ServletContextHandler instead of ServletHandler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // Add your servlet
        context.addServlet(new ServletHolder(new AppServlet()), "/*");

        server.start();
        server.join();
    }
}
