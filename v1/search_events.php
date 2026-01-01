<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "event_finder");

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Database connection failed"]);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $query = isset($_POST['query']) ? trim($_POST['query']) : '';

    if (empty($query)) {
        echo json_encode([]);
        exit();
    }

    $stmt = $conn->prepare("SELECT * FROM events WHERE name LIKE ? OR description LIKE ? OR location LIKE ?");
    $searchTerm = "%$query%";
    $stmt->bind_param("sss", $searchTerm, $searchTerm, $searchTerm);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $events = array();
    while($row = $result->fetch_assoc()) {
        $events[] = $row;
    }

    echo json_encode($events);
}
?>
