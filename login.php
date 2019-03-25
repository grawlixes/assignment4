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
$sql = "select * from users where username=\"" . $username . 
       "\" and password=\"" . $password . "\";";
$result = mysqli_query($cid, $sql);
$numRows = $result->num_rows;

// If it doesn't then create it.
if ($numRows) {
    echo "Success\n";

    $result = mysqli_query($cid, $sql);

    while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)) {
        echo $row['wins'];
        echo "\n";
        echo $row['losses'];
        echo "\n";
    }
} else {
    echo "Failed\n";
}
?>
