function showImages(src) {
    var html = new Array();
    html.push("<div class='show_cover'></div>");
    html.push("<div class='show_block'><img src='images/close.png' onclick='closeImages()'>");
    html.push("<div class='show_content'><img src='" + src + "'></div></div>");
    $("body").append(html.join(""));
}

function closeImages() {
    $(".show_cover").remove();
    $(".show_block").remove();
}