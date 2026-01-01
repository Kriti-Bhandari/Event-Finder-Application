<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');

$event_id = $_POST['event_id'] ?? '';
if (!$event_id) {
    echo json_encode(["seats_available" => false, "error" => "Missing event_id"]);
    exit;
}

$conn = new mysqli("localhost", "root", "", "event_finder");
if ($conn->connect_error) {
    echo json_encode(["seats_available" => false, "error" => "DB connection failed"]);
    exit;
}

if (!$event_id) {
    echo json_encode(["seats_available" => false, "error" => "Missing event_id"]);
    exit;
}

// Step 1: Get total seats for the event
$sql = "SELECT total_seats FROM events WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $event_id);
$stmt->execute();
$stmt->bind_result($total_seats);
$stmt->fetch();
$stmt->close();

// Step 2: Count how many users already registered
$sql2 = "SELECT COUNT(*) FROM event_registrations WHERE event_id = ?";
$stmt2 = $conn->prepare($sql2);
$stmt2->bind_param("i", $event_id);
$stmt2->execute();
$stmt2->bind_result($registered);
$stmt2->fetch();
$stmt2->close();

// Step 3: Compare
$seats_left = $total_seats - $registered;
$isAvailable = $seats_left > 0;

echo json_encode(["seats_available" => $isAvailable, "seats_left" => $seats_left]);
$conn->close();
?>
