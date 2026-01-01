<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "event_finder");

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "DB error"]);
    exit;
}

if (isset($_GET['event_id'])) {
    $event_id = intval($_GET['event_id']);
    $query = "DELETE FROM events WHERE id = $event_id";

    if ($conn->query($query)) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false, "message" => "Deletion failed"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Missing event_id"]);
}
?>
