$(document).on("click", "#dropOracle", function () {
    alertify.confirm("Are you sure you want to DELETE ALL THE TABLES?", function (e) {
        if (e) {
            // user clicked "ok"
            $.ajax({
            type: "POST",
            url: "OracleManager",
            data: {action: "dropOracle"},
            success: function (data) {
                alertify.success("Oralab DB is clear");
            },
            error: function (xhr, status, error) {
                alert(error);
            }});
        } else {
            // user clicked "cancel"
            alertify.log("Operation aborted");
        }
    });
});

$(document).on("click", "#createOracle", function () {
    alertify.alert("Creating the tables...");
    $.ajax({
        type: "POST",
        url: "OracleManager",
        data: {action: "createOracle"},
        success: function (data) {
            alertify.success("Oralab DB have now sentiments's tables");
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
});

$(document).on("click", "#loadOracle", function () {
    alertify.alert("Loading rex_res process start!");
    $.ajax({
        type: "POST",
        url: "Loader",
        data: {DBtype: "Oracle"},
        success: function (data) {
            alertify.success("Operation complete");
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
});

$(document).on("click", "#analyzeOracle", function () {
    alertify.confirm("Are you sure you want to START THE ANALYSIS of Tweets?", function (e) {
        if (e) {
            // user clicked "ok"
            $.ajax({
            type: "POST",
            url: "Analyser",
            data: {DBtype: "Oracle"},
            success: function (data) {
                alertify.success("Analysis complete!");
            },
            error: function (xhr, status, error) {
                alert(error);
            }});
        } else {
            // user clicked "cancel"
            alertify.log("Operation aborted");
        }
    });
});

$(document).on("click", "#dropMongo", function () {
    alertify.confirm("Are you sure you want to DELETE ALL THE TABLES?", function (e) {
        if (e) {
            // user clicked "ok"
            $.ajax({
            type: "POST",
            url: "MongoManager",
            data: {action: "dropMongo"},
            success: function (data) {
                alertify.success("Mongo database erased");
            },
            error: function (xhr, status, error) {
                alert(error);
            }});
        } else {
            // user clicked "cancel"
            alertify.log("Operation aborted");
        }
    });
});

$(document).on("click", "#createMongo", function () {
    alertify.alert("Creating the collections...");
    $.ajax({
        type: "POST",
        url: "MongoManager",
        data: {action: "createMongo"},
        success: function (data) {
            alertify.success("Mongo DB have now sentiments's collection");
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
});

$(document).on("click", "#loadMongo", function () {
    alertify.alert("Loading rex_res process start!");
    $.ajax({
        type: "POST",
        url: "Loader",
        data: {DBtype: "Mongo"},
        success: function (data) {
            alertify.success("Operation complete");
        },
        error: function (xhr, status, error) {
            alert(error);
        }
    });
});

$(document).on("click", "#analyzeMongo", function () {
    alertify.confirm("Are you sure you want to START THE ANALYSIS of Tweets?", function (e) {
        if (e) {
            // user clicked "ok"
            $.ajax({
            type: "POST",
            url: "Analyser",
            data: {DBtype: "Mongo"},
            success: function (data) {
                alertify.success("Analysis complete!");
            },
            error: function (xhr, status, error) {
                alert(error);
            }});
        } else {
            // user clicked "cancel"
            alertify.log("Operation aborted");
        }
    });
});

