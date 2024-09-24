// Handle file drop
const fileDropZone = document.getElementById('fileDropZone');
fileDropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    fileDropZone.style.backgroundColor = '#e0f7ff';
});
fileDropZone.addEventListener('dragleave', (e) => {
    fileDropZone.style.backgroundColor = '#f0f8ff';
});
fileDropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    fileDropZone.style.backgroundColor = '#f0f8ff';
    const files = [...e.dataTransfer.files];
    uploadFiles(files);  // Call your upload function here
});

// Handle folder drop
const folderDropZone = document.getElementById('folderDropZone');
folderDropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    folderDropZone.style.backgroundColor = '#e0ffe0';
});
folderDropZone.addEventListener('dragleave', (e) => {
    folderDropZone.style.backgroundColor = '#f0fff0';
});
folderDropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    folderDropZone.style.backgroundColor = '#f0fff0';
    const items = e.dataTransfer.items;
    const folders = [];

    for (let i = 0; i < items.length; i++) {
        const item = items[i].webkitGetAsEntry();
        if (item.isDirectory) {
            folders.push(items[i].getAsFile());
        }
    }
    uploadFolder(folders);  // Call your upload function here
});
