<?php
$host = "localhost";
$db = "event_finder";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$name = $_POST['name'] ?? '';
$description = $_POST['description'] ?? '';
$date = $_POST['date'] ?? '';
$time = $_POST['time'] ?? '';
$location = $_POST['location'] ?? '';
$category = $_POST['category'] ?? '';
$price = $_POST['price'] ?? '';
$seats = $_POST['total_seats'] ?? '';


if (!$name || !$description || !$date || !$time || !$location || !$category || !$price || !$seats) {
    echo "Missing required fields";
    exit;
}


$sql = "INSERT INTO events (name, description, date, time, location, category, price, total_seats) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo "Prepare failed: " . $conn->error;
    exit;
}

$stmt->bind_param("sssssssi", $name, $description, $date, $time, $location, $category, $price, $seats);

if ($stmt->execute()) {
    echo "Event Created Successfully!";
} else {
    echo "Failed to Create Event. Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>
