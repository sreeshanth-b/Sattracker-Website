function GroundStationSelector({ city, setCity }) {
  return (
    <div>
      <label className="block text-sm mb-1">Ground Station</label>
      <select
        value={city}
        onChange={(e) => setCity(e.target.value)}
        className="bg-gray-800 text-white p-2 rounded"
      >
        <option value="Hyderabad">Hyderabad</option>
        <option value="Bangalore">Bangalore</option>
        <option value="Delhi">Delhi</option>
      </select>
    </div>
  );
}

export default GroundStationSelector;
