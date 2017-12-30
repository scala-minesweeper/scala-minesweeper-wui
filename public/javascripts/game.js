$(function() {
    var socket = new WebSocket("ws://localhost:9000/socket");
    socket.onopen = function(){
        console.log('Connected to 127.0.0.1:9000!');
    };
    socket.onmessage = function (event) {
        console.log(event.data);
        window.location.href = "/game";
    };

    var gridField = $(".grid-field");

    gridField.on("click", function () {
        var colrow = $(this).attr('id').split("|");
        var col = colrow[0];
        var row = colrow[1];
        $.get("/game/"+col+"/"+row);
    });

    gridField.on("contextmenu", function () {
        var colrow = $(this).attr('id').split("|");
        var col = colrow[0];
        var row = colrow[1];
        $.get("/toggleField/"+col+"/"+row);
        return false;
    });

    $("#restartGame").on("click", function() {
        var cols = $("#inputCols").val();
        var rows = $("#inputRows").val();
        var bombs = $("#inputBombs").val();
        if(cols > 0 && rows > 0){
            $.get("/newGame/"+rows+"/"+cols+"/"+bombs);
        } else {
            $.get("/newGame/10/10/10");
        }

    })
});
