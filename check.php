<?php 
$challenger = $_POST["challenger"];
$challenged = $_POST["challenged"];

# connecting
$dbname = "kfranke1_assignment4";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

# Find the user in the table.
$sql = "select * from games where challenger='" . $challenger . "' and challenged='" . $challenged . "';";
$result = mysqli_query($cid, $sql);

while ($row = mysqli_fetch_array($result, MYSQL_ASSOC)) {
    $board = $row['board'];
    $turn = $row['turn'];
    echo $board . "\n";
    echo $turn . "\n";
}
?>
