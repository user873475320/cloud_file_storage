window.onload = function () {
    setTimeout(function () {
        var banner = document.querySelector('.banner');
        if (banner) {
            banner.style.display = 'none';
        }
    }, 5000);
};