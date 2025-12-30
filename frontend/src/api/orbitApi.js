const BASE_URL = "http://localhost:8080/api/orbit";

// ==============================
// 1️⃣ CURRENT SATELLITE POSITION
// ==============================
export async function getSatellitePosition({
  satellite,
  city,
  country,
  lat,
  lon,
  alt,
  utc,
}) {
  let url = `${BASE_URL}/pos/${satellite}?`;

  if (city) url += `city=${city}&`;
  if (country) url += `country=${country}&`;
  if (lat != null && lon != null) {
    url += `lat=${lat}&lon=${lon}&`;
    if (alt != null) url += `alt=${alt}&`;
  }
  if (utc) url += `utc=${utc}`;

  const res = await fetch(url);

  if (!res.ok) {
    throw new Error("Failed to fetch satellite position");
  }

  return res.json();
}

// ==============================
// 2️⃣ ORBIT TRACK (PATH POINTS)
// ==============================
export async function getOrbitTrack({
  satellite,
  city,
  lat,
  lon,
  alt,
}) {
  let url = `${BASE_URL}/track/${satellite}?`;

  if (city) url += `city=${city}&`;
  if (lat != null && lon != null) {
    url += `lat=${lat}&lon=${lon}&`;
    if (alt != null) url += `alt=${alt}&`;
  }

  const res = await fetch(url);

  if (!res.ok) {
    throw new Error("Failed to fetch orbit track");
  }

  return res.json();
}

// ==============================
// 3️⃣ NEXT PASS (AOS / LOS)
// ==============================
export async function getNextPass({
  satellite,
  city,
  lat,
  lon,
  alt,
}) {
  let url = `${BASE_URL}/pass/${satellite}?`;

  if (city) url += `city=${city}&`;
  if (lat != null && lon != null) {
    url += `lat=${lat}&lon=${lon}&`;
    if (alt != null) url += `alt=${alt}&`;
  }

  const res = await fetch(url);

  if (!res.ok) {
    throw new Error("Failed to fetch next pass");
  }

  return res.json();
}

// ==============================
// 4️⃣ LIVE RADAR (AZ / EL)
// ==============================
export async function getLiveRadar({
  satellite,
  city,
  lat,
  lon,
  alt,
}) {
  let url = `${BASE_URL}/live/${satellite}?`;

  if (city) url += `city=${city}&`;
  if (lat != null && lon != null) {
    url += `lat=${lat}&lon=${lon}&`;
    if (alt != null) url += `alt=${alt}&`;
  }

  const res = await fetch(url);

  if (!res.ok) {
    throw new Error("Failed to fetch live radar");
  }

  return res.json();
}
