$(function () {
    var socket = new WebSocket("ws://localhost:9000/socket");
    socket.onmessage = function (event) {
        handleEvent(JSON.parse(event.data));
    };

    $("#restartGame").on("click", function () {
        var cols = $("#inputCols").val();
        var rows = $("#inputRows").val();
        var bombs = $("#inputBombs").val();
        if (cols > 0 && rows > 0) {
            $.get("/newGame/" + rows + "/" + cols + "/" + bombs);
        } else {
            $.get("/newGame/10/10/10");
        }
    });

    var eventHandlers = {
        "FieldChanged": renderGrid,
        "GridChanged": renderGrid,
        "GameStart": renderGrid,
        "GameStatus": renderGrid,
        "PlayerUpdate": renderPlayer
    };

    function handleEvent(event) {
        var handler = eventHandlers[event.name];
        if (!handler) {
            console.error("No handler configured for " + event.name);
        } else {
            handler(event)
        }
    }

    function renderPlayer(event) {
        var playerTable = $("#player-info").find("table").find("tbody");
        playerTable.empty();
        $.each(event.value.history, function (index, value) {
            $('<tr>')
                .append($("<td>").text(" " + (value.win ? "win" : "lost")))
                .append($("<td>").text(value.score))
                .appendTo(playerTable);
        });
    }

    function renderGrid(event) {
        $("#game-frame").html(event.value);

        var gridField = $(".grid-field");

        gridField.on("click", function () {
            var colrow = $(this).attr('id').split("|");
            var col = colrow[0];
            var row = colrow[1];
            $.get("/game/" + col + "/" + row);
        });

        gridField.on("contextmenu", function () {
            var colrow = $(this).attr('id').split("|");
            var col = colrow[0];
            var row = colrow[1];
            $.get("/toggleField/" + col + "/" + row);
            return false;
        });
    }
});
