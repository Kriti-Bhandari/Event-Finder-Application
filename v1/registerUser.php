<?php 

ini_set('display_errors', 1);
error_reporting(E_ALL);

require_once '../includes/DbOperations.php';

$response = array(); 

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (
        isset($_POST['username']) &&
        isset($_POST['email']) &&
        isset($_POST['password']) &&
        isset($_POST['location']) 
    ) {
        $db = new DbOperations(); 

        $result = $db->createUser(
          $_POST['username'],
            $_POST['location'],
             $_POST['email'],           
            $_POST['password']
           
        );

        if ($result == 1) {
            $response['error'] = false; 
            $response['message'] = "User registered successfully";
        } elseif ($result == 2) {
            $response['error'] = true; 
            $response['message'] = "Some error occurred, please try again";			
        } elseif ($result == 0) {
            $response['error'] = true; 
            $response['message'] = "Already registered, try a different email or username";						
        }

    } else {
        $response['error'] = true; 
        $response['message'] = "Required fields are missing";
    }
} else {
    $response['error'] = true; 
    $response['message'] = "Invalid Request";
}

echo json_encode($response);
?>
