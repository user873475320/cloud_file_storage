function goHome() {
    window.location.href = '/home';
}

function logout() {
    // Define the URL for the logout endpoint
    const url = '/auth/logout'; // Replace with your actual URL

    // Send POST request for logout
    fetch(url, {
        method: 'POST', // Use POST method for logout
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded' // Set the content type for form data
        },
        // You can add body if needed, e.g., for passing additional data
        body: new URLSearchParams({}) // Empty body or add parameters if needed
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // or response.json() if the response is JSON
        })
        .then(result => {
            console.log('Logout successful:', result);
            // Redirect to login page or another appropriate action
            window.location.href = '/'; // Redirect to login page after successful logout
        })
        .catch(error => {
            console.error('Error during logout:', error);
            // Handle error (e.g., display an error message)
        });
}
