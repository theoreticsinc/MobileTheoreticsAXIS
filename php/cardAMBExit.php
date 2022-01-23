<?php
date_default_timezone_set('Asia/Manila'); 
//include_once 'dbconnect.php';
$response = array();
$rfid = isset($_GET['rfid']) ? $_GET['rfid'] : '';
$date = date('m/d/Y');		
$time = date('H:iA');
$rdadd = "R";
//SERVER
$servercon = mysqli_connect("localhost", "base", "theoreticsinc", "unidb") or die("Error " . mysqli_error($con));

	
	if ($rfid != ""){
		//if (!(strcmp($aut1,$authen1)) and  !(strcmp($aut2,$authen2))){
			$dateused = date('m/d/Y H:iA');
			//echo $dateused;
			$trtype = 'R';
			if($result = mysqli_query($servercon, "UPDATE `timeindb` SET `TRType` = 'AB', `Timein` = CURRENT_TIMESTAMP WHERE `timeindb`.`CardCode` = '" . $rfid . "'")) {

				//if ($row = mysqli_fetch_array($result)) {
				echo json_encode("ok");
				
			}
		
			
		
	}

?>