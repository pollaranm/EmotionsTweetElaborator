<%-- 
    Document   : DBManager
    Created on : 3-mag-2016, 13.43.48
    Author     : TTm
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DB Manager</title>
        
        <script src="js_lib/jquery-2.2.3.min.js"></script>
        <script src="js_lib/bootstrap.min.js"></script>
        <script src="js_lib/alertify.min.js"></script>
        <script src="js/DBManager.js"></script>

        <link rel="icon" type="image/ico" href="img/favicon.ico"/>
        <link rel="stylesheet" href="css/bootstrap.css" />
        <link rel="stylesheet" href="css/bootstrap-theme.css"/>
        <link rel="stylesheet" href="css/alertify.core.css" />
        <link rel="stylesheet" href="css/alertify.bootstrap.css"/>

    </head>
    <body>
        <h1>Oracle DB Manager</h1>
        <button id="dropOracle">Drop ALL Database</button>
        <button id="createOracle">Create structure</button>
        <button id="loadOracle">Load lexical resources</button>
        <button id="analyzeOracle">Analyze Tweet</button>
        <hr>
        <h1>Mongo DB Manager</h1>
        <button id="dropMongo">Drop ALL Database</button>
        <button id="createMongo">Create structure</button>
        <button id="loadMongo">Load lexical resources</button>
        <button id="analyzeMongo">Analyze Tweet</button>
    </body>
</html>
