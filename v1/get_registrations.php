<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "event_finder";

$conn = new mysqli($servername, $username, $password, $dbname);

// Check DB connection
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed: " . $conn->connect_error]));
}

// Get user_id from GET or POST
$user_id = isset($_GET['user_id']) ? $_GET['user_id'] : (isset($_POST['user_id']) ? $_POST['user_id'] : '');

if (!$user_id) {
    echo json_encode(["success" => false, "message" => "Missing user_id"]);
    exit;
}

// Fetch registrations
$sql = "SELECT event_name, payment_status, registered_at FROM registrations WHERE user_id = ? ORDER BY registered_at DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $user_id);
$stmt->execute();

$result = $stmt->get_result();
$registrations = [];

while ($row = $result->fetch_assoc()) {
    $registrations[] = $row;
}

echo json_encode([
    "success" => true,
    "registrations" => $registrations
]);

$stmt->close();
$conn->close();
?>
