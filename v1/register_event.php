<?php
header('Content-Type: application/json');

$host = "localhost";
$db = "event_finder";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "error" => "Database connection failed"]);
    exit;
}

$user_id = $_POST['user_id'] ?? '';
$event_id = $_POST['event_id'] ?? '';

if (!$user_id || !$event_id) {
    echo json_encode(["success" => false, "error" => "Missing required fields"]);
    exit;
}

// Check if user already registered
$checkStmt = $conn->prepare("SELECT id FROM event_registrations WHERE user_id = ? AND event_id = ?");
$checkStmt->bind_param("ii", $user_id, $event_id);
$checkStmt->execute();
$checkStmt->store_result();

if ($checkStmt->num_rows > 0) {
    echo json_encode(["success" => false, "error" => "Already registered"]);
    $checkStmt->close();
    $conn->close();
    exit;
}
$checkStmt->close();

// Check available seats
$seatStmt = $conn->prepare("SELECT total_seats FROM events WHERE id = ?");
$seatStmt->bind_param("i", $event_id);
$seatStmt->execute();
$seatStmt->bind_result($seats);
$seatStmt->fetch();
$seatStmt->close();

if ($seats <= 0) {
    echo json_encode(["success" => false, "error" => "No seats available"]);
    $conn->close();
    exit;
}

// Register user
$insertStmt = $conn->prepare("INSERT INTO event_registrations (user_id, event_id) VALUES (?, ?)");
$insertStmt->bind_param("ii", $user_id, $event_id);

if ($insertStmt->execute()) {
    $insertStmt->close();

    // ✅ Decrease seat count correctly
    $updateStmt = $conn->prepare("UPDATE events SET total_seats = total_seats - 1 WHERE id = ?");
    $updateStmt->bind_param("i", $event_id);
    $updateStmt->execute();
    $updateStmt->close();

    echo json_encode(["success" => true, "message" => "Event registered"]);
} else {
    echo json_encode(["success" => false, "error" => "Registration failed"]);
    $insertStmt->close();
}

$conn->close();
?>
