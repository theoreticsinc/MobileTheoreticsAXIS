<?php
session_start();
include 'terminaloverride.php';
include 'terminalclose.php';
date_default_timezone_set('Asia/Manila'); 

$response = array();
$date = date('m/d/Y');		
$time = date('H:iA');

			$dateused = date('m/d/Y H:iA');

			$response["sec"] = date('s');
			$response["min"] =date('i');
			$response["hr"] =date('H');
			$response["date"] =date('d');
			$response["mon"] =date('m');
			$response["year"] =date('y');
			
			$response["or"] = $over;
			$response["bc"] = $barrierclose;
			
			$response["day"] =date('N')+1;

			echo json_encode($response);

	//{"sec":"54","min":"08","hr":"09","date":"14","mon":"03","year":"21","or":"EN01bb","bc":"EN0bb","day":8}
	

?>