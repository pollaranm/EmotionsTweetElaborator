$(document).on("click", "button", function () {
    var sentiment = $(this).attr('id');
    $("#selection").html(sentiment.toUpperCase()+" results:");
    $.ajax({
        type: "POST",
        url: "OracleManager",
        data: {action: "old_words", sentiment: sentiment},
        success: function (data)
        {
            var array = new Array();
            var table = "<table class='resTab table table-striped'><tr><th>Old Words</th><th>Count</th></tr>";
            var splitted = data.split('###');
            for (var i = 0; i < splitted.length; i++) {
                var temp = splitted[i];
                var inner = temp.split(',');
                array.push({text: inner[0], weight: inner[1]});
                table += "<tr><td>" + inner[0] + "</td><td>" + inner[1] + "</td></tr>";
            }
            table += "</table>";

            //invoca la cloud usando data
            $("#old_words_cloud").html("");
            $("#old_words_cloud").jQCloud(array);
            //crea la tabella da mettere sotto
            $("#old_words_table").html(table);
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
    
    $.ajax({
        type: "POST",
        url: "OracleManager",
        data: {action: "new_words", sentiment: sentiment},
        success: function (data)
        {
            var array = new Array();
            var table = "<table class='table table-striped resTab'><tr><th>New Words</th><th>Count</th></tr>";
            var splitted = data.split('###');
            for (var i = 0; i < splitted.length; i++) {
                var temp = splitted[i];
                var inner = temp.split(',');
                array.push({text: inner[0], weight: inner[1]});
                table += "<tr><td>" + inner[0] + "</td><td>" + inner[1] + "</td></tr>";
            }
            table += "</table>";

            //invoca la cloud usando data
            $("#new_words_cloud").html("");
            $("#new_words_cloud").jQCloud(array);
            //crea la tabella da mettere sotto
            $("#new_words_table").html(table);
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
    
    $.ajax({
        type: "POST",
        url: "MongoManager",
        data: {action: "emoji", sentiment: sentiment},
        success: function (data)
        {
            var array = new Array();
            var splitted = data.split('###');
            for (var i = 0; i < splitted.length; i++) {
                var temp = splitted[i];
                var inner = temp.split(',');
                array.push({text: inner[0], weight: inner[1]+200});
            }

            //invoca la cloud usando data
            $("#emoji_cloud").html("");
            $("#emoji_cloud").jQCloud(array);
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
    
    $.ajax({
        type: "POST",
        url: "MongoManager",
        data: {action: "hashtag", sentiment: sentiment},
        success: function (data)
        {
            var array = new Array();
            var splitted = data.split('###');
            for (var i = 0; i < splitted.length; i++) {
                var temp = splitted[i];
                var inner = temp.split(',');
                array.push({text: inner[0], weight: inner[1]});
            }

            //invoca la cloud usando data
            $("#hashtag_cloud").html("");
            $("#hashtag_cloud").jQCloud(array);
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });

});


