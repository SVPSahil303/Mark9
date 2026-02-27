// ============================
// GLOBAL STATE (from Thymeleaf)
// ============================
const DEFAULT_REWARD = "https://www.youtube-nocookie.com/embed/2bQrAJUYaQU";

// These are injected in canvas.html (your new canvas.html)
let currentSymbol =
  (typeof serverCurrentSymbol !== "undefined" && serverCurrentSymbol !== null)
    ? String(serverCurrentSymbol).trim()
    : "0";

let currentLearningType =
  (typeof serverLearningType !== "undefined" && serverLearningType !== null)
    ? String(serverLearningType).trim().toUpperCase()
    : "NUMBER";



// Attempt tracking
let attemptStartTime = Date.now();
let tabSwitchCount = 0;

function resetAttemptTracking() {
  attemptStartTime = Date.now();
  tabSwitchCount = 0;
}

// ============================
// CANVAS SETUP
// ============================
window.addEventListener("load", () => {
  const canvas = document.querySelector("canvas");
  const toolBtns = document.querySelectorAll(".tool");
  const fillColor = document.querySelector("#fill-color");
  const sizeSlider = document.querySelector("#size-slider");
  const colorBtns = document.querySelectorAll(".colors .option");
  const colorPicker = document.querySelector("#color-picker");
  const clearCanvas = document.querySelector(".clear-canvas");
  const saveImg = document.querySelector(".save-img");
  const predictBtn = document.querySelector(".predict-img");

  if (!canvas) return;

  const ctx = canvas.getContext("2d");

  // Global default values
  let prevMouseX, prevMouseY, snapshot,
    isDrawing = false,
    selectedTool = "brush",
    brushWidth = 30,
    selectedColor = "#FFFFFF";

  const setCanvasBackground = () => {
    ctx.fillStyle = "#000000";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = selectedColor;
  };

  // Set canvas size
  canvas.width = canvas.offsetWidth;
  canvas.height = canvas.offsetHeight;
  setCanvasBackground();

  const drawRect = (e) => {
    if (!fillColor || !fillColor.checked) {
      return ctx.strokeRect(
        e.offsetX,
        e.offsetY,
        prevMouseX - e.offsetX,
        prevMouseY - e.offsetY
      );
    }
    ctx.fillRect(
      e.offsetX,
      e.offsetY,
      prevMouseX - e.offsetX,
      prevMouseY - e.offsetY
    );
  };

  const drawCircle = (e) => {
    ctx.beginPath();
    let radius = Math.sqrt(
      Math.pow(prevMouseX - e.offsetX, 2) + Math.pow(prevMouseY - e.offsetY, 2)
    );
    ctx.arc(prevMouseX, prevMouseY, radius, 0, 2 * Math.PI);
    (fillColor && fillColor.checked) ? ctx.fill() : ctx.stroke();
  };

  const drawTriangle = (e) => {
    ctx.beginPath();
    ctx.moveTo(prevMouseX, prevMouseY);
    ctx.lineTo(e.offsetX, e.offsetY);
    ctx.lineTo(prevMouseX * 2 - e.offsetX, e.offsetY);
    ctx.closePath();
    (fillColor && fillColor.checked) ? ctx.fill() : ctx.stroke();
  };

  const startDraw = (e) => {
    isDrawing = true;
    prevMouseX = e.offsetX;
    prevMouseY = e.offsetY;
    ctx.beginPath();
    ctx.lineWidth = brushWidth;
    ctx.strokeStyle = selectedColor;
    ctx.fillStyle = selectedColor;
    snapshot = ctx.getImageData(0, 0, canvas.width, canvas.height);
  };

  const drawing = (e) => {
    if (!isDrawing) return;
    ctx.putImageData(snapshot, 0, 0);

    if (selectedTool === "brush" || selectedTool === "eraser") {
      ctx.strokeStyle = selectedTool === "eraser" ? "#000000" : selectedColor;
      ctx.lineTo(e.offsetX, e.offsetY);
      ctx.stroke();
    } else if (selectedTool === "rectangle") {
      drawRect(e);
    } else if (selectedTool === "circle") {
      drawCircle(e);
    } else {
      drawTriangle(e);
    }
  };

  // Tool buttons
  toolBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const active = document.querySelector(".options .active");
      if (active) active.classList.remove("active");
      btn.classList.add("active");
      selectedTool = btn.id;
    });
  });

  // Brush size
  if (sizeSlider) {
    sizeSlider.addEventListener("change", () => {
      brushWidth = Number(sizeSlider.value);
    });
  }

  // Colors
  colorBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const selected = document.querySelector(".options .selected");
      if (selected) selected.classList.remove("selected");
      btn.classList.add("selected");
      selectedColor = window.getComputedStyle(btn).getPropertyValue("background-color");
    });
  });

  if (colorPicker) {
    colorPicker.addEventListener("change", () => {
      colorPicker.parentElement.style.background = colorPicker.value;
      colorPicker.parentElement.click();
    });
  }

  // Clear
  if (clearCanvas) {
    clearCanvas.addEventListener("click", () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      setCanvasBackground();
    });
  }

  // Save
  if (saveImg) {
    saveImg.addEventListener("click", () => {
      const link = document.createElement("a");
      link.download = `${Date.now()}.png`;
      link.href = canvas.toDataURL();
      link.click();
    });
  }

  // Predict
  if (predictBtn) {
    predictBtn.addEventListener("click", () => predictSymbol());
  }

  // Canvas listeners
  canvas.addEventListener("mousedown", startDraw);
  canvas.addEventListener("mousemove", drawing);
  canvas.addEventListener("mouseup", () => (isDrawing = false));

  // Start flow: demo for current symbol
  resetAttemptTracking();
  showDemoVideo(currentSymbol);
});

