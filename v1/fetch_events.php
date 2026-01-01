<?php
header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$password = "";
$database = "event_finder";

$conn = new mysqli($host, $user, $password, $database);

if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

$events = [];

// Check if a category filter was sent
if (isset($_GET['category']) && !empty($_GET['category'])) {
    $category = $_GET['category'];

    $stmt = $conn->prepare("SELECT * FROM events WHERE category = ? ORDER BY date DESC");
    $stmt->bind_param("s", $category);
    $stmt->execute();
    $result = $stmt->get_result();
} else {
    $sql = "SELECT * FROM events ORDER BY date DESC";
    $result = $conn->query($sql);
}

if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $events[] = [
            "event_id" => $row["id"],
            "total_seats"=> $row["total_seats"],
            "event_name" => $row["name"],
            "event_description" => $row["description"],
            "event_date" => $row["date"],
            "event_time" => $row["time"],
            "event_location" => $row["location"],
            "event_category" => $row["category"],
            "ticket_price" => $row["price"],
        ];
    }
}

echo json_encode($events);
$conn->close();
?>
