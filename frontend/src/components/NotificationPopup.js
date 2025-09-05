import { useEffect } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const NotificationPopup = ({ userId }) => {
  useEffect(() => {
    const fetchNotificationSummary = async () => {
      try {
        const res = await axios.get(
          `http://localhost:9090/api/reminders/notifications/${userId}`
        );
        const data = res.data;
        console.log(data);

        if (data.Overdue > 0) {
          toast.error(`🔴 You have ${data.Overdue} overdue bill(s)!`, {
            position: "top-right",
            autoClose: 5000,
          });
        }

        if (data.Upcoming > 0) {
          toast.warning(`🟡 You have ${data.Upcoming} upcoming bill(s).`, {
            position: "top-right",
            autoClose: 5000,
          });
        }
      } catch (err) {
        console.error("Failed to fetch notification summary:", err);
      }
    };

    fetchNotificationSummary();
  }, [userId]);

  return null;
}

export default NotificationPopup;
