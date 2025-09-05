import React, { useState, useEffect } from "react";
import axios from "axios";

const ReminderSettings = () => {
  const user = JSON.parse(localStorage.getItem("user"));
  const [reminderDaysBefore, setReminderDaysBefore] = useState(2);
  const [message, setMessage] = useState("");
  const [latestSetting, setLatestSetting] = useState(null);
  const [loading, setLoading] = useState(true);

  // Upcoming reminders
  const [upcomingReminders, setUpcomingReminders] = useState([]);
  const [loadingReminders, setLoadingReminders] = useState(true);

  useEffect(() => {
    if (!user) return;
    fetchLatestSetting();
    fetchUpcomingReminders();
    // eslint-disable-next-line
  }, []);

  // Fetch only the latest setting for this user
  const fetchLatestSetting = async () => {
    setLoading(true);
    try {
      const res = await axios.get(`http://localhost:9090/api/reminders/user/${user.id}`);
      if (Array.isArray(res.data) && res.data.length > 0) {
        const latest = res.data[0]; // latest first (DESC) ✅
        setLatestSetting(latest);
        setReminderDaysBefore(latest.reminderDaysBefore);
      } else {
        setLatestSetting(null);
        setReminderDaysBefore(2);
      }
    } catch (err) {
      console.error("Error fetching latest reminder setting:", err);
      setLatestSetting(null);
    } finally {
      setLoading(false);
    }
  };

  // Fetch upcoming reminders for this user
  const fetchUpcomingReminders = async () => {
    setLoadingReminders(true);
    try {
      const res = await axios.get(`http://localhost:9090/api/reminders/due/${user.id}`);
      setUpcomingReminders(res.data || []);
    } catch (err) {
      console.error("Error fetching upcoming reminders:", err);
      setUpcomingReminders([]);
    } finally {
      setLoadingReminders(false);
    }
  };

  // Save new setting (adds a new row)
  const saveSetting = async () => {
    const days =
      typeof reminderDaysBefore === "number" && reminderDaysBefore > 0
        ? reminderDaysBefore
        : 1;
    try {
      await axios.post("http://localhost:9090/api/reminders", {
        userId: user.id,
        reminderDaysBefore: days,
        billId: null,
        enabled: true,
      });
      setMessage("✅ Reminder preference saved!");
      setTimeout(() => setMessage(""), 3000);
      // Refresh both sections
      fetchLatestSetting();
      fetchUpcomingReminders();
    } catch (err) {
      console.error("Failed to save reminder:", err);
      setMessage("❌ Could not save setting.");
    }
  };

  // Summary banner
  const getSummaryMessage = () => {
    if (!latestSetting) return null;
    const n = latestSetting.reminderDaysBefore;
    return (
      <div
        className="alert alert-success my-4"
        style={{
          fontSize: "1.1rem",
          fontWeight: 500,
          boxShadow: "0 1px 5px rgba(80,170,100,0.18)",
          borderLeft: "6px solid #34c759",
        }}
      >
        <span role="img" aria-label="reminder" style={{ fontSize: "1.5em" }}>
          ⏰
        </span>{" "}
        You will be <b>reminded {n} day{n > 1 ? "s" : ""} before</b> every bill is due.
      </div>
    );
  };

  if (!user) {
    return (
      <div className="card p-3 mt-4">
        <div className="alert alert-warning">Please log in to manage reminders.</div>
      </div>
    );
  }

  return (
    <div className="card p-3 mt-4">
      <h5>📅 Reminder Preferences</h5>
      <div className="form-group mb-2">
        <label>Days before due date:</label>
        <input
          type="number"
          min="1"
          step="1"
          value={reminderDaysBefore}
          onChange={(e) => {
            const raw = e.target.value;
            // allow empty while typing; coerce to number otherwise
            setReminderDaysBefore(raw === "" ? "" : Number(raw));
          }}
          onBlur={() => {
            // sanitize on blur
            if (!reminderDaysBefore || reminderDaysBefore < 1) {
              setReminderDaysBefore(1);
            }
          }}
          className="form-control"
        />
      </div>
      <button className="btn btn-info" onClick={saveSetting}>
        💾 Save Reminder Setting
      </button>
      {message && <div className="alert alert-info mt-2">{message}</div>}

      <hr />

      <h6 className="mt-3 mb-2">🟢 Active Reminder</h6>
      {loading && <div>Loading setting...</div>}
      {!loading && latestSetting && getSummaryMessage()}
      {!loading && !latestSetting && (
        <div className="alert alert-info">No reminder preference set yet.</div>
      )}

      <h6 className="mt-4 mb-2">⏰ Upcoming Bill Reminders</h6>
      {loadingReminders && <div>Loading reminders...</div>}
      {!loadingReminders && upcomingReminders.length === 0 && (
        <div className="alert alert-info">No upcoming reminders! 🎉</div>
      )}
      {!loadingReminders && upcomingReminders.length > 0 && (
        <ul className="list-group">
          {upcomingReminders.map((msg, idx) => (
            <li className="list-group-item" key={idx} style={{ fontWeight: 500 }}>
              {msg}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ReminderSettings;
