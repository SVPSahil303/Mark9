document.addEventListener("DOMContentLoaded", () => {
  const cards = document.querySelectorAll(".mode-btn");
  const wrap = document.querySelector(".mode-card");

  // subtle main-card parallax tilt
  if (wrap) {
    document.addEventListener("mousemove", (e) => {
      const x = (e.clientX / window.innerWidth) - 0.5;
      const y = (e.clientY / window.innerHeight) - 0.5;
      wrap.style.transform = `rotateX(${(-y * 3).toFixed(2)}deg) rotateY(${(x * 3).toFixed(2)}deg)`;
    });
    document.addEventListener("mouseleave", () => wrap.style.transform = "");
  }

  // card tilt + sparkles + confetti burst on click
  cards.forEach(card => {
    const sparkBox = card.querySelector(".sparkles");

    card.addEventListener("mouseenter", () => spawnSparkles(sparkBox, 18));

    card.addEventListener("mousemove", (e) => {
      const rect = card.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      const cx = rect.width / 2;
      const cy = rect.height / 2;

      const dx = (x - cx) / cx;
      const dy = (y - cy) / cy;

      card.style.transform = `translateY(-12px) scale(1.02) rotateX(${(-dy * 7).toFixed(2)}deg) rotateY(${(dx * 7).toFixed(2)}deg)`;

      const emoji = card.querySelector(".emoji");
      if (emoji) emoji.style.transform = `translate(${(dx * 10).toFixed(1)}px, ${(dy * 10).toFixed(1)}px)`;
    });

    card.addEventListener("mouseleave", () => {
      card.style.transform = "";
      const emoji = card.querySelector(".emoji");
      if (emoji) emoji.style.transform = "";
      if (sparkBox) sparkBox.innerHTML = "";
    });

    card.addEventListener("click", (e) => {
      burstConfetti(e.clientX, e.clientY);
    });
  });

  function spawnSparkles(container, count) {
    if (!container) return;
    container.innerHTML = "";

    for (let i = 0; i < count; i++) {
      const s = document.createElement("span");
      s.style.position = "absolute";
      s.style.left = (Math.random() * 100) + "%";
      s.style.top = (Math.random() * 100) + "%";
      s.style.width = "10px";
      s.style.height = "10px";
      s.style.borderRadius = Math.random() > 0.5 ? "2px" : "999px";
      s.style.background = "rgba(255,255,255,0.95)";
      s.style.boxShadow = "0 0 18px rgba(255,255,255,0.65)";
      s.style.opacity = "0";
      s.style.transform = `rotate(${Math.random() * 90}deg)`;

      container.appendChild(s);

      s.animate(
        [
          { transform: "translateY(12px) scale(0.4) rotate(0deg)", opacity: 0 },
          { transform: "translateY(-6px) scale(1) rotate(45deg)", opacity: 1 },
          { transform: "translateY(-24px) scale(0.25) rotate(90deg)", opacity: 0 }
        ],
        {
          duration: 650 + Math.random() * 550,
          easing: "ease-out",
          delay: Math.random() * 160
        }
      );
    }
  }

  function burstConfetti(x, y) {
    const n = 26;
    const colors = ["#00E5FF", "#00FF85", "#FF4DFF", "#FFE65A", "#ffffff"];

    for (let i = 0; i < n; i++) {
      const p = document.createElement("div");
      p.style.position = "fixed";
      p.style.left = x + "px";
      p.style.top = y + "px";
      p.style.width = "8px";
      p.style.height = "8px";
      p.style.borderRadius = Math.random() > 0.5 ? "2px" : "999px";
      p.style.pointerEvents = "none";
      p.style.zIndex = "9999";
      p.style.background = colors[Math.floor(Math.random() * colors.length)];
      p.style.boxShadow = "0 0 12px rgba(255,255,255,0.35)";

      document.body.appendChild(p);

      const angle = Math.random() * Math.PI * 2;
      const dist = 90 + Math.random() * 80;
      const dx = Math.cos(angle) * dist;
      const dy = Math.sin(angle) * dist;

      p.animate(
        [
          { transform: "translate(0,0) scale(1)", opacity: 1 },
          { transform: `translate(${dx}px, ${dy}px) rotate(${Math.random()*360}deg) scale(0.9)`, opacity: 0 }
        ],
        { duration: 650 + Math.random()*350, easing: "cubic-bezier(.2,.8,.2,1)" }
      );

      setTimeout(() => p.remove(), 1100);
    }
  }
});
