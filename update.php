<?php 
$challenger = $_POST["challenger"];
$challenged = $_POST["challenged"];

$newBoard = $_POST["newBoard"];
$newTurn = $_POST["newTurn"];

echo $challenger . "a\n";
echo $challenged . "b\n";
echo $newBoard . "c\n";
echo $newTurn . "d\n";

# connecting
$dbname = "kfranke1_assignment4";
$dbpassword = "Imuabae6eita";

$dbuser = "kfranke1";
$host = "mysql.cs.binghamton.edu";
$cid = mysqli_connect($host, $dbuser, $dbpassword, $dbname);

# Find the user in the table.
$sql = "update games set board='" . $newBoard . "', turn=" . $newTurn . " where challenger='" . $challenger . "' and challenged='" . $challenged . "';";
$result = mysqli_query($cid, $sql);
echo $result . "\n";
?>
