$(function() {
    $(".minesweeperField").on("click", function () {
        var colrow = $(this).attr('id').split("|");
        var col = colrow[0];
        var row = colrow[1];
        window.location.href = "/game/"+col+"/"+row;
    })

    $(".minesweeperField").on("contextmenu", function () {
        var colrow = $(this).attr('id').split("|");
        var col = colrow[0];
        var row = colrow[1];
        window.location.href = "/toggleField/"+col+"/"+row;
        return false;
    })

    $("#restartGame").on("click", function() {
        var cols = $("#inputCols").val();
        var rows = $("#inputRows").val();
        var bombs = $("#inputBombs").val();
        if(cols > 0 && rows > 0){
            window.location.href = "/newGame/"+rows+"/"+cols+"/"+bombs;
        } else {
            window.location.href = "/newGame/10/10/10";
        }

    })
});
