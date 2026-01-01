<?php
header('Content-Type: application/json');

$user_id = $_GET['user_id'] ?? '';

$conn = new mysqli("localhost", "root", "", "event_finder");

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "DB connection failed"]);
    exit;
}

if (!$user_id) {
    echo json_encode(["success" => false, "message" => "Missing user_id"]);
    exit;
}

$sql = "
    SELECT e.id, e.name, e.date, e.location, e.category, e.price 
    FROM event_registrations r 
    JOIN events e ON r.event_id = e.id 
    WHERE r.user_id = ?
";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$events = [];

while ($row = $result->fetch_assoc()) {
    $events[] = $row;
}

echo json_encode(["success" => true, "events" => $events]);

$stmt->close();
$conn->close();
?>
