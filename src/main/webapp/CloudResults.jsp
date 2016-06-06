<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Results Page</title>

        <script src="js_lib/jquery-2.2.3.min.js"></script>
        <script src="js_lib/bootstrap.min.js"></script>
        <script src="js_lib/alertify.min.js"></script>
        <script src="js_lib/jqcloud-1.0.0.min.js"></script>
        <script src="js/CloudResults.js"></script>

        <link rel="icon" type="image/ico" href="img/favicon.ico"/>
        <link rel="stylesheet" href="css/bootstrap.css" />
        <link rel="stylesheet" href="css/bootstrap-theme.css"/>
        <link rel="stylesheet" href="css/alertify.core.css" />
        <link rel="stylesheet" href="css/alertify.bootstrap.css"/>
        <link rel="stylesheet" href="css/jqcloud.css" />

        <style type="text/css">
            .wordcloud {
                
                width: 230px;
                height: 460px;
                border: none;
            }
            .wordcloud span.w10, #wordcloud span.w9, #wordcloud span.w8, #wordcloud span.w7 {
                text-shadow: 0px 1px 1px #ccc;
            }
            .wordcloud span.w3, #wordcloud span.w2, #wordcloud span.w1 {
                text-shadow: 0px 1px 1px #fff;
            }
            table {
                margin: 0 auto 0 auto;/* or margin:  0 auto;*/
            }
            th {
                background-color: #4CAF50;
                color: white;
                border: 1px solid black;
            }
            th, td {
                padding: 15px;
            }
        </style>

    </head>
    <body style="text-align: center">
        <div style="width: 70% ; margin: 0 auto 0 auto">
            <h1>Choose one ...</h1>
            <button id="anger" class="btn btn-primary">ANGER</button>
            <button id="anticipation" class="btn btn-primary">ANTICIPATION</button>
            <button id="disgust" class="btn btn-primary">DISGUST</button>
            <button id="fear" class="btn btn-primary">FEAR</button>
            <button id="joy" class="btn btn-primary">JOY</button>
            <button id="sadness" class="btn btn-primary">SADNESS</button>
            <button id="surprise" class="btn btn-primary">SURPRISE</button>
            <button id="trust" class="btn btn-primary">TRUST</button>
            <h3 id="selection"></h3>
        </div>
        <div id="old_words" style="width: 49% ; float: left">
            <div id="old_words_cloud" class="wordcloud" style="width: 60% ; float: left"></div>
            <div id="old_words_table" style="width: 30% ; float: right"></div>
            
        </div>
        <div id="new_words" style="width: 49% ; float: right">
            <div id="new_words_table" style="width: 30% ; float: left"></div>
            <div id="new_words_cloud" class="wordcloud" style="width: 60% ; float: right"></div>
            
        </div>
    </body>

</html>
