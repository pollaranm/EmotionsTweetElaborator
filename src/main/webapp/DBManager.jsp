<%-- 
    Document   : DBManager
    Created on : 3-mag-2016, 13.43.48
    Author     : TTm
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html style="width: 100%">
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
        <div style="width: 49% ; float: left ; padding-left: 3%">
            <h1>Oracle DB Manager</h1>
            <button id="dropOracle" class="btn btn-primary">Drop ALL Database</button>
            <button id="createOracle" class="btn btn-primary">Create structure</button>
            <button id="loadOracle" class="btn btn-primary">Load lexical resources</button>
            <button id="analyzeOracle" class="btn btn-primary">Analyze Tweet</button>
        </div>
        <div style="padding-right: 3% ; float: right">
            <h1>Mongo DB Manager</h1>
            <button id="dropMongo" class="btn btn-success">Drop ALL Database</button>
            <button id="createMongo" class="btn btn-success">Create structure</button>
            <button id="loadMongo" class="btn btn-success">Load lexical resources</button>
            <button id="analyzeMongo" class="btn btn-success">Analyze Tweet</button>
        </div>
        <div style="text-align: center; position: fixed; bottom: 10%; width: 100%;">
            <a href="CloudResults.jsp"><button class="btn btn-warning btn-lg">Show results</button></a>
        </div>

    </body>
</html>
