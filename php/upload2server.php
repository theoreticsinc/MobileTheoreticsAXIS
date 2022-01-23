<?php
date_default_timezone_set('Asia/Manila'); 
//include_once 'dbconnect.php';
$response = array();
$sql = isset($_GET['sql']) ? $_GET['sql'] : '';
$date = date('m/d/Y');		
$time = date('H:iA');
$rdadd = "R";
//SERVER
$servercon = mysqli_connect("localhost", "base", "theoreticsinc", "unidb") or die("Error " . mysqli_error($con));

	
	if ($sql != ""){
		//if (!(strcmp($aut1,$authen1)) and  !(strcmp($aut2,$authen2))){
			$dateused = date('m/d/Y H:iA');
			//echo $dateused;
			$trtype = 'R';
			if($result = mysqli_query($servercon, $sql )) {

				//if ($row = mysqli_fetch_array($result)) {
				echo json_encode("ok");
				
			}
		
			
		
	}

?>