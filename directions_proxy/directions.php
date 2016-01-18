<?php

$url = 'https://maps.googleapis.com/maps/api/directions/json?';
$origin = $_GET['origin'];
$destination = $_GET['destination'];
$mode = $_GET['mode'];
$units = 'imperial';

// valid Google Maps Browser Key needs to be added in variable $apikey
$apikey = 'ENTER YOUR GOOGLE API KEY HERE';

$params = array( 'key' => $apikey, 'origin' => $origin, 'destination' => $destination, 'mode' => $mode, 'units' => $units );

$encoded_params = array();

//build query string parameters
foreach ($params as $k => $v){ $encoded_params[] = urlencode($k).'='.urlencode($v); }

//add query string to url
$url = "https://maps.googleapis.com/maps/api/directions/json?".implode('&', $encoded_params);
//request data from Google Directions
$json = file_get_contents($url, 0, null, null);

//echo json response from Google Directions

//If using JSONP wrap response in callback function
if (isset($_GET['callback'])) {
    echo $_GET['callback'] . '(' . $json . ')';
}
else {
 echo $json;
}

?>
