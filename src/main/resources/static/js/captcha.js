const fonts = ["cursive"];
let captchaValue = "";
var captchaResult = "";

function gencaptcha() {
    const operators = ['+', '-', '*'];
    const operator = operators[Math.floor(Math.random() * operators.length)];

    let number1, number2, result;

    if (operator === '+') {
        number1 = Math.floor(Math.random() * 9) + 1;
        number2 = Math.floor(Math.random() * 9) + 1;
        result = number1 + number2;
    } else if (operator === '-') {
        number1 = Math.floor(Math.random() % 9) + 1;
        number2 = Math.floor(Math.random() * number1);
        result = number1 - number2;
    } else {
        number1 = Math.floor(Math.random() * 5) + 1;
        number2 = Math.floor(Math.random() * 5) + 1;
        result = number1 * number2;
    }

    captchaValue = `${number1} ${operator} ${number2}`;

    captchaResult = result;
}

function setcaptcha() {
    let canvas = document.getElementById("captchaCanvas");
    let context = canvas.getContext("2d");
    // Clear the canvas before drawing the new captcha
    context.clearRect(0, 0, canvas.width, canvas.height);

    // Set up the drawing parameters
    const fontSize = 30;
    const canvasWidth = 120;
    const canvasHeight = canvas.height;
    const fontColor = "#c68036"; // Change to desired font color
    // Draw the captcha text on the canvas
    captchaValue.split("").forEach((char, index) => {
        let rotate = Math.random() < 0.5 ? -Math.trunc(Math.random() * 25) : Math.trunc(Math.random() * 25);
        const font = fonts[Math.trunc(Math.random() * fonts.length)];

        context.save();
        context.font = `${fontSize}px ${font}`;
        context.fillStyle = fontColor;
        context.textBaseline = "middle";
        context.textAlign = "center";
        context.translate((canvasWidth / captchaValue.length) * (index + 2.5), canvasHeight / 2);
        context.rotate((rotate * Math.PI) / 180);
        context.fillText(char, 0, 0);
        context.restore();
    });

    // Convert the canvas to an image
    const captchaImage = new Image();
    captchaImage.src = canvas.toDataURL();

}

function initCaptcha() {
    $(".captcha_refresh").on("click", function () {
        gencaptcha();
        setcaptcha();
    });

    gencaptcha();
    setcaptcha();
}
