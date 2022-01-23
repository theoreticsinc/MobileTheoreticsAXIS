<?php
include'ctrlpanel/config/db.php';
include'ctrlpanel/config/functions.php';
include'ctrlpanel/config/main_function.php';
include'ctrlpanel/config/DBController.php';

date_default_timezone_set('Asia/Manila'); 
//include_once 'dbconnect.php';
$JSONresponse = array();
$response = array();
$rfid = isset($_GET['rfid']) ? $_GET['rfid'] : '';
$date = date('m/d/Y');		
$time = date('H:iA');
$rdadd = "V";
$from = isset($_GET['from']) ? $_GET['from'] : '';
//$from = "2021-04-21 00:00:00";
//SERVER
$servercon = mysqli_connect("localhost", "base", "theoreticsinc", "carpark") or die("Error " . mysqli_error($con));
	
	if ($from <> ''){
		//if (!(strcmp($aut1,$authen1)) and  !(strcmp($aut2,$authen2))){
			$dateused = date('m/d/Y H:iA');
			//echo $dateused;
			$trtype = 'R';

			$query = "SELECT * FROM carpark.vipdb WHERE dateCreated > '$from' ORDER BY dateCreated DESC";
            $result = $dbcon->query($query);
			while($row = $result->fetch_assoc()) {
			   
			    //echo $row['CardID'] . " : ";
			    //echo $row['ParkType'] . " : ";
			    //echo $row['Plate'] . " : ";
			    //echo $row['Name'] . "<br>";
			    $response["CardID"] = $row['CardID'];
				$response["ParkerType"] = $row['ParkType'];
				$response["PlateNumber"] = $row['Plate'];
				$response["Name"] = $row['Name'];
				$response["CardCode"] = $row['CardCode'];
				$response["MaxUse"] = $row['MaxUse'];
				$response["Status"] = $row['Status'];
				$response["DateCreated"] = $row['dateCreated'];
				$response["DateModified"] = $row['dateModified'];
				echo json_encode($response) . "\n";
			}
		
	}
	

?>