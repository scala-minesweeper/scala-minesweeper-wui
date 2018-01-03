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
        "FieldUpdate": renderGrid,
        "GridUpdate": renderGrid,
        "GameStart": renderStatus,
        "GameWon": renderStatus,
        "GameLost": renderStatus,
        "GameUpdate": renderStatus,
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
                .append($("<td>").text(" " + (value.win ? "win" : "defeat")))
                .append($("<td>").text(value.score))
                .appendTo(playerTable);
        });
    }

    function renderStatus(event) {
        var status = $(".game-status");
        var value = event.value;
        status.removeClass("win lost running");
        status.empty();
        if (value.running) {
            status.addClass("running");
            status.text("Game is running!");
            return;
        }
        var result = value.gameResult;
        if (!result) {
            return;
        }
        if (result.win) {
            status.addClass("win")
                .append($("<b>").text("You win the game!"))
                .append($("<span>").text("Score: " + result.score + ""));
        } else {
            status.addClass("lost")
                .append($("<b>").text("You lost the game!"))
                .append($("<span>").text("Score: " + result.score + ""));
        }
    }

    function renderGrid(event) {
        $("#game-grid").html(event.value);

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
