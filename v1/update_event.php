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

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id = $_POST['event_id'];
    $name = $_POST['name'];
    $description = $_POST['description'];
    $date = $_POST['date'];
    $time = $_POST['time'];
    $location = $_POST['location'];
    $category = $_POST['category'];
    $price = $_POST['price'];
    $seats = $_POST['total_seats'];

    $sql = "UPDATE events SET name='$name', description='$description', date='$date', time='$time',
            location='$location', category='$category', price='$price', total_seats='$seats' WHERE id='$id'";

    if (mysqli_query($conn, $sql)) {
        echo "Success";
    } else {
        echo "Error updating record: " . mysqli_error($conn);
    }
}
?>
