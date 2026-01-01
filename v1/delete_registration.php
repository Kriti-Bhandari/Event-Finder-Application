<?php
header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$password = "";
$database = "event_finder";

$conn = new mysqli($host, $user, $password, $database);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit;
}

if (isset($_GET['registration_id'])) {
    $id = intval($_GET['registration_id']);

    $sql = "DELETE FROM event_registrations WHERE id = $id";

    if ($conn->query($sql)) {
        echo json_encode(["success" => true, "message" => "Registration deleted"]);
    } else {
        echo json_encode(["success" => false, "message" => "Failed to delete"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Missing ID"]);
}
?>
