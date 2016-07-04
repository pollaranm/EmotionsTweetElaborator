package DB;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;

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

        // Connessione alla singola istanza di MongoDB (mongod)
        //MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
        // Connessione all'istanza Query Router del cluster (mongos)
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27016));

        if (action.equals("dropMongo")) {
            dropMongoProcedure(mongoClient);
        } else if (action.equals("createMongo")) {
            createMongoProcedure(mongoClient);
        } else if (action.equals("emoji")) {
            getEmojiResult(request, response, mongoClient);
        } else if (action.equals("hashtag")) {
            getHashtagResult(request, response, mongoClient);
        }

        mongoClient.close();

    }

    /**
     * Procedura per l'eliminazione di ogni tabella dal database.
     */
    private void dropMongoProcedure(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("LabDB");
        if (database != null) {
            mongoClient.dropDatabase("LabDB");
        }
    }

    /**
     * Crea una collezione per ogni cartella/sentimento presente nella directory
     * puntata dalla variabile 'dir' in testa alla classe e ne abilita lo
     * sharding.
     */
    private void createMongoProcedure(MongoClient mongoClient) {
        for (File sentiment : sentimentsFoldersList) {
            mongoClient.getDatabase("LabDB").createCollection(sentiment.getName());
        }
        CommandResult res = mongoClient.getDB("admin").command(new BasicDBObject("enableSharding", "LabDB"));
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

    private void getEmojiResult(HttpServletRequest request, HttpServletResponse response, MongoClient mongoClient) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String sentiment = request.getParameter("sentiment");
            MongoDatabase database = mongoClient.getDatabase("LabDB");
            String res = "";
            MongoCollection collection = database.getCollection("emoji");
            FindIterable<Document> sorted = collection.find().sort(new BasicDBObject(sentiment, -1)).limit(10);
            for (Document t : sorted) {
                res += t.getString("html") + "," + t.getInteger(sentiment) + "###";
            }
            out.print(res);

        } catch (IOException ex) {
            Logger.getLogger(MongoManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

    private void getHashtagResult(HttpServletRequest request, HttpServletResponse response, MongoClient mongoClient) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String sentiment = request.getParameter("sentiment");
            MongoDatabase database = mongoClient.getDatabase("LabDB");
            String res = "";
            MongoCollection collection = database.getCollection("hashtag");
            FindIterable<Document> sorted = collection.find().sort(new BasicDBObject(sentiment, -1)).limit(10);
            for (Document t : sorted) {
                res += t.getString("hashtag") + "," + t.getInteger(sentiment) + "###";
            }
            out.print(res);

        } catch (IOException ex) {
            Logger.getLogger(MongoManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            out.close();
        }
    }

}
