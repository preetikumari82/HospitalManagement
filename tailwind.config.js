/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        ink: {
          50: "#f3f6f6",
          100: "#dde6e6",
          200: "#b9cccc",
          300: "#8fadad",
          400: "#5f8888",
          500: "#3f6b6b",
          600: "#2d5555",
          700: "#254545",
          800: "#1c3535",
          900: "#0f2323",
          950: "#081616",
        },
        clover: {
          50: "#f0faf4",
          100: "#d9f2e3",
          200: "#a9e2c1",
          300: "#6fcb9c",
          400: "#3fae7c",
          500: "#279264",
          600: "#1c7752",
          700: "#195f43",
          800: "#164b37",
          900: "#123e2e",
        },
        clay: {
          400: "#e08a5b",
          500: "#cf6f3d",
          600: "#b45830",
        },
        paper: "#f7f5ef",
        line: "#e4ded0",
      },
      fontFamily: {
        display: ["'Fraunces'", "serif"],
        body: ["'Inter'", "sans-serif"],
        mono: ["'IBM Plex Mono'", "monospace"],
      },
      boxShadow: {
        panel: "0 1px 2px rgba(15,35,35,0.06), 0 8px 24px -12px rgba(15,35,35,0.18)",
      },
      borderRadius: {
        xl2: "1.1rem",
      },
    },
  },
  plugins: [],
}

