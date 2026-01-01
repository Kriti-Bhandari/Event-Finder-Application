<?php 

require_once '../includes/DbOperations.php';

$response = array(); 

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['username']) && isset($_POST['password'])) {
        $db = new DbOperations();

        if ($db->userLogin($_POST['username'], $_POST['password'])) {
            $user = $db->getUserByUsername($_POST['username']);
            $response['error'] = false;
            $response['id'] = $user['id'];
            $response['username'] = $user['username'];
            $response['location'] = $user['location'];        
            $response['email'] = $user['email'];
        } else {
            $response['error'] = true;
            $response['message'] = "Invalid username or password";
        }
    } else {
        $response['error'] = true;
        $response['message'] = "Required fields are missing";
    }
} else {
    $response['error'] = true;
    $response['message'] = "Invalid Request Method";
}

echo json_encode($response);
