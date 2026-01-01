<?php
header('Content-Type: application/json'); // <-- Ensure correct content type

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['user_id']) && isset($_POST['id'])) {
        $user_id = $_POST['user_id'];
        $event_id = $_POST['id'];

        $host = "localhost";
        $username = "root";
        $password = "";
        $dbname = "event_finder";

        $conn = new mysqli($host, $username, $password, $dbname);

        if ($conn->connect_error) {
            $response['success'] = false;
            $response['message'] = "Connection failed: " . $conn->connect_error;
        } else {
            $stmt = $conn->prepare("DELETE FROM event_registrations WHERE user_id = ? AND event_id = ?");
            $stmt->bind_param("ii", $user_id, $event_id);

            if ($stmt->execute()) {
                $response['success'] = true;
                $response['message'] = "Registration deleted successfully.";
            } else {
                $response['success'] = false;
                $response['message'] = "Delete failed.";
            }

            $stmt->close();
            $conn->close();
        }
    } else {
        $response['success'] = false;
        $response['message'] = "Missing parameters.";
    }
} else {
    $response['success'] = false;
    $response['message'] = "Invalid request method.";
}

echo json_encode($response);
?>
