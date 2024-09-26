// Function to handle file uploads
function uploadFiles(files, currentPath) {
    const formData = new FormData();

    // Add each file to the form data
    for (const file of files) {
        formData.append('files', file); // 'files[]' is the name under which files will be sent
        console.log('Preparing to upload file:', file.name);
    }

    formData.append('path', currentPath);

    // Send the form data to the server
    fetch('/upload', { // Adjust this URL to your backend endpoint
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(result => {
            console.log('File upload successful:', result);
            // Handle success (e.g., update UI)
        })
        .catch(error => {
            console.error('Error uploading files:', error);
            // Handle error (e.g., display error message)
        });
}

// Function to handle folder uploads
function uploadFolder(files, currentPath) {
    const formData = new FormData();

    // Add each file in the folder to the form data
    for (const file of files) {
        // Use `webkitRelativePath` to retain folder structure
        formData.append('files', file, file.webkitRelativePath);
        console.log('Preparing to upload file from folder:', file.webkitRelativePath);
    }

    formData.append('path', currentPath);

    // Send the form data to the server
    fetch('/upload', { // Adjust this URL to your backend endpoint
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(result => {
            console.log('Folder upload successful:', result);
            // Handle success (e.g., update UI)
        })
        .catch(error => {
            console.error('Error uploading folder:', error);
            // Handle error (e.g., display error message)
        });
}