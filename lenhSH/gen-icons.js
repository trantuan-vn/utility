// gen-icons.js
const sharp = require("sharp");

const sizes = [76, 120, 152, 180, 192, 512];
const src = "/Users/cunkem/trading-sto/apps/web/public/logo.svg"; // file gốc SVG

(async () => {
  for (const size of sizes) {
    const out = `/Users/cunkem/trading-sto/apps/web/public/icons/icon-${size}x${size}.png`;
    await sharp(src)
      .resize(size, size)
      .png()
      .toFile(out);
    console.log("✔️ tạo:", out);
  }
})();
