<?php
header('Content-Type: application/json');
$host = "localhost";
$db = "event_finder";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "error" => "DB error"]);
    exit;
}

$user_id = $_GET['user_id'] ?? '';
if (!$user_id) {
    echo json_encode(["success" => false, "error" => "Missing user ID"]);
    exit;
}

$query = "
          SELECT e.id, e.name, e.date, e.location, e.category, e.price 
    FROM event_registrations r 
    JOIN events e ON r.event_id = e.id 
    WHERE r.user_id = ?";

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
$tickets = [];
while ($row = $result->fetch_assoc()) {
    $tickets[] = $row;
}

echo json_encode(["success" => true, "tickets" => $tickets]);
$stmt->close();
$conn->close();
?>