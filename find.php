<?php 
$theirUsername = $_POST["theirUsername"];
$myUsername = $_POST["myUsername"];

# connecting
$dbname = "kfranke1_assignment4";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

# Find the user in the table.
$sql = "select * from users where username=\"" . $theirUsername . "\"";
$result = mysqli_query($cid, $sql);
$numRows = $result->num_rows;

# If the user exists, check if there's a game.
if ($numRows) {
    echo "Success\n";

    $sql = "select * from games where ";
    $sql1 = "challenger=\"" . $theirUsername . "\" and challenged=\"" . $myUsername . "\"";
    $sql2 = "challenger=\"" . $myUsername . "\" and challenged=\"" . $theirUsername . "\"";

    $result1 = mysqli_query($cid, $sql . $sql1);
    $result2 = mysqli_query($cid, $sql . $sql2);

    $numRows1 = $result1->num_rows;
    $numRows2 = $result2->num_rows;

    if ($numRows1) {
        # Return the game they started
        $turn = 0;
        while ($row = mysqli_fetch_array($result1, MYSQL_ASSOC)) {
            $board = $row['board'];
            $turn = $row['turn'];
        }
        echo $board . "\n";
        echo $theirUsername . "\n";
        echo $myUsername . "\n";
        echo $turn . "\n";
    } else if ($numRows2) {
        # Return the game you started
        while ($row = mysqli_fetch_array($result2, MYSQL_ASSOC)) {
            $board = $row['board'];
            $turn = $row['turn'];
        }
        echo $board . "\n";
        echo $myUsername . "\n";
        echo $theirUsername . "\n";
        echo $turn . "\n";
    } else {
        # Create a new game
        $sql = "insert into games (challenger, challenged, board, turn) values (\"" . $myUsername . "\", \"" . $theirUsername . "\", ";
   
        $emptyBoard = "";

        for ($i = 0; $i < 64; $i++) {
            if ($i % 2 == 1) {
                if ($i < 24) {
                    if ($i >= 8 and $i < 16) {
                        $emptyBoard .= "na";
                    } else {
                        $emptyBoard .= "bn";
                    }
                } else if ($i > 40) {
                    if ($i >= 48 and $i < 56) {
                        $emptyBoard .= "rn";
                    } else {
                        $emptyBoard .= "na";
                    }
                } else {
                    $emptyBoard .= "na";
                }
            } else {
                if ($i < 24) {
                    if ($i >= 8 and $i < 16) {
                        $emptyBoard .= "bn";
                    } else {
                        $emptyBoard .= "na";
                    }
                } else if ($i >= 40) {
                    if ($i >= 48 and $i < 56) {
                        $emptyBoard .= "na";
                    } else {
                        $emptyBoard .= "rn";
                    }
                } else {
                    $emptyBoard .= "na";
                }
            }
        }

        $sql .= "\"";
        $sql .= $emptyBoard;
        $sql .= "\", 1);";

        mysqli_query($cid, $sql);
        echo $emptyBoard . "\n";
        echo $myUsername . "\n";
        echo $theirUsername . "\n";
        echo "1\n";
    }
}
?>
