<?php

	require_once 'db.class.php';

	$today = new DateTime();

	$today = date_format($today, 'Y-m-d');
	$result = null;

	// Define API response codes and their related HTTP response
	$api_response_code = array(
		0 => array('HTTP Response' => 400, 'Message' => 'Unknown Error'),
		1 => array('HTTP Response' => 200, 'Message' => 'Success'),
		2 => array('HTTP Response' => 403, 'Message' => 'HTTPS Required'),
		3 => array('HTTP Response' => 401, 'Message' => 'Authentication Required'),
		4 => array('HTTP Response' => 401, 'Message' => 'Authentication Failed'),
		5 => array('HTTP Response' => 404, 'Message' => 'Not found'),
		6 => array('HTTP Response' => 405, 'Message' => 'Method Not Allowed'),
		7 => array('HTTP Response' => 500, 'Message' => 'Internal Server Error'),
		8 => array('HTTP Response' => 304, 'Message' => 'Not changed')
	);

	//define initial api_response value
	$api_response = $api_response_code[0];

	$method = $_SERVER['REQUEST_METHOD'];


	if ($method !== 'GET') {
		//not GET request so send 405 error
		$api_response = $api_response_code[6];
	}

	else {

		$museum_id = isset($_GET['museum_id']) ? cleanInput($_GET['museum_id']) : 0;
		
		if ($museum_id === 0 ) {
			//no museum requested so get exhibitions for all museums
			$result = getAll($today);
			
		}
		elseif (is_numeric($museum_id) && $museum_id > 0) {
			//getByMuseum($museum_id, $today);
			$result = getByMuseum($museum_id, $today);
		}

		else {
			//wrong information provided
			$api_response = $api_response_code[0];
			$api_response['Message'] = 'Bad data - wrong parameters provided';	
		}
	}

	header("Content-Type:application/json");

	$status = $api_response['HTTP Response'];
	$status_message = $api_response['Message'];

	deliver_response($status, $status_message, $result);

	
	//get all current and future exhibitions and order by ascending date
	function getAll($today) {

		global $api_response;
		global $api_response_code;

		try {
        $database = new Database();
        $database->query("SELECT * FROM exhibitions_info WHERE end_date >= :today AND start_date <= :today ORDER BY start_date ASC");
        $database->bind(':today', $today);
        $rows = array();

        $rows = $database->resultset();

        $database = null;

        if ($rows == null) {
        	//not found any exhibitions return error
        	$api_response = $api_response_code[5];
        }
        else {
        	$api_response = $api_response_code[1];
        	
        }

        return $rows;

    	}

	    catch ( PDOException $e ) {
	        $database = null;
	        
	        $api_response = $api_response_code[0];
	        //die( "Query failed: " . $e->getMessage() );
	        return null;
	    }

	}

	//get exhibitions for specific museum and order by ascending date
	function getByMuseum($museum_id, $today) {

		global $api_response;
		global $api_response_code;

		try {
        $database = new Database();
        $database->query("SELECT * FROM exhibitions_info WHERE end_date >= :today AND museum_id = :museum_id ORDER BY start_date ASC");
        $database->bind(':today', $today);
        $database->bind(':museum_id', $museum_id);
        $rows = array();

        $rows = $database->resultset();

        $database = null;

        if ($rows == null) {
        	//not found any exhibitions return error
        	$api_response = $api_response_code[5];
        }
        else {
        	$api_response = $api_response_code[1];
        	
        }

        return $rows;

    	}

	    catch ( PDOException $e ) {
	        $database = null;
	        
	        $api_response = $api_response_code[0];
	        //die( "Query failed: " . $e->getMessage() );
	        return null;
	    }
	}

	function cleanInput($data) {
		$data = trim($data);
		$data = stripslashes($data);
		$data = htmlspecialchars($data, ENT_QUOTES, 'UTF-8');
		return $data;
	}

	function deliver_response($status, $status_message, $result=null) {

		header("HTTP/1.1 $status $status_message");
		header("Access-Control-Allow-Origin: *");
		if ($status == 405) {
			header ("Allow: GET");
		}

		if ($result==null) {
			$json_data = json_encode(array('status'=>$status, 
		'message'=>$status_message), JSON_PRETTY_PRINT);
		}
		else {
			$json_data = json_encode(array('status'=>$status, 
		'message'=>$status_message, 'exhibitions'=>$result), JSON_PRETTY_PRINT);	
		}
		
		echo $json_data;
	}


?>