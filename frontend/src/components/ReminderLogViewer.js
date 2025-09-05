import React, { useState, useEffect } from "react";
import axios from "axios";

const ReminderLogViewer = () => {
  const user = JSON.parse(localStorage.getItem("user"));
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const res = await axios.get(
        `http://localhost:9090/api/reminder-logs/user/${user.id}`
      );
      setLogs(res.data);
    } catch (err) {
      console.error("Error fetching reminder logs:", err);
      setError("Failed to load reminder logs.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-4">
      <h3>⏰ Reminder Log</h3>
      {loading && <p>Loading logs...</p>}
      {error && <div className="alert alert-danger">{error}</div>}

      {!loading && logs.length === 0 && (
        <div className="alert alert-info">
          No reminder logs found.
        </div>
      )}

      {logs.length > 0 && (
        <table className="table table-striped">
          <thead>
            <tr>
              <th>ID</th>
              <th>Message</th>
              <th>Bill ID</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {logs.map((log) => (
              <tr key={log.id}>
                <td>{log.id}</td>
                <td>{log.message}</td>
                <td>{log.billId}</td>
                <td>{new Date(log.logDate).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ReminderLogViewer;