// ============================
// PREDICTION (Flask)
// ============================
function predictSymbol() {
  const canvas = document.querySelector("canvas");
  if (!canvas) return;

  const imageData = canvas.toDataURL("image/png");

  if (currentLearningType === "NUMBER") {
        fetch("https://ml-webservice.onrender.com/predict", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ image: imageData })
        })
        .then((res) => res.json())
        .then((data) => {
        if (data && data.result !== undefined && data.result !== null) {
            const predicted = normalizePrediction(data.result);
            handlePrediction(predicted);
        } else {
            throw new Error(data?.error || "Prediction failed");
        }
        })
        .catch((err) => {
        console.error("Prediction error:", err);
        Swal.fire({
            title: "Error",
            text: "Prediction failed. Please try again.",
            icon: "error"
        });
        });
    } else {
    // For alphabets
    fetch("https://ml-webservice.onrender.com/predict_alpha", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ image: imageData })
    })
    .then((res) => res.json())
    .then((data) => {
      if (data && data.result !== undefined && data.result !== null) {
        const predicted = normalizePrediction(data.result);
        handlePrediction(predicted);
      } else {
        throw new Error(data?.error || "Prediction failed");
      }
    })
    .catch((err) => {
      console.error("Prediction error:", err);
      Swal.fire({
        title: "Error",
        text: "Prediction failed. Please try again.",
        icon: "error"
      });
    });
  }

  
}

function normalizePrediction(value) {
  // If NUMBER mode: keep only digit "0-9"
  // If ALPHABET mode: "A-Z"
  const s = String(value).trim();

  if (currentLearningType === "NUMBER") {
    // Extract first digit
    const match = s.match(/[0-9]/);
    return match ? match[0] : s;
  } else {
    // Extract first letter
    const match = s.toUpperCase().match(/[A-Z]/);
    return match ? match[0] : s.toUpperCase();
  }
}

// ============================
// HANDLE RESULT
// ============================
async function handlePrediction(predicted) {
  const expected = String(currentSymbol).trim().toUpperCase();
  const got = String(predicted).trim().toUpperCase();

  if (got === expected) {
    await sendProgressToServer(true);
  } else {
    await sendProgressToServer(false);
    Swal.fire({
      title: "Try Again!",
      html: `<p>You drew <b>${got}</b>, expected <b>${expected}</b>.</p>`,
      icon: "error",
      confirmButtonText: "OK"
    });
  }
}

// ============================
// SEND PROGRESS (Spring Boot)
// Matches your backend controller ProgressController.saveProgress()
// ============================
async function sendProgressToServer(correct) {
  const timeTakenMs = Date.now() - attemptStartTime;

  const params = new URLSearchParams({
    symbol: String(currentSymbol),
    learningType: String(currentLearningType), // must be NUMBER / ALPHABET
    correct: String(correct),                  // "true"/"false"
    timeTakenMs: String(timeTakenMs),
    tabSwitchCount: String(tabSwitchCount)
  });

  try {
    const res = await fetch("/child/progress/symbol", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params.toString()
    });

    if (!res.ok) throw new Error("Progress save failed: " + res.status);

    const data = await res.json();
    console.log("Progress response:", data);

    // Reset attempt tracking after server call
    resetAttemptTracking();

    // If incorrect, backend returns { goalAchieved:false } only
    if (!correct) return;

    // Goal achieved branch
    if (data.goalAchieved === true) {
      if (typeof confetti === "function") confetti(); // prevent crash if confetti not loaded

      await Swal.fire({
        title: "🎉 Goal Completed!",
        text: `You completed ${data.previousGoal} symbols!`,
        icon: "success",
        confirmButtonText: "Watch Reward"
      });

      // Show reward popup and WAIT until user closes it
      await showRewardVideo();

      // Backend sends nextStartSymbol ("0" or "A")
      currentSymbol = safeSymbol(data.nextStartSymbol);

      clearCanvasManually();
      showDemoVideo(currentSymbol);
      return;
    }

    // Normal correct flow: backend sends nextSymbol (or null)
    const next = safeSymbol(data.nextSymbol);

    if (!next) {
      showCompletionPopup();
      return;
    }

    currentSymbol = next;

    await Swal.fire({
      title: "Correct!",
      icon: "success",
      confirmButtonText: "Next"
    });

    clearCanvasManually();
    showDemoVideo(currentSymbol);

  } catch (err) {
    console.error(err);
    // session expired / not logged in
    window.location.href = "/child/login";
  }
}

