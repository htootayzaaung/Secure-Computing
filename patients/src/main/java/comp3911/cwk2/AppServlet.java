package comp3911.cwk2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;


import java.sql.PreparedStatement;


import java.util.UUID;

@SuppressWarnings("serial")
public class AppServlet extends HttpServlet {

  private static final String CONNECTION_URL = "jdbc:sqlite:db.sqlite3";
  private static final String AUTH_QUERY = "select * from user where username='%s' and password='%s'";
  private static final String SEARCH_QUERY = "select * from patient where surname='%s' collate nocase";

  private final Configuration fm = new Configuration(Configuration.VERSION_2_3_28);
  private Connection database;

  @Override
  public void init() throws ServletException {
    configureTemplateEngine();
    connectToDatabase();
  }

  private void configureTemplateEngine() throws ServletException {
    try {
      fm.setDirectoryForTemplateLoading(new File("./templates"));
      fm.setDefaultEncoding("UTF-8");
      fm.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
      fm.setLogTemplateExceptions(false);
      fm.setWrapUncheckedExceptions(true);
    }
    catch (IOException error) {
      throw new ServletException(error.getMessage());
    }
  }

  private void connectToDatabase() throws ServletException {
    try {
      database = DriverManager.getConnection(CONNECTION_URL);
    }
    catch (SQLException error) {
      throw new ServletException(error.getMessage());
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      HttpSession session = request.getSession();
      if (session.getAttribute("csrfToken") == null) {
          session.setAttribute("csrfToken", UUID.randomUUID().toString());
      }
      
      Map<String, Object> model = new HashMap<>();
      model.put("csrfToken", session.getAttribute("csrfToken"));
  
      try {
          Template template = fm.getTemplate("login.html");
          template.process(model, response.getWriter()); // Pass model here
          response.setContentType("text/html");
          response.setStatus(HttpServletResponse.SC_OK);
      }
      catch (TemplateException error) {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
  }
  

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
   throws ServletException, IOException {
     // Get form parameters
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String surname = request.getParameter("surname");

    HttpSession session = request.getSession();
    String sessionToken = (String) session.getAttribute("csrfToken");
    String requestToken = request.getParameter("csrfToken");

    if (sessionToken == null || !sessionToken.equals(requestToken)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token does not match");
        return;
    }


    try {
      if (authenticated(username, password)) {
        // Get search results and merge with template
        Map<String, Object> model = new HashMap<>();
        model.put("records", searchResults(surname));
        Template template = fm.getTemplate("details.html");
        template.process(model, response.getWriter());
      }
      else {
        Template template = fm.getTemplate("invalid.html");
        template.process(null, response.getWriter());
      }
      response.setContentType("text/html");
      response.setStatus(HttpServletResponse.SC_OK);
    }
    catch (Exception error) {
      
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private boolean authenticated(String username, String password) throws SQLException {
    String query = "select * from user where username=? and password=?";
    try (PreparedStatement pstmt = database.prepareStatement(query)) {
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        ResultSet results = pstmt.executeQuery();
        return results.next();
    }
  }


  private List<Record> searchResults(String surname) throws SQLException {
    List<Record> records = new ArrayList<>();
    String query = "select * from patient where surname=? collate nocase";
    try (PreparedStatement pstmt = database.prepareStatement(query)) {
        pstmt.setString(1, surname);
        ResultSet results = pstmt.executeQuery();
        while (results.next()) {
            Record rec = new Record();
            rec.setSurname(results.getString("surname"));
            rec.setForename(results.getString("forename"));
            rec.setAddress(results.getString("address"));
            rec.setDateOfBirth(results.getString("born")); 
            rec.setDoctorId(results.getString("gp_id")); 
            rec.setDiagnosis(results.getString("treated_for")); 
            records.add(rec);
        }
    }
    return records;
  }

}
//'OR '1'='1
