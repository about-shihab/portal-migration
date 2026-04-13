function bindSlider() {
    let slideTrack = $(".slide-track");
    let slides = slideTrack.find(".slide");
    let totalWidth = slides.length * 150;
    slideTrack.css("width", totalWidth + "px");
}
