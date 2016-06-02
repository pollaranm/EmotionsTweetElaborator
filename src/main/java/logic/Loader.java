package logic;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import static javax.servlet.SessionTrackingMode.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.bson.conversions.Bson;

public class Loader extends HttpServlet {

    /**
     * Directory contenente le cartelle con le risorse lessicali suddivise per
     * sentimento
     */
    File dir = new File("C:/Dropbox/lex_res_temp");

    /**
     * Lista delle cartelle 'sentimento' contenenti le risorse lessicali
     */
    File[] sentimentsFoldersList = dir.listFiles();

    /**
     * Parametro ricevuto dalla chiamata che specifica il DB da utilizzare
     */
    String DBtype;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DBtype = request.getParameter("DBtype");

        if (DBtype == null) {
            ServletContext ctx = getServletContext();
            RequestDispatcher rdErr = ctx.getRequestDispatcher("/DBManager.jsp");
            rdErr.forward(request, response);
        } else {
            // Per ogni cartella fa partire l'elaborazione di un 'sentimento'
            for (File sentimentFolder : sentimentsFoldersList) {
                System.out.println("------ FOLDER " + sentimentFolder.getName() + "------");
                elaborateSentiment(sentimentFolder);
            }
        }
    }

    /**
     * Metodo per l'elaborazione della singola cartella, quindi del singolo
     * sentimento. Analizza i singoli file contenuti nella directory,
     * caricandoli le parole associate al sentimento nella relativa tabella nel
     * database.
     *
     * @param sentiment Cartella contenente i vari file (risorse) associati ad
     * un'emozione
     */
    public void elaborateSentiment(File sentiment) {
        File[] sentimentResList = sentiment.listFiles();
        int numRes = sentimentResList.length;

        /**
         * HashMap temporanea per ogni sentimento che controllerà la presenza di
         * una parola in più risorse e ne terrà il conteggio
         */
        HashMap<String, Integer> hashSentiment = new HashMap<String, Integer>();

        /**
         * HashMap di supporto per il conteggio nei signoli file
         */
        HashMap<String, Integer> hashFile;

        BufferedReader br = null;
        // mi trovo dentro la cartella di un sentimento, ciclo per ogni risorsa
        for (File sentRes : sentimentResList) {
            hashFile = new HashMap<String, Integer>();
            System.out.println("###### Open res: " + sentRes.getName() + " ######");
            try {
                String sCurrentLine;
                br = new BufferedReader(new FileReader(sentRes.getAbsolutePath()));

                // finchè ci sono parole nella risorsa aperta da scorrere
                while ((sCurrentLine = br.readLine()) != null) {

                    // ogni parola viene pre-elaborata 
                    String elaboratedWord = preProcessingWord(sCurrentLine);

                    if (!elaboratedWord.equals("")) {
                        hashFile.putIfAbsent(elaboratedWord, 1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("###### Close res: " + sentRes.getName() + " ######");

            // Salvo le parole trovate nel singolo file all'interno dell'hash 
            // globale per quel sentimento
            for (Map.Entry word : hashFile.entrySet()) {
                if (hashSentiment.containsKey((String) word.getKey())) {
                    hashSentiment.replace((String) word.getKey(), hashSentiment.get((String) word.getKey()) + 1);
                } else {
                    hashSentiment.put((String) word.getKey(), 1);
                }
            }
        }
        if (DBtype.equals("Oracle")) {
            storeInDB(sentiment.getName(), numRes, hashSentiment);
        } else {
            storeInMongo(sentiment.getName(), numRes, hashSentiment);
        }
    }

    /**
     * Procedura di salvataggio delle risorse lessicali. Alla fine
     * dell'elaborazione dei diversi file contenuti in una cartella, prende i
     * risultati ottenuti e li memorizza in DB.
     *
     * @param sentimentName Sentimento analizzato
     * @param numRes Numero di file contenuti nella cartella 'sentimento'
     * @param hash Hash contenente le risorse lessicali ed il loro conteggio
     */
    private void storeInDB(String sentimentName, int numRes, HashMap<String, Integer> hash) {
        try {
            String myDriver = "oracle.jdbc.driver.OracleDriver";
            String myUrl = "jdbc:oracle:thin:@localhost:1521:oralab";
            String myUser = "USERTEST";
            String myPass = "app";
//            String myUrl = "jdbc:oracle:thin:@laboracle.educ.di.unito.it:1521:oralab";
//            String myUser = "sp138279";
//            String myPass = "testtest";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, myUser, myPass);
            conn.setAutoCommit(false);

            String query_insert_stm
                    = "INSERT INTO " + sentimentName + " (WORD, COUNT_RES, PERC_RES, COUNT_TWEET) VALUES (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(query_insert_stm);

            for (Map.Entry word : hash.entrySet()) {
                Float perc_res = new Float((int) word.getValue()) / numRes * 100;
                pstmt.setString(1, (String) word.getKey());
                pstmt.setInt(2, (int) word.getValue());
                pstmt.setFloat(3, perc_res);
                pstmt.setInt(4, 0);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Preprocessa la parola in input andando ad applicargli le seguenti
     * operazioni:
     * <ol>
     * <li>Controllo presenza carattere speciale '_' al fine di terminare
     * l'elaborazione</li>
     * <li>Conversione di tutti i caratteri in lower case</li>
     * <li>Lemmizzazione della parola</li>
     * </ol>
     *
     * @param rawS Parola in input da processare
     * @return La parola elaborata. Nel in caso di mancata corrispondenza nella
     * libreria o di parole composte restituisce una stringa vuota ("").
     */
    public String preProcessingWord(String rawS) {
        String finalS = "";
        // se non è un multigramma continuo l'elaborazione
        if (!rawS.contains("_")) {
            finalS = rawS.toLowerCase();
            Properties props = new Properties();
            props.put("annotators", "tokenize, ssplit, pos, lemma");
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);
            Annotation document = pipeline.process(finalS);
            for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
                for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                    String lemma = token.get(LemmaAnnotation.class);
                    finalS = lemma;
                }
            }
        }
        return finalS;
    }

    /**
     * Metodo ad hoc per testare il numero di bigrammi riscontrati da una veloce
     * analisi dei tweet.
     *
     * @param hash Hash contenente i bigrammi da conteggiare
     */
    private void RAW_counter(HashMap<String, Integer> hash) {
        File dir = new File("C:/Dropbox/tweet_temp");
        File[] sentimentsFoldersList = dir.listFiles();
        BufferedReader br = null;

        // azzeriamo conteggio nelle parole per poter utilizzare la stessa hashmap
        for (Map.Entry<String, Integer> word : hash.entrySet()) {
            hash.put(word.getKey(), 0);
        }
        int i = 0;
        int totalBiG = 0;
        int totalOcc = 0;
        for (File sentRes : sentimentsFoldersList) {
            System.out.println("###### Open res: " + sentRes.getName() + " ######");
            i = 0;
            try {
                String sCurrentLine;
                //br = new BufferedReader(new FileReader(sentRes.getAbsolutePath()));
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(sentRes.getAbsolutePath()), "UTF8"));
                // ciclo finchè ho tweet(righe) su cui fare controlli
                while ((sCurrentLine = br.readLine()) != null) {
                    System.out.println("DEBUG: tweet n°" + (++i));

                    sCurrentLine = sCurrentLine.toLowerCase();
                    // scorro tutta hash map controllando che siano presenti le parole nei tweet
                    // in pratica il contrario di quello che dovremmo fare poi
                    for (Map.Entry<String, Integer> word : hash.entrySet()) {
                        if (sCurrentLine.contains(word.getKey())) {
                            hash.put(word.getKey(), hash.get(word.getKey()) + 1);
                            totalOcc++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("###### Close res: " + sentRes.getName() + " ######");
        }
        i = 0;

        for (Map.Entry<String, Integer> word : hash.entrySet()) {
            if (word.getKey().contains(" ") && word.getValue() >= 10) {
                System.out.println(word.getKey() + " - " + word.getValue());
                totalBiG += word.getValue();
                i++;
            }

        }
        System.out.println("#diff bigrams:" + i);
        System.out.println("#tot bigrams:" + totalBiG);
        System.out.println("#tot occurrences: " + totalOcc);
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

    private void storeInMongo(String sentiment, int numRes, HashMap<String, Integer> hashSentiment) {
//        List<ServerAddress> servers = new ArrayList<>();
//        servers.add(new ServerAddress("localhost", 27017));
//        servers.add(new ServerAddress("localhost", 27018));
//        servers.add(new ServerAddress("localhost", 27019));
//        servers.add(new ServerAddress("localhost", 27020));
//        MongoClient mongoClient = new MongoClient(servers);
        MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
        MongoDatabase database = mongoClient.getDatabase("LabDB");
        MongoCollection<Document> collection = database.getCollection(sentiment);
        List<WriteModel<Document>> listOp = new LinkedList<>();

        for (Map.Entry word : hashSentiment.entrySet()) {
            Float perc_res = new Float((int) word.getValue()) / numRes * 100;
            Document tempDoc = new Document("word", (String) word.getKey())
                    .append("count_res", (int) word.getValue())
                    .append("perc_res", perc_res)
                    .append("count_tweet", 0);
            listOp.add(new InsertOneModel<>(tempDoc));
        }

        BulkWriteResult bulkWrite = collection.bulkWrite(listOp);
        //System.out.println(bulkWrite.toString());
        BasicDBObject indexObj = new BasicDBObject("word", 1);
        IndexOptions indexPropObj = new IndexOptions().unique(true);
        collection.createIndex(indexObj, indexPropObj);
        mongoClient.close();

    }

}
