package DB;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class OracleManager extends HttpServlet {

    File dir = new File("C:/Dropbox/lex_res_temp");
    File[] sentimentsFoldersList = dir.listFiles();

    String myDriver = "oracle.jdbc.driver.OracleDriver";
    String myUrl = "jdbc:oracle:thin:@localhost:1521:oralab";
    String myUser = "USERTEST";
    String myPass = "app";
//    String myUrl = "jdbc:oracle:thin:@laboracle.educ.di.unito.it:1521:oralab";
//    String myUser = "sp138279";
//    String myPass = "testtest";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            ServletContext ctx = getServletContext();
            RequestDispatcher rdErr = ctx.getRequestDispatcher("/DBManager.jsp");
            rdErr.forward(request, response);
        } else if (action.equals("dropOracle")) {
            dropDBProcedure();
        } else if (action.equals("createOracle")) {
            createDBProcedure();
        } else if (action.equals("old_words")) {
            getOldWordsResult(request, response);
        } else if (action.equals("new_words")) {
            getNewWordsResult(request, response);
        }
    }

    /**
     * Procedura per l'eliminazione di ogni tabella dal database.
     */
    private void dropDBProcedure() {
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, myUser, myPass);
            DatabaseMetaData md = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = md.getTables(null, "USERTEST", "%", types);
            while (rs.next()) {
                String queryDrop
                        = "DROP TABLE " + rs.getString(3);
                Statement st = conn.createStatement();
                st.executeQuery(queryDrop);
                System.out.println("TABLE " + rs.getString(3).toLowerCase() + " DELETED");
                st.close();
            }
            rs.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * Crea una tabella per ogni cartella/sentimento presente nella directory
     * puntata dalla variabile 'dir' in testa alla classe.
     */
    private void createDBProcedure() {
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, myUser, myPass);
            for (File sentiment : sentimentsFoldersList) {
                try {
                    String queryCreate
                            = "CREATE TABLE " + sentiment.getName().toUpperCase()
                            + " (WORD VARCHAR(50), COUNT_RES INT, PERC_RES FLOAT, COUNT_TWEET INT, PRIMARY KEY(WORD) )";
                    Statement st_create = conn.createStatement();
                    st_create.executeUpdate(queryCreate);
                    System.out.println("TABLE " + sentiment.getName().toLowerCase() + " CREATED");
                    st_create.close();
                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 955) {
                        System.out.println("TABLE " + sentiment.getName().toLowerCase() + " WAS ALREADY PRESENT...");
                    }
                }
            }
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * Metodo per il recupero delle parole già presenti tra le risorse lessicali
     * che hanno avuto il maggior numero di occorrenze riscontrate nell'analisi
     * dei tweet. In base al sentimento passato come parametro viene selezionata
     * la relativa tabella e restituiti i primi 50 maggior frequenti termini. Il
     * database puntato da questa funzione è Oracle. Il formato della rispota è
     * "<parola>,<conteggio>", separati da "###".
     *
     * @param request Richiesta arrivata alla servlet contenente i vari
     * parametri
     * @param response Risposta che verrà elaborata e rispedita
     */
    private void getOldWordsResult(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String sentiment = request.getParameter("sentiment");
            response.setContentType("text/html;charset=UTF-8");
            String res = "";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, myUser, myPass);
            String querySelect = "SELECT * FROM "
                    + "(SELECT * FROM " + sentiment + " "
                    + "WHERE COUNT_RES > 0 "
                    + "ORDER BY COUNT_TWEET DESC) "
                    + "WHERE ROWNUM <= 50";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(querySelect);
            while (rs.next()) {
                res += rs.getString("word") + "," + rs.getInt("count_tweet") + "###";
            }
            res = res.substring(0, res.length() - 3);
            rs.close();
            st.close();
            conn.close();
            out.print(res);
        } catch (IOException ex) {
            Logger.getLogger(OracleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OracleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OracleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo per il recupero delle nuove parole non presenti nelle risorse
     * lessicali che hanno avuto il maggior numero di occorrenze riscontrate
     * nell'analisi dei tweet. In base al sentimento passato come parametro
     * viene selezionata la relativa tabella e restituiti i primi 50 maggior
     * frequenti termini. Il database puntato da questa funzione è Oracle. Il
     * formato della rispota è "<parola>,<conteggio>", separati da "###".
     *
     * @param request Richiesta arrivata alla servlet contenente i vari
     * parametri
     * @param response Risposta che verrà elaborata e rispedita
     */
    private void getNewWordsResult(HttpServletRequest request, HttpServletResponse response) {
        try {
            PrintWriter out = response.getWriter();
            String sentiment = request.getParameter("sentiment");
            response.setContentType("text/html;charset=UTF-8");
            String res = "";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, myUser, myPass);
            String querySelect = "SELECT * FROM "
                    + "(SELECT * FROM " + sentiment + " "
                    + "WHERE COUNT_RES = 0 "
                    + "ORDER BY COUNT_TWEET DESC) "
                    + "WHERE ROWNUM <= 50";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(querySelect);
            while (rs.next()) {
                res += rs.getString("word") + "," + rs.getInt("count_tweet") + "###";
            }
            res = res.substring(0, res.length() - 3);
            rs.close();
            st.close();
            conn.close();
            out.print(res);
        } catch (IOException ex) {
            Logger.getLogger(OracleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OracleManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OracleManager.class.getName()).log(Level.SEVERE, null, ex);
        }
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

}
