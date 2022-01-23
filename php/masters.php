<?php
date_default_timezone_set('Asia/Manila'); 
//include_once 'dbconnect.php';
$response = array();
$terminalID = isset($_GET['terminalID']) ? $_GET['terminalID'] : '';
$rawText = isset($_GET['rawText']) ? $_GET['rawText'] : '';
$date = date('m/d/Y');		
$time = date('H:iA');
$rdadd = "R";
//SERVER

	
	if ($rawText != ""){
		//if (!(strcmp($aut1,$authen1)) and  !(strcmp($aut2,$authen2))){
			$filedateused = date('md.0y');
			$dateused = date('m/d/Y H:iA');		
			$rText = str_replace('$', "\n", $rawText);	
			$fp = fopen('MSTR'.$terminalID.$filedateused, 'a');//opens file in append mode  
			fwrite($fp, $Text." *" . $dateused);
			fclose($fp);  
			  echo $rText;
			echo "File appended successfully";  
		
	}

?>