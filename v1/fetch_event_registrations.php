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

$sql = "
SELECT 
    er.id AS registration_id,
    e.name,
    u.username,
    u.email,
    er.registered_at
FROM event_registrations er
JOIN users u ON er.user_id = u.id
JOIN events e ON er.event_id = e.id
ORDER BY er.registered_at DESC
";

$result = mysqli_query($conn, $sql);

$data = array();
while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

echo json_encode($data);
?>
