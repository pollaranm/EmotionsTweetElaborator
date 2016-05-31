/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author TTm
 */
public class MongoManager extends HttpServlet {

    File dir = new File("C:/Dropbox/lex_res_temp");
    File[] sentimentsFoldersList = dir.listFiles();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            ServletContext ctx = getServletContext();
            RequestDispatcher rdErr = ctx.getRequestDispatcher("/DBManager.jsp");
            rdErr.forward(request, response);
        }

        // Creo la lista dei server di tipologia 'SHARD' a cui mi connetterò
//        List<ServerAddress> servers = new ArrayList<>();
//        servers.add(new ServerAddress("localhost", 27017));
//        servers.add(new ServerAddress("localhost", 27018));
//        servers.add(new ServerAddress("localhost", 27019));
//        servers.add(new ServerAddress("localhost", 27020));
//        MongoClient mongoClient = new MongoClient(servers);
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));

        if (action.equals("dropMongo")) {
            dropMongoProcedure(mongoClient);
        } else if (action.equals("createMongo")) {
            createMongoProcedure(mongoClient);
        }
        
        mongoClient.close();

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void dropMongoProcedure(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("LabDB");
        if (database != null) {
            mongoClient.dropDatabase("LabDB");
        }
    }

    private void createMongoProcedure(MongoClient mongoClient) {
        for (File sentiment : sentimentsFoldersList) {
            mongoClient.getDatabase("LabDB").createCollection(sentiment.getName());
        }
    }

}