function safeSymbol(v) {
  if (v === null || v === undefined) return null;
  const s = String(v).trim();
  if (!s || s === "null" || s === "undefined") return null;
  return s.toUpperCase();
}

// ============================
// DEMO VIDEO
// ============================
function showDemoVideo(symbol) {
  const s = safeSymbol(symbol) || (currentLearningType === "NUMBER" ? "0" : "A");

  Swal.fire({
    title: `Draw ${s}`,
    html: `
      <video id="demoVideo" width="100%" controls>
        <source src="/videos/${s}.mp4" type="video/mp4">
        Your browser does not support the video tag.
      </video>
    `,
    confirmButtonText: "Start Drawing",
    didOpen: () => {
      const video = Swal.getPopup()?.querySelector("#demoVideo");
      if (video) video.pause(); // don't autoplay while popup opens
    }
  });
}

// ============================
// COMPLETION POPUP (when nextSymbol is null)
// ============================
function showCompletionPopup() {
  Swal.fire({
    title: "🎉 All Symbols Completed!",
    text: "You finished this learning mode!",
    icon: "success",
    confirmButtonText: "OK"
  });
}

// ============================
// REWARD VIDEO POPUP
// IMPORTANT: returns Promise so we can await it
// ============================
function showRewardVideo() {
  const list =
    (Array.isArray(rewardList) && rewardList.length > 0)
      ? rewardList
      : [DEFAULT_REWARD];

  const raw = String(list[Math.floor(Math.random() * list.length)] || DEFAULT_REWARD).trim();
  const embedUrl = toEmbedUrl(raw);

  return Swal.fire({
    title: "Bonus Video!",
    width: "800px",
    html: `
      <div style="position:relative;padding-bottom:56.25%;height:0;overflow:hidden;">
        <iframe
          src="${embedUrl}"
          style="position:absolute;top:0;left:0;width:100%;height:100%;border:0;"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen>
        </iframe>
      </div>
    `,
    confirmButtonText: "Continue"
  });
}

// ============================
// YOUTUBE EMBED
// ============================
function toEmbedUrl(url) {
  try {
    let clean = String(url).trim();

    // Remove wrapping quotes if Thymeleaf/DB includes them
    if (
      (clean.startsWith('"') && clean.endsWith('"')) ||
      (clean.startsWith("'") && clean.endsWith("'"))
    ) {
      clean = clean.slice(1, -1).trim();
    }

    const u = new URL(clean);

    if (u.hostname.includes("youtu.be")) {
      const id = u.pathname.replace("/", "");
      return "https://www.youtube-nocookie.com/embed/" + id;
    }

    if (u.hostname.includes("youtube.com") && u.searchParams.get("v")) {
      return "https://www.youtube-nocookie.com/embed/" + u.searchParams.get("v");
    }

    if (u.hostname.includes("youtube.com") && u.pathname.includes("/embed/")) {
      const after = u.pathname.split("/embed/")[1];
      const id = after.split("?")[0];
      return "https://www.youtube-nocookie.com/embed/" + id;
    }

    return clean;
  } catch (e) {
    // If it's already an embed url or non-url string, fallback safely
    if (String(url).includes("youtube-nocookie.com/embed/")) return String(url).trim();
    return DEFAULT_REWARD;
  }
}

// ============================
// TAB SWITCH
// ============================
document.addEventListener("visibilitychange", () => {
  if (document.hidden) {
    tabSwitchCount++;

    // Optional: keep your alert-parent endpoint
    fetch("/api/child/alert-parent").catch(() => {});
  }
});

// ============================
// CLEAR CANVAS
// ============================
function clearCanvasManually() {
  const canvas = document.querySelector("canvas");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  ctx.fillStyle = "#000000";
  ctx.fillRect(0, 0, canvas.width, canvas.height);
}