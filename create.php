<?php 
$username = $_POST["username"];
$password = $_POST["password"];

# connecting
$dbname = "kfranke1_assignment4";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

# First make sure the user doesn't exist in the table.
$sql = "select * from users where username=\"" . $username . "\"";
$result = mysqli_query($cid, $sql);
$numRows = $result->num_rows;

// If it doesn't then create it.
if ($numRows) {
    echo "Failed";
} else {
    echo "Success";

    $sql = "insert into users (username, password, wins, losses)
                values (\"" . $username . "\", \"" .
                $password . "\", 0, 0)";
    $result = mysqli_query($cid, $sql);
    if (!$result) {
        echo "Failed to insert into users";
    }
}
?>
