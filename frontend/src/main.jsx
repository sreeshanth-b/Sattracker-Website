import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "./index.css";

/* 🔑 CESIUM ION TOKEN (REQUIRED) */
window.Cesium.Ion.defaultAccessToken =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI0NDNmNDM3OC00YmM3LTQwMTItYTYwNi1lMTJjYzhmNzk1ZDQiLCJpZCI6MzcyMTA2LCJpYXQiOjE3NjY1NDc5NTl9.0TUCyzZCLLbr2_N8mz2GKg3LNUyJbNikwbzXHNf2tFU";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
