document.getElementById('searchQuery').addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault(); // Prevent the default form submission behavior
        document.getElementById('searchForm').submit(); // Manually submit the form
    }
});

function goToFileFolder(pathToFolder) {
    window.location.href = '/home?path=' + pathToFolder;
}